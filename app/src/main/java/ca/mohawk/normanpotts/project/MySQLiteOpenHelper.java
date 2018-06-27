package ca.mohawk.normanpotts.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Class MySQLiteOpenHelper
 *      Helps with handling the the database.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDatabase.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SQL_DROP = "DROP TABLE IF EXISTS mytable;";
    public static final String SQL_CREATE = "CREATE TABLE mytable (_id INTEGER PRIMARY KEY, ProgramName TEXT, Link TEXT);";
            /// Drop table if it exists, then create table.


    public MySQLiteOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db )
    {   System.out.println("CREATE TABLE");
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion )
    {

    }

}// End of MySQLiteOpenHelper
