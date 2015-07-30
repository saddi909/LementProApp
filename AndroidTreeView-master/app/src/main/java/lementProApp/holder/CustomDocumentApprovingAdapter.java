package lementProApp.holder;

/**
 * Created by Saadi on 14/07/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import lementProApp.R;
import lementProApp.model.App;
import lementProApp.model.DocumentApproving;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

/**
 * Created by User on 08.07.2015.
 */
public class CustomDocumentApprovingAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Context cntx;
    DocumentApproving tempValues=null;
    int i=0;
    String cookieVal = "";

    /*************  CustomAdapter Constructor *****************/
    public CustomDocumentApprovingAdapter(Activity a, ArrayList<DocumentApproving> d, Resources resLocal, String val) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        cookieVal = val;
        try {
            /***********  Layout inflator to call external xml layout () ***********/
            inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }catch(Exception e){
            e.printStackTrace();}
    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView stageName;
        public TextView displayName;
        public TextView startDate;
        public TextView endDate;
        public TextView comment;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.document_approving_layout, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.stageName = (TextView) vi.findViewById(R.id.txtStageName);
                holder.displayName= (TextView) vi.findViewById(R.id.txtDisplayName);
                holder.startDate = (TextView) vi.findViewById(R.id.txtstartDate);
                holder.endDate= (TextView) vi.findViewById(R.id.txtendDate);
                holder.comment = (TextView) vi.findViewById(R.id.txtComment);


                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            }
            if (data.size() > 0) {
                holder = (ViewHolder) vi.getTag();

                /***** Get each Model object from Arraylist ********/
                tempValues = null;
                tempValues = (DocumentApproving) data.get(position);
                holder.stageName.setText(tempValues.stageText);
                holder.displayName.setText(tempValues.displayName);
                holder.startDate.setText(tempValues.startDate);
                holder.endDate.setText(tempValues.endDate);
                holder.comment.setText(tempValues.comment);
            }
        }catch(Exception e){
            e.printStackTrace();}
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}

