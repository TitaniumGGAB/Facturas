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
        //public Cliente(int id, String nombre, String apellidos, String telefono, String email,  String cif, String direccion, Date fecha_agregado, List<Factura> facturas)
        Cliente cliente1 = new Cliente(1, "Guillermo", "García Almeida", "633326028", "guillermogarciaalmeida@gmail.com", "53629333A", "Guerrillero Groc de sala 11", new Date(), null);
        Cliente cliente2 = new Cliente(2, "Ana", "López", "123456789", "ana.lopez@example.com", "12345678B", "Calle Principal 123", new Date(), null);
        Cliente cliente3 = new Cliente(3, "Pedro", "Sánchez", "987654321", "pedro.sanchez@example.com", "98765432C", "Avenida Central 456", new Date(), null);
        Cliente cliente4 = new Cliente(4, "María", "Martínez", "555555555", "maria.martinez@example.com", "55555555D", "Plaza Mayor 789", new Date(), null);
        Cliente cliente5 = new Cliente(5, "Carlos", "Fernández", "111122223", "carlos.fernandez@example.com", "11112222E", "Paseo del Parque 567", new Date(), null);
        Cliente cliente6 = new Cliente(6, "Sofía", "Gómez", "444488889", "sofia.gomez@example.com", "44448888F", "Callejón Secreto 234", new Date(), null);
        Cliente cliente7 = new Cliente(7, "Luis", "Rodríguez", "777799998", "luis.rodriguez@example.com", "77779999G", "Camino de Montaña 890", new Date(), null);
        Cliente cliente8 = new Cliente(8, "Carmen", "Pérez", "999966667", "carmen.perez@example.com", "99996666H", "Ronda del Lago 1234", new Date(), null);
        Cliente cliente9 = new Cliente(9, "Javier", "Hernández", "333322225", "javier.hernandez@example.com", "33332222I", "Avenida Costera 4321", new Date(), null);
        Cliente cliente10 = new Cliente(10, "Isabel", "Díaz", "666611119", "isabel.diaz@example.com", "66661111J", "Plaza del Sol 5678", new Date(), null);
        clientes.add(cliente1);
        clientes.add(cliente2);
        clientes.add(cliente3);
        clientes.add(cliente4);
        clientes.add(cliente5);
        clientes.add(cliente6);
        clientes.add(cliente7);
        clientes.add(cliente8);
        clientes.add(cliente9);
        clientes.add(cliente10);


        /*for (int i = 1; i <= 10; i++) {
            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente " + i);
            cliente.setApellidos("Apellidos " + i);
            cliente.setCif("CIF " + i);
            cliente.setDireccion("Dirección " + i);
            clientes.add(cliente);
        }*/

        facturas = new ArrayList<>();

        Factura factura1 = new Factura(1, "FAC001", new GregorianCalendar(2023, 8, 1), "Venta de productos", 100.0, 21.0, 121.0, new Date(), true, false, cliente1);
        Factura factura2 = new Factura(2, "FAC002", new GregorianCalendar(2023, 8, 2), "Servicios de consultoría", 500.0, 105.0, 605.0, new Date(), false, false, cliente2);
        Factura factura3 =  new Factura(3, "FAC003", new GregorianCalendar(2023, 8, 3), "Compra de material de oficina", 50.0, 10.5, 60.5, new Date(), true, false, cliente3);
        Factura factura4 = new Factura(4, "FAC004", new GregorianCalendar(2023, 8, 4), "Venta de productos", 300.0, 63.0, 363.0, new Date(), false, false, cliente4);
        Factura factura5 = new Factura(5, "FAC005", new GregorianCalendar(2023, 8, 5), "Servicios de diseño gráfico", 200.0, 42.0, 242.0, new Date(), true, false, cliente4);
        Factura factura6 = new Factura(6, "FAC006", new GregorianCalendar(2023, 8, 6), "Compra de material de construcción", 800.0, 168.0, 968.0, new Date(), false, false, cliente4);
        Factura factura7 = new Factura(7, "FAC007", new GregorianCalendar(2023, 8, 7), "Venta de productos", 120.0, 25.2, 145.2, new Date(), true, false, cliente1);
        Factura factura8 = new Factura(8, "FAC008", new GregorianCalendar(2023, 8, 8), "Servicios de marketing", 350.0, 73.5, 423.5, new Date(), false, false, cliente5);
        Factura factura9 = new Factura(9, "FAC009", new GregorianCalendar(2023, 8, 9), "Venta de productos", 75.0, 15.75, 90.75, new Date(), true, false, cliente6);
        Factura factura10 = new Factura(10, "FAC010", new GregorianCalendar(2023, 8, 10), "Mantenimiento de equipos", 450.0, 94.5, 544.5, new Date(), false, false, cliente7);

        facturas.add(factura1);
        facturas.add(factura2);
        facturas.add(factura3);
        facturas.add(factura4);
        facturas.add(factura5);
        facturas.add(factura6);
        facturas.add(factura7);
        facturas.add(factura8);
        facturas.add(factura9);
        facturas.add(factura10);

        // Insertar facturas ficticias
        /*
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
        }*/

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