package lementProApp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import lementProAppTree.model.TreeNode;
import lementProApp.R;
import lementProApp.holder.IconTreeItemHolder;
import lementProAppTree.view.AndroidTreeView;

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
import java.util.LinkedHashMap;
import java.util.List;

import lementProApp.model.Folder;
/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class DocumentFolders extends ActionBarActivity {
    private TextView statusBar;
    private AndroidTreeView tView;
    View rootView;
    ViewGroup containerView;
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "text";
    List<String> folderList = new ArrayList<String>();
    ExpandableListView listv;
    LinkedHashMap<String, Folder> listFolders = new LinkedHashMap<String, Folder>();
    String status = "RUNNING";
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";
    String cookieVal = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        SharedPreferences shared = getSharedPreferences(Login.SharedPrefs, MODE_PRIVATE);
        cookieVal = (shared.getString(strCookie, ""));
        if(cookieVal == null || cookieVal.equals("")){
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(DocumentFolders.this, Login.class);
            DocumentFolders.this.startActivity(i);
        }

        rootView = getLayoutInflater().inflate(R.layout.fragment_default, null, false);
        containerView = (ViewGroup) rootView.findViewById(R.id.container);
        (new AsyncListViewLoader()).execute(getString(R.string.Link) + "Services/Documents/Folder/GetTree.do");
        setContentView(rootView);
        //setHasOptionsMenu(true);
    }

    private class AsyncListViewLoader extends AsyncTask<String, Void, Void> {
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
                Folder objFolder;
                JSONArray arr = new JSONArray(serverResponse);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonFolder = arr.getJSONObject(i);
                    String name = jsonFolder.getString("text");
                    String folderkey = jsonFolder.getString("folderKey");
                    boolean hasGroupings = jsonFolder.getBoolean("hasGroupings");
                    boolean hasSubFolders = jsonFolder.getBoolean("hasSubFolders");
                    boolean isGrouping = jsonFolder.getBoolean("isGrouping");
                    String parentID = jsonFolder.getString("parentId");

                    JSONObject objkeys = new JSONObject(jsonFolder.getString("folderKey"));
                    //JSONObject objkeys = objFolder.getJSONObject("folderKey");
                    int ID = objkeys.getInt("id");
                    objFolder = new Folder(name, ID, folderkey, hasSubFolders, hasGroupings, isGrouping, parentID);
                    listFolders.put(folderkey, objFolder);
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
            TreeNode root = TreeNode.root();
            TreeNode child;
            List<Folder> arrFolder = new ArrayList<Folder>(listFolders.values());
            for (int i = 0; i < arrFolder.size(); i++) {
                Folder obj = arrFolder.get(i);
                child = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, obj.text),obj.folderkey);
                root.addChild(child);
            }
            //LinearLayout llTree = (LinearLayout) findViewById(R.id.LLTree);
            //ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
            tView = new AndroidTreeView(getApplicationContext(), root);
            tView.setDefaultAnimation(true);
            tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
            tView.setDefaultViewHolder(IconTreeItemHolder.class);
            tView.setDefaultNodeClickListener(nodeClickListener);
            tView.setDefaultNodeLongClickListener(nodeLongClickListener);
            try {
                containerView.addView(tView.getView());
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expandAll:
                tView.expandAll();
                break;

            case R.id.collapseAll:
                tView.collapseAll();
                break;
        }
        return true;
    }

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            Log.v("LongClick:","Clicked:");
            Log.v("LongClick:",node.nodeID);
            Folder objFolder = listFolders.get(node.nodeID);
            Intent i = new Intent(DocumentFolders.this, TasksList.class);
            i.putExtra("link", getString(R.string.Link) + "Services/Document/GetList.do");
            i.putExtra("folderKey", objFolder.folderkey);
            i.putExtra("type", "2");
            DocumentFolders.this.startActivity(i);
        }
    };

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            //fillDownloadsFolder(node);
            //statusBar.setText("Last clicked: " + item.text);
            AsyncTask obj = null;
            Folder objFolder = listFolders.get(node.nodeID);
            if(!objFolder.hasSubFolders){
                Intent i = new Intent(DocumentFolders.this, TasksList.class);
                i.putExtra("link", getString(R.string.Link) + "Services/Document/GetList.do");
                i.putExtra("folderKey", objFolder.folderkey);
                i.putExtra("type", "2");
                DocumentFolders.this.startActivity(i);
            }
            if(!node.mSelected) {
                try {
                    obj = (new AsyncSubFolderLoader()).execute(node);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                while(true){
                    if(status == "RUNNING"){
                        try{
                            Thread.sleep(500);
                            Log.v("AsyncTask:",status);
                        }catch(Exception e){}
                    }else{
                        status = "RUNNING";
                        break;
                    }

                }
            }

        }
    };

    private class AsyncSubFolderLoader extends AsyncTask<Object, Void, Integer> {
        protected Integer doInBackground(Object... param) {
            TreeNode node = (TreeNode) param[0];
            String folderID = node.nodeID;
            try {
                HttpClient client = new DefaultHttpClient();
                Folder obj = listFolders.get(folderID);
                if (obj.hasSubFolders) {
                    //JSONObject objkeys = new JSONObject(obj.folderkey);
                    String query = URLEncoder.encode(folderID, "utf-8");
                    String link1 = getString(R.string.Link) + "/Services/Documents/Folder/GetSubFolders.do?folderKey=" + query;
                    HttpPost post1 = new HttpPost(link1);
                    post1.setHeader("Cookie", ".ASPXAUTH=" + cookieVal + ";");
                    HttpResponse httpResponse = client.execute(post1);
                    int connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                    Log.d("--", "Connection code: " + connectionStatusCode + " for request: ");
                    HttpEntity entity = httpResponse.getEntity();
                    String subfolders = EntityUtils.toString(entity);
                    JSONArray arrsubFolder = new JSONArray(subfolders);
                    for (int j = 0; j < arrsubFolder.length(); j++) {
                        JSONObject jsonFolder = arrsubFolder.getJSONObject(j);
                        String name = jsonFolder.getString("text");
                        String folderkey = jsonFolder.getString("folderKey");
                        boolean hasGroupings = jsonFolder.getBoolean("hasGroupings");
                        boolean hasSubFolders = jsonFolder.getBoolean("hasSubFolders");
                        boolean isGrouping = jsonFolder.getBoolean("isGrouping");
                        String parentID = jsonFolder.getString("parentId");

                        JSONObject objkeys = new JSONObject(jsonFolder.getString("folderKey"));
                        //JSONObject objkeys = objFolder.getJSONObject("folderKey");
                        int ID = objkeys.getInt("id");
                        Folder objFolder = new Folder(name, ID, folderkey, hasSubFolders, hasGroupings, isGrouping, parentID);
                        listFolders.put(folderkey, objFolder);
                        //subFolderKeys.add(folderkey);
                        //getTreeView().addNode(node, newFolder);
                        TreeNode child = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, name),folderkey);
                        node.addChild(child);
                        Log.v("Async:",Integer.toString(j) + "   "+Integer.toString(arrsubFolder.length()));
                    }
                    node.mSelected = true;
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

            status = "FINISHED";
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
