package lementProApp.holder;

/**
 * Created by Saadi on 14/07/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import lementProApp.R;
import lementProApp.model.Checkpoints;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

/**
 * Created by User on 08.07.2015.
 */
public class CustomCheckpointsAdapter extends BaseAdapter implements View.OnClickListener {

    /**
     * ******** Declare Used Variables ********
     */
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    Context cntx;
    Checkpoints tempValues = null;
    int i = 0;
    String cookieVal = "";

    /**
     * **********  CustomAdapter Constructor ****************
     */
    public CustomCheckpointsAdapter(Activity a, ArrayList<Checkpoints> d, Resources resLocal, String val) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
        cookieVal = val;
        try {
            /***********  Layout inflator to call external xml layout () ***********/
            inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ***** What is the size of Passed Arraylist Size ***********
     */
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * ****** Create a holder Class to contain inflated xml file elements ********
     */
    public static class ViewHolder {

        public CheckBox isClosed;
        public TextView Name;
        public TextView startDateTime;
        public TextView duration;

    }

    /**
     * *** Depends upon data size called for each row , Create each ListView row ****
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.checkpoints_layout, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.isClosed = (CheckBox) vi.findViewById(R.id.chkClosed);
                holder.Name = (TextView) vi.findViewById(R.id.txtName);
                holder.startDateTime = (TextView) vi.findViewById(R.id.txtStartDateTime);
                holder.duration = (TextView) vi.findViewById(R.id.txtDuration);


                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            }
            if (data.size() > 0) {
                holder = (ViewHolder) vi.getTag();

                /***** Get each Model object from Arraylist ********/
                tempValues = null;
                tempValues = (Checkpoints) data.get(position);
                holder.isClosed.setChecked(tempValues.isClosed);
                holder.Name.setText(tempValues.name);
                holder.startDateTime.setText(tempValues.startDateTime);
                String duration = "";
                Log.v("CheckPoint:",tempValues.name);
                Log.v("CheckPoint:",tempValues.startDateTime);

                if (tempValues.duration == 0) {
                    duration = "No";
                } else if (tempValues.duration < 60) {
                    duration = Integer.toString(tempValues.duration) + "m";
                } else {
                    duration = Integer.toString(tempValues.duration % 60) + "h";
                }
                holder.duration.setText(duration);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }
}