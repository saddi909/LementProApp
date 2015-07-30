package lementProApp.holder;

/**
 * Created by Saadi on 14/07/2015.
 */

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lementProApp.fragment.DiscussionsTabFragment;
import lementProApp.model.App;
import lementProApp.model.Discussion;
import lementProApp.R;
import lementProApp.model.ImageManager;

import java.util.ArrayList;


/**
 * Created by User on 10.07.2015.
 */

import android.widget.ImageView;

/**
 * Created by User on 08.07.2015.
 */
public class CustomDiscussionAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private DiscussionsTabFragment activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Context cntx;
    Discussion tempValues=null;
    int i=0;
    String cookieVal = "";
    public ImageManager imageManager;

    /*************  CustomAdapter Constructor *****************/
    public CustomDiscussionAdapter(DiscussionsTabFragment a, ArrayList<Discussion> d,Resources resLocal,String val) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        cookieVal = val;
        imageManager = new ImageManager(activity.getActivity().getApplicationContext(),100000,".ASPXAUTH=" + cookieVal);
        try {
            /***********  Layout inflator to call external xml layout () ***********/
          inflater = (LayoutInflater) activity.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        public ImageView imgReply;
        public ImageView avatarAuthor;
        public TextView author;
        public TextView date;
        public TextView message;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        tempValues = null;
    try {
    if (convertView == null) {

        /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
           // vi = inflater.inflate(R.layout.selfdiscussion_layout, null);

        vi = inflater.inflate(R.layout.discussion_layout, null);
        /****** View Holder Object to contain tabitem.xml file elements ******/

        holder = new ViewHolder();
        holder.imgReply = (ImageView) vi.findViewById(R.id.imgReply);
        holder.author = (TextView) vi.findViewById(R.id.txtAuthor);
        holder.avatarAuthor = (ImageView) vi.findViewById(R.id.authorAvatar);
        holder.date = (TextView) vi.findViewById(R.id.txtDiscussionDate);
        holder.message = (TextView) vi.findViewById(R.id.txtDiscussionMessage);

        /************  Set holder with LayoutInflater ************/
        vi.setTag(holder);
    }
    if (data.size() > 0) {
        holder = (ViewHolder) vi.getTag();
        tempValues = (Discussion) data.get(position);
        if(tempValues.isSelfAction){
            vi = inflater.inflate(R.layout.selfdiscussion_layout, null);
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.imgReply = (ImageView) vi.findViewById(R.id.imgReply);
            holder.author = (TextView) vi.findViewById(R.id.txtAuthor);
            holder.avatarAuthor = (ImageView) vi.findViewById(R.id.authorAvatar);
            holder.date = (TextView) vi.findViewById(R.id.txtDiscussionDate);
            holder.message = (TextView) vi.findViewById(R.id.txtDiscussionMessage);
            holder.author.setText("You");
        }else{
            vi = inflater.inflate(R.layout.discussion_layout, null);
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.imgReply = (ImageView) vi.findViewById(R.id.imgReply);
            holder.author = (TextView) vi.findViewById(R.id.txtAuthor);
            holder.avatarAuthor = (ImageView) vi.findViewById(R.id.authorAvatar);
            holder.date = (TextView) vi.findViewById(R.id.txtDiscussionDate);
            holder.message = (TextView) vi.findViewById(R.id.txtDiscussionMessage);
            holder.author.setText(tempValues.authorText);
        }

        /***** Get each Model object from Arraylist ********/

       // holder.author.setText(tempValues.authorText);
        holder.date.setText(tempValues.lastModifiedDate);
        holder.message.setText(Html.fromHtml(tempValues.message.toString()));
        String msg = "<blockquote class=\"reply\" contenteditable=\"false\">";
        msg = msg + "<span style=\"font-size:10px;\">" + tempValues.lastModifiedDate + " " + tempValues.authorText + "</span><br/>";
        msg = msg + tempValues.message + "</blockquote>";
        holder.imgReply.setTag(msg);
        holder.imgReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.v("Message: Clicked", (String) v.getTag());
                    activity.onReplyClick((String) v.getTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        (new AsyncAvatarLoader(holder.avatarAuthor)).execute(tempValues.authorAvatarFileld, cookieVal);
       /* if (bitmap != null) {
            holder.avatarAuthor.setImageBitmap(bitmap);
        } else {*/
        //    holder.avatarAuthor.setBackgroundColor(Color.parseColor("#ffffff"));
        //}*/


            /*holder.date.setImageResource(
                    res.getIdentifier(
                            "com.androidexample.customlistview:drawable/" + tempValues.getImage()
                            , null, null));
             */
        /******** Set Item Click Listner for LayoutInflater for each row *******/

        //vi.setOnClickListener(new OnItemClickListener(position,tempValuess.ID));
    }
}catch(Exception e){
    e.printStackTrace();}
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

     private class AsyncAvatarLoader extends AsyncTask<String, Void, Void> {
        private ImageView avatarImgView;
        public AsyncAvatarLoader(ImageView imgV){
            avatarImgView = imgV;
        }
        protected Void doInBackground(String... param) {
            try {

                imageManager.displayImage(App.getInstance().getApplicationContext().getString(R.string.Link) + "Services/Employee/GetAvatarByFileId.do?fileId=" + param[0], avatarImgView,R.drawable.no_img);
                /*HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://lementtest.lement.pro/Services/Employee/GetAvatarByFileId.do?fileId=" + param[0]);
                Log.v("URI:", post.getURI().toString());
                post.setHeader("Cookie", ".ASPXAUTH=" + param[1] + ";");
                HttpResponse httpResponse = client.execute(post);
                int StatusCode = httpResponse.getStatusLine().getStatusCode();
                if (StatusCode == 200) {
                    Log.v("Background:FileID", param[0]);
                   // avatarImgView.setImageBitmap(LementUtility.getResizedBitmap((BitmapFactory.decodeStream((InputStream) httpResponse.getEntity().getContent())), 120, 120));
                    avatarImgView.setImageBitmap(BitmapFactory.decodeStream((InputStream) httpResponse.getEntity().getContent()));
                }else{
                    Log.v("Background:White","Here we are:");
                    avatarImgView.setBackgroundColor(Color.parseColor("#ffffff"));
                }*/
                Log.v("Background:","InsideImageLoader");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

