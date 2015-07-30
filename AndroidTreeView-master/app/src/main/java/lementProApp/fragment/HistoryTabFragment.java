package lementProApp.fragment;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import lementProApp.R;
import lementProApp.activity.Login;
import lementProApp.holder.CustomHistoryAdapter;
import lementProApp.model.History;
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

public class HistoryTabFragment extends Fragment {

	String objectID= "";
	View rootView;
	ViewGroup containerView;
	String strCookie = ".ASPXAUTH";
	String strExpire = "expires";
	String strPath = "path";
	String cookieVal = "";
	ArrayList<History> arrList = new ArrayList<History>();

	ListView listHistory;
	CustomHistoryAdapter adapter;
	public HistoryTabFragment CustomListView = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//View temp = rootView;
		//if (temp == null) {
		try {
			objectID = container.getTag().toString();
			//String query = URLEncoder.encode(folderKey, "utf-8");
			rootView = inflater.inflate(R.layout.fragment_history_tab, null, false);
			try {
				Resources res = getResources();
				listHistory = (ListView) rootView.findViewById(R.id.listHistory);
				arrList.clear();
				adapter = new CustomHistoryAdapter(CustomListView.getActivity(), arrList, res, cookieVal);
				listHistory.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//containerView = (ViewGroup) rootView.findViewById(R.id.container);
			(new AsyncHistoryLoader()).execute(getString(R.string.Link) + "Services/Tasks/History/GetList.do?objectId=" + objectID);
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
	private class AsyncHistoryLoader extends AsyncTask<String, Void, Void> {
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
				History objHistory;
				//JSONObject objJSon = new JSONObject(serverResponse);
				JSONArray arr = new JSONArray(serverResponse);

				for (int i = 0; i < arr.length(); i++) {
					objHistory = new History();
					JSONObject jsonHistory = arr.getJSONObject(i);
					objHistory.changeDate = LementUtility.convertJSONDate(jsonHistory.getString("changeDate"));
					objHistory.id = jsonHistory.getString("id");
					objHistory.description = jsonHistory.getString("description");
					JSONObject jsonAuthor = jsonHistory.getJSONObject("author");
					objHistory.authorAvatarFileld = jsonAuthor.getString("avatarFileId");
					objHistory.authorId = jsonAuthor.getString("id");
					objHistory.authorisInVacation = jsonAuthor.getBoolean("isInVacation");
					objHistory.authorText = jsonAuthor.getString("text");
					JSONObject jsonAuthorFrom = jsonHistory.getJSONObject("authorFrom");
					objHistory.authorFromAvatarFileld = jsonAuthorFrom.getString("avatarFileId");
					objHistory.authorFromId = jsonAuthorFrom.getString("id");
					objHistory.authorFromisInVacation = jsonAuthorFrom.getBoolean("isInVacation");
					objHistory.authorFromText = jsonAuthorFrom.getString("text");
					arrList.add(objHistory);
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
			listHistory= (ListView) rootView.findViewById( R.id.listHistory);  // List defined in XML ( See Below )
			/**************** Create Custom Adapter *********/
			adapter=new CustomHistoryAdapter(CustomListView.getActivity(), arrList,res,cookieVal);
			listHistory.setAdapter( adapter );
		}

	}
}
