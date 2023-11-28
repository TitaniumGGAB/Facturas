package com.guillermogarcia.facturas.adaptadores;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.listeners.IFacturaListener2;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.List;
/*
public class AdaptadorFactura2 extends FirestoreRecyclerAdapter<Factura, AdaptadorFactura2.FacturaHolder> {
    private final IFacturaListener2 listener;
    private final Context context;

    public AdaptadorFactura2(@NonNull FirestoreRecyclerOptions<Factura> options, IFacturaListener2 listener, Context context) {
        super(options);
        this.context = context;
        this.listener = listener;
        Log.d("Check", "Adaptador1");
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaHolder facturaHolder, int i, @NonNull Factura factura) {
        Log.d("Check", "Adaptador2");
        String nombreCliente = factura.getCliente().getNombre() + " " + factura.getCliente(). getApellidos();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha());

        facturaHolder.tvNombreCliente.setText(nombreCliente);
        facturaHolder.tvNumeroFactura.setText(factura.getNumeroFactura());
        Log.d("Check", "El numero de una de las facturas es " + factura.getNumeroFactura());
        facturaHolder.tvFecha.setText(fecha);
        facturaHolder.tvBaseImponible.setText(String.valueOf(factura.getBaseImponible()));
        facturaHolder.tvIva.setText(String.valueOf(factura.getIvaPrecio()));
        facturaHolder.tvPrecioTotal.setText(String.valueOf(factura.getPrecioTotal()));


    }

    @NonNull
    @Override
    public AdaptadorFactura2.FacturaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Check", "Adaptador3");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_factura, parent, false);
        return new AdaptadorFactura2.FacturaHolder(v);
    }

    class FacturaHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente;
        TextView tvNumeroFactura;
        TextView tvFecha;
        TextView tvBaseImponible;
        TextView tvIva;
        TextView tvPrecioTotal;

        public FacturaHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvBaseImponible = itemView.findViewById(R.id.tvBaseImponible);
            tvIva = itemView.findViewById(R.id.tvIva);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
            Log.d("Check", "Adaptador4");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getBindingAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listener.onFactura2ListSelected(getSnapshots().getSnapshot(getBindingAdapterPosition()), getBindingAdapterPosition());
                    }
                }
            });

        }
    }

}
*/


