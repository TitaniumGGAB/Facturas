package com.guillermogarcia.facturas.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturasDetalle;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FragmentDetalleCliente extends Fragment implements IFacturaListener {


    private TextView tvNombreApellidos, tvCif, tvDireccion, tvTelefono, tvEmail, tvFechaAgregado, tvFacturasAsociadas;
    private Button btnModificar, btnEliminar;
    private RecyclerView rvListadoFacturasDelClienteDetalle;
    private AdaptadorFacturasDetalle adaptadorFacturas;

    private Cliente cliente;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);

        // Inicializar las vistas
        tvNombreApellidos = view.findViewById(R.id.tvNombreApellidosClienteDetalle);
        tvCif = view.findViewById(R.id.tvCifClienteDetalle);
        tvDireccion = view.findViewById(R.id.tvDireccionClienteDetalle);
        tvTelefono = view.findViewById(R.id.tvTelefonoClienteDetalle);
        tvEmail = view.findViewById(R.id.tvEmailClienteDetalle);
        tvFechaAgregado = view.findViewById(R.id.tvFechaAgregadoClienteDetalle);
        btnModificar = view.findViewById(R.id.buttonModificarClienteDetalle);
        btnEliminar = view.findViewById(R.id.buttonEliminarClienteDetalle);
        tvFacturasAsociadas = view.findViewById(R.id.textViewFacturasAsociadas);

        // Obtener datos del cliente de los argumentos del fragmento
        Bundle bundle = getArguments();
        if (bundle != null) {
            cliente = (Cliente) bundle.getSerializable("cliente");
            mostrarDetallesCliente(cliente);

            if (!cliente.getFacturas().isEmpty()) {
                rvListadoFacturasDelClienteDetalle = view.findViewById(R.id.rvListadoFacturasDelClienteDetalle);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference facturasRef = db.collection("Facturas");
                Query query = facturasRef.whereIn("identificador", cliente.getFacturas());
                FirestoreRecyclerOptions<Factura> options = new FirestoreRecyclerOptions.Builder<Factura>()
                        .setQuery(query, Factura.class)
                        .build();


                adaptadorFacturas = new AdaptadorFacturasDetalle(options, this, getContext());
                rvListadoFacturasDelClienteDetalle.setLayoutManager(new LinearLayoutManager(getContext()));
                rvListadoFacturasDelClienteDetalle.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                rvListadoFacturasDelClienteDetalle.setAdapter(adaptadorFacturas);
                //cargarFacturasDelCliente(cliente.getFacturas());
                adaptadorFacturas.startListening();


            }else{
                tvFacturasAsociadas.setText("SIN FACTURAS ASOCIADAS");
            }

            btnModificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Abrir el FragmentModificarCliente con los datos del cliente
                    FragmentModificarCliente fragmentModificarCliente = FragmentModificarCliente.newInstance(cliente);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, fragmentModificarCliente);
                    transaction.addToBackStack(null);  // Permite regresar al FragmentDetalleCliente
                    transaction.commit();
                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarDialogoConfirmacion();
                }
            });

        }else{
            tvFacturasAsociadas.setText("SIN FACTURAS ASOCIADAS");
        }



        return view;
    }


    private void mostrarDetallesCliente(Cliente cliente) {
        if (cliente != null) {
            tvNombreApellidos.setText(cliente.getNombre() + " " + cliente.getApellidos());
            tvCif.setText("CIF: " + cliente.getCif());
            tvDireccion.setText("Dirección: " + cliente.getDireccion());
            tvTelefono.setText("Teléfono: " + cliente.getTelefono());
            tvEmail.setText("Email: " + cliente.getEmail());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaAgregado = dateFormat.format(cliente.getFecha_agregado());
            tvFechaAgregado.setText("Cliente agregado el: " + fechaAgregado);
        }
    }


    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar Eliminación");
        builder.setMessage("¿Está seguro de eliminar el cliente? Las facturas asociadas al cliente también se eliminarán.");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Usuario ha confirmado eliminar el cliente
                eliminarClienteYFacturas();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Usuario ha cancelado la eliminación
                dialog.dismiss(); // Cerrar el diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para eliminar el cliente


    private void eliminarClienteYFacturas() {
        // Eliminar facturas asociadas
        eliminarFacturas();

        // Eliminar al cliente después de haber eliminado las facturas
        eliminarCliente();
    }

    // Método para eliminar las facturas asociadas al cliente
    private void eliminarFacturas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facturasRef = db.collection("Facturas");

        List<String> facturasCliente = cliente.getFacturas();

        // Iterar sobre las facturas del cliente y eliminar cada una
        for (String idFactura : facturasCliente) {
            facturasRef.document(idFactura).delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar error al eliminar factura (opcional)
                            Log.e("Eliminar Factura", "Error al eliminar factura: " + idFactura, e);
                        }
                    });
        }
    }

    // Método para eliminar al cliente después de eliminar las facturas
    private void eliminarCliente() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clientesRef = db.collection("clientes");

        // Obtener el identificador único del cliente
        String idCliente = cliente.getIdentificador();

        // Eliminar los datos de la base de datos
        clientesRef.document(idCliente).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al eliminar cliente
                        Toast.makeText(getContext(), "Cliente y facturas asociadas eliminados", Toast.LENGTH_SHORT).show();
                        // Cerrar el fragmento después de eliminar
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar cliente
                        Toast.makeText(getContext(), "Error al eliminar cliente", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }

    @Override
    public void onStop() {

        // Cuando la app se para, el adapter parará de eschuchar a la base de datos.
        super.onStop();
        if (adaptadorFacturas != null){
            adaptadorFacturas.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle("Facturas");
        // Cuando se reanude la app, el adaptador reanudará la eschucha a la base de datos.
        if (adaptadorFacturas != null){
            adaptadorFacturas.startListening();
        }
    }
}