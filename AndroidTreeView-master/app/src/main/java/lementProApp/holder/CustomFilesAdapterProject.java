package lementProApp.holder;

/**
 * Created by Saadi on 14/07/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
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
import lementProApp.model.ProjectDocuments;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

/**
 * Created by User on 08.07.2015.
 */
public class CustomFilesAdapterProject extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Context cntx;
    ProjectDocuments tempValues=null;
    int i=0;
    String cookieVal = "";

    /*************  CustomAdapter Constructor *****************/
    public CustomFilesAdapterProject(Activity a, ArrayList<ProjectDocuments> d, Resources resLocal, String val) {

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

        public TextView Name;
        public TextView Description;
        public TextView Author;
        public TextView RegistrationDate;
        public TextView DeadLineDate;
        public TextView LastModified;
        public TextView ClosedDate;


    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.document_layout, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.Name= (TextView) vi.findViewById(R.id.txtDocumentName);
                holder.Description = (TextView) vi.findViewById(R.id.txtDescription);
                holder.Author= (TextView) vi.findViewById(R.id.txtAuthor);
                holder.RegistrationDate = (TextView) vi.findViewById(R.id.txtRegistrationDate);
                holder.DeadLineDate = (TextView) vi.findViewById(R.id.txtDeadline);
                holder.LastModified = (TextView) vi.findViewById(R.id.txtlastModified);
                holder.ClosedDate = (TextView) vi.findViewById(R.id.txtClosedDate);


                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            }
            if (data.size() > 0) {
                holder = (ViewHolder) vi.getTag();

                /***** Get each Model object from Arraylist ********/
                tempValues = null;
                tempValues = (ProjectDocuments) data.get(position);
                holder.Name.setText(tempValues.name);
                if(tempValues.isClosed){
                    holder.Name.setPaintFlags(holder.Name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                holder.Description.setText(tempValues.Description);
                holder.Author.setText(tempValues.author);
                holder.RegistrationDate.setText(tempValues.registrationDate);
                holder.DeadLineDate.setText(tempValues.deadline);
                holder.LastModified.setText(tempValues.lastModifiedDate);
                holder.ClosedDate.setText(tempValues.closedDate);

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

