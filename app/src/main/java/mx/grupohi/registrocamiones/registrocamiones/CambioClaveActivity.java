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
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class CambioClaveActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ProgressDialog progressDialogSync;
    private ProgressDialog progressDialogCambio;
    private Usuario usuario;
    private EditText uss;
    private EditText actual;
    private EditText pass;
    private EditText passConfirmacion;
    private Button cambio;
    private String us_sesion;
    private String us_escrito;
    CambioClave c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_clave);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = new Usuario(this);
        usuario = usuario.getUsuario();
        us_sesion = usuario.usr.toUpperCase();
        uss = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);
        passConfirmacion = (EditText) findViewById(R.id.passCambio);
        actual = (EditText) findViewById(R.id.passAnterior);
        cambio = (Button) findViewById(R.id.CambioButton);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (!Util.isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(CambioClaveActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();

        }


        cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checar()) {

                    us_escrito = uss.getText().toString().toUpperCase();

                    if (us_escrito.equals(us_sesion) && actual.getText().toString().equals(usuario.pass)) {

                        if (pass.getText().toString().length() >= 8 && passConfirmacion.getText().toString().length() >= 8) {

                            if (pass.getText().toString().equals(passConfirmacion.getText().toString())) {
                                //OK
                                progressDialogCambio = ProgressDialog.show(CambioClaveActivity.this, "Cambiando Contraseña", "Por favor espere...", true);
                                c = new CambioClave(getApplicationContext(), progressDialogSync, pass.getText().toString());
                                c.execute((Void) null);

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.error_pass, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_field_requiredpass, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!us_escrito.equals(us_sesion)) {
                            Toast.makeText(getApplicationContext(), R.string.error_uss, Toast.LENGTH_SHORT).show();
                        } else if (!actual.getText().toString().equals(usuario.pass)) {
                            Toast.makeText(getApplicationContext(), R.string.error_anter, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

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

    private boolean checar() {

        //Reset Errors

        //sindicato.setError(null);
        uss.setError(null);
        pass.setError(null);
        passConfirmacion.setError(null);
        actual.setError(null);

        final String usuarios = uss.getText().toString();
        final String passw = pass.getText().toString();
        final String passCambio = passConfirmacion.getText().toString();
        final String vieja = actual.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(usuarios)) {
            uss.setError(getString(R.string.error_field_required));
            focusView = uss;
            cancel = true;
        }
        if (TextUtils.isEmpty(vieja)) {
            actual.setError(getString(R.string.error_field_required));
            focusView = actual;
            cancel = true;
        }
        if (TextUtils.isEmpty(passw)) {
            pass.setError(getString(R.string.error_field_required));
            focusView = pass;
            cancel = true;
        }
        if (TextUtils.isEmpty(passCambio)) {
            passConfirmacion.setError(getString(R.string.error_field_required));
            focusView = passConfirmacion;
            cancel = true;
        }

        return cancel;

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent re = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(re);
        } else if (id == R.id.nav_re) {
            Intent re = new Intent(getApplicationContext(), ReactivacionActivity.class);
            startActivity(re);
        } else if (id == R.id.nav_sync) {
            new AlertDialog.Builder(CambioClaveActivity.this)
                    .setTitle("¡ADVERTENCIA!")
                    .setMessage("¿Deséas continuar con la sincronización?")
                    .setNegativeButton("NO", null)
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                if (!Camion.isSync(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(CambioClaveActivity.this, "Sincronizando datos", "Por favor espere...", true);
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

            if (!Camion.isSync(getApplicationContext())) {
                new AlertDialog.Builder(CambioClaveActivity.this)
                        .setTitle("¡ADVERTENCIA!")
                        .setMessage("Hay camiones aún sin sincronizar, se borrarán los registros almacenados en este dispositivo,  \n ¿Deséas sincronizar?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                                    progressDialogSync = ProgressDialog.show(CambioClaveActivity.this, "Sincronizando datos", "Por favor espere...", true);
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
            } else {
                Intent login_activity = new Intent(getApplicationContext(), LoginActivity.class);
                usuario.deleteAll();
                startActivity(login_activity);
            }
        } else if (id == R.id.nav_cambio) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_des) {

            Intent descarga = new Intent(this, DescargaActivity.class);
            startActivity(descarga);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class CambioClave extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private ProgressDialog progressDialog;
        private Usuario usuario;


        private String IMEI;
        private String NuevaClave;

        private JSONObject JSONVIAJES;
        private JSONObject JSON;
        Intent in;

        CambioClave(Context context, ProgressDialog progressDialog, String clavenueva) {

            this.context = context;
            this.progressDialog = progressDialog;
            this.NuevaClave = clavenueva;
            usuario = new Usuario(context);
            usuario = usuario.getUsuario();

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            TelephonyManager phneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = phneMgr.getDeviceId();
            in = new Intent(context, CambioClaveActivity.class);
            ContentValues values = new ContentValues();
            Boolean resp = null;
            values.clear();

            values.put("metodo", "ActualizarAcceso");
            values.put("usr", usuario.usr);
            values.put("pass", usuario.pass);
            values.put("idusuario", usuario.getId());
            values.put("bd", usuario.baseDatos);
            values.put("IMEI", IMEI);
            values.put("Version", String.valueOf(BuildConfig.VERSION_NAME));
            values.put("NuevaClave", NuevaClave);

            try {

                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSONVIAJES = HttpConnection.POST(url, values);
                Log.i("josn", String.valueOf(JSONVIAJES));
                Log.i("jsonviajes:  ", String.valueOf(values));
                if (JSONVIAJES.has("error")) {
                    //Toast.makeText(context, (String) JSONVIAJES.get("error"), Toast.LENGTH_SHORT).show();
                    resp = false;
                } else if (JSONVIAJES.has("msj")) {
                    Usuario.updatePass(NuevaClave, context);
                    //Toast.makeText(context, (String) JSONVIAJES.get("msj"), Toast.LENGTH_LONG).show();
                    resp = true;
                }

            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                resp =  false;
            }
           return resp;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            c = null;
            progressDialogCambio.dismiss();
            if (aBoolean) {
                try {
                    Toast.makeText(context, (String) JSONVIAJES.get("msj"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //startActivity(in);
            }
        }
    }
}
