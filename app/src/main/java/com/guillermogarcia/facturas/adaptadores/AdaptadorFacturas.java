package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.fragments.FragmentListado;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

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
        String nombreCliente = factura.getCliente().getNombre() + " " + factura.getCliente().getApellidos();
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(factura.getFecha());

        facturaHolder.tvNumeroFactura.setText(factura.getNumeroFactura());
        facturaHolder.tvNombreCliente.setText(nombreCliente);
        facturaHolder.tvFecha.setText(fecha);
        facturaHolder.tvBaseImponible.setText(String.valueOf(factura.getBaseImponible()));
        facturaHolder.tvIva.setText(String.valueOf(factura.getIvaPrecio()));
        facturaHolder.tvPrecioTotal.setText(String.valueOf(factura.getPrecioTotal()));

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
        TextView tvBaseImponible;
        TextView tvIva;


        public FacturaHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);
            tvBaseImponible = itemView.findViewById(R.id.tvBaseImponible);
            tvIva = itemView.findViewById(R.id.tvIva);


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








