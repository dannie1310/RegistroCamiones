package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Usuario on 23/12/2016.
 */

public class Camion {

    public Integer idCamion;
    public String sindicato;
    public String empresa;
    public String propietario;
    public String operador;
    public String licencia;
    public String economico;
    public String placasC;
    public String pCaja;
    public String marca;
    public String modelo;
    public String color;
    public Double ancho;
    public Double largo;
    public Double alto;
    public Double gato;
    public Double disminucion;
    public Double extension;
    public Double cu_real;
    public Double cu_pago;
    public Integer estatus;
    public String vigencia_licencia;

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
                this.sindicato = c.getString(1);
                this.empresa = c.getString(2);
                this.propietario = c.getString(3);
                this.operador = c.getString(4);
                this.licencia = c.getString(5);
                this.economico = c.getString(6);
                this.placasC = c.getString(7);
                this.pCaja = c.getString(8);
                this.marca = c.getString(9);
                this.modelo = c.getString(10);
                this.ancho = c.getDouble(11);
                this.largo = c.getDouble(12);
                this.alto = c.getDouble(13);
                this.gato = c.getDouble(14);
                this.extension = c.getDouble(15);
                this.disminucion = c.getDouble(16);
                this.cu_real = c.getDouble(17);
                this.cu_pago = c.getDouble(18);
                this.estatus = c.getInt(19);
                this.vigencia_licencia = c.getString(20);

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

    ArrayList<String> getArrayListId() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * from camiones ORDER BY idcamion ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("idcamion")));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("idcamion")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("idcamion")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    ArrayList<String> getArrayListDescripciones() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM camiones ORDER BY idcamion ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("economico")));
                } else {
                    data.add("-- Seleccione --");
                    data.add(c.getString(c.getColumnIndex("economico")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("economico")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    static boolean update(String idcamion, ContentValues data, Context context){
        boolean resp=false;
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();

        try{
            db.update("camiones", data, "idcamion = '"+idcamion+"'", null);
            resp = true;
        }catch (Exception e){
            resp = false;
        }finally {
            db.close();
        }
        return resp;
    }
}
