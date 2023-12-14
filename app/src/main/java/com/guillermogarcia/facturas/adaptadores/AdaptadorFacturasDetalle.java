package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

public class AdaptadorFacturasDetalle extends FirestoreRecyclerAdapter<Factura, AdaptadorFacturasDetalle.FacturaHolder> {

    private final IFacturaListener listener;
    private final Context context;

    public AdaptadorFacturasDetalle(@NonNull FirestoreRecyclerOptions<Factura> options, IFacturaListener listener, Context context) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull FacturaHolder holder, int position, @NonNull Factura factura) {

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha());
        holder.tvNumeroFactura.setText(factura.getNumeroFactura());
        holder.tvFecha.setText(fecha); // Ajusta la forma de mostrar la fecha según tus necesidades

        BigDecimal bd = new BigDecimal(factura.getPrecioTotal());
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        Double precioTotal = bd.doubleValue ();
        holder.tvPrecioTotal.setText("Total: " + String.valueOf(precioTotal) + "€");
        /*holder.tvBaseImponible.setText(String.valueOf(factura.getBaseImponible()));
        holder.tvIva.setText(String.valueOf(factura.getIvaPrecio()));*/

        String clienteId = factura.getCliente();
        Log.d("FacturaAdapterDetalle", "LLegamos");
        if (clienteId != null && !clienteId.isEmpty()) {
            Log.d("FacturaAdapterDetalle", "Cliente ID: " + clienteId);

            // Realizar una consulta directa al documento del cliente usando el ID
            DocumentReference clienteRef = FirebaseFirestore.getInstance().collection("clientes").document(clienteId);

            clienteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if (cliente != null) {
                            String nombreCliente = cliente.getNombre() + " " + cliente.getApellidos();
                            holder.tvNombreCliente.setText(nombreCliente);
                        }
                    } else {
                        Log.d("FacturaAdapterDetalle", "AdaptadorFacturasDetalle: no existe el documento del cliente");
                    }
                }
            });
        } else {
            Log.e("FacturaAdapterDetalle", "ID del cliente es nulo o vacío para la factura con ID: " + factura.getIdentificador());
        }

        if (factura.isBorrador()) {
            // Factura en borrador
            holder.icono.setImageResource(R.drawable.borrador);
        } else if (factura.isPagado()) {
            // Factura pagada
            holder.icono.setImageResource(R.drawable.pagado);
        } else {
            // Factura pendiente
            holder.icono.setImageResource(R.drawable.pendiente);
        }

    }

    @NonNull
    @Override
    public FacturaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_factura, parent, false);
        return new FacturaHolder(view);
    }

    class FacturaHolder extends RecyclerView.ViewHolder {

        private final TextView tvNumeroFactura;
        private final TextView tvNombreCliente;
        private final TextView tvFecha;
        private final TextView tvPrecioTotal;

        ImageView icono;
        /*private final TextView tvBaseImponible;
        private final TextView tvIva;*/

        public FacturaHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
            icono = itemView.findViewById(R.id.icono_estado_factura);
            /*tvBaseImponible = itemView.findViewById(R.id.tvBaseImponible);
            tvIva = itemView.findViewById(R.id.tvIva);*/

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getBindingAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listener.onFacturaListSelected(getSnapshots().getSnapshot(getBindingAdapterPosition()), getBindingAdapterPosition());
                    }
                }
            });
        }


    }
}
