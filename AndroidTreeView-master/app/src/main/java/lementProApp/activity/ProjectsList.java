package lementProApp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import lementProApp.model.LementUtility;
import lementProApp.R;
import lementProApp.model.Task;
import lementProApp.holder.CustomTaskAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProjectsList extends ActionBarActivity {
    String folderKey = "";
    ArrayList<Task> arrList = new ArrayList<Task>();
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    String link = "";
    String type = "";

    ListView list;
    CustomTaskAdapter adapter;
    public ProjectsList CustomListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);
        Bundle extras = getIntent().getExtras();
        CustomListView = this;
        if (extras != null) {
            folderKey = extras.getString("folderKey");
            link = extras.getString("link");
            type = extras.getString("type");
        }
        SharedPreferences shared = getSharedPreferences(Login.SharedPrefs, MODE_PRIVATE);
        cookieVal = (shared.getString(strCookie, ""));
        if(cookieVal == null || cookieVal.equals("")){
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(ProjectsList.this, Login.class);
            ProjectsList.this.startActivity(i);
        }
        (new AsyncListTasksLoader()).execute(link + "?folderKey=");

    }

    private class AsyncListTasksLoader extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... param) {

            try {
                HttpClient client = new DefaultHttpClient();
                String query = URLEncoder.encode(folderKey , "utf-8");
                HttpPost post = new HttpPost(param[0] + query + "&includeLastAction=true");
                Log.v("URI:",post.getURI().toString());
                post.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                Log.d("--", "Try to open => " + post.getURI().toString());
                HttpResponse httpResponse = client.execute(post);
                int connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + param[0]);

                HttpEntity entity = httpResponse.getEntity();
                String serverResponse = EntityUtils.toString(entity);
                Task objTask;
                JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = new JSONArray(objJSon.getString("items"));

                for (int i = 0; i < arr.length(); i++) {
                    objTask = new Task();
                    JSONObject jsonTask = arr.getJSONObject(i);
                    objTask.CanDelete = jsonTask.getBoolean("CanDelete");
                    objTask.CanEdit = jsonTask.getBoolean("CanEdit");
                    objTask.CanRead = jsonTask.getBoolean("CanRead");
                    if(type == "1") {
                        objTask.CanUnread = jsonTask.getBoolean("CanUnread");
                    }else{
                        objTask.CanUnread = jsonTask.getBoolean("isUnread");
                    }
                    objTask.childrenCount = jsonTask.getInt("childrenCount");
                    if(type == "1") {
                        objTask.expanded = jsonTask.getBoolean("expanded");
                    }
                    objTask.ID = jsonTask.getString("id");
                    objTask.isClosed = jsonTask.getBoolean("isClosed");
                    objTask.isMarked = jsonTask.getBoolean("isMarked");
                    objTask.isOutOfDate = jsonTask.getBoolean("isOutOfDate");
                    if(type == "1") {
                        objTask.isRouteTask = jsonTask.getBoolean("isRouteTask");
                    }
                    objTask.isUnread = jsonTask.getBoolean("isUnread");
                    objTask.newCommentCount = jsonTask.getInt("newCommentCount");
                    objTask.state = jsonTask.getInt("state");
                    if(type == "1") {
                        objTask.unreadChildrenCount = jsonTask.getInt("unreadChildrenCount");
                    }
                    if(type == "1") {
                        JSONObject jsonlastComment = jsonTask.getJSONObject("lastComment");
                        if (jsonlastComment.has("isEmptyComment")) {
                            objTask.isEmptyComment = true;
                        } else {
                            JSONObject jsonAuthor = jsonlastComment.getJSONObject("author");
                            objTask.authorAvatarFileId = jsonAuthor.getString("avatarFileId");
                            objTask.authorID = jsonAuthor.getString("id");
                            objTask.authorText = jsonAuthor.getString("text");
                            objTask.isInVacation = jsonAuthor.getBoolean("isInVacation");
                            objTask.creationDate = LementUtility.convertJSONDate(jsonlastComment.getString("creationDate"));
                            objTask.commentID = jsonlastComment.getString("id");
                            objTask.lastModifiedDate = LementUtility.convertJSONDate(jsonlastComment.getString("lastModifiedDate"));
                            objTask.simplifiedMessage = jsonlastComment.getString("simplifiedMessage");
                            objTask.subject = jsonlastComment.getString("subject");
                        }
                    }
                    JSONObject jsonValues = jsonTask.getJSONObject("values");
                    objTask.name = jsonValues.getString("name");
                    arrList.add(objTask);
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
            Resources res =getResources();
            list= ( ListView )findViewById( R.id.listTasks );  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter=new CustomTaskAdapter( CustomListView, arrList,res,type );
            list.setAdapter( adapter );
        }

    }

    public void onItemClick(int mPosition,String Id)
    {
        Intent i = new Intent(ProjectsList.this, TaskDetails.class);
        i.putExtra("objectID",Id);
        ProjectsList.this.startActivity(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SimpleArrayAdapter extends ArrayAdapter<String> {
        public SimpleArrayAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);

        }
    }
}

