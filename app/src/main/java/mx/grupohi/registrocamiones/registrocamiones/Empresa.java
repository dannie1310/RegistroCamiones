package mx.grupohi.registrocamiones.registrocamiones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by DBENITEZ on 03/04/2017.
 */

public class Empresa {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer idempresas;
    String descripcion;


    Empresa(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("empresas", null, data) > -1;
        if (result) {
            this.idempresas = Integer.valueOf(data.getAsInteger("idempresa"));
            this.descripcion = data.getAsString("descripcion");

        }
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM empresas");
        db.close();
    }

    ArrayList<String> getArrayListNombres() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM empresas ORDER BY descripcion ASC", null);
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
        Cursor c = db.rawQuery("SELECT * FROM empresas ORDER BY descripcion ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("idempresas")));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("idempresas")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("idempresas")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    public Empresa find(String descripcion){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM empresas WHERE descripcion = '"+descripcion+"'", null);
        try{
            if(c != null && c.moveToFirst()){
                this.idempresas = c.getInt(c.getColumnIndex("idempresas"));
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
