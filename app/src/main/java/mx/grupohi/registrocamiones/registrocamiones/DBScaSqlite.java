package mx.grupohi.registrocamiones.registrocamiones;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DBScaSqlite extends SQLiteOpenHelper {

    private String[] queries = new String[] {
            "CREATE TABLE user (idusuario INTEGER PRIMARY KEY, nombre TEXT, usr TEXT, pass TEXT, idproyecto INTEGER, base_datos TEXT, descripcion_database TEXT)",
            "CREATE TABLE camiones (idcamion INTEGER,sindicato TEXT, empresa TEXT,propietario TEXT, operador TEXT, licencia TEXT, economico TEXT, placas_camion TEXT, placas_caja TEXT,  marca TEXT, modelo TEXT, ancho REAL, largo REAL, alto REAL, espacio_gato REAL, altura_extension REAL, disminucion REAL, cubicacion_real REAL, cubicacion_para_pago REAL,  estatus INTEGER, vigencia_licencia TEXT)",
    };

    public DBScaSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String query : queries) {
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS camiones");

        for (String query : queries) {
            db.execSQL(query);
        }

        db.close();
    }

    void deleteCatalogos() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("DELETE FROM user");
        db.execSQL("DELETE FROM camiones");

        db.close();
    }
}
