package com.guillermogarcia.facturas.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guillermogarcia.facturas.R;
import com.guillermogarcia.facturas.listeners.IFacturaListener;
import com.guillermogarcia.facturas.modelos.Cliente;
import com.guillermogarcia.facturas.modelos.Factura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentModificarFactura extends Fragment implements IFacturaListener {

    private EditText etNumeroFactura, etDescripcionFactura, etBaseImponible;
    private DatePicker datePicker;
    private Switch switchBorrador, switchPagado;
    private Button btnModificarFactura, btnEliminarFactura;
    private TextView tvIva, tvPrecioTotal;
    TextInputLayout inputLayoutBaseImponible;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerClientes;
    private List<Cliente> listaClientes = new ArrayList<>();
    private String idFacturaModificar; // Variable para almacenar el ID de la factura a modificar
    private Factura factura;
    private Context context;

    public FragmentModificarFactura() {
    }

    public static FragmentModificarFactura newInstance(Factura factura) {
        FragmentModificarFactura fragment = new FragmentModificarFactura();
        Bundle args = new Bundle();
        args.putSerializable("factura", factura);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modificar_factura, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNumeroFactura = view.findViewById(R.id.agregarNumeroFacturaModificar);
        etDescripcionFactura = view.findViewById(R.id.agregarDescripcionFacturaModificar);
        etBaseImponible = view.findViewById(R.id.agregarBaseImponibleModificar);
        datePicker = view.findViewById(R.id.datePickerModificar);
        switchBorrador = view.findViewById(R.id.switchBorradorAgregarModificar);
        switchPagado = view.findViewById(R.id.switchPagadoAgregarModificar);
        btnModificarFactura = view.findViewById(R.id.buttonAgregarFacturaModificar);
        btnEliminarFactura = view.findViewById(R.id.buttonEliminarFacturaModificar);
        tvIva = view.findViewById(R.id.tvIvaAgregarModificar);
        tvPrecioTotal = view.findViewById(R.id.tvPrecioTotalAgregarModificar);
        spinnerClientes = view.findViewById(R.id.spinnerClientesModificar);
        inputLayoutBaseImponible = view.findViewById(R.id.inputBaseImponibleModificar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            factura = (Factura) bundle.getSerializable("factura");
        }
        idFacturaModificar = factura.getIdentificador();

        setupSpinner();

        // Llenamos los datos de la factura actual en los campos
        llenarDatosFactura();

        btnModificarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarFactura();
            }
        });

        btnEliminarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

        etBaseImponible.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calcularIvaYPrecioTotal();
            }
        });

        switchBorrador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchPagado.setVisibility(View.GONE);
                } else {
                    switchPagado.setVisibility(View.VISIBLE);
                }
            }
        });

        switchPagado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchBorrador.setVisibility(View.GONE);
                } else {
                    switchBorrador.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void llenarDatosFactura() {

        // Llenar los campos con los datos de la factura
        etNumeroFactura.setText(factura.getNumeroFactura());
        etDescripcionFactura.setText(factura.getDescripcion());
        etBaseImponible.setText(String.valueOf(factura.getBaseImponible()));
        BigDecimal bd = new BigDecimal(factura.getIvaPrecio());
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        Double ivaPrecio = bd.doubleValue ();
        tvIva.setText("IVA(21%): " + String.valueOf(ivaPrecio) + "€");
        bd = new BigDecimal(factura.getPrecioTotal());
        bd = bd.setScale (2, BigDecimal.ROUND_UP);
        Double precioTotal = bd.doubleValue ();
        tvPrecioTotal.setText("Total: " + String.valueOf(precioTotal) + "€");
        switchBorrador.setChecked(factura.isBorrador());
        switchPagado.setChecked( factura.isPagado());

        // Establecemos la fecha en el DatePicker
        Calendar calendar = Calendar.getInstance();
        Date fecha = factura.getFecha();
        calendar.setTime(fecha);
        datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Colocamos el cliente asociado en el Spinner
        String idCliente = factura.getCliente();
        seleccionarClienteEnSpinner(idCliente);

    }

    private void seleccionarClienteEnSpinner(String idClienteSeleccionado) {

        // Iteramos sobre la lista de clientes y seleccionamos el cliente con el ID correspondiente
        for (int i = 0; i < listaClientes.size(); i++) {
            Cliente cliente = listaClientes.get(i);
            if (cliente.getIdentificador().equals(idClienteSeleccionado)) {
                spinnerClientes.setSelection(i);
                break;
            }
        }
    }

    private void setupSpinner() {

        db.collection("clientes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaClientes.clear();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Cliente cliente = documentSnapshot.toObject(Cliente.class);
                        if (cliente != null) {
                            listaClientes.add(cliente);
                        }
                    }

                    ArrayAdapter<Cliente> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listaClientes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerClientes.setAdapter(adapter);
                    seleccionarClienteEnSpinner(factura.getCliente());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al obtener la lista de clientes", Toast.LENGTH_SHORT).show();
                });
    }

    private void modificarFactura() {

        if (TextUtils.isEmpty(etNumeroFactura.getText())
                || TextUtils.isEmpty(etDescripcionFactura.getText())
                || TextUtils.isEmpty(etBaseImponible.getText())) {
            Toast.makeText(requireContext(), "Completa todos los campos antes de modificar la factura", Toast.LENGTH_SHORT).show();
            return;
        }
        Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();
        if (clienteSeleccionado == null) {
            Toast.makeText(requireContext(), context.getString(R.string.selecciona_un_cliente), Toast.LENGTH_SHORT).show();
            return;
        }

        if(idFacturaModificar != null) {
            String idClienteAnterior = factura.getCliente();
            if (!clienteSeleccionado.getIdentificador().equals(idClienteAnterior)) {
                // Eliminamos la factura del cliente anterior
                eliminarFacturaDeClienteAnterior(idClienteAnterior);

                // Añadimos la factura al nuevo cliente
                añadirFacturaANuevoCliente(clienteSeleccionado);
            }

            Factura facturaActualizar = new Factura();
            facturaActualizar.setIdentificador(idFacturaModificar);
            facturaActualizar.setNumeroFactura(etNumeroFactura.getText().toString());
            facturaActualizar.setDescripcion(etDescripcionFactura.getText().toString());

            Map<String, Object> facturaData = new HashMap<>();
            facturaData.put("numeroFactura", etNumeroFactura.getText().toString());
            facturaData.put("descripcion", etDescripcionFactura.getText().toString());

            try {
                double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
                facturaData.put("baseImponible", baseImponible);

                facturaActualizar.setBaseImponible(baseImponible);
            } catch (NumberFormatException e) {
                inputLayoutBaseImponible.setError(String.valueOf(context.getString(R.string.ingrese_valor_valido_base_imponible)));
                return;
            }

            facturaActualizar.setCliente(clienteSeleccionado.getIdentificador());
            facturaActualizar.setFecha(getSelectedDate());
            facturaActualizar.setFechaModificacion(new Date());
            facturaActualizar.setPagado(switchPagado.isChecked());
            facturaActualizar.setBorrador(switchBorrador.isChecked());


            facturaData.put("cliente", clienteSeleccionado.getIdentificador());
            facturaData.put("fecha", getSelectedDate());
            facturaData.put("fechaModificacion", new Date());
            facturaData.put("pagado", switchPagado.isChecked());
            facturaData.put("borrador", switchBorrador.isChecked());

            // Calcular el IVA y el precio total
            double baseImponible = Double.parseDouble(etBaseImponible.getText().toString());
            double ivaPrecio = baseImponible * 0.21;
            facturaData.put("ivaPrecio", ivaPrecio);
            facturaActualizar.setIvaPrecio(ivaPrecio);

            double precioTotal = baseImponible + ivaPrecio;
            facturaData.put("precioTotal", precioTotal);
            facturaActualizar.setPrecioTotal(precioTotal);

            // Actualizamos la factura en la base de datos
            db.collection("Facturas")
                .document(idFacturaModificar)
                .update(facturaData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), context.getString(R.string.factura_modificada_exito), Toast.LENGTH_SHORT).show();

                    FragmentDetalleFactura fragmentDetalleFactura = new FragmentDetalleFactura();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("factura", facturaActualizar);
                    fragmentDetalleFactura.setArguments(bundle);

                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content_frame, fragmentDetalleFactura);
                    transaction.addToBackStack(null);
                    transaction.commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al modificar factura", Toast.LENGTH_SHORT).show();
                });
        }else{
            Toast.makeText(requireContext(), "Error: ID de factura nulo", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarFacturaDeClienteAnterior(String idClienteAnterior) {
        Context context = getContext();

        // Obtener el cliente anterior
        db.collection("clientes")
                .document(idClienteAnterior)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Cliente clienteAnterior = documentSnapshot.toObject(Cliente.class);
                    if (clienteAnterior != null) {
                        // Eliminamos la factura del array de facturas del cliente
                        clienteAnterior.getFacturas().remove(idFacturaModificar);

                        // Actualizamos el documento del cliente en Firestore
                        db.collection("clientes")
                                .document(clienteAnterior.getIdentificador())
                                .set(clienteAnterior)  // Utilizamos set para reemplazar completamente el documento
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, context.getString(R.string.cliente_actualizado_eliminar_factura), Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al actualizar el cliente después de eliminar factura", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al obtener datos del cliente anterior", Toast.LENGTH_SHORT).show();
                });
    }

    private void añadirFacturaANuevoCliente(Cliente nuevoCliente) {
        // Añadimos la factura al array de facturas del nuevo cliente
        nuevoCliente.getFacturas().add(idFacturaModificar);

        // Actualizamos el documento del nuevo cliente en Firestore
        db.collection("clientes").document(nuevoCliente.getIdentificador())
                .update("facturas", nuevoCliente.getFacturas())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), context.getString(R.string.factura_añadida_nuevo_cliente), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al añadir factura al nuevo cliente", Toast.LENGTH_SHORT).show();
                });
    }

    private Date getSelectedDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0); // Hora fija a las 00:00:00

        return calendar.getTime();
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(context.getString(R.string.seguro_eliminar_factura))
                .setPositiveButton(context.getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarFactura();
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void calcularIvaYPrecioTotal() {
        try {
            if (!etBaseImponible.getText().toString().isEmpty()) {
                String baseImponibleStr = etBaseImponible.getText().toString();

                double baseImponible = Double.parseDouble(baseImponibleStr);
                double ivaPorcentaje = 0.21;
                double ivaPrecio = baseImponible * ivaPorcentaje;
                BigDecimal bd = new BigDecimal(ivaPrecio);
                bd = bd.setScale (2, BigDecimal.ROUND_UP);
                ivaPrecio = bd.doubleValue ();
                double precioTotal = baseImponible + ivaPrecio;
                bd = new BigDecimal(precioTotal);
                bd = bd.setScale (2, BigDecimal.ROUND_UP);
                precioTotal = bd.doubleValue ();

                tvIva.setText("IVA (21%): " + ivaPrecio + "€");
                tvPrecioTotal.setText("Total: " + precioTotal + "€");
            }

        } catch (NumberFormatException e) {
            inputLayoutBaseImponible.setError(String.valueOf(context.getString(R.string.ingrese_valor_valido_base_imponible)));
        }
    }

    private void eliminarFactura() {

        Context context = getContext();

        // Eliminaos la factura de la base de datos
        db.collection("Facturas")
                .document(idFacturaModificar)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, context.getString(R.string.factura_eliminada_exito), Toast.LENGTH_SHORT).show();
                    // Eliminar la factura de la lista de facturas asociadas al cliente
                    eliminarFacturaDeCliente();
                    FragmentFacturas f = new FragmentFacturas(this);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame, f);
                    transaction.addToBackStack(null);  // Permite regresar al FragmentDetalleCliente
                    transaction.commit();
                    Log.d("Check", "MainActivity:AonNavigationItemSelectd Facturas");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar factura", Toast.LENGTH_SHORT).show();
                });
    }

    private void eliminarFacturaDeCliente() {
        Context context = getContext();

        // Obtenemos el cliente seleccionado en el Spinner
        Cliente clienteSeleccionado = (Cliente) spinnerClientes.getSelectedItem();

        if (clienteSeleccionado != null) {
            // Eliminamos la factura del array de facturas del cliente
            clienteSeleccionado.getFacturas().remove(idFacturaModificar);

            // Actualizamos el documento del cliente en Firestore
            db.collection("clientes")
                .document(clienteSeleccionado.getIdentificador())
                .set(clienteSeleccionado)  // Utilizar set para reemplazar completamente el documento
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, R.string.cliente_actualizado_eliminar_factura, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el cliente después de eliminar factura", Toast.LENGTH_SHORT).show();
                });

        }

    }

    @Override
    public void onFacturaListSelected(DocumentSnapshot documentSnapshot, int position) {

    }
}