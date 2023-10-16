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
import com.guillermogarcia.facturas.modelos.Factura;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class FragmentDetalleFactura extends Fragment {

    public static final String FACTURA_EXTRA_DETALLE = "com.guillermogarcia.facturas.factura";

    private Factura factura;

    private TextView tvNumeroFactura;
    private TextView tvNombreApellidosCliente;
    private TextView tvFecha;
    private TextView tvDescripcion;
    private TextView tvBaseImponible;
    private TextView tvIva;
    private TextView tvPrecioTotal;
    private Switch switchBorrador;
    private Switch switchPagado;
    private Button btModificar;
    private Button btEliminar;
    private TextView tvFechaModificacion;


    public FragmentDetalleFactura() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            if(bundle.containsKey(FACTURA_EXTRA_DETALLE)) {
                factura = (Factura) bundle.getSerializable(FACTURA_EXTRA_DETALLE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_detalle_cliente, container, false);

        tvNumeroFactura = inflatedView.findViewById(R.id.tvNumeroFacturaDetalle);
        tvNombreApellidosCliente = inflatedView.findViewById(R.id.tvNombreApellidosClienteDeFacturaDetalle);
        tvFecha = inflatedView.findViewById(R.id.tvFecha);
        tvDescripcion = inflatedView.findViewById(R.id.tvDescripcionDetalle);
        tvBaseImponible= inflatedView.findViewById(R.id.tvBaseImponibleDetalle);
        tvIva = inflatedView.findViewById(R.id.tvIvaDetalle);
        tvPrecioTotal= inflatedView.findViewById(R.id.tvPrecioTotalDetalle);
        switchBorrador = inflatedView.findViewById(R.id.switchBorrador);
        switchPagado= inflatedView.findViewById(R.id.switchPagado);
        btModificar= inflatedView.findViewById(R.id.buttonModificar);
        btEliminar= inflatedView.findViewById(R.id.buttonEliminarFactura);
        tvFechaModificacion= inflatedView.findViewById(R.id.tvFechaModificacionDetalle);
        return inflatedView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String nombreApellidosCliente = "Cliente: " + factura.getCliente().getNombre() + " " + factura.getCliente().getApellidos();
        String baseImponible = ""+factura.getBaseImponible();
        String iva = "" + factura.getIvaPrecio();
        String precioTotal = ""+factura.getPrecioTotal();
        String fechaModificacion = factura.getFechaModificacion().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMMM/yyyy");
        String fecha = sdf.format(factura.getFecha().getTime());
        tvNumeroFactura.setText(factura.getNumeroFactura());
        tvNombreApellidosCliente.setText(nombreApellidosCliente);
        tvFecha.setText(fecha);

        //Lo de abajo es un ejemplo
        //Se puede hacer new GregorianCalendar(2018, 6, 27);
        //GregorianCalendar g = new GregorianCalendar();
        //g.set(2020, 5, 2);

        tvDescripcion.setText(factura.getDescripcion());
        tvBaseImponible.setText(baseImponible);
        tvIva.setText(iva);
        tvPrecioTotal.setText(precioTotal);
        switchBorrador.setChecked(factura.isBorrador());
        switchPagado.setChecked(factura.isPagado());
        tvFechaModificacion.setText(fechaModificacion);






    }
}
