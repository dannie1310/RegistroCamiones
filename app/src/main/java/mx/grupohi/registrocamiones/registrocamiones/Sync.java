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
    Integer imagenesRegistradas = 0;
    Integer imagenesTotales = 0;

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
            if ( Camion.getCountInactivos(context) != 0){
                JSONObject inactivos = Camion.getJSONInactivos(context);
                values.put("solicitud_activacion", String.valueOf(inactivos));
            }

            try {

                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSONCAMIONES = HttpConnection.POST(url, values);
                System.out.println("JSON: "+String.valueOf(values));
                ContentValues aux = new ContentValues();
                int i = 0;
                imagenesTotales= ImagenesCamion.getCount(context);
                System.out.println("JSON2: "+JSONCAMIONES);
                while (ImagenesCamion.getCount(context) != 0) {
                    i++;
                    JSON = null;
                    //System.out.println("Existen imagenes para sincronizar: " + ImagenesViaje.getCount(context));
                    aux.put("metodo", "cargaImagenesCamiones");
                    aux.put("usr", usuario.usr);
                    aux.put("pass", usuario.pass);
                    aux.put("bd", usuario.baseDatos);
                    aux.put("Imagenes", String.valueOf(ImagenesCamion.getJSONImagenes(context)));

                    try {
                        JSON = HttpConnection.POST(url, aux);
                        Log.i("json-Imagenes", String.valueOf(aux));
                        try {
                              if (JSON.has("imagenes_registradas")) {
                                final JSONArray imagenes = new JSONArray(JSON.getString("imagenes_registradas"));
                                for (int r = 0; r < imagenes.length(); r++) {
                                    ImagenesCamion.syncLimit(context, imagenes.getInt(r));
                                    imagenesRegistradas++;
                                }
                            }
                            System.out.println("JSON3: "+JSON);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

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
                    Toast.makeText(context, (String) JSONCAMIONES.get("msj") + ".  Imagenes Registradas: "+imagenesRegistradas+" de "+imagenesTotales, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}