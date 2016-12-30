package mx.grupohi.registrocamiones.registrocamiones;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;


class Sync extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private ProgressDialog progressDialog;
    private Usuario usuario;


    private JSONObject JSONCAMIONES;
    private JSONObject JSON;

    Sync(Context context, ProgressDialog progressDialog) {

        this.context = context;
        this.progressDialog = progressDialog;
        usuario = new Usuario(context);
        usuario = usuario.getUsuario();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

            ContentValues values = new ContentValues();

            values.clear();
            values.put("metodo", "capturaActualizacionCamiones");
            values.put("usr", usuario.usr);
            values.put("pass", usuario.pass);
            values.put("bd", usuario.baseDatos);
            values.put("idusuario", usuario.idUsuario);
            values.put("Version", String.valueOf(BuildConfig.VERSION_NAME));
            values.put("id_proyecto", usuario.idProyecto);

            if (Camion.getCount(context) != 0) {
                JSONObject Obj = Camion.getJSON(context);
                values.put("camiones_editados", String.valueOf(Obj));

            }


            try {

                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSONCAMIONES = HttpConnection.POST(url, values);
                System.out.println("JSON: "+String.valueOf(values));

            }catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
            return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
        if(aBoolean) {
            try {
                if (JSONCAMIONES.has("error")) {
                    Toast.makeText(context, (String) JSONCAMIONES.get("error"), Toast.LENGTH_SHORT).show();
                } else if(JSONCAMIONES.has("msj")) {
                    Camion.deleteAll(context); //cambiar estatus
                    Toast.makeText(context, (String) JSONCAMIONES.get("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}