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
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VisualizarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Usuario usuario;
    TextView economico;
    TextView cu_real;
    TextView cu_pago;
    EditText sindicato;
    EditText empresa;
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
    String idcamion;
    Camion camion;

    Double cubicacion;

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
        System.out.println("camion: " +idcamion);
        camion = new Camion(this);
        camion = camion.find(Integer.valueOf(idcamion));
        usuario = new Usuario(getApplicationContext());

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        economico = (TextView) findViewById(R.id.textViewCamion);
        economico.setText(camion.economico);

        sindicato = (EditText) findViewById(R.id.textViewSindicato);
        sindicato.setText(camion.sindicato);

        empresa = (EditText) findViewById(R.id.textViewEmpresa);
        empresa.setText(camion.empresa);

        propietario = (EditText) findViewById(R.id.textViewPropietario);
        propietario.setText(camion.propietario);

        pcamion =(EditText) findViewById(R.id.textViewPCamion);
        pcamion.setText(camion.placasC);

        pcaja = (EditText) findViewById(R.id.textViewPCaja);
        pcaja.setText(camion.pCaja);

        marca =(EditText) findViewById(R.id.textViewMarca);
        marca.setText(camion.marca);

        modelo = (EditText) findViewById(R.id.textViewModelo);
        modelo.setText(camion.modelo);

       /*color = (EditText) findViewById(R.id.textViewColor);
        color.setText(camion.color);*/

        ancho =(EditText) findViewById(R.id.textViewAncho);
        ancho.setText(String.valueOf(camion.ancho));
        ancho.requestFocus();


        ancho.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         cubicacion = setCubicacion(String.valueOf(ancho.getText()),String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()),String.valueOf(gato.getText()),String.valueOf(disminucion.getText()));
                                         System.out.println("2cubicacion: "+Math.ceil(cubicacion));
                                         cu_pago.setText(String.valueOf(Math.ceil(cubicacion)));
                                         cu_real.setText(String.valueOf(redondear(cubicacion,2)));
                                     }
        });

        largo = (EditText) findViewById(R.id.textViewLargo);
        largo.setText(String.valueOf(camion.largo));
        largo.requestFocus();
        largo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cubicacion = setCubicacion(String.valueOf(ancho.getText()),String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()),String.valueOf(gato.getText()),String.valueOf(disminucion.getText()));
                System.out.println("2cubicacion: "+Math.ceil(cubicacion));
                cu_pago.setText(String.valueOf(Math.ceil(cubicacion)));
                cu_real.setText(String.valueOf(redondear(cubicacion,2)));
            }
        });

        gato = (EditText) findViewById(R.id.textViewGato);
        gato.setText(String.valueOf(camion.gato));

        alto = (EditText) findViewById(R.id.textViewAlto);
        alto.setText(String.valueOf(camion.alto));

        operador = (EditText) findViewById(R.id.textViewNOperador);
        operador.setText(camion.operador);

        licencia =(EditText) findViewById(R.id.textViewLicencia);
        licencia.setText(camion.licencia);

        extension =(EditText)findViewById(R.id.textViewExtension);
        extension.setText(String.valueOf(camion.extension));

        disminucion = (EditText) findViewById(R.id.textViewDisminucion);
        disminucion.setText(String.valueOf(camion.disminucion));

        cubicacion = setCubicacion(String.valueOf(ancho.getText()),String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()),String.valueOf(gato.getText()),String.valueOf(disminucion.getText()));
        System.out.println("cubicacion: "+Math.ceil(cubicacion));

        cu_pago = (TextView) findViewById(R.id.textViewCUPago);
        cu_pago.setText(String.valueOf(Math.ceil(cubicacion)));

        cu_real = (TextView) findViewById(R.id.textViewCUReal);
        cu_real.setText(String.valueOf(redondear(cubicacion,2)));

        vig_licencia = (EditText) findViewById(R.id.textViewVigencia);
        vig_licencia.setText(camion.vigencia_licencia);

        vig_licencia.setInputType(InputType.TYPE_NULL);
        vig_licencia.requestFocus();

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

        cancelar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
            }
        } );

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camion.sindicato.equals(String.valueOf(sindicato.getText())) && camion.placasC.equals(String.valueOf(pcamion.getText())) && camion.pCaja.equals(String.valueOf(pcaja.getText())) && camion.empresa.equals(String.valueOf(empresa.getText())) && camion.propietario.equals(String.valueOf(propietario.getText())) && camion.operador.equals(String.valueOf(operador.getText())) && camion.licencia.equals(String.valueOf(licencia.getText())) && camion.vigencia_licencia.equals(String.valueOf(vig_licencia.getText())) && camion.modelo.equals(String.valueOf(modelo.getText())) && camion.marca.equals(String.valueOf(marca.getText())) && String.valueOf(camion.ancho).equals(String.valueOf(ancho.getText())) && String.valueOf(camion.largo).equals(String.valueOf(largo.getText())) && String.valueOf(camion.alto).equals(String.valueOf(alto.getText())) && String.valueOf(camion.gato).equals(String.valueOf(gato.getText())) && String.valueOf(camion.extension).equals(String.valueOf(extension.getText())) && String.valueOf(camion.disminucion).equals(String.valueOf(disminucion.getText()))){
                        Toast.makeText(getApplicationContext(), R.string.ningun_cambio, Toast.LENGTH_SHORT).show();
                }else {
                    System.out.println("w: " + camion.idCamion + " economico: " + economico.getText()); //guardar (update) Camion.update
                    cubicacion = setCubicacion(String.valueOf(ancho.getText()),String.valueOf(alto.getText()), String.valueOf(largo.getText()), String.valueOf(extension.getText()),String.valueOf(gato.getText()),String.valueOf(disminucion.getText()));
                    System.out.println("cubicacion: "+Math.ceil(cubicacion));
                    ContentValues data = new ContentValues();

                    data.put("sindicato", String.valueOf(sindicato.getText()));
                    data.put("empresa",  String.valueOf(empresa.getText()));
                    data.put("propietario",  String.valueOf(propietario.getText()));
                    data.put("operador",  String.valueOf(operador.getText()));
                    data.put("licencia",  String.valueOf(licencia.getText()));
                    data.put("economico",  String.valueOf(economico.getText()));
                    data.put("placas_camion",  String.valueOf(pcamion.getText()));
                    data.put("placas_caja",  String.valueOf(pcaja.getText()));
                    data.put("marca",  String.valueOf(marca.getText()));
                    data.put("modelo", String.valueOf(modelo.getText()));
                    data.put("ancho",  String.valueOf(ancho.getText()));
                    data.put("largo",  String.valueOf(largo.getText()));
                    data.put("alto",  String.valueOf(alto.getText()));
                    data.put("espacio_gato",  String.valueOf(gato.getText()));
                    data.put("altura_extension",  String.valueOf(extension.getText()));
                    data.put("disminucion",  String.valueOf(disminucion.getText()));
                    data.put("cubicacion_real", String.valueOf(redondear(cubicacion,2)));
                    data.put("cubicacion_para_pago",  String.valueOf(Math.ceil(cubicacion)));
                    data.put("estatus",  "1");
                    data.put("vigencia_licencia",  String.valueOf(vig_licencia.getText()));

                    System.out.println("guardar: "+data);

                    Boolean r = camion.update(idcamion, data, getApplicationContext());
                    if(!r){
                        Toast.makeText(getApplicationContext(), R.string.error_guardar, Toast.LENGTH_SHORT).show();
                    }else{
                        Intent ok = new Intent(getApplicationContext(), MainActivity.class);
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
                            tvv.setText("Versión " + String.valueOf(BuildConfig.VERSION_NAME));
                        }
                    }
                }
            });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public Double setCubicacion(String ancho, String alto, String largo, String extension, String gato, String disminucion) {
        return Double.valueOf(ancho) * Double.valueOf(largo) * (Double.valueOf(alto) + Double.valueOf(extension)) - Double.valueOf(gato) - Double.valueOf(disminucion);
    }

    public Double redondear(double numero, int digitos){
        int cifras = (int) Math.pow(10, digitos);
        return Math.rint(numero * cifras)/cifras;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
           Intent main = new Intent(this,MainActivity.class);
            startActivity(main);
        } else if (id == R.id.nav_sync) {
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
}
