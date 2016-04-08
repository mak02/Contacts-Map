package database;

/**
 * Created by mayank on 6/4/16.
 */
public interface DbConstants {

    String DB_NAME = "contactsmap.db";
    int DB_VERSION = 2;

    String INTEGER_PRIMARY_KEY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    String INTEGER = " INTEGER ";
    String TEXT = " TEXT ";
    String REAL =  " REAL ";
    String DEFAULT = " DEFAULT ";






    String TBL_CONTACTS = "tbl_contacts";
    String COL_CONTACTS_NAME = "col_contacts_name";
    String COL_CONTACTS_EMAIL = "col_contacts_email";
    String COL_CONTACTS_PHONE = "col_contacts_phone";
    String COL_CONTACTS_OFFICE_PHONE = "col_contacts_officePhone";
    String COL_CONTACTS_LATITUDE = "col_contacts_latitude";
    String COL_CONTACTS_LONGITUDE = "col_contacts_longitude";

    String COL_CONTACTS[] = new String[] {
            COL_CONTACTS_NAME + TEXT,
            COL_CONTACTS_EMAIL + TEXT,
            COL_CONTACTS_PHONE + INTEGER,
            COL_CONTACTS_OFFICE_PHONE + INTEGER,
            COL_CONTACTS_LATITUDE + TEXT,
            COL_CONTACTS_LONGITUDE + TEXT
    };

}
