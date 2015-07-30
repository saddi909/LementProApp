package lementProApp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import lementProApp.R;
import lementProApp.activity.Login;
import lementProApp.holder.CustomFilesAdapter;
import lementProApp.model.App;
import lementProApp.model.LementUtility;
import lementProApp.model.TaskFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DescriptionTabFragmentProject extends Fragment {
    String objectID= "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    LinearLayout tblDescription ;
    ListView listFiles;
    CustomFilesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            objectID = container.getTag().toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_description_tab, null, false);
            tblDescription = (LinearLayout) rootView.findViewById(R.id.tblDescription);
            (new AsyncDescriptionLoader()).execute(getString(R.string.Link) + "Services/ObjectBase/GetAttributeDefs.do?id=66&onlyVisibleOnGeneralPage=true&includeListOfValues=true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //View rootView = inflater.inflate(R.layout.fragment_discussions_tab, null, false);
        //}
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences shared = getActivity().getSharedPreferences(Login.SharedPrefs, getActivity().MODE_PRIVATE);
        cookieVal = (shared.getString(strCookie, ""));
        if(cookieVal == null || cookieVal.equals("")){
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
        }
    }
    private class AsyncDescriptionLoader extends AsyncTask<String, Void, Void> {
        ArrayList<String> arrList = new ArrayList<String>();
        ArrayList<String> arrValues = new ArrayList<String>();
        protected Void doInBackground(String... param) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(param[0]);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + param[0]);
                HttpResponse httpResponse = client.execute(post);
                int connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + param[0]);

                HttpEntity entity = httpResponse.getEntity();
                String serverResponse = EntityUtils.toString(entity);
                TaskFile objTaskFile;
                //JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = new JSONArray(serverResponse);


                client  = new DefaultHttpClient();
                String uri = getString(R.string.Link) + "Services/Project/Get.do?id=" + objectID + "&updateLastViewDate=true";
                post = new HttpPost(uri);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + param[0]);
                httpResponse = client.execute(post);
                connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + param[0]);
                entity = httpResponse.getEntity();
                serverResponse = EntityUtils.toString(entity);
                //JSONArray arrVal = new JSONArray(serverResponse);
                JSONObject objVal = new JSONObject(serverResponse);
                JSONObject objtemp = objVal.getJSONObject("object");
                JSONObject objValues = objtemp.getJSONObject("values");
                Log.v("Description:","DescriptionAsync");
                int index = 0;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject objTabs= arr.getJSONObject(i);
                    String name = objTabs.getString("name");
                    String field = objTabs.getString("field");
                    Log.d("Description:",name);
                    if(objTabs.getBoolean("showOnGeneralPage")){

                        arrList.add(name);
                        //name = name.toLowerCase();
                        arrValues.add("");
                        if(field.equals("type")){
                            arrValues.set(index,objValues.getJSONObject(field).getString("text"));
                        }else if(field.equals("description")){
                            arrValues.set(index,objValues.getString(field).replaceAll("\n", "<br/>"));
                        }
                        else if(field.equals("startDate")){
                            arrValues.set(index,LementUtility.convertJSONDate(objValues.getString(field)));
                        }else if(field.equals("endDate")){
                            if(!objValues.getString(field).equals("null")){
                                arrValues.set(index,LementUtility.convertJSONDate(objValues.getString(field)));
                            }else{
                                arrValues.set(index,"");
                            }
                        }else if(field.equals("managers")){
                            arrValues.set(index,objValues.getJSONObject(field).getString("text"));
                        } else if(field.equals("controllers")){
                            JSONArray arrControllers = objValues.getJSONArray(field);
                            for(int j = 0;j<arrControllers.length();j++){
                                arrValues.set(index, arrValues.get(index) + arrControllers.getJSONObject(j).getString("text") + "<br/>");
                            }
                        }else if(field.equals("executors")){
                            JSONArray arrControllers = objValues.getJSONArray(field);
                            for(int j = 0;j<arrControllers.length();j++){
                                arrValues.set(index,arrValues.get(index) + arrControllers.getJSONObject(j).getString("text") + "<br/>");
                            }
                        }
                        else{
                            arrList.remove(arrList.size()-1);
                            index -- ;
                            //arrValues.set(index,name);
                        }
                        index++;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for(int i=0;i<arrList.size();i++){
                TextView txt1 = getTextView1();
                TextView txt2 = getTextView2();
                txt1.setText(arrList.get(i) + ":");
                txt2.setText(Html.fromHtml(arrValues.get(i)));
                tblDescription.addView(txt1);
                tblDescription.addView(txt2);
            }

        }

        public TextView getTextView1(){
            TableRow.LayoutParams  params1=new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView txt1=new TextView(App.getInstance().getApplicationContext());
            txt1.setTextColor(Color.parseColor("#000000"));
            txt1.setBackgroundColor(Color.parseColor("#D1D1D1"));
            txt1.setTypeface(null, Typeface.BOLD);
            txt1.setTextSize(20);
            txt1.setPadding(5,5,5,5);
            txt1.setLayoutParams(params1);
            return txt1;
        }
        public TextView getTextView2() {
            TableRow.LayoutParams  params1=new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView txt2 = new TextView(App.getInstance().getApplicationContext());
            //setting the text
            txt2.setLayoutParams(params1);
            txt2.setSingleLine(false);
            txt2.setTextColor(Color.parseColor("#000000"));
            txt2.setBackgroundColor(Color.parseColor("#F1F1F1"));
            txt2.setTextSize(16);
            return txt2;
        }

    }
    public class DescriptionTask{
        public boolean canView;
        public String id;
        public boolean isClosed;
        public String name;
        public DescriptionTask(String id,boolean isClosed,boolean canView,String name){
            this.id = id;
            this.isClosed = isClosed;
            this.canView = canView;
            this.name = name;
        }
    }
}
