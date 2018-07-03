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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
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
    MySQLiteOpenHelper  mydbhelper;/// Initialize MySQLiteOpenHelper mydbhelper which helps with handling the database.
    private DrawerLayout mDrawerLayout;/// Initalize the DrawerLayout
    private NavigationView navigationView;/// Initalize the NavigationView
    LoadingFrag loadingFrag = new LoadingFrag();/// Intitalize the Loading Fragment
    FragmentManager fragManager = getFragmentManager();/// Initialize the Fragment Manager
    DefualtFrag defualtFrag = new DefualtFrag();/// Initialize the default fragment.
    CourseDisplayFrag courseDisplay = new CourseDisplayFrag();/// Initialize the CourseDisplay fragment
    CourseDisplayFrag courseDisplayFrag = new CourseDisplayFrag();




    /** Method onCreate
     *      Purpose: opens up the main activity layout.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydbhelper = new MySQLiteOpenHelper(this);


        new CollectCourse().execute("https://csunix.mohawkcollege.ca/~geczy/mohawkprograms.php");



        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }// End of onCreate MainActivity.







    /** Class CollectCourse*****************************************************************************************************************************************************************************
     *      Purpose: This class will make use of AsyncTask and retrieve this courses.
     */
    private class CollectCourse extends AsyncTask<String, Void, String> {
        protected String doInBackground(String ... urls ) {
            String apiStr = "";
            try {
                URL url = null; url = new URL(urls[0]);
                InputStream stream = url.openConnection().getInputStream();
                BufferedReader br = new BufferedReader( new InputStreamReader(stream));
                String line; int c; StringBuilder response = new StringBuilder();
                while((c = br.read()) != -1) { response.append( (char) c ) ; }
                apiStr = response.toString();
            }catch (MalformedURLException e) {  }catch (IOException e) {  }
            return apiStr;
        }
        protected void onPostExecute(String result) {     if( result != null ) { process_apiStr(result); } }
        protected  void onPreExecute() {  loadingFrag.show(getSupportFragmentManager(), "myDialogFragment" ); /* Display loading fragment... */  }
        /** Method process_apistr()
         *  With the json string apiStr, parse out the program name, program description, program number,
         *  and course information.
         * @param apiStr
         */
        public void process_apiStr(String  apiStr ) {
            ArrayList<Course> AllCourses = new ArrayList<Course>();
            try {  /// Try to parse json api str into an arraylist of courses.
                JSONArray arr = new JSONArray(apiStr);
                for(  int i = 0; i < arr.length(); i++  ) {
                    int _id =  Integer.parseInt(   arr.getJSONObject(i).getString("_id")   );
                    int program = Integer.parseInt(    arr.getJSONObject(i).getString("program")   );
                    int semesterNum = Integer.parseInt(    arr.getJSONObject(i).getString("semesterNum")   );
                    String courseCode = arr.getJSONObject(i).getString("courseCode");
                    String courseTitle = arr.getJSONObject(i).getString("courseTitle");
                    String courseDescription = arr.getJSONObject(i).getString("courseDescription");
                    String courseOwner = arr.getJSONObject(i).getString("courseOwner");
                    int optional = Integer.parseInt(   arr.getJSONObject(i).getString("optional")   );
                    int hours = Integer.parseInt(   arr.getJSONObject(i).getString("hours")   );
                    Course course = new Course( _id, program , semesterNum, courseCode, courseTitle, courseDescription, courseOwner, optional, hours);
                    AllCourses.add(course);

                }
                PlaceCoursesInDB(AllCourses);
                loadMyDrawerPlz();
            } catch (JSONException e) {  e.printStackTrace();  } catch ( NumberFormatException  n ) {  n.printStackTrace();  }
        }//End of method parse_out_program_and_numbers_plz()
        /** Method PlaceCoursesInDB
         *  Puts courses in courses table.
         * @param allCourses
         */
        private void PlaceCoursesInDB(ArrayList<Course> allCourses) {
            SQLiteDatabase db = mydbhelper.getWritableDatabase();
            db.delete("courses", null, null);/// Delete all from mytable, basically clear table
            for( int c = 0; c < allCourses.size(); c++) {
                // Insert a record into the database.
                Course course = allCourses.get(c);
                ContentValues values = new ContentValues();
                values.put("_id", allCourses.get(c)._id());
                values.put("program", allCourses.get(c).program());
                values.put("semesterNum", allCourses.get(c).semesterNum());
                values.put("courseCode", allCourses.get(c).courseCode());
                values.put("courseTitle", allCourses.get(c).courseTitle());
                values.put("courseDescription", allCourses.get(c).courseDescription());
                values.put("courseOwner", allCourses.get(c).courseOwner());
                values.put("optional", allCourses.get(c).optional());
                values.put("hours", allCourses.get(c).hours());
                long nextRowID = db.insert("courses", null, values);
            }
            System.out.println("Courses array has been placed in Database.");
        }
    }/** End of private class CollectPrograms***********************************************************************************************************************************************************/










    /** Method loadMyDrawerPlz()
     *  This method will load the drawer with the info from the database.
     */
    public void loadMyDrawerPlz() {

        ArrayList<String> programlist = new ArrayList<>();

        String name = "Computer Systems Technician - Network Systems  455 \n ";
        programlist.add(name);
        name = "Computer Systems Technology - Network Engineering and Security Analyst  555 \n ";
        programlist.add(name);
        name = "Computer Systems Technician - Software Support  558 \n ";
        programlist.add(name);
        name = "Computer Systems Technology - Software Development  559 \n ";
        programlist.add(name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , programlist );
        final ListView listView = (ListView) navigationView.findViewById(R.id.listmenu);
        listView.setAdapter(adapter);
            /* Set listener for when items get touched. */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                mDrawerLayout.closeDrawers();
                int _id = (int) id + 1;
                // Now use _id to retrive the Codes for a selected item...
                showProgramDetails(_id);
            };
        });
        RemoveLoadingFragment();
    }/// End of method loadMyDrawerPlz





    /** Method showProgramDetails
     *  show Program Detailsthat are in the db.
     *
     */
    private void showProgramDetails(int GivenID) {
        if(courseDisplayFrag.isVisible()) {
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.remove(courseDisplay).commit();
            Bundle myBundle = new Bundle();
            myBundle.putInt("GivenID", GivenID);
            courseDisplay.setArguments(myBundle);
            fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, courseDisplay);
            fragmentTransaction.commit();
        } else {
            Bundle myBundle = new Bundle();
            myBundle.putInt("GivenID", GivenID);
            courseDisplayFrag.setArguments(myBundle);
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, courseDisplayFrag);
            fragmentTransaction.commit();
        }
    }/// End of method showCourse





    /** Method RemoveLoadingFragment
     *      This method removes loading fragment and places the course display but empty.
     */
    public void RemoveLoadingFragment() {
        System.out.println("Close dialog");
        loadingFrag.dismiss();
        Bundle myBundle = new Bundle();
        defualtFrag.setArguments(myBundle);
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, defualtFrag);
        fragmentTransaction.commit();
    }/// End of method RemoveLoadingFragment





}// End of class MainActivity






