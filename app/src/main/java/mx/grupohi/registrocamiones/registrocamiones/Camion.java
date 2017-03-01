package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

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
    public Integer estatus_camion;

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
                this.estatus_camion = c.getInt(21);

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
        Cursor c = db.rawQuery("SELECT * from camiones WHERE estatus_camion = 1 ORDER BY idcamion ASC", null);
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
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus_camion = 1 ORDER BY idcamion ASC", null);
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
            //for(int i=1; i<=100; i++) {
                db.update("camiones", data, "idcamion = '" + idcamion + "'", null);
            //}
            resp = true;
        }catch (Exception e){
            resp = false;
        }finally {
            db.close();
        }
        return resp;
    }

    static Boolean isSync(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();

        Boolean result = true;
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus = 1", null);
        try {
            if(c != null && c.moveToFirst()) {
                result = false;
            }
            return result;
        } finally {
            c.close();
            db.close();
        }
    }


    static Integer getCount(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus = 1",null);
        try {
            return c.getCount();
        } finally {
            c.close();
            db.close();
        }
    }


    static JSONObject getJSON(Context context) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus = 1 ORDER BY idcamion", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id_camion", c.getString(0));
                    json.put("sindicato", c.getString(1));
                    json.put("empresa", c.getString(2));
                    json.put("propietario", c.getString(3));
                    json.put("operador", c.getString(4));
                    json.put("licencia", c.getString(5));
                    json.put("economico", c.getString(6));
                    json.put("placas_camion", c.getString(7));
                    json.put("placas_caja", c.getString(8));
                    json.put("marca", c.getString(9));
                    json.put("modelo", c.getString(10));
                    json.put("ancho", c.getString(11));
                    json.put("largo", c.getString(12));
                    json.put("alto", c.getString(13));
                    json.put("gato", c.getString(14));
                    json.put("extension", c.getString(15));
                    json.put("disminucion", c.getString(16));
                    json.put("cu_real", c.getString(17));
                    json.put("cu_pago",c.getString(18));
                    json.put("vigencia",c.getString(20));
                    JSON.put(i + "", json);
                    i++;
                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
        }

        return JSON;
    }


 static void deleteAll(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        ContentValues data = new ContentValues();
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus = 1 ORDER BY idcamion", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {
                    data.clear();
                    data.put("estatus", "0");
                    db.update("camiones", data, "idcamion = '" + c.getInt(c.getColumnIndex("idcamion")) + "'", null);
                } while (c.moveToNext());
            }
        } finally {
            db.close();
        }
    }

    ArrayList<String> getArrayListIdInactivo() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * from camiones WHERE estatus_camion = 0 ORDER BY idcamion ASC", null);
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

    ArrayList<String> getArrayListDescripcionesInactivo() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM camiones WHERE estatus_camion = 0 ORDER BY idcamion ASC", null);
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
}
