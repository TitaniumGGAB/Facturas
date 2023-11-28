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
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import java.util.ArrayList;

public class AdaptadorClientes extends FirestoreRecyclerAdapter<Cliente, AdaptadorClientes.ClienteHolder> {

    private final IClienteListener listener;
    private final Context context;

    public AdaptadorClientes(@NonNull FirestoreRecyclerOptions<Cliente> options, IClienteListener listener, Context context) {
        super(options);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ClienteHolder clienteHolder, int i, @NonNull Cliente cliente) {
        clienteHolder.tvNombreCliente.setText(cliente.getNombre());

    }

    @NonNull
    @Override
    public ClienteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_cliente, parent, false);
        return new ClienteHolder(v);
    }

    class ClienteHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente;

        public ClienteHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNameCliente);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getBindingAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        listener.onClienteListSelected(getSnapshots().getSnapshot(getBindingAdapterPosition()), getBindingAdapterPosition());
                    }
                }
            });
        }
    }
}