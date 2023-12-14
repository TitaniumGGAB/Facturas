package com.guillermogarcia.facturas.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;
public class FragmentFacturas extends Fragment{

    private AdaptadorFacturas adaptadorFacturas;
    private final IFacturaListener listener;

    public FragmentFacturas(IFacturaListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facturas, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("Facturas");
        Query query = ref.orderBy("fechaModificacion", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Factura> options = new FirestoreRecyclerOptions.Builder<Factura>()
                .setQuery(query, Factura.class)
                .build();

        if (options.getSnapshots().isEmpty()) {
            Log.d("FragmentDetalleCliente", "FirestoreRecyclerOptions de Facturas está vacío. No hay datos para mostrar.");
            // Puedes mostrar un mensaje de que no hay datos o realizar alguna otra acción según tus necesidades.
        }else{
            Log.d("FragmentDetalleCliente", "FirestoreRecyclerOptions de Facturas está rellenado. Si hay datos para mostrar.");
        }


        adaptadorFacturas = new AdaptadorFacturas(options, listener, getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.rvList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptadorFacturas);

        // El adaptador empieza a escuchar a la base de datos.
        adaptadorFacturas.startListening();

        return view;
    }

    @Override
    public void onStop() {

        // Cuando la app se para, el adapter parará de eschuchar a la base de datos.
        super.onStop();
        if (adaptadorFacturas != null){
            adaptadorFacturas.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle("Facturas");
        // Cuando se reanude la app, el adaptador reanudará la eschucha a la base de datos.
        if (adaptadorFacturas != null){
            adaptadorFacturas.startListening();
        }
    }



}
