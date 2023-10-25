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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorClientes;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;

public class FragmentListado extends Fragment {
    private RecyclerView rvListado;
    private IFacturaListener listenerFactura;
    private IClienteListener listenerCliente;
    private ArrayList<Factura> facturas;
    public enum TipoListado {
        SEGUN_CLIENTE, SEGUN_FACTURA, SEGUN_PRECIO_FACTURA,
        SEGUN_BORRADOR, SEGUN_PENDIENTE_PAGO
    }
    private ArrayList<Cliente> clientes;
    private TipoListado tipoListado;
    public static final String TIPO = "com.guillermogarcia.facturas.Fragments.TIPO";

    public static boolean modificar;


    public FragmentListado() {

    }

    public void setFacturas(ArrayList<Factura> facturas){
        this.facturas = facturas;
    }

    public void setClientes(ArrayList<Cliente> clientes){
        this.clientes = clientes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Check", "FragmentListado -> onCreate()");
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            if(bundle.containsKey(TIPO)) {
                tipoListado = (TipoListado) bundle.getSerializable(TIPO);
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvListado = view.findViewById(R.id.rvListado);
        Log.d("Check", "El tipolistado es " + tipoListado);
        if(tipoListado.equals(TipoListado.SEGUN_FACTURA)){
        }
        if(tipoListado.equals(TipoListado.SEGUN_CLIENTE)){
            AdaptadorClientes adaptadorClientes = new AdaptadorClientes(getActivity(), clientes);
            adaptadorClientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listenerCliente != null) {
                        listenerCliente.onClienteSeleccionado(clientes.get(rvListado.getChildAdapterPosition(view)));
                    }
                }
            });
            rvListado.setAdapter(adaptadorClientes);

        }else{
            AdaptadorFacturas adaptadorFacturas = new AdaptadorFacturas(getActivity(), tipoListado, facturas);
            adaptadorFacturas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listenerFactura != null) {
                        listenerFactura.onFacturaSeleccionado(facturas.get(rvListado.getChildAdapterPosition(view)));
                    }
                }
            });
            rvListado.setAdapter(adaptadorFacturas);
        }

        rvListado.addItemDecoration(new DividerItemDecoration(rvListado.getContext(), DividerItemDecoration.VERTICAL));
        rvListado.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


    }


    public void setFacturasListener(IFacturaListener listenerFactura) {
        this.listenerFactura = listenerFactura;
    }

    public void setClientesListener(IClienteListener listenerCliente) {
        this.listenerCliente = listenerCliente;
    }


    public ArrayList<Factura> getFacturas() {
        return facturas;
    }
    public ArrayList<Cliente> getClientes() {
        return clientes;
    }


    public static boolean isModificar() {
        return modificar;
    }

    public static void setModificar(boolean modificar) {
        FragmentListado.modificar = modificar;
    }
}
