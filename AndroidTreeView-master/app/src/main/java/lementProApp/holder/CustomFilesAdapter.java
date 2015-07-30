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
import lementProApp.model.TaskFile;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

/**
 * Created by User on 08.07.2015.
 */
public class CustomFilesAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Context cntx;
    TaskFile tempValues=null;
    int i=0;
    String cookieVal = "";

    /*************  CustomAdapter Constructor *****************/
    public CustomFilesAdapter(Activity a, ArrayList<TaskFile> d, Resources resLocal, String val) {

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

        public ImageView imgDownload;
        public TextView fileName;
        public TextView fileSize;
        public TextView fileUser;
        public TextView fileDate;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.files_layout, null);
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.imgDownload = (ImageView) vi.findViewById(R.id.imgFileDownload);
                holder.fileName= (TextView) vi.findViewById(R.id.txtFileName);
                holder.fileSize = (TextView) vi.findViewById(R.id.txtFileSize);
                holder.fileUser= (TextView) vi.findViewById(R.id.txtFileUserName);
                holder.fileDate = (TextView) vi.findViewById(R.id.txtFileDate);


                /************  Set holder with LayoutInflater ************/
                vi.setTag(holder);
            }
            if (data.size() > 0) {
                holder = (ViewHolder) vi.getTag();

                /***** Get each Model object from Arraylist ********/
                tempValues = null;
                tempValues = (TaskFile) data.get(position);
                String fName = tempValues.name + "(Version:" + Integer.toString(tempValues.revision)+")" ;
                holder.fileName.setText(fName);
                holder.fileSize.setText("Size:" + tempValues.size);
                holder.fileUser.setText(tempValues.authorText);
                holder.fileDate.setText(tempValues.creationDate);
                holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String link = App.getInstance().getApplicationContext().getString(R.string.Link) + "Services/Files/DocumentFile/GetFile.do?fileId=" + tempValues.id;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(link));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getInstance().getApplicationContext().startActivity(intent);
                    }
                });

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

