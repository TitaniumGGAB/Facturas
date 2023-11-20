package com.guillermogarcia.facturas;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.guillermogarcia.facturas.fragments.FragmentDetalleCliente;
import com.guillermogarcia.facturas.fragments.FragmentDetalleFactura;
import com.guillermogarcia.facturas.fragments.FragmentListado;
import com.guillermogarcia.facturas.fragments.FragmentModificarCliente;
import com.guillermogarcia.facturas.fragments.FragmentModificarFactura;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFacturaListener, IClienteListener {

    private DrawerLayout drawer;
    private ArrayList<Factura> facturas;
    private ArrayList<Cliente> clientes;
    private final FragmentListado fragmentListado = new FragmentListado();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            // Iniciamos Activity para Login/Registro
            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),

                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                Toast.makeText(MainActivity.this,
                                                "Acceso autorizado. ¡Bienvenido!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                getClientesFacturas();
                            } else {
                                Toast.makeText(MainActivity.this,
                                                "Acceso denegado. Inténtalo de nuevo más tarde.",
                                                Toast.LENGTH_LONG)
                                        .show();
                                // Close the app
                                finish();
                            }

                            Toolbar toolbar = findViewById(R.id.toolbar);
                            setSupportActionBar(toolbar);

                            drawer = findViewById(R.id.drawer_layout);
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                                    MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                            drawer.addDrawerListener(toggle);
                            toggle.syncState();

                            NavigationView navigationView = findViewById(R.id.nav_view);
                            navigationView.setNavigationItemSelectedListener(MainActivity.this);
                        }
                    }
            );
            activityResultLauncher.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build());

        }else {//El usuario ya se ha autenticado

            Toast.makeText(this,
                    "Bienvenido " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();

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
    }


    public void getClientesFacturas(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        clientes = new ArrayList<>();
        facturas = new ArrayList<>();

        CollectionReference clientesRef = db.collection("Clientes");
        CollectionReference facturasRef = db.collection("Facturas");
        clientesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Cliente cliente = new Cliente();

                        // Añadir campos uno por uno
                        cliente.setId(document.getLong("id").intValue());
                        cliente.setNombre(document.getString("nombre"));
                        cliente.setApellidos(document.getString("apellidos"));
                        cliente.setTelefono(document.getString("telefono"));
                        cliente.setEmail(document.getString("email"));
                        cliente.setCif(document.getString("cif"));
                        cliente.setDireccion(document.getString("direccion"));
                        cliente.setFecha_agregado(document.getDate("fecha_agregado"));

                        // Ahora, en lugar de convertir directamente el campo Facturas, obtén las referencias
                        List<DocumentReference> facturasRefs = (List<DocumentReference>) document.get("facturas");

                        // Contador para realizar un seguimiento de cuántas facturas se han recuperado
                        AtomicInteger count = new AtomicInteger(0);

                        // Lista para almacenar las facturas asociadas al cliente
                        ArrayList<Factura> facturasCliente = new ArrayList<>();

                        // Recupera los documentos de factura asociados a las referencias
                        for (DocumentReference facturaRef : facturasRefs) {
                            facturaRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    // Convierte el documento Factura a un objeto Factura
                                    Factura factura = documentSnapshot.toObject(Factura.class);

                                    // Añade la factura a la lista de facturas del cliente
                                    facturasCliente.add(factura);

                                    // Incrementa el contador
                                    int currentCount = count.incrementAndGet();

                                    // Si se han recuperado todas las facturas, agrega el cliente a la lista
                                    if (currentCount == facturasRefs.size()) {
                                        cliente.setFacturas(facturasCliente);
                                        clientes.add(cliente);
                                        Log.d("hola", "Comprobación");
                                        Log.d("hola", "El tamaño de clientes es " + clientes.size() + ". El tamaño de lo recibido es " + task.getResult().size());
                                        fragmentListado.setClientes(clientes);
                                        cargarFacturas();

                                        // Notifica que se han cargado todos los clientes con sus facturas asociadas
                                        if (clientes.size() == task.getResult().size()) {
                                            Log.d("hola", "Comprobación2");
                                            fragmentListado.setClientes(clientes);
                                            cargarFacturas();
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.e("TAG", "Error al leer los clientes: " + task.getException());
                }
            }
        });
    }

    public void cargarFacturas() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facturasRef = db.collection("Facturas");

        facturasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Factura factura = document.toObject(Factura.class);
                        Log.d("hola", "Hola");
                        facturas.add(factura);
                    }
                    fragmentListado.setFacturas(facturas);
                    loadFragmentListado(FragmentListado.TipoListado.SEGUN_FACTURA, "Facturas", false); // Llamar a loadFragmentListado aquí
                } else {
                    Log.e("TAG", "Error al leer las facturas: " + task.getException());
                }
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            fragmentListado.setClientes(clientes);
            fragmentListado.setFacturas(facturas);
            loadFragmentListado(FragmentListado.TipoListado.SEGUN_CLIENTE, "Clientes", false);
        } else if (id == R.id.nav_facturas) {
            fragmentListado.setClientes(clientes);
            fragmentListado.setFacturas(facturas);
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

            if(listado.equals(FragmentListado.TipoListado.SEGUN_CLIENTE)){
                Log.d("Check", "equals Cliente");

                FragmentListado fragmentListado2 = new FragmentListado();

                fragmentListado2.setClientes(clientes);
                fragmentListado2.setFacturas(facturas);
                Bundle bundle2;
                fragmentListado2.setFacturasListener(this);
                fragmentListado2.setClientesListener(this);
                bundle2 = new Bundle();
                bundle2.putSerializable(FragmentListado.TIPO, listado);
                fragmentListado2.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentListado2).commit();
                setTitle(titulo);
            }else {


                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentListado).commit();
                setTitle(titulo);
            }
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