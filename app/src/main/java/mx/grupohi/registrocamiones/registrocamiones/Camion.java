package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 23/12/2016.
 */

public class Camion {

    public Integer idCamion;
    public String placas;
    public String marca;
    public String modelo;
    public Double ancho;
    public Double largo;
    public Double alto;
    public String economico;
    public Integer capacidad;
    public Integer numero_viajes;
    public String sindicato;
    public String empresa;


    private Context context;

    private SQLiteDatabase db;
    private DBScaSqlite db_sca;

    Camion(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    Boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        try {
            return db.insert("camiones", null, data) > -1;
        } finally {
            db.close();
        }
    }

    public Camion find(Integer idCamion) {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE idcamion = '" + idCamion + "'", null);
        try {
            if (c != null && c.moveToFirst()) {
                this.idCamion = c.getInt(0);
                this.placas = c.getString(1);
                this.marca = c.getString(2);
                this.modelo = c.getString(3);
                this.ancho = c.getDouble(4);
                this.largo = c.getDouble(5);
                this.alto = c.getDouble(6);
                this.economico = c.getString(7);
                this.capacidad = c.getInt(8);
                this.numero_viajes = c.getInt(9);

                return this;
            } else {
                return null;
            }
        } finally {
            assert c != null;
            c.close();
            db.close();
        }
    }

    public static Boolean findId(Integer idCamion, Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE idcamion = '" + idCamion + "'", null);
        try {
            if (c != null && c.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } finally {
            c.close();
            db.close();
        }
    }
}
