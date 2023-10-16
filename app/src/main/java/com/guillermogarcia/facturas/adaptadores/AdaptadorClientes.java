package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.modelos.Cliente;
import java.util.ArrayList;

public class AdaptadorClientes extends RecyclerView.Adapter<AdaptadorClientes.ClienteViewHolder>  implements View.OnClickListener{

    private final ArrayList<Cliente> clientes;
    private final Context context;
    private View.OnClickListener listener;

    public AdaptadorClientes(Context context, ArrayList<Cliente> clientesRecibidos) {
        this.context = context;
        this.clientes = clientesRecibidos;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_cliente, parent, false);
        itemView.setOnClickListener(this);
        return new AdaptadorClientes.ClienteViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder (@NonNull ClienteViewHolder holder, int position){
        Cliente clienteAux = clientes.get(position);
        holder.bindCliente(clienteAux);
    }

    @Override
    public int getItemCount () {
        return clientes.size();
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

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombreCliente;
        private final Context context;

        public ClienteViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvNombreCliente = itemView.findViewById(R.id.tvNameCliente);
        }

        public void bindCliente(Cliente cliente) {
                tvNombreCliente.setText(cliente.getNombre());
        }



    }
}
