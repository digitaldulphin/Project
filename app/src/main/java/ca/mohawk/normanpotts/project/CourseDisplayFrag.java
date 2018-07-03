package ca.mohawk.normanpotts.project;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Norman on 2018-04-17.
 */

/** CourseDisplayFrag
 *  A fragment that handles the display of a selected program content.
 *  Has a textview, spinner, and expandable list to present the information about the
 *  program's courses by semester.
 */
public class CourseDisplayFrag extends Fragment {
    private int Selectedid;
    MySQLiteOpenHelper  mydbhelper;/// Initialize MySQLiteOpenHelper mydbhelper which helps with handling the database.
    ExpandableListView explist;
    ArrayList<Course> theseCourses;
    public CourseDisplayFrag() { /* Empty Constructor */ }


    /** Method onCreateView
     *  Sets up the CourseDisplayFrag. Determines which title to display, and which program number
     *  was chosen. Then it makes a database call for the specified program. Then it builds the spinner
     *  and populates the ExpandableList.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View myView = inflater.inflate(R.layout.fragment_coursedisplay, container, false );
        TextView tv = (TextView) myView.findViewById(R.id.tv_ProgramName);
        Selectedid = getArguments().getInt("GivenID");
        String name = "";  int programNumber = 0;
        switch(Selectedid) {
            case 1:
                name = "Computer Systems Technician - Network Systems  455 \n ";  programNumber=455;
                break;
            case 2:
                name = "Computer Systems Technology - Network Engineering and Security Analyst  555 \n ";  programNumber=555;
                break;
            case 3:
                name = "Computer Systems Technician - Software Support  558 \n ";  programNumber=558;
                break;
            case 4:
                name = "Computer Systems Technology - Software Development  559 \n ";  programNumber=559;
                break;
        }
        //// Retrive all courses from table with same program as selected.
        mydbhelper = new MySQLiteOpenHelper(getActivity());
        SQLiteDatabase db = mydbhelper.getReadableDatabase();
        String[] projection = { };
        Cursor cursor = db.query( "courses", projection, "program = "+programNumber+"", null, null, null, null );
        theseCourses = new ArrayList<Course>();
        ArrayList<String> semesterArray = new ArrayList<>();
        while(cursor.moveToNext()) {
            int _id =   cursor.getInt( cursor.getColumnIndex("_id"))  ;
            int program =  cursor.getInt( cursor.getColumnIndex("program")   );
            int semesterNum = cursor.getInt( cursor.getColumnIndex("semesterNum")   );
            if(!semesterArray.contains(""+semesterNum))
            { semesterArray.add(""+semesterNum); } /*Collect semesters numbers to show in spinner. */
            String courseCode = cursor.getString(cursor.getColumnIndex("courseCode"));
            String courseTitle = cursor.getString(cursor.getColumnIndex("courseTitle"));
            String courseDescription = cursor.getString(cursor.getColumnIndex("courseDescription"));
            String courseOwner = cursor.getString(cursor.getColumnIndex("courseOwner"));
            int optional = cursor.getInt( cursor.getColumnIndex("optional")   );
            int hours = cursor.getInt( cursor.getColumnIndex("hours")   );
            Course course = new Course( _id, program , semesterNum, courseCode, courseTitle, courseDescription, courseOwner, optional, hours);
            theseCourses.add(course);
        }
        cursor.close();
        explist =  (ExpandableListView) myView.findViewById(R.id.expandableListView);
        tv.setText(name);/// Set Program name selected.
        Collections.sort(semesterArray);
        ArrayAdapter<String> mySpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,  semesterArray  );
        Spinner semesterSpinner = (Spinner) myView.findViewById(R.id.SemesterSpinner);
        semesterSpinner.setAdapter(mySpinnerAdapter);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id ) {
                int semester = (int) id +1;
                System.out.println("Semester: "+semester); ///Set expandeble list.
                HashMap<String, List<String>> listDataChild = prepareDataChild(theseCourses, semester);
                List<String> listDataHeader = new ArrayList<String>( listDataChild.keySet() );
                MyExpandableListAdapter listAdapter = new MyExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
                explist.setAdapter(listAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Nothing selected");
            }
        });
        semesterSpinner.setPrompt("Select a Semester");
        return myView;
    }/* End of onCreateView. */


    /** Method prepareDataChild
     *  Produces the HashMap for the ExpandableListAdapter based off the cousre list and selected semester.
     * @param clist
     * @param semst
     * @return
     */
    private HashMap<String, List<String>> prepareDataChild(ArrayList<Course> clist, int semst) {
        HashMap<String, List<String>> lstdatchild = new HashMap<String, List<String>>();
        List<String> lstdataHeader = new ArrayList<String>();
        for(int i =0; i < clist.size(); i++) {
            if( clist.get(i).semesterNum() == semst ) {
                String title = ""+clist.get(i).courseCode()+" "+clist.get(i).courseTitle()+"";
                if( !lstdataHeader.contains(title) ) {
                    List<String> lst =  new ArrayList<String>();
                    lst.add(clist.get(i).courseDescription());
                    lst.add("Hours: "+clist.get(i).hours());
                    String optional =  ""+(  clist.get(i).optional() == 1  ?   "Yes"  : "No" );
                    lst.add("Optional: "+optional);
                    lstdatchild.put(title, lst);
                }
            }
        }
        return lstdatchild;
    }/* End of prepareDataChild. */


}/* End of CourseDisplayFrag. */
