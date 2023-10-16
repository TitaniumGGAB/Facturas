package com.guillermogarcia.facturas.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;
import java.util.List;

public class FragmentDetalleCliente extends Fragment {

    public static final String CLIENTE_EXTRA_DETALLE = "com.guillermogarcia.facturas.cliente";

    private Cliente cliente;
    private RecyclerView rvListado;
    private IFacturaListener listenerFactura;
    private TextView tvNameCliente;
    private TextView tvApellidosCliente ;
    private TextView tvCifCliente;
    private TextView tvDireccionCliente;
    private Button btModificarCliente;
    private Button btEliminarCliente;


    public FragmentDetalleCliente() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            if(bundle.containsKey(CLIENTE_EXTRA_DETALLE)) {
                cliente = (Cliente) bundle.getSerializable(CLIENTE_EXTRA_DETALLE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);
        rvListado = inflatedView.findViewById(R.id.rvListadoFacturasDelCliente);
        AdaptadorFacturas adaptadorFacturas = new AdaptadorFacturas(getActivity(), FragmentListado.TipoListado.SEGUN_FACTURA, new ArrayList<Factura>(cliente.getFacturas()));
        adaptadorFacturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerFactura != null) {
                    List<Factura> facturasList = cliente.getFacturas();
                    ArrayList<Factura> facturas = new ArrayList<Factura>(facturasList);
                    listenerFactura.onFacturaSeleccionado(facturas.get(rvListado.getChildAdapterPosition(view)));
                }
            }
        });
        rvListado.setAdapter(adaptadorFacturas);
        rvListado.addItemDecoration(new DividerItemDecoration(rvListado.getContext(), DividerItemDecoration.VERTICAL));
        rvListado.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        tvNameCliente = inflatedView.findViewById(R.id.tvNameCliente);
        tvApellidosCliente = inflatedView.findViewById(R.id.tvApellidosCliente);
        tvCifCliente = inflatedView.findViewById(R.id.tvCifCliente);
        tvDireccionCliente = inflatedView.findViewById(R.id.tvDireccionCliente);
        btModificarCliente = inflatedView.findViewById(R.id.buttonModificarCliente);
        btEliminarCliente = inflatedView.findViewById(R.id.buttonEliminarCliente);
        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String nombreCliente = "Nombre: " + cliente.getNombre();
        String apellidosCliente = "Apellidos" + cliente.getApellidos();
        String cifCliente = "CIF: " + cliente.getCif();
        String direccionCliente = "Dirección: " + cliente.getDireccion();
        tvNameCliente.setText(nombreCliente);
        tvApellidosCliente.setText(apellidosCliente);
        tvCifCliente.setText(cifCliente);
        tvDireccionCliente.setText(direccionCliente);
        //btModificarCliente.?
        //btEliminarCliente.?

    }

    public void setFacturasListener(IFacturaListener listener){
        this.listenerFactura = listener;
    }
}
