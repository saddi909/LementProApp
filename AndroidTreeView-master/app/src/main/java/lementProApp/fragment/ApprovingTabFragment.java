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
import lementProApp.holder.CustomDocumentApprovingAdapter;
import lementProApp.holder.CustomFilesAdapter;
import lementProApp.model.DocumentApproving;
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

public class ApprovingTabFragment extends Fragment {

    String objectID= "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    ArrayList<DocumentApproving> arrList = new ArrayList<DocumentApproving>();

    ListView listDocumentApproving;
    CustomDocumentApprovingAdapter adapter;
    public ApprovingTabFragment CustomListView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View temp = rootView;
        //if (temp == null) {
        try {
            objectID = container.getTag(R.string.FirstTag).toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_document_approving, null, false);
            try {
                Resources res = getResources();
                listDocumentApproving = (ListView) rootView.findViewById(R.id.listDocumentApproving);
                arrList.clear();
                adapter = new CustomDocumentApprovingAdapter(CustomListView.getActivity(), arrList, res, cookieVal);
                listDocumentApproving.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncDocumentApprovingLoader()).execute(getString(R.string.Link)+"Services/Document/GetDocumentStatus.do?id=" + objectID);
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
    private class AsyncDocumentApprovingLoader extends AsyncTask<String, Void, Void> {
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
                DocumentApproving objDocumentApproving;
                //JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = new JSONArray(serverResponse);

                for (int i = 0; i < arr.length(); i++) {
                    objDocumentApproving = new DocumentApproving();
                    JSONObject jsonDocumentApproving = arr.getJSONObject(i);
                    objDocumentApproving.avatarFileId = jsonDocumentApproving.getString("avatarFileId");
                    objDocumentApproving.comment = jsonDocumentApproving.getString("comment");
                    objDocumentApproving.department = jsonDocumentApproving.getString("department");
                    objDocumentApproving.displayName = jsonDocumentApproving.getString("displayName");
                    if(!jsonDocumentApproving.getString("endDate").equals("null")) {
                        objDocumentApproving.endDate = LementUtility.convertJSONDate(jsonDocumentApproving.getString("endDate"));
                    }
                    objDocumentApproving.id = jsonDocumentApproving.getString("id");
                    objDocumentApproving.position = jsonDocumentApproving.getString("position");
                    if(!jsonDocumentApproving.getString("startDate").equals("null")) {
                        objDocumentApproving.startDate = LementUtility.convertJSONDate(jsonDocumentApproving.getString("startDate"));
                    }
                    JSONObject jsonStage = jsonDocumentApproving.getJSONObject("stage");
                    objDocumentApproving.stageId = jsonStage.getString("id");
                    objDocumentApproving.stageText = jsonStage.getString("text");
                    arrList.add(objDocumentApproving);
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
            listDocumentApproving= (ListView) rootView.findViewById( R.id.listDocumentApproving);  // List defined in XML ( See Below )
            /**************** Create Custom Adapter *********/
            adapter=new CustomDocumentApprovingAdapter(CustomListView.getActivity(), arrList,res,cookieVal);
            listDocumentApproving.setAdapter( adapter );
        }

    }
}
