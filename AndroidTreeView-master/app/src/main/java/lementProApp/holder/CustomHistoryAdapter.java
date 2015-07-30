package lementProApp.holder;

/**
 * Created by Saadi on 14/07/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lementProApp.model.History;
import lementProApp.R;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

/**
 * Created by User on 08.07.2015.
 */
public class CustomHistoryAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Context cntx;
    History tempValues=null;
    int i=0;
    String cookieVal = "";

    /*************  CustomAdapter Constructor *****************/
    public CustomHistoryAdapter(Activity a, ArrayList<History> d,Resources resLocal,String val) {

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

        public TextView changeDate;
        public TextView User;
        public TextView Description;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.history_layout, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.changeDate = (TextView) vi.findViewById(R.id.txtHistoryDate);
                holder.User = (TextView) vi.findViewById(R.id.txtHistoryUser);
                holder.Description = (TextView) vi.findViewById(R.id.txtHistoryDescription);

                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            }
            if (data.size() > 0) {
                /*TableRow objtblRow= (TableRow) vi.findViewById(R.id.tbHeader);
                TableRow objtblRowData= (TableRow) vi.findViewById(R.id.tbHistoryRow);

                if(position == 0){
                    objtblRow.setVisibility(View.VISIBLE);
                }else{
                    objtblRow.setVisibility(View.GONE);
                }
                if((position%2) == 0 ){
                    objtblRowData.setBackgroundColor(Color.parseColor("#F5F5F5"));
                }else{
                    objtblRowData.setBackgroundColor(Color.parseColor("#F1F1F1"));
                }
*/

                holder = (ViewHolder) vi.getTag();

                /***** Get each Model object from Arraylist ********/
                tempValues = null;
                tempValues = (History) data.get(position);
                holder.changeDate.setText(tempValues.changeDate);
                holder.User.setText(tempValues.authorText);
                holder.Description.setText(Html.fromHtml(tempValues.description).toString());
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

