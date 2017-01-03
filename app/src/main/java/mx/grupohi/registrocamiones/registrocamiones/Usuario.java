package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


class Usuario {




    static String usr;
    private static String bd;
    private static String proyecto;
    private Context context;

    private static SQLiteDatabase db;
    private static DBScaSqlite db_sca;

    String name;
    String pass;

    Integer idUsuario;
    Integer idProyecto;

    String nombre;
    String baseDatos;
    String descripcionBaseDatos;
    String empresa;

    Usuario(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues values) {
        db = db_sca.getWritableDatabase();
        try{
            return db.insert("user", null, values) > -1;
        } finally {
            db.close();
        }
    }

    Usuario getUsuario() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user LIMIT 1", null);
        try {
            if(c != null && c.moveToFirst()) {
                this.idUsuario = c.getInt(c.getColumnIndex("idusuario"));
                this.idProyecto = c.getInt(c.getColumnIndex("idproyecto"));
                this.nombre = c.getString(c.getColumnIndex("nombre"));
                this.baseDatos = c.getString(c.getColumnIndex("base_datos"));
                this.descripcionBaseDatos = c.getString(c.getColumnIndex("descripcion_database"));
                this.usr = c.getString(c.getColumnIndex("usr"));
                this.pass = c.getString(c.getColumnIndex("pass"));

                return this;
            } else {
                return null;
            }
        }finally {
            c.close();
            db.close();
        }
    }

    void deleteAll() {
        db = db_sca.getWritableDatabase();
        try{
            db.execSQL("DELETE FROM user");
        } finally {
            db.close();
        }
    }

    boolean get() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user", null);
        try{
            return c != null && c.moveToFirst();
        } finally {
            c.close();
            db.close();
        }
    }

    String getName() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT nombre FROM user LIMIT 1", null);
        try{
            if (c != null && c.moveToFirst()) {
                name = c.getString(c.getColumnIndex("nombre"));
            }
            return name;
        } finally {
            c.close();
        }
    }

    String getPass() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT pass FROM user LIMIT 1", null);
        try{
            if (c.moveToFirst()) {
                pass = c.getString(c.getColumnIndex("pass"));
            }
            return pass;
        } finally {
            c.close();
            db.close();
        }
    }

    static String getProyecto(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT descripcion_database FROM user LIMIT 1", null);
        try{
            if (c.moveToFirst()) {
                proyecto = c.getString(c.getColumnIndex("descripcion_database"));
            }
            return proyecto;
        } finally {
            c.close();
            db.close();
        }
    }

    static String getIdProyecto(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT idproyecto FROM user LIMIT 1", null);
        try{
            if (c.moveToFirst()) {
                proyecto = c.getString(c.getColumnIndex("idproyecto"));
            }
            return proyecto;
        } finally {
            c.close();
            db.close();
        }
    }
    
    static String getUser(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT usr FROM user LIMIT 1", null);
        try{
            if (c.moveToFirst()) {
                usr = c.getString(c.getColumnIndex("usr"));
            }
            return usr;
        } finally {
            c.close();
            db.close();
        }
    }
    
    static String getBaseDatos(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT base_datos FROM user LIMIT 1", null);
        try{
            if (c.moveToFirst()) {
                bd = c.getString(c.getColumnIndex("base_datos"));
            }
            return bd;
        } finally {
            c.close();
            db.close();
        }
    }

    public static String encodeToBase64Imagen(Bitmap image, int quality)
    {

        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        String respuesta = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        respuesta = respuesta.replace("\n","");
        return respuesta;
    }


}
