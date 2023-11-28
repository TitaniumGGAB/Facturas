package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.fragments.FragmentListado;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
/*
public class AdaptadorFacturas extends RecyclerView.Adapter<AdaptadorFacturas.FacturaViewHolder>  implements View.OnClickListener{
    private final ArrayList<Factura> facturas;
    private static final ArrayList<Factura> borradores = new ArrayList<Factura>();
    private static final ArrayList<Factura> pendientesPago = new ArrayList<Factura>();
    private final Context context;
    private final FragmentListado.TipoListado tipoListado;
    private View.OnClickListener listener;
    private static View itemView;

    public AdaptadorFacturas(Context context, FragmentListado.TipoListado tipoListado, ArrayList<Factura> facturasrecibidas) {
        this.context = context;
        this.facturas = facturasrecibidas;
        this.tipoListado = tipoListado;

        for(Factura factura: facturasrecibidas){
            if(factura.isBorrador()){
                borradores.add(factura);
            }
            if(factura.isPagado()){
                pendientesPago.add(factura);
            }
        }

    }



    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_factura, parent, false);
        itemView.setOnClickListener(this);
        return new AdaptadorFacturas.FacturaViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder (@NonNull FacturaViewHolder holder, int position){
        //En este método según el tipo de listado ordenamos la lista de facturas de una u otra manera
        if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_PRECIO_FACTURA)){
            Factura facturaAux = facturas.get(position);
            Collections.sort(facturas);
            holder.bindFactura(facturaAux, tipoListado);
        } else if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_BORRADOR)){
            Factura borrador = borradores.get(position);
            holder.bindFactura(borrador, tipoListado);
        } else if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_PENDIENTE_PAGO)){
            Factura pendientePago = pendientesPago.get(position);
            holder.bindFactura(pendientePago, tipoListado);
        } else if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_FACTURA)) {
            Factura factura = facturas.get(position);
            holder.bindFactura(factura, tipoListado);
        }

    }

    @Override
    public int getItemCount () {
        //Cómo no podemos esperar que hay el mismo numero de autores y de categorias
        //dependiendo del tipo de listado devolvermos el tamaño de autor o de categoria
        if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_FACTURA) ||
                tipoListado.equals(FragmentListado.TipoListado.SEGUN_PRECIO_FACTURA)){
            return facturas.size();
        }else if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_BORRADOR)){
            return borradores.size();
        }else if(tipoListado.equals(FragmentListado.TipoListado.SEGUN_PENDIENTE_PAGO)){
            return pendientesPago.size();
        }
        return 0;
    }

    public void setOnClickListener (View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick (View view){
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public static class FacturaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreCliente;
        private final TextView tvNumeroFactura;
        private final TextView tvFecha;
        private final TextView tvBaseImponible;
        private final TextView tvIva;
        private final TextView tvPrecioTotal;
        private final Context context;

        public FacturaViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvNumeroFactura = itemView.findViewById(R.id.tvNumeroFactura);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvBaseImponible = itemView.findViewById(R.id.tvBaseImponible);
            tvIva = itemView.findViewById(R.id.tvIva);
            tvPrecioTotal = itemView.findViewById(R.id.tvPrecioTotal);

        }

        public void bindFactura(Factura factura, FragmentListado.TipoListado tipoListado) {
            String nombreApellidosCliente = factura.getCliente().getNombre() + " " + factura.getCliente().getApellidos();
            //String nombreApellidosCliente = factura.getCliente().getApellidos();
            tvNumeroFactura.setText(factura.getNumeroFactura());
            tvNombreCliente.setText(nombreApellidosCliente);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            tvFecha.setText(sdf.format(factura.getFecha().getTime()));
            String baseImponible = "Base: " + String.format("%.2f", factura.getBaseImponible()) + "€";
            tvBaseImponible.setText(baseImponible);
            String iva = "IVA: " + String.format("%.2f", factura.getIvaPrecio()) + "€";
            tvIva.setText(iva);
            String precioTotal = "Total: " + String.format("%.2f", factura.getPrecioTotal()) + "€";
            tvPrecioTotal.setText(precioTotal);
            tvNombreCliente.setText(factura.getCliente().getNombre());

        }


    }
}


*/





