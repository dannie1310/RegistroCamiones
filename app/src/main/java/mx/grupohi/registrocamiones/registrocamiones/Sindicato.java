package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Usuario on 20/01/2017.
 */

public class Sindicato {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer idsindicato;
    String descripcion;


    Sindicato(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("sindicatos", null, data) > -1;
        if (result) {
            this.idsindicato = Integer.valueOf(data.getAsInteger("idsindicato"));
            this.descripcion = data.getAsString("descripcion");

        }
        System.out.println("sindicato: "+ result + " i "+idsindicato+descripcion);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM sindicatos");
        db.close();
    }

    ArrayList<String> getArrayListNombres() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sindicatos ORDER BY descripcion ASC", null);
        if (c != null && c.moveToFirst())
            try {

                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("descripcion")));
                } else {
                    data.add("--Pendiente por asignar--");
                    data.add(c.getString(c.getColumnIndex("descripcion")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("descripcion")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }

        return data;
    }

    ArrayList<String> getArrayListId() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sindicatos ORDER BY descripcion ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("idsindicato")));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("idsindicato")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("idsindicato")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    public Sindicato find(String descripcion){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM sindicatos WHERE descripcion = '"+descripcion+"'", null);
        try{
            if(c != null && c.moveToFirst()){
                this.idsindicato = c.getInt(c.getColumnIndex("idsindicato"));
                this.descripcion = c.getString(c.getColumnIndex("descripcion"));

            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }
}
