package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.fragments.FragmentDetalleFactura;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class AdaptadorFacturas extends FirestoreRecyclerAdapter<Factura, AdaptadorFacturas.FacturaHolder> {

    private final IFacturaListener listener;
    private final Context context;

    public AdaptadorFacturas(@NonNull FirestoreRecyclerOptions<Factura> options, IFacturaListener listener, Context context) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull FacturaHolder facturaHolder, int i, @NonNull Factura factura) {

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha());
        facturaHolder.tvNumeroFactura.setText(factura.getNumeroFactura());
        facturaHolder.tvFecha.setText(fecha);
        //Utilizamos BigDecimal para limitar el número de decimales
        BigDecimal bd = new BigDecimal(factura.getPrecioTotal());
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        Double precioTotal = bd.doubleValue ();
        facturaHolder.tvPrecioTotal.setText(context.getString(R.string.campo_total) + String.valueOf(precioTotal) + "€");

        // Obtenemos el ID del cliente  para acceder a este en la base de datos
        String clienteId = factura.getCliente();


        DocumentReference clienteRef = FirebaseFirestore.getInstance().collection("clientes").document(clienteId);

        clienteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Cliente cliente = documentSnapshot.toObject(Cliente.class);
                    if (cliente != null) {
                        String nombreCliente = cliente.getNombre() + " " + cliente.getApellidos();
                        facturaHolder.tvNombreCliente.setText(nombreCliente);
                    }
                } else {
                    Log.e("AdaptadorFacturas", "No existe el documento del cliente " + clienteId);
                }
            }
        });

        //Dependiendo del estado de la factura ponemos un icono u otro
        if (factura.isBorrador()) {
            // Factura en borrador
            facturaHolder.icono.setImageResource(R.drawable.borrador);
        } else if (factura.isPagado()) {
            // Factura pagada
            facturaHolder.icono.setImageResource(R.drawable.pagado);
        } else {
            // Factura pendiente
            facturaHolder.icono.setImageResource(R.drawable.pendiente);
        }

    }

    @NonNull
    @Override
    public FacturaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_factura, parent, false);
        return new FacturaHolder(v);
    }

    class FacturaHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroFactura;
        TextView tvNombreCliente;
        TextView tvFecha;
        TextView tvPrecioTotal;

        ImageView icono;

        public FacturaHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
            icono = itemView.findViewById(R.id.icono_estado_factura);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getBindingAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(getBindingAdapterPosition());
                        Factura facturaSeleccionada = snapshot.toObject(Factura.class);

                        abrirFragmentoDetalleFactura(facturaSeleccionada);
                    }
                }
            });

        }
        private void abrirFragmentoDetalleFactura(Factura factura) {
            FragmentDetalleFactura fragmentDetalleFactura = new FragmentDetalleFactura();
            Bundle bundle = new Bundle();
            bundle.putSerializable("factura", factura);
            fragmentDetalleFactura.setArguments(bundle);

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragmentDetalleFactura);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}








