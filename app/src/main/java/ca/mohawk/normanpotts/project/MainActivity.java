package ca.mohawk.normanpotts.project;

/** Android Project: Mohawk Course Browser.
 *
 * The project was updated. There is a navigation drawer that presents a listview of of all the
 * programs retrieved from the html parse. A loading dialog fragment is displayed to show the app
 * is still working on getting the information. When done it displays a default fragment instructing
 * the user to open the drawer. When a program is selected, it's name is display on a new fragment.
 *
 * What still needs to be finished is showing the course infomation for each program.
 *
 *
 *  Date: 2018-April-17
 *  Time: 10:54 pm
 *
 * Statement of Authorship.
 * I, Norman Potts, 000344657 certify that this material is my original work. No other person's work
 * has been used without due acknowledgement.
 *
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





/** Class MainActivity
 *
 *      This class is responsible for a couple of things, such as...
 *           Managing the layout of the mainactivity.
 *           Retiving an html file about mohawk's programs from a url.
 *           Organizing and storing data in a database.
 *           Displaying a loading message when necessary.
 *           Displaying a navigation drawer.
 *           Retriving program course information.
 *           Handling how a program courses are displayed.
 *
 */
public class MainActivity extends AppCompatActivity {

    // Initialize MySQLiteOpenHelper mydbhelper which helps with handling the database.
    MySQLiteOpenHelper  mydbhelper;
    // Initalize the DrawerLayout
    private DrawerLayout mDrawerLayout;
    // Initalize the NavigationView
    private NavigationView navigationView;
    // Intitalize the Loading Fragment
    LoadingFrag loadingFrag = new LoadingFrag();
    // Initialize the Fragment Manager
    FragmentManager fragManager = getFragmentManager();
    // Initialize the default fragment.
    DefualtFrag defualtFrag = new DefualtFrag();
    // Initialize the CourseDisplay fragment
    CourseDisplayFrag courseDisplay = new CourseDisplayFrag();



    /** Method onCreate
     *      Purpose: opens up the main activity layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydbhelper = new MySQLiteOpenHelper(this);
        new CollectPrograms().execute("https://www.mohawkcollege.ca/programs/search");



        //final TextView tv_ProName = (TextView) findViewById(R.id.tv_ProName);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

    }// End of onCreate MainActivity.






    /** Class CollectPrograms
     *      Purpose: This class will make use of AsyncTask and retrieve an web page as a string.
     *
     */
    private class CollectPrograms extends AsyncTask<String, Void, String> {
        protected String doInBackground(String ... urls ) {
            String htmlstr = null;
            try {
                URL url = null;
                url = new URL(urls[0]);
                InputStream stream = url.openConnection().getInputStream();
                BufferedReader br = new BufferedReader( new InputStreamReader(stream));
                String line;
                while((line = br.readLine()) != null)
                {
                    htmlstr += line;
                }
            }catch (MalformedURLException e) {
            }catch (IOException e) { }
            return htmlstr;
        }
        protected void onPostExecute(String result) {
            if( result != null )
            {
                parse_out_program_and_numbers_plz(result);
            }
        }
        protected  void onPreExecute(){
            //Display loading fragment...
            loadingFrag.show(getSupportFragmentManager(), "myDialogFragment" );
        }
    }//End of private class CollectPrograms





    /** Method parse_out_program_and_numbers_plz
     *  With the GivenHtml as a string, parse out the program names, and links using regular
     *  expressions and splits.
     * @param GivenHtml
     */
    public void parse_out_program_and_numbers_plz(String GivenHtml )
    {
        String strArr[] = GivenHtml.split("<");
        //Html is split on < so begining of tag.
        ArrayList<String> hrefCollection = new ArrayList<String>();
        String pattern = "div class=\"jsfilter-content-container\">";
        Pattern r = Pattern.compile(pattern);
        int counter = 0;
        String Chop = "";
        boolean foundMYMarker = false;
        for( int i = 0; i < strArr.length; i++) {
            String ln = strArr[i];

            if(foundMYMarker == false )
            {
                Matcher m_marker = r.matcher(ln);
                if (m_marker.find())
                {    foundMYMarker = true;  }
            }
            else
            {
                String pattern_4_ahref = ".?a href=\"/programs/.?";
                Pattern pat_4_ahref = Pattern.compile(pattern_4_ahref);
                Matcher m_ahref = pat_4_ahref.matcher(ln);
                if(m_ahref.find())
                {
                    hrefCollection.add(ln);
                    counter++;
                }
            }
        }
        if (counter == 160)
        {
            String[][] names_And_Links = new String[160][2];
            for(int item = 0; item < hrefCollection.size(); item++)
            {
                //Parse out link and name.
                String Link = "";
                String Name = "";
                String disItem = hrefCollection.get(item);
                //Looks like this...
                //a href="/programs/skilled-trades/welder-456a">Welder - 456A
                String pattern_4_link = "programs/.+\" *>";
                Pattern pat_4_link = Pattern.compile(pattern_4_link);
                Matcher m_link = pat_4_link.matcher(disItem);
                if (m_link.find())
                {
                    String[] x = (m_link.group(0)).split("\"");
                    Link = x[0];
                }
                //Split on >
                String[] z = disItem.split(">");
                Name = z[1];
                names_And_Links[item][0] = Name;
                names_And_Links[item][1] = Link ;
            }

            //Now have an array list of programs and links
            //Begin next method which will that that list and
            //To create a database and find the course information.

            PlaceStringArrayInDatabase(names_And_Links);
        }
        else
        {
            //The number of expected programs did not match the number retrieved.
            //Display error message..
            System.err.println(counter);
            System.err.println(hrefCollection.size());
        }
    }//End of method parse_out_program_and_numbers_plz()





    /** Method PlaceStringArrayInDatabase
     *  Loads the 2D string array of all the programs and links in to the database.
     * @param names_And_Links
     */
    private void PlaceStringArrayInDatabase(String[][] names_And_Links) {

        SQLiteDatabase db = mydbhelper.getWritableDatabase();
        db.delete("mytable", null, null);/// Delete all from mytable, basically clear table

        for( int program = 0; program < names_And_Links.length; program++)
        {
            //Insert a record into the database.
            String ProgramName = names_And_Links[program][0];
            String Link = names_And_Links[program][1];


            ContentValues values = new ContentValues();

            values.put("ProgramName", ProgramName);
            values.put("Link", Link);

            long nextRowID = db.insert("mytable", null, values);
        }
        System.out.println("Info has been placed in Database.");
        RemoveLoadingFragment();
        loadMyDrawerPlz();


    }// End of method PlaceStringArrayInDatabase()


    CourseDisplayFrag courseDisplayFrag = new CourseDisplayFrag();



    /** Method RemoveLoadingFragment
     *      This method removes loading fragment and places the course display but empty.
     */
    public void RemoveLoadingFragment()
    {
        System.out.println("Close dialog");
        loadingFrag.dismiss();

        Bundle myBundle = new Bundle();
        defualtFrag.setArguments(myBundle);
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, defualtFrag);
        fragmentTransaction.commit();


    }/// End of method RemoveLoadingFragment





    /** Method loadMyDrawerPlz()
     *  This method will load the drawer with the info from the database.
     */
    public void loadMyDrawerPlz()
    {
        SQLiteDatabase db = mydbhelper.getReadableDatabase();
        String[] projection = { "_id, ProgramName, Link"};
        Cursor cursor = db.query( "mytable", projection, null, null, null, null, null );
        ArrayList<String> programlist = new ArrayList<>();
        while(cursor.moveToNext()) {
            String ProgramName = cursor.getString(cursor.getColumnIndex( "ProgramName") );
            String Link = cursor.getString(cursor.getColumnIndex("Link") );

            //Process results.
            String results = ""+ProgramName+" \n ";
            programlist.add(results);
        }

        cursor.close();

        System.out.println("programlist.size(): "+programlist.size());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , programlist );
        final ListView listView = (ListView) navigationView.findViewById(R.id.listmenu);
        listView.setAdapter(adapter);

         /* Set listener for when items get touched. */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                mDrawerLayout.closeDrawers();
                long _id = id+1;
                // Now use _id to retrive the link for a selected item...
                showCourse( _id );
                showCoursesPlz( _id );


            }
        });
    }// End of method loadMyDrawerPlz


    /**
     *
     */
    public void showCourse(long GivenID)
    {

        String idstr = ""+GivenID+"";
        SQLiteDatabase db = mydbhelper.getReadableDatabase();
        String[] projection = { "_id, ProgramName, Link"};
        Cursor cursor = db.query( "mytable", projection, "_id ="+idstr+"", null, null, null, null );
        String Link = "";
        String Name = "";

        while(cursor.moveToNext()) {
            Link = cursor.getString(cursor.getColumnIndex("Link") );
            Name = cursor.getString(cursor.getColumnIndex("ProgramName") );
        }
        cursor.close();


        if(courseDisplay.isVisible())
        {
            TextView tv = findViewById(R.id.tv_ProgramName);
            tv.setText(Name);
        }
        else
        {

            Bundle myBundle = new Bundle();
            myBundle.putString("ProgramName", Name);
            courseDisplay.setArguments(myBundle);
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, courseDisplay);
            fragmentTransaction.commit();
        }

    }





    /** Method showCoursesPlz
     *      Search DB for record with this GivenID
     * @param GivenID
     */
    public void showCoursesPlz(long GivenID )
    {
        String Full_Link = "";
        retrieveSelectedProgramInfo(Full_Link);
    }// End of method showCoursesPlz





    /** Method retrieveSelectedProgramInfo
     *      Retrives the course info of a program based on the given Url link.
     *      Course info includes
     *          Course names
     *          Course codes
     *          Course description
     *      Based on the selected semester too!
     *
     * @param Full_Link
     */
    public void retrieveSelectedProgramInfo(String Full_Link)
    {
        Full_Link = Full_Link +"#edit-group-pos";

        int Semester = 1;
        new CollectCourseInfo().execute(Full_Link);

    }
    private class CollectCourseInfo extends AsyncTask<String, Void, String> {
        protected String doInBackground(String ... urls ) {
            String htmlstr = null;
            try {
                URL url = null;
                url = new URL(urls[0]);
                InputStream stream = url.openConnection().getInputStream();
                BufferedReader br = new BufferedReader( new InputStreamReader(stream));
                String line;
                while((line = br.readLine()) != null)
                {
                    htmlstr += line;
                }
            }catch (MalformedURLException e) {
            }catch (IOException e) { }
            return htmlstr;
        }
        protected void onPostExecute(String result) {
            //After Execute...
            if( result != null )
            {
                parseOut_Courses(result);
            }
        }
        protected  void onPreExecute(){
            //Before execute...
        }
    }//End of private class CollectPrograms
    public void parseOut_Courses(String htmlstr)
    {
        //Marker for a semester "semester course-container semester semester-"

        //Cut into blocks for each Marker until another market is found or end.
        String tagsArr[] = htmlstr.split("<");
        //Html is split on < so begining of tag.
        ArrayList<String> hrefCollection = new ArrayList<String>();
        //String pattern = "div +class *= *\" *course-container +semester +semester-[0-9]+";
        String pattern = "https://myssb.mohawkcollege.ca:";
        Pattern r = Pattern.compile(pattern);

        int Counter_OF_SemesterMArkers = 0;

        for(int i = 0; i < tagsArr.length; i++ )
        {
            String TAG = tagsArr[i];
            Matcher m_marker = r.matcher(TAG);
            if (m_marker.find()) {
                Counter_OF_SemesterMArkers++;
                System.out.println(TAG);

            }
            else
            {

            }

        }// End of for loop.
        System.out.println(Counter_OF_SemesterMArkers);


    }///End of method parseOut_Courses


}// End of class MainActivity






