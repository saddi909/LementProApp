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
import lementProApp.holder.CustomCheckpointsAdapter;
import lementProApp.model.Checkpoints;
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

public class CheckPointsTabFragment extends Fragment {

    String objectID= "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    ArrayList<Checkpoints> arrList = new ArrayList<Checkpoints>();

    ListView listCheckpoints;
    CustomCheckpointsAdapter adapter;
    public CheckPointsTabFragment CustomListView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View temp = rootView;
        //if (temp == null) {
        try {
            objectID = container.getTag().toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_check_points_tab, null, false);
            try {
                Resources res = getResources();
                listCheckpoints = (ListView) rootView.findViewById(R.id.listCheckpoints);
                arrList.clear();
                adapter = new CustomCheckpointsAdapter(CustomListView.getActivity(), arrList, res, cookieVal);
                listCheckpoints.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncCheckpointsLoader()).execute(getString(R.string.Link) + "Services/Tasks/Checkpoint/GetList.do?taskId="+objectID+"&includeClosed=true");
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
    private class AsyncCheckpointsLoader extends AsyncTask<String, Void, Void> {
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
                Checkpoints objCheckpoint;
                //JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = new JSONArray(serverResponse);

                for (int i = 0; i < arr.length(); i++) {
                    objCheckpoint = new Checkpoints();
                    JSONObject jsonCheckpoint = arr.getJSONObject(i);
                    objCheckpoint.startDateTime = LementUtility.convertJSONDate(jsonCheckpoint.getString("startDateTime"));
                    objCheckpoint.dateClosed = LementUtility.convertJSONDate(jsonCheckpoint.getString("dateClosed"));
                    objCheckpoint.description = jsonCheckpoint.getString("description");
                    objCheckpoint.name = jsonCheckpoint.getString("name");
                    objCheckpoint.duration = jsonCheckpoint.getInt("duration");
                    objCheckpoint.isClosed = jsonCheckpoint.getBoolean("isClosed");
                    objCheckpoint.isExpired = jsonCheckpoint.getBoolean("isExpired");
                    objCheckpoint.id = jsonCheckpoint.getString("id");
                    JSONObject jsonAuthor = jsonCheckpoint.getJSONObject("author");
                    objCheckpoint.authorAvatarFileld = jsonAuthor.getString("avatarFileId");
                    objCheckpoint.authorId = jsonAuthor.getString("id");
                    objCheckpoint.authorisInVacation = jsonAuthor.getBoolean("isInVacation");
                    objCheckpoint.authorText = jsonAuthor.getString("text");
                    JSONObject jsonAuthorFrom = jsonCheckpoint.getJSONObject("authorFrom");
                    objCheckpoint.authorFromAvatarFileld = jsonAuthorFrom.getString("avatarFileId");
                    objCheckpoint.authorFromId = jsonAuthorFrom.getString("id");
                    objCheckpoint.authorFromisInVacation = jsonAuthorFrom.getBoolean("isInVacation");
                    objCheckpoint.authorFromText = jsonAuthorFrom.getString("text");
                    arrList.add(objCheckpoint);
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
            listCheckpoints= (ListView) rootView.findViewById( R.id.listCheckpoints);  // List defined in XML ( See Below )
            /**************** Create Custom Adapter *********/
            adapter=new CustomCheckpointsAdapter(CustomListView.getActivity(), arrList,res,cookieVal);
            listCheckpoints.setAdapter( adapter );
        }

    }
}

