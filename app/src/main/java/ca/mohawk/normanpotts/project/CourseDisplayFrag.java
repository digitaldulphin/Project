package ca.mohawk.normanpotts.project;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Norman on 2018-04-17.
 */

public class CourseDisplayFrag extends Fragment {


    public CourseDisplayFrag()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View myView = inflater.inflate(R.layout.fragment_coursedisplay, container, false );
        String Name = getArguments().getString("ProgramName");
        TextView tv = (TextView) myView.findViewById(R.id.tv_ProgramName);
        tv.setText(Name);
        return myView;

    }
}
