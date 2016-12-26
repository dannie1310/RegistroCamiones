package mx.grupohi.registrocamiones.registrocamiones;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class VisualizarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Usuario usuario;
    TextView economico;
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
    Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usuario = new Usuario(getApplicationContext());
        economico = (TextView) findViewById(R.id.textViewCamion);
        economico.setText("datos");
        sindicato = (EditText) findViewById(R.id.textViewSindicato);
        empresa = (EditText) findViewById(R.id.textViewEmpresa);
        propietario = (EditText) findViewById(R.id.textViewPropietario);
        pcamion =(EditText) findViewById(R.id.textViewPCamion);
        pcaja = (EditText) findViewById(R.id.textViewPCaja);
        marca =(EditText) findViewById(R.id.textViewMarca);
        modelo = (EditText) findViewById(R.id.textViewModelo);
        color = (EditText) findViewById(R.id.textViewColor);
        ancho =(EditText) findViewById(R.id.textViewAncho);
        largo = (EditText) findViewById(R.id.textViewLargo);
        gato = (EditText) findViewById(R.id.textViewGato);
        alto = (EditText) findViewById(R.id.textViewAlto);
        operador = (EditText) findViewById(R.id.textViewNOperador);
        licencia =(EditText) findViewById(R.id.textViewLicencia);

        guardar= (Button) findViewById(R.id.buttonGuardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("w: "+sindicato.getText()+ " alto: "+alto.getText());
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

        } else if (id == R.id.nav_logout) {
            Intent login_activity = new Intent(getApplicationContext(), LoginActivity.class);
            usuario.deleteAll();
            startActivity(login_activity);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
