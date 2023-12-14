package com.guillermogarcia.facturas.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorClientes;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.modelos.Cliente;


public class FragmentClientes extends Fragment {

    private AdaptadorClientes adaptadorClientes;
    private final IClienteListener listener;

    private Context context;

    public FragmentClientes(IClienteListener listener){
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("clientes");
        Query query = ref.orderBy("nombre", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Cliente> options = new FirestoreRecyclerOptions.Builder<Cliente>()
                .setQuery(query, Cliente.class)
                .build();

        adaptadorClientes = new AdaptadorClientes(options, listener, getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.rvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptadorClientes);

        // El adaptador empieza a escuchar a la base de datos.
        adaptadorClientes.startListening();

        return view;
    }

    @Override
    public void onStop() {

        // Cuando la app se para, el adapter parará de eschuchar a la base de datos.
        super.onStop();
        if (adaptadorClientes != null){
            adaptadorClientes.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(context.getString(R.string.clientes));
        // Cuando se reanude la app, el adaptador reanudará la eschucha a la base de datos.
        if (adaptadorClientes != null){
            adaptadorClientes.startListening();
        }
    }
}
