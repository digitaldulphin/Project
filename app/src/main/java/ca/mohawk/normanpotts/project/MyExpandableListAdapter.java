package ca.mohawk.normanpotts.project;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Norman on 2018-07-02.
 */

public class MyExpandableListAdapter implements ExpandableListAdapter {
    private Context context;
    private List<String> dataHeader;
    private HashMap<String, List<String> > datachild;

    public MyExpandableListAdapter(Activity activity, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {

        this.context = activity;
        this.dataHeader = listDataHeader;
        this.datachild = listDataChild;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return this.dataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        String key = dataHeader.get(i);
        List<String> programChildren = datachild.get(key);
        return programChildren.size();
    }

    @Override
    public List<String> getGroup(int i) {
        String key = dataHeader.get(i);
        List<String> programChildren = datachild.get(key);
        return programChildren;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        String key = dataHeader.get(groupPosition);
        String child = datachild.get(key).get(childPosition);
        return child;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /** getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent):
     *  This method is used when we need to create our group or parent View .
     *
     * @param groupPosition This is the first parameter that tells the position for the parent or group of the child. The function returns an integer type value.
     * @param isExpanded This is the second parameter that returns Boolean value. It returns true if the current group is expanded and false if it’s not.
     * @param convertView This is the fourth parameter that returns View which is used to set the layout for group items.
     * @param parent This is the fifth or last parameter that is used to set the view for the parent or group item.  The eventual parent of this new View
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_items, null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.heading);
        heading.setText(dataHeader.get(groupPosition).trim());
        return convertView;
    }

    /** getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent):
     * This method is  used when we need to create a child View means a child item for a parent or group.
     *
     * @param groupPosition This is the first parameter that tells the position for the parent(group) of the current child. The function returns an integer type value.
     * @param childPosition This is the second parameter that tells the position for current child item of the parent.
     * @param isLastChild This is the third parameter that returns Boolean value. It returns true if the current child item is the last child within its group and false if it’s not.
     * @param convertView This is the fourth parameter that returns View which is used to set the layout for child items.
     * @param parent parent is the fifth or last parameter that used to set the view for the parent or group item.  The eventual parent of this new View.
     * @return
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String detailInfo = getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_items, null);
        }
        TextView childItem = (TextView) convertView.findViewById(R.id.childItem);
        childItem.setText(detailInfo.trim());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /** This method is called when a group is collapsed.
     *
     * @param groupPosition
     */
    @Override
    public void onGroupExpanded(int groupPosition) {}

    /** This method is called when a group is expanded. */
    @Override
    public void onGroupCollapsed(int i) { }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
