package lementProApp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

public class DescriptionTabFragmentDocument extends Fragment {
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
    String folderKey = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            objectID = container.getTag(R.string.FirstTag).toString();
            folderKey = container.getTag(R.string.SecondTag).toString();
            JSONObject obj = new JSONObject(folderKey);
            JSONArray arr = obj.getJSONArray("subKeys");
            if(arr.length()>0){
                folderKey = (String)arr.get(0);
            }else{
                folderKey = "95";
            }
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_description_tab, null, false);
            tblDescription = (LinearLayout) rootView.findViewById(R.id.tblDescription);
            (new AsyncDescriptionLoader()).execute(getString(R.string.Link)+"Services/ObjectBase/GetAttributeDefs.do?id="+folderKey+"&onlyVisibleOnGeneralPage=true&includeListOfValues=true");
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
        ArrayList<DescriptionTask> arrLinkedTask =new ArrayList<DescriptionTask>();
        ArrayList<DescriptionTask> arrLinkedDocuments =new ArrayList<DescriptionTask>();
        ArrayList<DescriptionTask> arrReferToDocuments =new ArrayList<DescriptionTask>();
        ArrayList<TaskFile> arrFiles = new ArrayList<TaskFile>();
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
                String uri = getString(R.string.Link)+"Services/Document/Get.do?id=" + objectID + "&updateLastViewDate=true";
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
                    String attrId = objTabs.getString("attrId");
                    String field = objTabs.getString("field");
                    Log.d("Description:",attrId);
                    if(objTabs.getBoolean("showOnGeneralPage")){

                        arrList.add(objTabs.getString("name"));
                        arrValues.add("");
                        if(field.startsWith("i")){
                            Object o = objValues.get(field);
                            if(!objValues.get(field).equals(null)) {
                                Object val = objValues.get(field);
                                if (val instanceof JSONObject ) {
                                    if (objTabs.getInt("dataType") == 4)
                                        if(objValues.getJSONObject(field).has("value"))
                                            arrValues.add(index, LementUtility.convertJSONDate(objValues.getJSONObject(field).getString("value")));
                                        else
                                            arrValues.add(index, LementUtility.convertJSONDate(objValues.getJSONObject(field).getString("text")));
                                    else
                                        if(objValues.getJSONObject(field).has("value"))
                                            arrValues.add(index, objValues.getJSONObject(field).getString("value"));
                                        else
                                            arrValues.add(index, objValues.getJSONObject(field).getString("text"));
                                } else {
                                    if (objTabs.getInt("dataType") == 4)
                                        arrValues.add(index, LementUtility.convertJSONDate(objValues.getString(field)));
                                    else
                                        arrValues.add(index, objValues.getString(field));
                                }
                            }else{
                                arrValues.add(index,"");
                            }
                        }else {
                            // name = name.toLowerCase();

                            if (attrId.equals("118")) {
                                if(objValues.has("parentTask")) {
                                    if (!objValues.getString("parentTask").equals("null")) {
                                        arrValues.set(index, objValues.getJSONObject("parentTask").getString("text"));
                                    } else {
                                        arrList.remove(arrList.size() - 1);
                                        arrValues.remove(arrValues.size() - 1);
                                        index--;
                                    }
                                }else{
                                    arrList.remove(arrList.size() - 1);
                                    arrValues.remove(arrValues.size() - 1);
                                    index--;
                                }
                            } else if (attrId.equals("13")) {
                                if (!objValues.getString("author").equals("null")) {
                                    arrValues.set(index, objValues.getJSONObject("author").getString("text"));
                                } else {
                                    arrList.remove(arrList.size() - 1);
                                    arrValues.remove(arrValues.size() - 1);
                                    index--;
                                }
                            } else if (attrId.equals("1")) {
                                arrValues.set(index, objValues.getJSONObject("type").getString("fullName"));
                            } else if (attrId.equals("4")) {
                                arrValues.set(index, objValues.getJSONObject("project").getString("text"));
                            } else if (attrId.equals("14")) {
                                arrValues.set(index, LementUtility.convertJSONDate(objValues.getString("registrationDate")));
                            } else if (attrId.equals("15")) {
                                if (!objValues.getString("deadlineDate").equals(null))
                                    arrValues.set(index, LementUtility.convertJSONDate(objValues.getString("deadlineDate")));
                                else
                                    arrValues.set(index, "");
                            }/*else if(attrId.equals("145")){
                            arrValues.set(index,objValues.getJSONObject("i145").getString("value"));
                        }*/ else if (attrId.equals("13")) {
                                arrValues.set(index, objValues.getJSONObject("author").getString("text"));
                            } else {
                                arrList.remove(arrList.size() - 1);
                                index--;
                                //arrValues.set(index,name);
                            }
                        }
                        index++;
                    }
                }

                client = new DefaultHttpClient();
                uri = getString(R.string.Link)+"Services/Document/GetLinkedTasks.do?id=" + objectID;
                post = new HttpPost(uri);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + uri);
                httpResponse = client.execute(post);
                connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + uri);
                entity = httpResponse.getEntity();
                serverResponse = EntityUtils.toString(entity);
                JSONArray arrVal = new JSONArray(serverResponse);
                if (arrVal.length() > 0) {
                    for (int j = 0; j < arrVal.length(); j++) {
                        JSONObject objt = arrVal.getJSONObject(j);
                        arrLinkedTask.add(new DescriptionTask(objt.getString("id"),objt.getBoolean("isClosed"),objt.getBoolean("canView"),objt.getString("name")));
                    }
                }

                client  = new DefaultHttpClient();
                uri = getString(R.string.Link)+"Services/Document/GetLinkedDocuments.do?id=" + objectID;
                post = new HttpPost(uri);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + uri);
                httpResponse = client.execute(post);
                connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + uri);
                entity = httpResponse.getEntity();
                serverResponse = EntityUtils.toString(entity);
                arrVal = new JSONArray(serverResponse);
                if (arrVal.length() > 0) {
                    for (int j = 0; j < arrVal.length(); j++) {
                        JSONObject objt = arrVal.getJSONObject(j);
                        arrLinkedDocuments.add(new DescriptionTask(objt.getString("id"),objt.getBoolean("isClosed"),objt.getBoolean("canView"),objt.getString("name")));
                    }
                }

                client  = new DefaultHttpClient();
                uri = getString(R.string.Link)+"Services/Document/GetReferToDocuments.do?id=" + objectID;
                post = new HttpPost(uri);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + uri);
                httpResponse = client.execute(post);
                connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + uri);
                entity = httpResponse.getEntity();
                serverResponse = EntityUtils.toString(entity);
                arrVal = new JSONArray(serverResponse);
                if (arrVal.length() > 0) {
                    for (int j = 0; j < arrVal.length(); j++) {
                        JSONObject objt = arrVal.getJSONObject(j);
                        arrReferToDocuments.add(new DescriptionTask(objt.getString("id"),objt.getBoolean("isClosed"),objt.getBoolean("canView"),objt.getString("name")));
                    }
                }

                client  = new DefaultHttpClient();
                uri = getString(R.string.Link)+"Services/Documents/File/GetList.do?id="+objectID+"&showOnlyLastest=true&showOnlyAddedOnCreation=true&showOnlyFromOtherObject=false";
                post = new HttpPost(uri);
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + uri);
                httpResponse = client.execute(post);
                connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + uri);
                entity = httpResponse.getEntity();
                serverResponse = EntityUtils.toString(entity);
                arrVal = new JSONArray(serverResponse);
                JSONArray arrTask = new JSONArray(serverResponse);
                for (int i = 0; i < arrTask.length(); i++) {
                    objTaskFile = new TaskFile();
                    JSONObject jsonTaskFile = arrTask.getJSONObject(i);
                    objTaskFile.creationDate = LementUtility.convertJSONDate(jsonTaskFile.getString("creationDate"));
                    objTaskFile.name = jsonTaskFile.getString("fileName");
                    objTaskFile.isFinal = jsonTaskFile.getBoolean("isFinal");
                    objTaskFile.isInStorage = jsonTaskFile.getBoolean("isInStorage");
                    objTaskFile.revision = jsonTaskFile.getInt("revision");
                    objTaskFile.size = jsonTaskFile.getString("size");
                    objTaskFile.id = jsonTaskFile.getString("id");
                    JSONObject jsonAuthor = jsonTaskFile.getJSONObject("author");
                    objTaskFile.authorAvatarFileld = jsonAuthor.getString("avatarFileId");
                    objTaskFile.authorId = jsonAuthor.getString("id");
                    objTaskFile.authorisInVacation = jsonAuthor.getBoolean("isInVacation");
                    objTaskFile.authorText = jsonAuthor.getString("text");
                    JSONObject jsonAuthorFrom = jsonTaskFile.getJSONObject("authorFrom");
                    objTaskFile.authorFromAvatarFileld = jsonAuthorFrom.getString("avatarFileId");
                    objTaskFile.authorFromId = jsonAuthorFrom.getString("id");
                    objTaskFile.authorFromisInVacation = jsonAuthorFrom.getBoolean("isInVacation");
                    objTaskFile.authorFromText = jsonAuthorFrom.getString("text");
                    arrFiles.add(objTaskFile);
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
            if(arrLinkedTask.size()>0){
                TextView txt1 = getTextView1();
                TextView txt2 = getTextView3();
                txt1.setText("Linked Tasks:");
                tblDescription.addView(txt1);
                for(int i=0;i<arrLinkedTask.size();i++){
                    txt2 = getTextView3();
                    DescriptionTask objDT = arrLinkedTask.get(i);
                    txt2.setText(objDT.name);
                    if(objDT.isClosed){
                        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    tblDescription.addView(txt2);
                }
            }
            if(arrLinkedDocuments.size()>0){
                TextView txt1 = getTextView1();
                TextView txt2 = getTextView3();
                txt1.setText("Linked Documents:");
                tblDescription.addView(txt1);
                for(int i=0;i<arrLinkedDocuments.size();i++){
                    txt2 = getTextView3();
                    DescriptionTask objDT = arrLinkedDocuments.get(i);
                    txt2.setText(objDT.name);
                    if(objDT.isClosed){
                        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    tblDescription.addView(txt2);
                }
            }
            if(arrReferToDocuments.size()>0){
                TextView txt1 = getTextView1();
                TextView txt2 = getTextView3();
                txt1.setText("Related Documents:");
                tblDescription.addView(txt1);
                for(int i=0;i<arrReferToDocuments.size();i++){
                    txt2 = getTextView3();
                    DescriptionTask objDT = arrReferToDocuments.get(i);
                    txt2.setText(objDT.name);
                    if(objDT.isClosed){
                        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    tblDescription.addView(txt2);
                }
            }
            if(arrFiles.size()>0){
                TextView txt1 = getTextView1();
                TextView txt2 = getTextView3();
                txt1.setText("Files:");
                tblDescription.addView(txt1);
                for(int i=0;i<arrFiles.size();i++){
                    txt2 = getTextView3();
                    final TaskFile objDT = arrFiles.get(i);
                    String viewName = objDT.name + " (Version:" + objDT.revision + ")," + objDT.size;
                    txt2.setText(viewName);
                    txt2.setPaintFlags(txt2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    txt2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String link = getString(R.string.Link)+"Services/Files/DocumentFile/GetFile.do?fileId=" + objDT.id;
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(link));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.getInstance().getApplicationContext().startActivity(intent);
                        }
                    });
                    tblDescription.addView(txt2);
                }
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
        public TextView getTextView3() {
            TableRow.LayoutParams  params1=new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView txt2 = new TextView(App.getInstance().getApplicationContext());
            //setting the text
            txt2.setLayoutParams(params1);
            txt2.setSingleLine(false);
            txt2.setTextColor(Color.parseColor("#000000"));
            txt2.setBackgroundColor(Color.parseColor("#F1F1F1"));
            txt2.setTextSize(16);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#F1F1F1")); // Changes this drawbale to use a single color instead of a gradient
            gd.setCornerRadius(5);
            gd.setStroke(2, Color.parseColor("#A1A1A1"));
            txt2.setBackgroundDrawable(gd);
            txt2.setPadding(10,5,10,5);
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
