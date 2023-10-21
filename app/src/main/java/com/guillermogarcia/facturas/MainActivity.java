package com.guillermogarcia.facturas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.guillermogarcia.facturas.fragments.FragmentDetalleCliente;
import com.guillermogarcia.facturas.fragments.FragmentDetalleFactura;
import com.guillermogarcia.facturas.fragments.FragmentListado;
import com.guillermogarcia.facturas.fragments.FragmentModificarCliente;
import com.guillermogarcia.facturas.fragments.FragmentModificarFactura;
import com.guillermogarcia.facturas.interfaces.IAPIService;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;
import com.guillermogarcia.facturas.rest.RestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFacturaListener, IClienteListener {

    private IAPIService apiService;
    private DrawerLayout drawer;
    private ArrayList<Factura> facturas;
    private ArrayList<Cliente> clientes;
    private final FragmentListado fragmentListado = new FragmentListado();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiService = RestClient.getInstance();
        getClientesFacturas();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void getClientesFacturas(){


        clientes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente " + i);
            cliente.setApellidos("Apellidos " + i);
            cliente.setCif("CIF " + i);
            cliente.setDireccion("Dirección " + i);
            clientes.add(cliente);
        }

        // Insertar facturas ficticias
        facturas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Factura factura = new Factura();
            factura.setNumeroFactura("Factura " + i);
            // Establece otros campos de la factura aquí
            factura.setBaseImponible(100.0 * i);
            factura.setIvaPrecio(0.21);
            factura.setPrecioTotal(factura.getBaseImponible() * (1 + factura.getIvaPrecio()));
            // Asigna una fecha ficticia
            GregorianCalendar fecha = new GregorianCalendar(2023, 8, i);
            factura.setFecha(fecha);
            factura.setFechaModificacion(new Date(fecha.getTimeInMillis()));
            factura.setPagado(i % 2 == 0); // Alternar entre pagado y no pagado
            factura.setBorrador(false);
            factura.setCliente(clientes.get(i - 1)); // Asocia una factura a un cliente
            facturas.add(factura);
        }

        // Ahora que tienes los datos ficticios, puedes establecerlos en tu FragmentListado
        fragmentListado.setClientes(clientes);
        fragmentListado.setFacturas(facturas);
        loadFragmentListado(FragmentListado.TipoListado.SEGUN_FACTURA, "Facturas", false);

        /*apiService.getClientes().enqueue(new Callback<List<Cliente>>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if(response.isSuccessful()){
                    Log.d("MainActivy", "Se han obtenido los datos");
                    assert response.body() != null;

                    clientes = (ArrayList<Cliente>) response.body();
                    fragmentListado.setClientes(clientes);

                    for(Cliente cliente: response.body()) {
                        Log.i(MainActivity.class.getSimpleName(), cliente.toString());
                    }
                }else{
                    Log.d("MainActivy", "No se ah podido obtener los datos");
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        apiService.getFacturas().enqueue(new Callback<List<Factura>>() {
            @Override
            public void onResponse(Call<List<Factura>> call, Response<List<Factura>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;

                        //facturas = (ArrayList<Factura>) response.body();
                    List<Factura> facturasList = (List<Factura>) response.body();
                    facturas = new ArrayList<Factura>(facturasList);

                    fragmentListado.setFacturas(facturas);

                    loadFragmentListado(FragmentListado.TipoListado.SEGUN_FACTURA, "Facturas", false);

                    for(Factura factura: response.body()) {
                        Log.i(MainActivity.class.getSimpleName(), factura.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Factura>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_CLIENTE, "Clientes", false);
        } else if (id == R.id.nav_facturas) {
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_FACTURA, "Facturas", false);
        }else if (id == R.id.nav_facturas_borradores) {
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_BORRADOR, "Borradores", false);
        }else if (id == R.id.nav_facturas_pendientes_pago) {
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_PENDIENTE_PAGO, "Facturas pendientes de pagar", false);
        }else if(id == R.id.nav_modificar_factura){
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_CLIENTE, "Modificar cliente", true);
        }else if(id == R.id.nav_modificar_cliente){
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_FACTURA, "Modificar factura", true);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragmentListado(FragmentListado.TipoListado listado, String titulo, Boolean modificar){
        if(modificar){
            fragmentListado.setModificar(true);
        }else{
            Bundle bundle;
            fragmentListado.setFacturasListener(this);
            fragmentListado.setClientesListener(this);
            bundle = new Bundle();
            bundle.putSerializable(FragmentListado.TIPO, listado);
            fragmentListado.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentListado).commit();
            setTitle(titulo);
        }

    }


    //habría que hacer un método así en DetalleActivity para cargar el fragmentModificarCliente/Factura
    @Override
    public void onFacturaSeleccionado(Factura factura) {
        if(fragmentListado.isModificar()){
            FragmentModificarFactura f = new FragmentModificarFactura(clientes);
            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentModificarFactura.FACTURA_MODIFICAR_DETALLE, factura);
            f.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        }else{
            FragmentDetalleFactura f = new FragmentDetalleFactura();
            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentDetalleFactura.FACTURA_EXTRA_DETALLE, factura);
            f.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        }

    }

    @Override
    public void onClienteSeleccionado(Cliente cliente) {
        if(fragmentListado.isModificar()){
            FragmentModificarCliente f = new FragmentModificarCliente();
            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentModificarCliente.CLIENTE_MODIFICAR_DETALLE, cliente);
            f.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        }else{
            FragmentDetalleCliente f = new FragmentDetalleCliente();
            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentDetalleCliente.CLIENTE_EXTRA_DETALLE, cliente);
            f.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).addToBackStack(null).commit();
        }

    }



    @Override
    public void onBackPressed() {
        /**
         * Si el usuario pulsa el botón atrás mientras está mostrándose el menú del NavigationView,
         * hacemos que se cierre dicho menú, ya que el comportamiento por defecto es cerrar la
         * Activity.
         */
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Se ha hecho click en algún item del menú de la ActionBar
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}