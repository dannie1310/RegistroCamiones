package mx.grupohi.registrocamiones.registrocamiones;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ImagenDetalle extends AppCompatActivity {
    private ImagenesCamion itemDetallado;
    private ImageView imagenExtendida;

    public static final String EXTRA_PARAM_ID = "";
    public static final String VIEW_NAME_HEADER_IMAGE = "imagen_compartida";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_detalle);

        // Obtener el coche con el identificador establecido en la actividad principal
        itemDetallado = ImagenesCamion.getItem(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        imagenExtendida = (ImageView) findViewById(R.id.imagen_extendida);

        cargarImagenExtendida();
    }

    private void cargarImagenExtendida() {
        String imagen = itemDetallado.getIdDrawable();
       // Bitmap imagenUsar= Usuario.decodeBase64(imagen);
        Bitmap bitmap = BitmapFactory.decodeFile(imagen);
        imagenExtendida.setImageBitmap(bitmap);
       /* Glide.with(imagenExtendida.getContext())
                .load(itemDetallado.getIdDrawable())
                .into(imagenExtendida);*/
    }



   
}
