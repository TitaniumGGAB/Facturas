package com.guillermogarcia.facturas.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class FragmentClientes extends Fragment {
    private ArrayList<Cliente> listaClientes;

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
    }
}
