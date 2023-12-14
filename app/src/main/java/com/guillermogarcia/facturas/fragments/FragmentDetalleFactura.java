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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

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

    private Context context;

    public FragmentDetalleFactura() {
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

        tvNumeroFactura.setText(context.getString(R.string.campo_numero_factura) + factura.getNumeroFactura());
        tvFecha.setText(context.getString(R.string.campo_fecha) + fecha);
        tvDescripcion.setGravity(Gravity.CENTER);
        tvDescripcion.setText(factura.getDescripcion());
        tvBaseImponible.setText(String.valueOf(context.getString(R.string.campo_base_imponible) + factura.getBaseImponible() + "€"));
        BigDecimal bd = new BigDecimal(factura.getIvaPrecio());
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        Double ivaPrecio = bd.doubleValue ();
        tvIva.setText(String.valueOf("IVA: " + ivaPrecio + "€"));
        tvPrecioTotal.setText("Total: " + String.valueOf(factura.getPrecioTotal()) + "€");
        switchBorrador.setChecked(factura.isBorrador());
        switchPagado.setChecked(factura.isPagado());
        tvFechaModificacion.setText(context.getString(R.string.campo_ultima_modificacion) + fechaModificacion);

        //Mediante el id del cliente conseguimos acceder a su documento en la nube y obtener el nombre y apellidos
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
                            tvNombreApellidosCliente.setText(context.getString(R.string.campo_cliente) + nombreApellidosCliente);
                        }
                    }
                }
            });
        }

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentModificarFactura fragmentModificarFactura = FragmentModificarFactura.newInstance(factura);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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
        builder.setMessage(context.getString(R.string.seguro_eliminar_factura))
                .setPositiveButton(context.getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarFactura();
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacemos nada si el usuario elige "No"
                    }
                })
                .show();
    }

    private void eliminarFactura() {

        Context context = getContext();

        // Eliminamos la factura de la base de datos
        db.collection("Facturas")
                .document(idFacturaModificar)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, context.getString(R.string.factura_eliminada_exito), Toast.LENGTH_SHORT).show();
                    // Eliminamos la factura de la lista de facturas asociadas al cliente
                    eliminarFacturaDeCliente();
                    FragmentFacturas f = new FragmentFacturas(this);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, f);
                    transaction.addToBackStack(null);  // Permite regresar al FragmentDetalleCliente
                    transaction.commit();
                    Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, context.getString(R.string.error_eliminar_factura), Toast.LENGTH_SHORT).show();
                });
    }
    private void eliminarFacturaDeCliente() {
        Context context = getContext();
        DocumentReference clienteRef = db.collection("clientes").document(factura.getCliente());

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
                                        Toast.makeText(context, context.getString(R.string.cliente_actualizado_eliminar_factura), Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, context.getString(R.string.error_cliente_actualizado_eliminar_factura), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });

    }


    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}
