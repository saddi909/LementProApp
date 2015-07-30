package lementProApp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lementProApp.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import lementProApp.activity.Login;
import lementProApp.holder.CustomFilesAdapter;
import lementProApp.holder.CustomFilesAdapterDocuments;
import lementProApp.holder.CustomFilesAdapterProject;
import lementProApp.model.ProjectDocuments;
import lementProApp.model.LementUtility;

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
import java.util.ArrayList;

public class FilesTabFragmentProject extends Fragment {

    String objectID= "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    ArrayList<ProjectDocuments> arrList = new ArrayList<ProjectDocuments>();
    String link = "";
    ListView listFiles;
    CustomFilesAdapterProject adapter;
    public FilesTabFragmentProject CustomListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View temp = rootView;
        //if (temp == null) {
        try {
            objectID = container.getTag().toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_files_tab, null, false);
            try {
                Resources res = getResources();
                listFiles = (ListView) rootView.findViewById(R.id.listFiles);
                arrList.clear();
                adapter = new CustomFilesAdapterProject(CustomListView.getActivity(), arrList, res, cookieVal);
                listFiles.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncProjectDocumentsLoader()).execute(getString(R.string.Link) + "Services/Project/GetDocuments.do?id=" + objectID + "&includeClosed=true");
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
        CustomListView = this;
        SharedPreferences shared = getActivity().getSharedPreferences(Login.SharedPrefs, getActivity().MODE_PRIVATE);
        cookieVal = (shared.getString(strCookie, ""));
        if(cookieVal == null || cookieVal.equals("")){
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
        }
    }
    private class AsyncProjectDocumentsLoader extends AsyncTask<String, Void, Void> {
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
                ProjectDocuments objProjectDocuments;
                JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = objJSon.getJSONArray("items");

                for (int i = 0; i < arr.length(); i++) {
                    objProjectDocuments = new ProjectDocuments();
                    JSONObject jsonProjectDocuments = arr.getJSONObject(i);
                    JSONObject objval = jsonProjectDocuments.getJSONObject("values");
                    objProjectDocuments.registrationDate = LementUtility.convertJSONDate(objval.getString("registrationDate"));
                    objProjectDocuments.deadline = LementUtility.convertJSONDate(objval.getString("deadlineDate"));
                    objProjectDocuments.lastModifiedDate = LementUtility.convertJSONDate(objval.getString("lastModifiedDate"));
                    objProjectDocuments.closedDate = LementUtility.convertJSONDate(objval.getString("closeDate"));
                    objProjectDocuments.name = objval.getString("name");
                    objProjectDocuments.Description = objval.getString("i46");
                    objProjectDocuments.author = objval.getJSONObject("author").getString("text");
                    objProjectDocuments.isClosed = jsonProjectDocuments.getBoolean("isClosed");
                    arrList.add(objProjectDocuments);
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
            listFiles= (ListView) rootView.findViewById( R.id.listFiles);  // List defined in XML ( See Below )
            /**************** Create Custom Adapter *********/
            adapter=new CustomFilesAdapterProject(CustomListView.getActivity(), arrList,res,cookieVal);
            listFiles.setAdapter( adapter );
        }

    }
}
