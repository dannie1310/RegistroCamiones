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
            values.put("metodo", "registro_camiones");
            values.put("usr", usuario.usr);
            values.put("pass", usuario.pass);
            values.put("bd", usuario.baseDatos);
            values.put("idusuario", usuario.idUsuario);
            values.put("Version", String.valueOf(BuildConfig.VERSION_NAME));

            if (Camion.getCount(context) != 0) {
                JSONObject Obj = Camion.getJSON(context);
                values.put("camiones_editados", String.valueOf(Obj));
                System.out.println("JSON: "+String.valueOf(Obj));
            }


            try {

                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSONCAMIONES = HttpConnection.POST(url, values);

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
                if (JSON.has("error")) {
                    Toast.makeText(context, (String) JSON.get("error"), Toast.LENGTH_SHORT).show();
                } else if(JSON.has("msj")) {
                    Camion.deleteAll(context);
                    Toast.makeText(context, (String) JSON.get("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}