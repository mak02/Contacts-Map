package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by mayank on 6/4/16.
 */
public class DbMethods implements DbConstants {

    Context context;
    DbHelper dbHelper;
    SQLiteDatabase db;
    String TAG = "DATABASE";

    public DbMethods(Context context){
        this.context=context;
        dbHelper=new DbHelper(context);
        db=dbHelper.getWritableDatabase();
    }

    public long insertContacts(String name, String email, String phone, String officePhone, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTACTS_NAME, name);
        values.put(COL_CONTACTS_EMAIL, email);
        values.put(COL_CONTACTS_PHONE, phone);
        values.put(COL_CONTACTS_OFFICE_PHONE, officePhone);
        values.put(COL_CONTACTS_LATITUDE, latitude);
        values.put(COL_CONTACTS_LONGITUDE, longitude);
        long id = db.insert(TBL_CONTACTS, null, values);
        Log.d(TAG + " CONTACTS", values.toString());
        return id;
    }

    public Cursor queryContacts(String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy){
        return  db.query(TBL_CONTACTS,columns,selection,selectionArgs,groupBy,having,orderBy);
    }

    public void deleteContacts(String[] phone) {
        db.delete(TBL_CONTACTS, COL_CONTACTS+" = ? ",phone);
    }

    public void deleteAllContacts() {
        db.delete(TBL_CONTACTS, null,null);
    }

}
