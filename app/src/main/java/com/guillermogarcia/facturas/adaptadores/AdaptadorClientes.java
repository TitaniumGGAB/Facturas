package com.guillermogarcia.facturas.adaptadores;

import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.fragments.FragmentDetalleCliente;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.modelos.Cliente;

import java.text.SimpleDateFormat;
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
        String fechaAgregado = new SimpleDateFormat("dd/MM/yyyy").format(cliente.getFecha_agregado());
        clienteHolder.tvNombreCliente.setText(cliente.getNombre() + " " + cliente.getApellidos());
        clienteHolder.tvCifCliente.setText("CIF: " + cliente.getCif());
        clienteHolder.tvFacturasAsociadas.setText("Nº Facturas: " + String.valueOf(cliente.getFacturas().size()));
        clienteHolder.tvDireccionCliente.setText("Dirección: " + cliente.getDireccion());
        clienteHolder.tvFechaAgregado.setText("Agregado: " + fechaAgregado);
    }

    @NonNull
    @Override
    public ClienteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado_cliente, parent, false);
        return new ClienteHolder(v);
    }

    class ClienteHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente;
        TextView tvCifCliente;
        TextView tvFacturasAsociadas;
        TextView tvDireccionCliente;

        TextView tvFechaAgregado;

        public ClienteHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNameCliente);
            tvCifCliente = itemView.findViewById(R.id.tvCif);
            tvFacturasAsociadas = itemView.findViewById(R.id.tvFacturasAsociadas);
            tvDireccionCliente = itemView.findViewById(R.id.tvDireccion);
            tvFechaAgregado = itemView.findViewById(R.id.tvFechaAgregado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getBindingAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                        // Obtenemos el cliente seleccionado
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(getBindingAdapterPosition());
                        Cliente clienteSeleccionado = snapshot.toObject(Cliente.class);

                        // Abrimos el fragmento de detalles del cliente
                        abrirFragmentoDetalleCliente(clienteSeleccionado);
                    }
                }
            });
        }
    }

    private void abrirFragmentoDetalleCliente(Cliente cliente) {
        FragmentDetalleCliente fragmentDetalleCliente = new FragmentDetalleCliente();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cliente", cliente);
        fragmentDetalleCliente.setArguments(bundle);

        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragmentDetalleCliente);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}