package com.guillermogarcia.facturas.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.adaptadores.AdaptadorClientes;
import com.guillermogarcia.facturas.adaptadores.AdaptadorFacturas;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;

public class FragmentModificarFactura extends Fragment {

    public static final String FACTURA_MODIFICAR_DETALLE = "com.guillermogarcia.facturas.FACTURA_MODIFICAR_DETALLE";

    private Factura factura;

    private EditText editTextNumeroFactura;
    private Spinner spinnerCliente;
    private EditText editTextDescripcion;
    private EditText editTextBaseImponible;
    private TextView tvIva;
    private TextView tvPrecioTotal;
    private Switch switchBorrador;
    private Switch switchPagado;
    private Button btGuardar;
    private Button btActualizar;
    private AdaptadorClientes adaptador;
    private ArrayList<Cliente> clientes;

    public FragmentModificarFactura(ArrayList<Cliente> clientes) {
        this.clientes = clientes;
    }

    public FragmentModificarFactura() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            if(bundle.containsKey(FACTURA_MODIFICAR_DETALLE)) {
                factura = (Factura) bundle.getSerializable(FACTURA_MODIFICAR_DETALLE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_detalle_modificar_factura, container, false);
        spinnerCliente = inflatedView.findViewById(R.id.spinnerCliente);
        editTextNumeroFactura = inflatedView.findViewById(R.id.editTextNumeroFactura);
        editTextDescripcion = inflatedView.findViewById(R.id.editTextDescripcion);
        editTextBaseImponible = inflatedView.findViewById(R.id.editTextBaseImponible);
        tvIva = inflatedView.findViewById(R.id.tvIvaFacturaModificar);
        tvPrecioTotal = inflatedView.findViewById(R.id.tvPrecioTotalFacturaModificar);
        switchBorrador = inflatedView.findViewById(R.id.switchBorradorFacturaModificar);
        switchPagado = inflatedView.findViewById(R.id.switchPagadoFacturaModificar);
        btGuardar = inflatedView.findViewById(R.id.buttonGuardar);
        btActualizar = inflatedView.findViewById(R.id.buttonActualizarPrecioIVA);
            return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        editTextNumeroFactura.setHint(factura.getNumeroFactura());
        editTextDescripcion.setHint(factura.getDescripcion());
        editTextBaseImponible.setHint(String.valueOf(factura.getBaseImponible()));
        AdaptadorClientes adaptador = new AdaptadorClientes(getActivity(), clientes);
        spinnerCliente.setAdapter((SpinnerAdapter) adaptador);

        btActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

}
