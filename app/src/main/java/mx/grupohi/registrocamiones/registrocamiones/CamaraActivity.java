package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CamaraActivity extends AppCompatActivity {

    private static String APP_DIRECTORY = "Picture/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Camiones";

    private final int PHOTO_CODE = 200;

    private ImageView mSetImage;
    private Button button;
    private RelativeLayout mRlView;

    Bitmap bitmap;

    private String mPath;
    private String base64="";

    private HashMap<String, String> spinnerTiposMap;
    private Spinner tiposSpinner;
    TipoImagenes tipo;
    String idTipo;
    String idcamion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        idcamion = getIntent().getStringExtra("idcamion");
        System.out.println("camion: " +idcamion);

        mSetImage = (ImageView) findViewById(R.id.set_picture);
        mRlView = (RelativeLayout) findViewById(R.id.activity_camara);
        button = (Button) findViewById(R.id.buttonGuardar);
        try {
            openCamera();
        }catch (Exception e) {
            e.printStackTrace();
        }


        tipo = new TipoImagenes(getApplicationContext());
        tiposSpinner = (Spinner) findViewById(R.id.spinnerTipos);

        final ArrayList<String> tiposImagenes= tipo.getArrayListTipos(Integer.valueOf(idcamion));
        final ArrayList<String> ids = tipo.getArrayListId(Integer.valueOf(idcamion));
        final String[] spinnerTiposArray = new String[ids.size()];
        spinnerTiposMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            spinnerTiposMap.put(tiposImagenes.get(i), ids.get(i));
            spinnerTiposArray[i] = tiposImagenes.get(i);
        }

        final ArrayAdapter<String> arrayAdapterTiros = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, spinnerTiposArray);
        arrayAdapterTiros.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        tiposSpinner.setAdapter(arrayAdapterTiros);

        tiposSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String descripcion = String.valueOf(parent.getItemAtPosition(position));
                idTipo = spinnerTiposMap.get(descripcion);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean respuesta = null;
                if (idTipo.equals("0")) {
                    Toast.makeText(getApplicationContext(),R.string.seleccionar_opcion, Toast.LENGTH_SHORT).show();
                }
                else {
                    ContentValues cv = new ContentValues();
                    Integer aux = Integer.valueOf(idcamion);
                    cv.put("idcamion", aux);
                    cv.put("idtipo_imagen", idTipo);

                    cv.put("url", mPath);
                    cv.put("imagen", base64);
                    cv.put("estatus", "1");

                    ImagenesCamion imagenesViaje = new ImagenesCamion(CamaraActivity.this);


                    respuesta = imagenesViaje.create(cv);

                    if (respuesta) {
                        Toast.makeText(getApplicationContext(), "Se Guardo la Imagen", Toast.LENGTH_LONG).show();
                        Intent imagen = new Intent(CamaraActivity.this, ImagenesActivity.class);
                        imagen.putExtra("idcamion", idcamion);
                        startActivity(imagen);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al guardar imagen", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });




    }


    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpeg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PHOTO_CODE:
                MediaScannerConnection.scanFile(this,
                        new String[]{mPath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> Uri = " + uri);
                            }
                        });


                bitmap = BitmapFactory.decodeFile(mPath);
                mSetImage.setImageBitmap(bitmap);
                try {
                    base64 = Usuario.encodeToBase64Imagen(bitmap, 30);
                }catch (Exception e){
                    e.printStackTrace();
                    Intent intent = new Intent(getApplicationContext(), VisualizarActivity.class);
                    intent.putExtra("idcamion", idcamion);
                    startActivity(intent);
                }
                break;


        }
    }

}
