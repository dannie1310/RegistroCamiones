package mx.grupohi.registrocamiones.registrocamiones;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    Usuario user;

    // Referencias UI.
    private AutoCompleteTextView mUsuarioView;
    private TextInputLayout formLayout;
    private EditText mPasswordView;
    private ProgressDialog mProgressDialog;
    private Button mIniciarSesionButton;
    Intent mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = new Usuario(this);
        if(user.get()) {
            nextActivity();
        }
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_login_activity);
        setContentView(R.layout.activity_login);

        mUsuarioView = (AutoCompleteTextView) findViewById(R.id.usuario);
        mPasswordView = (EditText) findViewById(R.id.password);
        System.out.println("w0: "+mUsuarioView+mPasswordView);
        formLayout = (TextInputLayout) findViewById(R.id.layout);
        mIniciarSesionButton = (Button) findViewById(R.id.iniciar_sesion_button);


        mIniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                    attemptLogin();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();
                }
            }
        });

        mUsuarioView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                }
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mIniciarSesionButton.performClick();
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();
        if(user.get()) {
            nextActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity() {
        mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        //Reset Errors
        mUsuarioView.setError(null);
        mPasswordView.setError(null);
        formLayout.setError(null);

        // Store values at the time of the login attempt.
        final String usuario = mUsuarioView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if(TextUtils.isEmpty(usuario)) {
            mUsuarioView.setError(getString(R.string.error_field_required));
            focusView = mUsuarioView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "Autenticando", "Por favor espere...", true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mAuthTask = new UserLoginTask(usuario, password);
                    mAuthTask.execute((Void) null);
                }
            }).run();
        }
    }

    public void deleteAllTables() {
        user.deleteAll();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsuario;
        private final String mPassword;
        private JSONObject JSON;

        UserLoginTask(String email, String password) {
            mUsuario = email;
            mPassword = password;


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ContentValues values = new ContentValues();

            values.put("metodo", "paraRegistroCamiones");
            values.put("usr", mUsuario);
            values.put("pass", mPassword);
            System.out.println("w: "+mUsuario+mPassword);

            try {
                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSON = Util.JsonHttp(url, values);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage(getResources().getString(R.string.general_exception));
                return false;
            }
            deleteAllTables();
            try {
                if(JSON.has("error")) {
                    errorLayout(formLayout, (String) JSON.get("error"));
                    return false;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setTitle("Actualizando");
                            mProgressDialog.setMessage("Actualizando datos de usuario...");
                        }
                    });
                    Boolean value;
                    ContentValues data = new ContentValues();

                    data.put("idusuario", (String) JSON.get("IdUsuario"));
                    data.put("nombre", (String) JSON.get("Nombre"));
                    data.put("usr", mUsuario);
                    data.put("pass", mPassword);
                    data.put("idproyecto",(String) JSON.get("IdProyecto"));
                    data.put("base_datos", (String) JSON.get("base_datos"));
                    data.put("descripcion_database", (String) JSON.get("descripcion_database"));


                    user.create(data);

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
                            data.put("idcamion", camion.getString("id_camion"));
                            data.put("sindicato", camion.getString("sindicato"));
                            data.put("empresa",  camion.getString("empresa"));
                            data.put("propietario",  camion.getString("propietario"));
                            data.put("operador",  camion.getString("operador"));
                            data.put("licencia",  camion.getString("numero_licencia"));
                            data.put("economico",  camion.getString("economico"));
                            data.put("placas_camion",  camion.getString("placas_camion"));
                            data.put("placas_caja",  camion.getString("placas_caja"));
                            data.put("marca",  camion.getString("marca"));
                            data.put("modelo",  camion.getString("modelo"));
                            data.put("ancho",  camion.getString("ancho"));
                            data.put("largo",  camion.getString("largo"));
                            data.put("alto",  camion.getString("alto"));
                            data.put("espacio_gato",  camion.getString("espacio_gato"));
                            data.put("altura_extension",  camion.getString("altura_extension"));
                            data.put("disminucion",  camion.getString("disminucion"));
                            data.put("cubicacion_real",  camion.getString("cubicacion_real"));
                            data.put("cubicacion_para_pago",  camion.getString("cubicacion_para_pago"));
                            data.put("estatus",  "0");
                            data.put("vigencia_licencia",  camion.getString("vigencia_licencia"));
                            System.out.println("q "+camion);
                            if(!camionModel.create(data)){
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
                errorMessage(getResources().getString(R.string.general_exception));
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAuthTask = null;
            mProgressDialog.dismiss();
            if (aBoolean) {
                nextActivity();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void errorMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void errorLayout(final TextInputLayout layout, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setErrorEnabled(true);
                layout.setError(message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}

