package lementProApp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import lementProApp.holder.CustomDiscussionAdapterDocuments;
import lementProApp.model.App;
import lementProApp.model.Discussion;
import lementProApp.model.LementUtility;
import lementProApp.model.TaskFile;
import lementProApp.R;
import lementProApp.activity.Login;
import lementProApp.holder.CustomDiscussionAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.EditText;
public class DiscussionsTabFragmentDocument extends Fragment {
    String objectID = "";
    View rootView;
    ViewGroup containerView;
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";
    ArrayList<Discussion> arrList = new ArrayList<Discussion>();
    ImageView imgSend;
    EditText txtMessage;
    ListView listDiscussion;
    CustomDiscussionAdapterDocuments adapter;
    public DiscussionsTabFragmentDocument CustomListView = null;
    String replyMessage = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View temp = rootView;
        //if (temp == null) {
        try {
            objectID = container.getTag(R.string.FirstTag).toString();
            //String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_discussions_tab, null, false);
            txtMessage = (EditText) rootView.findViewById(R.id.txtEditChat);
            imgSend = (ImageView) rootView.findViewById(R.id.imgChatSend);
            imgSend.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    (new AsyncMessageSender()).execute(getString(R.string.Link)+"Services/Documents/Action/Create.do");

                }
            });
            try {
                Resources res = getResources();
                listDiscussion = (ListView) rootView.findViewById(R.id.listDiscussions);
                arrList.clear();
                adapter = new CustomDiscussionAdapterDocuments(CustomListView, arrList, res, cookieVal);
                listDiscussion.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncDiscussionLoader()).execute(getString(R.string.Link)+"Services/Documents/Action/GetList.do?objectId=" + objectID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //	View rootView = inflater.inflate(R.layout.fragment_discussions_tab, null, false);
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
        if (cookieVal == null || cookieVal.equals("")) {
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
        }
    }

    private class AsyncDiscussionLoader extends AsyncTask<String, Void, Void> {
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
                Discussion objDiscussion;
                //JSONObject objJSon = new JSONObject(serverResponse);
                JSONArray arr = new JSONArray(serverResponse);

                for (int i = 0; i < arr.length(); i++) {
                    objDiscussion = new Discussion();
                    JSONObject jsonDiscussion = arr.getJSONObject(i);
                    objDiscussion.creationDate = LementUtility.convertJSONDate(jsonDiscussion.getString("creationDate"));
                    objDiscussion.id = jsonDiscussion.getString("id");
                    objDiscussion.isEditable = jsonDiscussion.getBoolean("isEditable");
                    objDiscussion.isSelfAction = jsonDiscussion.getBoolean("isSelfAction");
                    objDiscussion.isSystem = jsonDiscussion.getBoolean("isSystem");
                    objDiscussion.isUnread = jsonDiscussion.getBoolean("isUnread");
                    objDiscussion.lastModifiedDate = LementUtility.convertJSONDate(jsonDiscussion.getString("lastModifiedDate"));
                    objDiscussion.message = jsonDiscussion.getString("message");
                    objDiscussion.simplifiedMessage = jsonDiscussion.getString("simplifiedMessage");
                    objDiscussion.subject = jsonDiscussion.getString("subject");
                    JSONObject jsonAuthor = jsonDiscussion.getJSONObject("author");
                    objDiscussion.authorAvatarFileld = jsonAuthor.getString("avatarFileId");
                    objDiscussion.authorId = jsonAuthor.getString("id");
                    objDiscussion.authorisInVacation = jsonAuthor.getBoolean("isInVacation");
                    objDiscussion.authorText = jsonAuthor.getString("text");
                    JSONObject jsonAuthorFrom = jsonDiscussion.getJSONObject("authorFrom");
                    objDiscussion.authorFromAvatarFileld = jsonAuthorFrom.getString("avatarFileId");
                    objDiscussion.authorFromId = jsonAuthorFrom.getString("id");
                    objDiscussion.authorFromisInVacation = jsonAuthorFrom.getBoolean("isInVacation");
                    objDiscussion.authorFromText = jsonAuthorFrom.getString("text");
                    JSONArray arrFiles = jsonDiscussion.getJSONArray("files");
                    objDiscussion.files = new TaskFile[arrFiles.length()];
                    for (int j = 0; j < arrFiles.length(); j++) {
                        JSONObject jsonFile = arrFiles.getJSONObject(j);
                        TaskFile objTaskFile = new TaskFile();
                        objTaskFile.id = jsonFile.getString("id");
                        objTaskFile.isFinal = jsonFile.getBoolean("isFinal");
                        objTaskFile.isInStorage = jsonFile.getBoolean("isInStorage");
                        objTaskFile.name = jsonFile.getString("name");
                        objTaskFile.revision = jsonFile.getInt("revision");
                        objTaskFile.size = jsonFile.getString("size");
                        objDiscussion.files[j] = objTaskFile;
                    }
                    arrList.add(objDiscussion);
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
            Resources res = getResources();
            listDiscussion = (ListView) rootView.findViewById(R.id.listDiscussions);  // List defined in XML ( See Below )
            /**************** Create Custom Adapter *********/
            adapter = new CustomDiscussionAdapterDocuments(CustomListView, arrList, res, cookieVal);
            listDiscussion.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private class AsyncMessageSender extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... param) {
            URL url;
            HttpURLConnection connection = null;
            try {

                //Create connection
                url = new URL(param[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Cookie", ".ASPXAUTH=" + cookieVal);
                connection.connect();
                String msg = replyMessage + txtMessage.getText();
					/*int idx = msg1.lastIndexOf("<p");
					String sub = msg1.substring(idx).replaceAll("<br>","");
					msg1 = msg1.substring(0,idx);
					msg1 = msg1+sub;
                    */
                //int idx = msg1.replace(msg1.substring(msg1.indexOf("<p "),),"");

					/*String msg2 = Html.toHtml(txtMessage.getText()).toString();
					String msg = Html.toHtml(txtMessage.getText()).replaceAll("<p dir=\"Itr\"><br><br>","").replaceAll("<p dir=\"Itr\"><br>","").replaceAll("<p dir=\"Itr\">","").replaceAll("</p>","");
					*/
                String urlParameters =
                        "objectId=" + URLEncoder.encode(objectID, "UTF-8") +
                                "&subject=" + URLEncoder.encode("", "UTF-8")+"&message=" + URLEncoder.encode(msg, "UTF-8") + "&fileIds=%5B%5D&employees=%5B%5D";


                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int acc = connection.getResponseCode();
                Log.v("StatusCode", Integer.toString(acc));
                //Get Response
					/*InputStream isError= connection.getErrorStream();


					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(isError));
					String line;
					StringBuffer response = new StringBuffer();
					while ((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();
					String strResponse = response.toString();
					JSONObject obj = new JSONObject(strResponse);
					Log.v("Response:",strResponse);
                    */
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            txtMessage.setText("");
            Resources res = getResources();
            listDiscussion = (ListView) rootView.findViewById(R.id.listDiscussions);
            arrList.clear();
            adapter = new CustomDiscussionAdapterDocuments(CustomListView, arrList, res, cookieVal);
            listDiscussion.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            (new AsyncDiscussionLoader()).execute(getString(R.string.Link) + "Services/Documents/Action/GetList.do?objectId=" + objectID);
        }


    }

    public void onReplyClick(String message){
        Log.v("Message:",message);
        replyMessage =message;
        txtMessage.setText("");
        txtMessage.setSelection(txtMessage.getText().length());
        txtMessage.requestFocus();
        InputMethodManager imm = (InputMethodManager) App.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtMessage, InputMethodManager.SHOW_IMPLICIT);
    }
}

