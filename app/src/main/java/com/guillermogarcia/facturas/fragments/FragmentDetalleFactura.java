package com.guillermogarcia.facturas.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class FragmentDetalleFactura extends Fragment implements IFacturaListener {

    private Factura factura;

    private String idFacturaModificar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView
            tvNumeroFactura,
            tvNombreApellidosCliente,
            tvFecha,
            tvDescripcion,
            tvBaseImponible,
            tvIva,
            tvPrecioTotal,
            tvFechaModificacion;

    private Switch switchBorrador, switchPagado;
    private Button btModificar, btEliminar;

    public FragmentDetalleFactura() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_detalle_factura, container, false);


        tvNumeroFactura = inflatedView.findViewById(R.id.tvNumeroFacturaDetalle);
        tvNombreApellidosCliente = inflatedView.findViewById(R.id.tvNombreApellidosClienteDeFacturaDetalle);
        tvFecha = inflatedView.findViewById(R.id.tvFechaDetalle);
        tvDescripcion = inflatedView.findViewById(R.id.tvDescripcionDetalle);
        tvBaseImponible= inflatedView.findViewById(R.id.tvBaseImponibleDetalle);
        tvIva = inflatedView.findViewById(R.id.tvIvaDetalle);
        tvPrecioTotal= inflatedView.findViewById(R.id.tvPrecioTotalDetalle);
        switchBorrador = inflatedView.findViewById(R.id.switchBorrador);
        switchPagado= inflatedView.findViewById(R.id.switchPagado);
        btModificar= inflatedView.findViewById(R.id.buttonModificar);
        btEliminar= inflatedView.findViewById(R.id.buttonEliminarFactura);
        tvFechaModificacion= inflatedView.findViewById(R.id.tvFechaModificacionDetalle);


        Bundle bundle = getArguments();

        factura = (Factura) bundle.getSerializable("factura");

        idFacturaModificar = factura.getIdentificador();

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha());
        String fechaModificacion = new SimpleDateFormat("dd/MM/yyyy").format(factura.getFechaModificacion());

        tvNumeroFactura.setText("Nº Factura: " + factura.getNumeroFactura());
        tvFecha.setText("Fecha: " + fecha);
        tvDescripcion.setGravity(Gravity.CENTER);
        tvDescripcion.setText(factura.getDescripcion());
        tvBaseImponible.setText(String.valueOf("Base imponible: " + factura.getBaseImponible() + "€"));
        tvIva.setText(String.valueOf("IVA: " + factura.getIvaPrecio()) + "€");
        tvPrecioTotal.setText("Total: " + String.valueOf(factura.getPrecioTotal()) + "€");
        switchBorrador.setChecked(factura.isBorrador());
        switchPagado.setChecked(factura.isPagado());
        tvFechaModificacion.setText("Última modificación: " + fechaModificacion);


        String clienteId = factura.getCliente();
        if (clienteId != null && !clienteId.isEmpty()) {
            DocumentReference clienteRef = FirebaseFirestore.getInstance().collection("clientes").document(clienteId);
            clienteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if (cliente != null) {
                            String nombreApellidosCliente = cliente.getNombre() + " " + cliente.getApellidos();
                            tvNombreApellidosCliente.setText("Cliente: " + nombreApellidosCliente);
                        }
                    }
                }
            });
        }

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una instancia del FragmentModificarFactura y pasar la factura como argumento
                FragmentModificarFactura fragmentModificarFactura = FragmentModificarFactura.newInstance(factura);

                // Obtener el FragmentManager y comenzar la transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Reemplazar el FragmentDetalleFactura con el FragmentModificarFactura
                fragmentTransaction.replace(R.id.content_frame, fragmentModificarFactura);
                fragmentTransaction.addToBackStack(null);  // Permite volver al fragmento anterior con el botón de retroceso
                fragmentTransaction.commit();
            }
        });

        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

        if(switchPagado.isChecked()){
            switchBorrador.setVisibility(View.GONE);
        }else if(switchBorrador.isChecked()){
            switchPagado.setVisibility(View.GONE);
        }


        return inflatedView;
    }

    private void mostrarDialogoConfirmacion() {
        // Crear y mostrar un diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("¿Está seguro de eliminar la factura?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarFactura();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada si el usuario elige "No"
                    }
                })
                .show();
    }

    private void eliminarFactura() {

        Context context = getContext();

        // Eliminar la factura de la base de datos
        db.collection("Facturas")
                .document(idFacturaModificar)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Factura eliminada con éxito", Toast.LENGTH_SHORT).show();
                    // Eliminar la factura de la lista de facturas asociadas al cliente
                    eliminarFacturaDeCliente();
                    FragmentFacturas f = new FragmentFacturas(this);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, f);
                    transaction.addToBackStack(null);  // Permite regresar al FragmentDetalleCliente
                    transaction.commit();
                    Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar factura", Toast.LENGTH_SHORT).show();
                });
    }
    private void eliminarFacturaDeCliente() {
        Context context = getContext();
        // Obtener el cliente seleccionado en el Spinner
        DocumentReference clienteRef = db.collection("clientes").document(factura.getCliente());

        // Obtener el cliente por su identificador
        clienteRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento del cliente existe
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if (cliente != null) {
                            cliente.getFacturas().remove(idFacturaModificar);

                            // Actualizar el documento del cliente en Firestore
                            db.collection("clientes")
                                    .document(cliente.getIdentificador())
                                    .update("facturas", cliente.getFacturas())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Cliente actualizado después de eliminar factura", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al actualizar el cliente después de eliminar factura", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error en la obtención de datos desde Firestore
                    // ...
                });

    }


    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}
