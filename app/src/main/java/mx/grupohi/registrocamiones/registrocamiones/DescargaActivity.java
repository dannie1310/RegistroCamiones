package mx.grupohi.registrocamiones.registrocamiones;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class DescargaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Usuario usuario;
    Intent mainActivity;
    private ProgressDialog progressDialogSync;
    private TextInputLayout loginFormLayout;
    private ProgressDialog mProgressDialog;

    private DescargaCatalogos descargaCatalogos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descarga);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = new Usuario(this);
        usuario = usuario.getUsuario();
        mainActivity = new Intent(this, MainActivity.class);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if(!Util.isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(DescargaActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();

        }

        mProgressDialog = ProgressDialog.show(DescargaActivity.this, "Descargando", "Por favor espere...", true);
        descargaCatalogos = new DescargaActivity.DescargaCatalogos(getApplicationContext(), progressDialogSync);
        descargaCatalogos.execute((Void) null);
        if (drawer != null)
            drawer.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < drawer.getChildCount(); i++) {
                        View child = drawer.getChildAt(i);
                        TextView tvp = (TextView) child.findViewById(R.id.textViewProyecto);
                        TextView tvu = (TextView) child.findViewById(R.id.textViewUser);
                        TextView tvv = (TextView) child.findViewById(R.id.textViewVersion);

                        if (tvp != null) {
                            tvp.setText(usuario.descripcionBaseDatos);
                        }
                        if (tvu != null) {
                            tvu.setText(usuario.nombre);
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
            Intent re  = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(re);
        } else if (id == R.id.nav_re){
            Intent re  = new Intent(getApplicationContext(),ReactivacionActivity.class);
            startActivity(re);
        } else if (id == R.id.nav_sync) {
            new AlertDialog.Builder(DescargaActivity.this)
                    .setTitle("¡ADVERTENCIA!")
                    .setMessage("¿Deséas continuar con la sincronización?")
                    .setNegativeButton("NO", null)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                if(!Camion.isSync(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(DescargaActivity.this, "Sincronizando datos", "Por favor espere...", true);
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
                new AlertDialog.Builder(DescargaActivity.this)
                        .setTitle("¡ADVERTENCIA!")
                        .setMessage("Hay camiones aún sin sincronizar, se borrarán los registros almacenados en este dispositivo,  \n ¿Deséas sincronizar?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(DescargaActivity.this, "Sincronizando datos", "Por favor espere...", true);
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
            Intent descarga = new Intent(this, CambioClaveActivity.class);
            startActivity(descarga);
        }else if (id == R.id.nav_des) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class DescargaCatalogos extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private ProgressDialog progressDialog;
        private Usuario usuario;
        private DBScaSqlite db_sca;
        private JSONObject JSON;
        Integer estatusCamion = 0;


        DescargaCatalogos(Context context, ProgressDialog progressDialog) {
            this.context = context;
            this.progressDialog = progressDialog;
            usuario = new Usuario(context);
            usuario = usuario.getUsuario();
            db_sca = new DBScaSqlite(context, "sca", null, 1);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            ContentValues data = new ContentValues();
            data.put("metodo", "paraRegistroCamiones");
            data.put("usr", usuario.usr);
            data.put("pass", usuario.pass);

            try {
                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSON = HttpConnection.POST(url, data);
                if (JSON.has("error")) {
                    Toast.makeText(context, "Error al descargar ", Toast.LENGTH_LONG).show();
                    return false;
                } else {

                    db_sca.descargaCatalogos();

                    Camion camionModel = new Camion(getApplicationContext());
                    try {
                        final JSONArray camiones = new JSONArray(JSON.getString("camiones"));
                        for (int i = 0; i < camiones.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de camiones... \n Camion " + finalI + " de " + camiones.length());
                                }
                            });

                            JSONObject camion = camiones.getJSONObject(i);

                            data.clear();



                            estatusCamion = Camion.findCamion(Integer.valueOf(camion.getString("id_camion")), context);
                            if (!estatusCamion.equals("0")) {
                                data.put("idcamion", camion.getString("id_camion"));
                                data.put("sindicato", camion.getString("sindicato"));
                                data.put("empresa", camion.getString("empresa"));
                                data.put("propietario", camion.getString("propietario"));
                                data.put("operador", camion.getString("operador"));
                                data.put("licencia", camion.getString("numero_licencia"));
                                data.put("economico", camion.getString("economico"));
                                data.put("placas_camion", camion.getString("placas_camion"));
                                data.put("placas_caja", camion.getString("placas_caja"));
                                data.put("marca", camion.getString("marca"));
                                data.put("modelo", camion.getString("modelo"));
                                data.put("ancho", camion.getString("ancho"));
                                data.put("largo", camion.getString("largo"));
                                data.put("alto", camion.getString("alto"));
                                data.put("espacio_gato", camion.getString("espacio_gato"));
                                data.put("altura_extension", camion.getString("altura_extension"));
                                data.put("disminucion", camion.getString("disminucion"));
                                data.put("cubicacion_real", camion.getString("cubicacion_real"));
                                data.put("cubicacion_para_pago", camion.getString("cubicacion_para_pago"));
                                data.put("estatus", "0");
                                data.put("vigencia_licencia", camion.getString("vigencia_licencia"));
                                data.put("estatus_camion", camion.getString("estatus"));

                                System.out.println("q " + camion);

                                if(estatusCamion == 1){
                                    if (!camionModel.update(camion.getString("id_camion"),data,getApplicationContext())) {

                                    }
                                }else if (estatusCamion == 2) {
                                    if (!camionModel.create(data)) {

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Tipos Imagenes

                    TipoImagenes tipo = new TipoImagenes(getApplicationContext());
                    try {
                        final JSONArray tags = new JSONArray(JSON.getString("tipos_imagen"));
                        for (int i = 0; i < tags.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Tipos de Imagenes... \n  " + finalI + " de " + tags.length());
                                }
                            });
                            JSONObject info = tags.getJSONObject(i);

                            data.clear();
                            data.put("id", info.getString("id"));
                            data.put("descripcion", info.getString("descripcion"));

                            if (!tipo.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //sindicatos

                    Sindicato sindicato = new Sindicato(getApplicationContext());
                    try {
                        final JSONArray sindicatos = new JSONArray(JSON.getString("sindicatos"));
                        for (int i = 0; i < sindicatos.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de sindicatos... \n Sindicato " + finalI + " de " + sindicatos.length());
                                }
                            });

                            JSONObject sind = sindicatos.getJSONObject(i);

                            data.clear();
                            data.put("idsindicato", sind.getString("id"));
                            data.put("descripcion", sind.getString("sindicato"));

                            System.out.println("q "+sind);
                            if(!sindicato.create(data)){
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //empresas

                    Empresa empresa = new Empresa(getApplicationContext());
                    try {
                        final JSONArray empresas = new JSONArray(JSON.getString("empresas"));
                        for (int i = 0; i < empresas.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de empresas... \n Empresa " + finalI + " de " + empresas.length());
                                }
                            });

                            JSONObject sind = empresas.getJSONObject(i);

                            data.clear();
                            data.put("idempresa", sind.getString("id"));
                            data.put("descripcion", sind.getString("empresa"));

                            System.out.println("q "+sind);
                            if(!empresa.create(data)){
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            descargaCatalogos = null;
            mProgressDialog.dismiss();
            if (success) {
                startActivity(mainActivity);
            }
        }
    }
}
