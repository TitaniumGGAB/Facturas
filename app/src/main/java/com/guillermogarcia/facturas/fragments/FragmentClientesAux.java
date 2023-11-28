package com.guillermogarcia.facturas.fragments;

import androidx.fragment.app.Fragment;

import com.guillermogarcia.facturas.modelos.Cliente;

import java.util.ArrayList;

public class FragmentClientesAux extends Fragment {
    private ArrayList<Cliente> listaClientes;
/*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        // Aquí deberías obtener tu lista de clientes, por ejemplo, desde tu actividad principal
        // listaClientes = ((MainActivity) requireActivity()).getClientes();

        // Luego, configura un adaptador para mostrar la lista de clientes en una vista
        ListView listViewClientes = view.findViewById(R.id.listViewClientes);
        AdaptadorClientes adaptadorClientes = new AdaptadorClientes(requireContext(), listaClientes);
        listViewClientes.setAdapter(adaptadorClientes);

        return view;
    }*/
}
