package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mayank on 6/4/16.
 */
public class DbHelper extends SQLiteOpenHelper implements DbConstants {

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db, TBL_CONTACTS, COL_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTable(db, TBL_CONTACTS);
    }

    public void createTables(SQLiteDatabase db,String tableName, String[] columns) {
        String columnString="";

        for(int i=0;i<columns.length;i++){
            columnString+= columns[i]+" , ";
        }
        columnString=columnString.substring(0,columnString.length()-2);

        String SQL_CREATE_TABlE = " CREATE TABLE "+tableName+
                " ( "
                +columnString+
                " ); ";
        db.execSQL(SQL_CREATE_TABlE);
        Log.d("TBL CREATED", SQL_CREATE_TABlE);
    }


    public void deleteTable(SQLiteDatabase db,String tableName) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
    }
}
