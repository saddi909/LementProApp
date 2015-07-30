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
import lementProApp.model.TaskFile;
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

public class FilesTabFragmentDocuments extends Fragment {

    String objectID= "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    ArrayList<TaskFile> arrList = new ArrayList<TaskFile>();
    String link = "";
    ListView listFiles;
    CustomFilesAdapterDocuments adapter;
    public FilesTabFragmentDocuments CustomListView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View temp = rootView;
        //if (temp == null) {
        try {
            objectID = container.getTag(R.string.FirstTag).toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_files_tab, null, false);
            try {
                Resources res = getResources();
                listFiles = (ListView) rootView.findViewById(R.id.listFiles);
                arrList.clear();
                adapter = new CustomFilesAdapterDocuments(CustomListView.getActivity(), arrList, res, cookieVal);
                listFiles.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncTaskFileLoader()).execute(getString(R.string.Link) + "Services/Documents/File/GetList.do?id=" + objectID);
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
    private class AsyncTaskFileLoader extends AsyncTask<String, Void, Void> {
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

                for (int i = 0; i < arr.length(); i++) {
                    objTaskFile = new TaskFile();
                    JSONObject jsonTaskFile = arr.getJSONObject(i);
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
                    arrList.add(objTaskFile);
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
            adapter=new CustomFilesAdapterDocuments(CustomListView.getActivity(), arrList,res,cookieVal);
            listFiles.setAdapter( adapter );
        }

    }
}
