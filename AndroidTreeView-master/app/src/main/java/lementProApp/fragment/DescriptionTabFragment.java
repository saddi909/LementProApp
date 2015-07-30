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

public class DescriptionTabFragment extends Fragment {
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

	public DescriptionTabFragment(){

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try {
			objectID = container.getTag().toString();
			//String query = URLEncoder.encode(folderKey, "utf-8");
			rootView = inflater.inflate(R.layout.fragment_description_tab, null, false);
			tblDescription = (LinearLayout) rootView.findViewById(R.id.tblDescription);
			(new AsyncDescriptionLoader()).execute(getString(R.string.Link) + "Services/ObjectBase/GetAttributeDefs.do?id=5&onlyVisibleOnGeneralPage=true&includeListOfValues=true");
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
        ArrayList<DescriptionTask> arrCompletedSubTask =new ArrayList<DescriptionTask>();
		ArrayList<DescriptionTask> arrActiveSubTask =new ArrayList<DescriptionTask>();
		ArrayList<DescriptionTask> arrLinkedDocuments =new ArrayList<DescriptionTask>();
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
				String uri = getString(R.string.Link) + "Services/Task/Get.do?id=" + objectID + "&updateLastViewDate=true";
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
					Log.d("Description:",name);
					if(objTabs.getBoolean("showOnGeneralPage")){

						arrList.add(name);
						name = name.toLowerCase();
						arrValues.add("");
						if(name.equals("parent task")){
							if(! objValues.getString("parentTask").equals("null")){
								arrValues.set(index,objValues.getJSONObject("parentTask").getString("text"));
							}else{
								arrList.remove(arrList.size()-1);
								arrValues.remove(arrValues.size()-1);
								index--;
							}
						}else if(name.equals("parent document")) {
							if(! objValues.getString("parentDocument").equals("null")){
								arrValues.set(index,objValues.getJSONObject("parentDocument").getString("text"));
							}else{
								arrList.remove(arrList.size() - 1);
								arrValues.remove(arrValues.size()-1);
								index--;
							}
						}
						else if(name.equals("type") || name.equals("project")){
							arrValues.set(index,objValues.getJSONObject(name).getString("text"));
						}else if(name.equals("description")){
							arrValues.set(index,objValues.getString(name).replaceAll("\n", "<br/>"));
						}
						else if(name.compareTo("k??????????") == 0) {
							arrValues.set(index,objValues.getString("i156"));
						}else if(name.equals("?????? ??????")){
							arrValues.set(index,objValues.getJSONObject("i152").getString("value"));
						}
						else if(name.equals("start")){
							arrValues.set(index,LementUtility.convertJSONDate(objValues.getString("startDate")));
						}else if(name.equals("deadline")){
							if(!objValues.getString("endDate").equals("null")){
								arrValues.set(index,LementUtility.convertJSONDate(objValues.getString("endDate")));
							}else{
								arrValues.set(index,"");
							}
						}else if(name.equals("manager")){
							arrValues.set(index,objValues.getJSONObject("managers").getString("text"));
						} else if(name.equals("controllers")){
							JSONArray arrControllers = objValues.getJSONArray("controllers");
							for(int j = 0;j<arrControllers.length();j++){
								arrValues.set(index, arrValues.get(index) + arrControllers.getJSONObject(j).getString("text") + "<br/>");
							}
						}else if(name.equals("members")){
							JSONArray arrControllers = objValues.getJSONArray("executors");
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

				client = new DefaultHttpClient();
				uri = getString(R.string.Link) + "Services/Task/GetCompletedSubTasks.do?id=" + objectID;
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
						arrCompletedSubTask.add(new DescriptionTask(objt.getString("id"),objt.getBoolean("isClosed"),objt.getBoolean("canView"),objt.getString("name")));
					}
				}

					client  = new DefaultHttpClient();
					uri = getString(R.string.Link) + "Services/Task/GetActiveSubTasks.do?id=" + objectID;
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
							arrActiveSubTask.add(new DescriptionTask(objt.getString("id"),objt.getBoolean("isClosed"),objt.getBoolean("canView"),objt.getString("name")));
						}
					}

				client  = new DefaultHttpClient();
				uri = getString(R.string.Link) + "Services/Task/GetLinkedDocuments.do?id=" + objectID;
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
				uri = getString(R.string.Link) + "Services/Tasks/File/GetList.do?id="+objectID+"&showOnlyLastest=true&showOnlyAddedOnCreation=true&showOnlyFromOtherObject=false";
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
			if(arrCompletedSubTask.size()>0){
				TextView txt1 = getTextView1();
				TextView txt2 = getTextView3();
				txt1.setText("Completed subtasks:");
				tblDescription.addView(txt1);
				for(int i=0;i<arrCompletedSubTask.size();i++){
					txt2 = getTextView3();
					DescriptionTask objDT = arrCompletedSubTask.get(i);
					txt2.setText(objDT.name);
					if(objDT.isClosed){
						txt2.setPaintFlags(txt2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					}
					tblDescription.addView(txt2);
				}
			}
			if(arrActiveSubTask.size()>0){
				TextView txt1 = getTextView1();
				TextView txt2 = getTextView3();
				txt1.setText("Active subtasks:");
				tblDescription.addView(txt1);
				for(int i=0;i<arrActiveSubTask.size();i++){
					txt2 = getTextView3();
					DescriptionTask objDT = arrActiveSubTask.get(i);
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
							String link = getString(R.string.Link) + "Services/Files/DocumentFile/GetFile.do?fileId=" + objDT.id;
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
			txt2.setPadding(10, 5, 10, 5);
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
