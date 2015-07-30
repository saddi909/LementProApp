package lementProApp.holder;

/**
 * Created by User on 10.07.2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import lementProApp.R;
import lementProApp.model.Task;

import java.util.ArrayList;

import lementProApp.activity.TasksList;

/**
 * Created by User on 08.07.2015.
 */
public class CustomTaskAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Task tempValues=null;
    int i=0;
    String type = "";

    /*************  CustomAdapter Constructor *****************/
    public CustomTaskAdapter(Activity a, ArrayList<Task> d,Resources resLocal,String type) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        type = type;
         try {
             /***********  Layout inflator to call external xml layout () ***********/
             inflater = (LayoutInflater) activity.
                     getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        public TextView title;
        public TextView person;
        public TextView date;
        public TextView message;
        public ImageView star;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.task_layout, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.title = (TextView) vi.findViewById(R.id.txtTitle);
            holder.person=(TextView)vi.findViewById(R.id.txtPerson);
            holder.date=(TextView)vi.findViewById(R.id.txtDate);
            holder.message=(TextView)vi.findViewById(R.id.txtMessage);
            holder.star = (ImageView) vi.findViewById(R.id.imgStarIcon);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        if(data.size()>0) {
            holder = (ViewHolder) vi.getTag();

            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (Task) data.get(position);
            holder.title.setTag(R.string.FirstTag, tempValues.isOutOfDate);
            holder.title.setTag(R.string.SecondTag, tempValues.isClosed);
            holder.star.setTag(tempValues.isMarked);

            if(type.equals("3")){
                holder.date.setTag("GONE");
                holder.message.setTag("GONE");
                holder.person.setTag("GONE");
            }else{
                holder.date.setTag("VISIBLE");
                holder.message.setTag("VISIBLE");
                holder.person.setTag("VISIBLE");
            }


            if((Boolean)holder.title.getTag(R.string.FirstTag)){
                holder.title.setTextColor(Color.parseColor("#FF0000"));
            }else{
                holder.title.setTextColor(Color.parseColor("#000000"));
            }

            if((Boolean)holder.title.getTag(R.string.SecondTag)){
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.LINEAR_TEXT_FLAG);
            }

            if((Boolean)holder.star.getTag()){
                holder.star.setImageResource(R.drawable.gold_star);
            }else{
                holder.star.setImageResource(R.drawable.star_icon);
            }
                /*if(tempValues.isClosed){

                }*/
            //}

            if(holder.date.getTag().toString().equals("GONE"))
                holder.date.setVisibility(View.GONE);
            else
                holder.date.setVisibility(View.VISIBLE);


            if(holder.person.getTag().toString().equals("GONE"))
                holder.person.setVisibility(View.GONE);
            else
                holder.person.setVisibility(View.VISIBLE);


            if(holder.message.getTag().toString().equals("GONE"))
                holder.message.setVisibility(View.GONE);
            else
                holder.message.setVisibility(View.VISIBLE);
            /************  Set Model values in Holder elements ***********/
            if (tempValues.name.length()>27) {
                holder.title.setText(tempValues.name.substring(0,27)+"...");
            }else {
                holder.title.setText(tempValues.name);
            }

            Log.v("Text:Person",tempValues.authorText);
            Log.v("Text:Date",tempValues.lastModifiedDate);
            Log.v("Text:Message",Html.fromHtml(tempValues.simplifiedMessage).toString());

            holder.person.setText(tempValues.authorText);
            holder.date.setText(tempValues.lastModifiedDate.toString());
            if (tempValues.simplifiedMessage.length()>40) {
                holder.message.setText(Html.fromHtml(tempValues.simplifiedMessage).toString().substring(0,40)+"...");
            }else {
                holder.message.setText(Html.fromHtml(tempValues.simplifiedMessage).toString());
            }

            /*holder.date.setImageResource(
                    res.getIdentifier(
                            "com.androidexample.customlistview:drawable/" + tempValues.getImage()
                            , null, null));
             */
            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener(position,tempValues.ID,tempValues.folderKey));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;
        private String mId;
        private String folderKey;
        OnItemClickListener(int position,String Id,String fKey){
            mPosition = position;
            mId = Id;
            folderKey = fKey;
        }

        @Override
        public void onClick(View arg0) {


            TasksList sct = (TasksList)activity;
            try {
                sct.onItemClick(mPosition,mId,folderKey);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

