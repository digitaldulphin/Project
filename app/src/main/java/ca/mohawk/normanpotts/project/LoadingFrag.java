package ca.mohawk.normanpotts.project;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/** Class LoadingFrag
 *    Displayed when ever the loading screen is needed.
 */
public class LoadingFrag extends DialogFragment {
    public LoadingFrag() {  /* Required empty public constructor. */  }
    @Override
    public Dialog onCreateDialog(Bundle savedIstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View myView = inflater.inflate( R.layout.fragment_loading, null , false);
        // Inflate the layout for this fragment
        builder.setView(myView);
        return builder.create();
    }
}/// End of LoadingFrag
