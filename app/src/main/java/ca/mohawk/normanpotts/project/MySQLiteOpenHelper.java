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


    public static final String SQL_DROP_COURSES_TABLE = "DROP TABLE IF EXISTS courses;";
    public static final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE courses ( " +
            "tblID INTEGER PRIMARY KEY, " +
            "_id INTEGER," +
            "program INTEGER,  " +
            "semesterNum INTEGER, " +
            "courseCode TEXT, " +
            "courseTitle TEXT,  " +
            "courseDescription TEXT,  " +
            "courseOwner TEXT,  " +
            "optional INTEGER, " +
            "hours INTEGER  );";

    public MySQLiteOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db )
    {

        System.out.println("CREATE COURSES TABLE");
        db.execSQL(SQL_DROP_COURSES_TABLE);
        db.execSQL(SQL_CREATE_COURSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion )
    {

    }

}// End of MySQLiteOpenHelper
