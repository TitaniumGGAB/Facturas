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
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.guillermogarcia.facturas.fragments.FragmentAgregarCliente;
import com.guillermogarcia.facturas.fragments.FragmentAgregarFactura;
import com.guillermogarcia.facturas.fragments.FragmentClientes;
import com.guillermogarcia.facturas.fragments.FragmentFacturas;
import com.guillermogarcia.facturas.listeners.IClienteListener;
import com.guillermogarcia.facturas.listeners.IFacturaListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFacturaListener, IClienteListener {

    private DrawerLayout drawer;
    private FirebaseFirestore db;


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
                                                MainActivity.this.getString(R.string.acceso_autorizado),
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
                            Log.d("Check", "MainActivity:AonActivityResult()");
                        }
                    }
            );
            activityResultLauncher.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build());

        }else {//El usuario ya se ha autenticado

            Toast.makeText(this,
                    MainActivity.this.getString(R.string.bienvenido) + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();

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
            Log.d("Check", "MainActivity:AOnActivityResultElse");


        }
    }

    public void getClientesFacturas(){
        db = FirebaseFirestore.getInstance();
        db.collection("Facturas").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Mostramos las listas.
                        FragmentFacturas fragmentFacturas = new FragmentFacturas(MainActivity.this);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentFacturas).commit();
                        Log.d("Check", "MainActivity:AgetClientesFacturas Despues de cargar el fragment");
                    }
                });
        Log.d("Check", "MainActivity:AgetClientesFacturasFinal");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_clientes) {
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Clientes1");
            FragmentClientes f = new FragmentClientes(MainActivity.this);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
            setTitle(MainActivity.this.getString(R.string.clientes));
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Clientes2");
        } else if (id == R.id.nav_facturas) {
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas1");
            FragmentFacturas f = new FragmentFacturas(MainActivity.this);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
            setTitle(MainActivity.this.getString(R.string.facturas));
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas");
        }else if (id == R.id.nav_agregar_cliente) {
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Agregar Cliente");
            FragmentAgregarCliente f = new FragmentAgregarCliente();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
            setTitle(MainActivity.this.getString(R.string.agregar_cliente));
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Agregra Cliente ");
        }
        else if (id == R.id.nav_agregar_factura) {
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Agregar Factura");
            FragmentAgregarFactura f = new FragmentAgregarFactura();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, f).commit();
            setTitle(MainActivity.this.getString(R.string.agregar_factura));
            Log.d("Check", "MainActivity:AonNavigationItemSelectd Agregra Factura ");
        }
        drawer.closeDrawer(GravityCompat.START);
        Log.d("Check", "MainActivity:AonNavigationItemSelectd Final");
        return true;
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


    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }

    @Override
    public void onClienteListSelected(DocumentSnapshot documentSnapshot, int position) {
    }
}