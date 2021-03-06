package mx.grupohi.registrocamiones.registrocamiones;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class VisualizarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Usuario usuario;
    Empresa e;
    ImagenesCamion icamion;
    TextView economico;
    TextView cu_real;
    TextView cu_pago;
    EditText propietario;
    EditText pcamion;
    EditText pcaja;
    EditText marca;
    EditText modelo;
    EditText color;
    EditText ancho;
    EditText largo;
    EditText gato;
    EditText alto;
    EditText operador;
    EditText licencia;
    EditText extension;
    EditText disminucion;
    EditText vig_licencia;
    Button guardar;
    Button cancelar;
    Button imagenes;
    String idcamion;
    String idSindicato;
    Spinner spinner;
    private HashMap<String, String> spinnerMap;
    Spinner empresa;
    private HashMap<String, String> spinnerMapEmpresa;
    Sindicato sindicato;
    Camion camion;
    String nombre;
    Double cubicacion;
    String numSindicato;
    String activar;
    String nombreEm;
    String idEm;

    private DatePickerDialog vigenciaDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    private ProgressDialog progressDialogSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        idcamion = getIntent().getStringExtra("idcamion");
        activar = getIntent().getStringExtra("reactivar");
        camion = new Camion(this);
        camion = camion.find(Integer.valueOf(idcamion));
        usuario = new Usuario(getApplicationContext());
        icamion = new ImagenesCamion(getApplicationContext());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sindicato = new Sindicato(getApplicationContext());
        sindicato = sindicato.find(camion.sindicato);
        numSindicato = camion.sindicato;

        spinner = (Spinner) findViewById(R.id.spinner);

        final ArrayList<String> nombres = sindicato.getArrayListNombres();
        final ArrayList<String> ids = sindicato.getArrayListId();

        String[] spinnerArray = new String[ids.size()];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            spinnerMap.put(nombres.get(i), ids.get(i));
            spinnerArray[i] = nombres.get(i);
        }

        final ArrayAdapter<String> a = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(a);
        if(numSindicato !=  null) {
            spinner.setSelection(getIndex(spinner, numSindicato));
        }


        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    nombre = spinner.getSelectedItem().toString();
                    idSindicato = spinnerMap.get(nombre);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        economico = (TextView) findViewById(R.id.textViewCamion);
        economico.setText(camion.economico);

        /*sindicato = (EditText) findViewById(R.id.textViewSindicato);
        sindicato.setText(camion.sindicato);*/

        empresa = (Spinner) findViewById(R.id.spinnerEmpresa);

        e = new Empresa(getApplicationContext());

        final ArrayList<String> empresas = e.getArrayListNombres();
        final ArrayList<String> idsE = e.getArrayListId();

        String[] spinnerArrayEm = new String[idsE.size()];
        spinnerMapEmpresa = new HashMap<>();

        for (int i = 0; i < idsE.size(); i++) {
            spinnerMapEmpresa.put(empresas.get(i), idsE.get(i));
            spinnerArrayEm[i] = empresas.get(i);
        }

        final ArrayAdapter<String> ae = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArrayEm);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        empresa.setAdapter(ae);
        if(!camion.empresa.isEmpty()) {
            String c = camion.empresa;
            empresa.setSelection(getIndex(empresa, camion.empresa));
        }


        if (empresa != null) {
            empresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    nombreEm = empresa.getSelectedItem().toString();
                    idEm = spinnerMapEmpresa.get(nombreEm);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        propietario = (EditText) findViewById(R.id.textViewPropietario);
        if(!camion.propietario.equals("SIN")){
            propietario.setText(camion.propietario);
        }

        pcamion =(EditText) findViewById(R.id.textViewPCamion);
        pcamion.setText(camion.placasC);

        pcaja = (EditText) findViewById(R.id.textViewPCaja);
        pcaja.setText(camion.pCaja);

        marca =(EditText) findViewById(R.id.textViewMarca);
        marca.setText(camion.marca);

        modelo = (EditText) findViewById(R.id.textViewModelo);
        modelo.setText(camion.modelo);

        operador = (EditText) findViewById(R.id.textViewNOperador);
        operador.setText(camion.operador);

        licencia =(EditText) findViewById(R.id.textViewLicencia);
        if(!camion.licencia.equals("SL")){
            licencia.setText(camion.licencia);
        }


       /*color = (EditText) findViewById(R.id.textViewColor);
        color.setText(camion.color);*/

        ancho =(EditText) findViewById(R.id.textViewAncho);
        ancho.setText(String.valueOf(camion.ancho));
        ancho.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                                     @Override
                                     public void onFocusChange(View v, boolean hasFocus) {
                                         cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                                         cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                                         cu_real.setText(String.valueOf(redondear(cubicacion)));
                                     }
        });

        largo = (EditText) findViewById(R.id.textViewLargo);
        largo.setText(String.valueOf(camion.largo));
        largo.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                //System.out.println("2cubicacion: " + Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                cu_real.setText(String.valueOf(redondear(cubicacion)));
            }
        });


        gato = (EditText) findViewById(R.id.textViewGato);
        gato.setText(String.valueOf(camion.gato));
        gato.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                //System.out.println("2cubicacion: " + Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                cu_real.setText(String.valueOf(redondear(cubicacion)));
            }
        });

        alto = (EditText) findViewById(R.id.textViewAlto);
        alto.setText(String.valueOf(camion.alto));
        alto.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                //System.out.println("2cubicacion: " + Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                cu_real.setText(String.valueOf(redondear(cubicacion)));
            }
        });

        extension =(EditText)findViewById(R.id.textViewExtension);
        extension.setText(String.valueOf(camion.extension));
        extension.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                //System.out.println("2cubicacion: " + Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                cu_real.setText(String.valueOf(redondear(cubicacion)));
            }
        });

        disminucion = (EditText) findViewById(R.id.textViewDisminucion);
        disminucion.setText(String.valueOf(camion.disminucion));
        disminucion.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
                //System.out.println("2cubicacion: " + Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));
                cu_real.setText(String.valueOf(redondear(cubicacion)));
            }
        });

        cubicacion = setCubicacion(String.valueOf(ancho.getText()),String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()),String.valueOf(gato.getText()),String.valueOf(disminucion.getText()));

        cu_pago = (TextView) findViewById(R.id.textViewCUPago);
        cu_pago.setText(String.valueOf(Math.round(redondear(cubicacion))));


        cu_real = (TextView) findViewById(R.id.textViewCUReal);
        cu_real.setText(String.valueOf(redondear(cubicacion)));

        vig_licencia = (EditText) findViewById(R.id.textViewVigencia);
        if(!camion.vigencia_licencia.equals("0000-00-00")) {
            vig_licencia.setText(camion.vigencia_licencia);
        }
        vig_licencia.setInputType(InputType.TYPE_NULL);
        vig_licencia.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        vigenciaDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                vig_licencia.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        guardar= (Button) findViewById(R.id.buttonGuardar);
        cancelar = (Button) findViewById(R.id.buttonCancelar);
        imagenes = (Button) findViewById(R.id.buttonImagenes);
        if(icamion.getCount(Integer.valueOf(idcamion))==4){
            imagenes.setEnabled(false);
        }

        imagenes.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Boolean resp = guardar();
                if(resp == true) {
                    Integer cantidad = icamion.getCount(Integer.valueOf(idcamion));
                    Intent imagen;
                    if (cantidad == 0) {
                        imagen = new Intent(getApplicationContext(), CamaraActivity.class);
                    } else {
                        imagen = new Intent(getApplicationContext(), ImagenesActivity.class);
                    }
                    imagen.putExtra("idcamion", idcamion);
                    startActivity(imagen);
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
            }
        } );

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Boolean resp = guardar();
                //System.out.println("camion"+activar);
                if (resp==true){
                    if(activar == null){
                        Intent ok = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(ok);
                    }
                    else if(activar.equals("1")){//"camion inactivo"
                        Intent ok = new Intent(getApplicationContext(), ReactivacionActivity.class);
                        startActivity(ok);
                    }

                }

            }
        });
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if(drawer != null)
            drawer.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < drawer.getChildCount(); i++) {
                        View child = drawer.getChildAt(i);
                        TextView tvp = (TextView) child.findViewById(R.id.textViewProyecto);
                        TextView tvu = (TextView) child.findViewById(R.id.textViewUser);
                        TextView tvv = (TextView) child.findViewById(R.id.textViewVersion);

                        if (tvp != null) {
                            tvp.setText(usuario.getProyecto(getApplicationContext()));
                        }
                        if (tvu != null) {
                            tvu.setText(usuario.getName());
                        }
                        if (tvv != null) {
                            tvv.setText(getString(R.string.app_name)+"     "+"Versión " + String.valueOf(BuildConfig.VERSION_NAME));
                        }
                    }
                }
            });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
    public Boolean guardar(){
         /* if(camion.sindicato.equals(String.valueOf(sindicato.getText())) && camion.placasC.equals(String.valueOf(pcamion.getText())) && camion.pCaja.equals(String.valueOf(pcaja.getText())) && camion.empresa.equals(String.valueOf(empresa.getText())) && camion.propietario.equals(String.valueOf(propietario.getText())) && camion.operador.equals(String.valueOf(operador.getText())) && camion.licencia.equals(String.valueOf(licencia.getText())) && camion.vigencia_licencia.equals(String.valueOf(vig_licencia.getText())) && camion.modelo.equals(String.valueOf(modelo.getText())) && camion.marca.equals(String.valueOf(marca.getText())) && String.valueOf(camion.ancho).equals(String.valueOf(ancho.getText())) && String.valueOf(camion.largo).equals(String.valueOf(largo.getText())) && String.valueOf(camion.alto).equals(String.valueOf(alto.getText())) && String.valueOf(camion.gato).equals(String.valueOf(gato.getText())) && String.valueOf(camion.extension).equals(String.valueOf(extension.getText())) && String.valueOf(camion.disminucion).equals(String.valueOf(disminucion.getText()))){
                        Toast.makeText(getApplicationContext(), R.string.ningun_cambio, Toast.LENGTH_SHORT).show();
                }else {
        }*/
        Boolean r = false;
        if(!checar()) {
            //System.out.println("w: " + camion.idCamion + " economico: " + economico.getText()); //guardar (update) Camion.update
            cubicacion = setCubicacion(String.valueOf(ancho.getText()), String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()), String.valueOf(gato.getText()), String.valueOf(disminucion.getText()));
            //System.out.println("cubicacion: " + Math.ceil(cubicacion));
            ContentValues data = new ContentValues();
            if(idSindicato != String.valueOf(0)) {
                data.put("sindicato", String.valueOf(nombre.replaceAll(" +", " ").trim()));
            }else {
                data.put("sindicato", String.valueOf(idSindicato.replaceAll(" +", " ").trim()));
            }
            if(idEm != String.valueOf(0)){
                data.put("empresa", String.valueOf(nombreEm.replaceAll(" +", " ").trim()));
            }else{
                data.put("empresa", String.valueOf(idEm.replaceAll(" +", " ").trim()));
            }

            data.put("propietario", String.valueOf(propietario.getText()).replaceAll(" +"," ").trim());
            data.put("operador", String.valueOf(operador.getText()).replaceAll(" +"," ").trim());
            data.put("licencia", String.valueOf(licencia.getText()).replaceAll(" +"," ").trim());
            // data.put("economico", String.valueOf(economico.getText()).replaceAll(" +"," ").trim());
            data.put("placas_camion", String.valueOf(pcamion.getText()).replaceAll(" +"," ").trim());
            data.put("placas_caja", String.valueOf(pcaja.getText()).replaceAll(" +"," ").trim());
            data.put("marca", String.valueOf(marca.getText()).replaceAll(" +"," ").trim());
            data.put("modelo", String.valueOf(modelo.getText()).replaceAll(" +"," ").trim());
            data.put("ancho", String.valueOf(ancho.getText()).replaceAll(" +"," ").trim());
            data.put("largo", String.valueOf(largo.getText()).replaceAll(" +"," ").trim());
            data.put("alto", String.valueOf(alto.getText()).replaceAll(" +"," ").trim());
            data.put("espacio_gato", String.valueOf(gato.getText()).replaceAll(" +"," ").trim());
            data.put("altura_extension", String.valueOf(extension.getText()).replaceAll(" +"," ").trim());
            data.put("disminucion", String.valueOf(disminucion.getText()).replaceAll(" +"," ").trim());
            data.put("cubicacion_real", String.valueOf(redondear(cubicacion)).replaceAll(" +"," ").trim());
            data.put("cubicacion_para_pago", String.valueOf(Math.round(redondear(cubicacion))).replaceAll(" +"," ").trim());
            data.put("estatus", "1");
            data.put("vigencia_licencia", String.valueOf(vig_licencia.getText()).replaceAll(" +"," ").trim());

            //System.out.println("guardar: " + data);

            r = camion.update(idcamion, data, getApplicationContext());
            if (!r) {
                Toast.makeText(getApplicationContext(), R.string.error_guardar, Toast.LENGTH_SHORT).show();
            } /*else {
                Intent ok = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(ok);
            }*/
        }
        return r;
    }

    public Double setCubicacion(String ancho, String alto, String largo, String extension, String gato, String disminucion) {
        Double r = Double.valueOf(ancho) * Double.valueOf(largo) * (Double.valueOf(alto) + Double.valueOf(extension)) - Double.valueOf(gato) - Double.valueOf(disminucion);
        //System.out.println(Double.valueOf(ancho)+" * "+ Double.valueOf(largo) +" * " +(Double.valueOf(alto)+" + "+Double.valueOf(extension)) +" - "+ Double.valueOf(gato)+" - "+ Double.valueOf(disminucion)+" = "+ r);
        return r;
    }

    public Double redondear(double numero){
        int cifras = (int) Math.pow(10, 2);
        return  Math.rint(numero * cifras)/cifras;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
           Intent main = new Intent(this,MainActivity.class);
            startActivity(main);
        } else if (id == R.id.nav_re){
            Intent re  = new Intent(getApplicationContext(),ReactivacionActivity.class);
            startActivity(re);
        }else if (id == R.id.nav_sync) {
            new AlertDialog.Builder(VisualizarActivity.this)
                    .setTitle("¡ADVERTENCIA!")
                    .setMessage("¿Deséas continuar con la sincronización?")
                    .setNegativeButton("NO", null)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                if(!Camion.isSync(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(VisualizarActivity.this, "Sincronizando datos", "Por favor espere...", true);
                                    new Sync(getApplicationContext(), progressDialogSync).execute((Void) null);
                                } else {
                                    Toast.makeText(getApplicationContext(), "No es necesaria la sincronización en este momento", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_internet, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .create()
                    .show();
        } else if (id == R.id.nav_logout) {
            if(!Camion.isSync(getApplicationContext())){
                new AlertDialog.Builder(VisualizarActivity.this)
                        .setTitle("¡ADVERTENCIA!")
                        .setMessage("Hay camiones aún sin sincronizar, se borrarán los registros almacenados en este dispositivo, \n ¿Deséas sincronizar?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(VisualizarActivity.this, "Sincronizando datos", "Por favor espere...", true);
                                    new Sync(getApplicationContext(), progressDialogSync).execute((Void) null);

                                    Intent login_activity = new Intent(getApplicationContext(), LoginActivity.class);
                                    usuario.deleteAll();
                                    startActivity(login_activity);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.error_internet, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .create()
                        .show();
            }
            else {
                Intent login_activity = new Intent(getApplicationContext(), LoginActivity.class);
                usuario.deleteAll();
                startActivity(login_activity);
            }
        }else if(id == R.id.nav_cambio){
            Intent main = new Intent(this,CambioClaveActivity.class);
            startActivity(main);
        }
        else if (id == R.id.nav_des) {

            Intent descarga = new Intent(this, DescargaActivity.class);
            startActivity(descarga);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == vig_licencia) {
            vigenciaDatePickerDialog.show();
        }
    }

    private boolean checar() {

        //Reset Errors

        //sindicato.setError(null);
       // empresa.setError(null);
        propietario.setError(null);
        pcamion.setError(null);
        pcaja.setError(null);
        marca.setError(null);
        modelo.setError(null);
        //color.setError(null);
        ancho.setError(null);
        largo.setError(null);
        gato.setError(null);
        alto.setError(null);
        operador.setError(null);
        licencia.setError(null);
        extension.setError(null);
        disminucion.setError(null);
        vig_licencia.setError(null);

        // Store values at the time of the login attempt.
        //final String sindicatos = sindicato.getText().toString();
       // final String empresas = empresa.getText().toString();
        final String propietarios = propietario.getText().toString();
        final String pcamions = pcamion.getText().toString();
      //  final String pcajas = pcaja.getText().toString();
        final String marcas = marca.getText().toString();
        final String modelos = modelo.getText().toString();
        final String anchos= ancho.getText().toString();
        final String largos = largo.getText().toString();
        final String gatos = gato.getText().toString();
        final String altos = alto.getText().toString();
        final String operadors = operador.getText().toString();
        final String licencias = licencia.getText().toString();
        final String extensions = extension.getText().toString();
        final String disminucions = disminucion.getText().toString();
        final String vig_licencias = vig_licencia.getText().toString();


        boolean cancel = false;
        View focusView = null;

        /*if(TextUtils.isEmpty(sindicatos)) {
            sindicato.setError(getString(R.string.error_field_required));
            focusView = sindicato;
            cancel = true;
        }*/
       /* if(TextUtils.isEmpty(propietarios)) {
            propietario.setError(getString(R.string.error_field_required));
            focusView = propietario;
            cancel = true;
        }*/
        if(TextUtils.isEmpty(pcamions)) {
            pcamion.setError(getString(R.string.error_field_required));
            focusView = pcamion;
            cancel = true;
        }
        if(TextUtils.isEmpty(marcas)) {
            marca.setError(getString(R.string.error_field_required));
            focusView = marca;
            cancel = true;
        }
        if(TextUtils.isEmpty(modelos)) {
            modelo.setError(getString(R.string.error_field_required));
            focusView = modelo;
            cancel = true;
        }
        if(TextUtils.isEmpty(anchos)) {
            ancho.setError(getString(R.string.error_field_required));
            focusView = ancho;
            cancel = true;
        }
        if(TextUtils.isEmpty(largos)) {
            largo.setError(getString(R.string.error_field_required));
            focusView = largo;
            cancel = true;
        }
        if(TextUtils.isEmpty(gatos)) {
            gato.setError(getString(R.string.error_field_required));
            focusView = gato;
            cancel = true;
        }
        if(TextUtils.isEmpty(altos)) {
            alto.setError(getString(R.string.error_field_required));
            focusView = alto;
            cancel = true;
        }
        if(TextUtils.isEmpty(operadors)) {
            operador.setError(getString(R.string.error_field_required));
            focusView = operador;
            cancel = true;
        }
       /* if(TextUtils.isEmpty(licencias)) {
            licencia.setError(getString(R.string.error_field_required));
            focusView = licencia;
            cancel = true;
        }*/
        if(TextUtils.isEmpty(extensions)) {
            extension.setError(getString(R.string.error_field_required));
            focusView = extension;
            cancel = true;
        }
        if(TextUtils.isEmpty(disminucions)) {
            disminucion.setError(getString(R.string.error_field_required));
            focusView = disminucion;
            cancel = true;
        }
       /* if(TextUtils.isEmpty(vig_licencias)) {
            vig_licencia.setError(getString(R.string.error_field_required));
            focusView = vig_licencia;
            cancel = true;
        }*/
        return cancel;

    }
}
