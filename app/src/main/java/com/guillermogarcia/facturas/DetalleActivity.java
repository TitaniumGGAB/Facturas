package com.guillermogarcia.facturas;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.navigation.NavigationView;
import com.guillermogarcia.facturas.fragments.FragmentDetalleCliente;
import com.guillermogarcia.facturas.fragments.FragmentDetalleFactura;
import com.guillermogarcia.facturas.fragments.FragmentModificarFactura;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;
import java.util.Objects;

public class DetalleActivity extends AppCompatActivity {
    public static final String EXTRA_CLIENTE = "com.guillermogarcia.facturas.EXTRA_CLIENTE";
    public static final String EXTRA_FACTURA = "com.guillermogarcia.facturas.EXTRA_FACTURA";
    private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    private ArrayList<Factura> facturas = new ArrayList<Factura>();

    public DetalleActivity() {
        super(R.layout.activity_detalle);
    }

    //DUDA: Necesito dos DetalleActivity para clientes y factura?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            clientes = (ArrayList<Cliente>) Objects.requireNonNull(getIntent().getSerializableExtra(EXTRA_CLIENTE));
            //facturas = (ArrayList<Factura>) Objects.requireNonNull(getIntent().getSerializableExtra(EXTRA_FACTURA));
            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentDetalleCliente.CLIENTE_EXTRA_DETALLE, clientes);
            //bundle.putSerializable(FragmentDetalleFactura.FACTURA_EXTRA_DETALLE, facturas);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.FrgDetalle, FragmentDetalleCliente.class, bundle)
                    //.add(R.id.FrgDetalle, FragmentDetalleFactura.class, bundle)
                    .commit();


            //View inflatedView = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);
        }


    }

}
