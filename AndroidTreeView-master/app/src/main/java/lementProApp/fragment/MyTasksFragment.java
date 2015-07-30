package lementProApp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import lementProAppTree.model.TreeNode;
import lementProApp.model.Folder;
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

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class MyTasksFragment extends Fragment {
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
    String folderKey = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            folderKey = getArguments().getString("folderKey");
            String query = URLEncoder.encode(folderKey, "utf-8");
            rootView = inflater.inflate(R.layout.fragment_default, null, false);
            containerView = (ViewGroup) rootView.findViewById(R.id.container);
            (new AsyncListViewLoader()).execute(getString(R.string.Link) + "Services/Task/GetList.do?folderKey=" + folderKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        return rootView;

    }
    private class AsyncListViewLoader extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... param) {

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(param[0]);
                String token = "FDC2A0FB867DF24B96A74CB30C0344EC9E357010DE191B79ABC9C7C52F129DBD8B82674F758CA4F6FF805CCB9E6F997FE33C7AAB5CAC53C89E3F09B2EABFE9F6519790B9DFAB446956635C485D5504F6E7EBA2E3D73C36B443394B22AF93957C304FE64632DB4558E869074B43170F4591C5BEE96B5C9DC04FD9AAF7D84E1FACDAB7C79A0E089757C5B6B064332ACCA2ED7575A6F5CD0519D2C65C037575E0ABE1D02937897E53A1F8D9EA6909C4A98C113A46A6EFE20825233F8804219CC125";
                post.setHeader("Cookie", ".ASPXAUTH=" + token + ";");
                Log.d("--", "Try to open => " + param[0]);
                //List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("loginName", "muhammad"));
                //params.add(new BasicNameValuePair("password", "123"));
                //post.setEntity(new UrlEncodedFormEntity(params));
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
            /*LinearLayout llTree = (LinearLayout) findViewById(R.id.LLTree);*/
            //ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
            tView = new AndroidTreeView(getActivity(), root);
            tView.setDefaultAnimation(true);
            tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
            tView.setDefaultViewHolder(IconTreeItemHolder.class);
            tView.setDefaultNodeClickListener(nodeClickListener);
            containerView.addView(tView.getView());

          /*  super.onPostExecute(result);
            ExpandableListAdapter adapter = new ExpandableListAdapter(getApplicationContext(),folderList,folderChild);
                  // MyTasksFragment.this, folderList,
                //    R.layout.task_list_item, new String[] {TAG_NAME}, new int[] {R.id.txtMyTasks});
            listv = (ExpandableListView)findViewById(R.id.listTasks);
            listv.setAdapter(adapter);*/
        }

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    private int counter = 0;

    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + (counter++)),"");
        node.addChild(downloads);
        if (counter < 5) {
            fillDownloadsFolder(downloads);
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            //fillDownloadsFolder(node);
            //statusBar.setText("Last clicked: " + item.text);
            AsyncTask obj = null;
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
                //List<Folder> arrFolder = new ArrayList<Folder>(listFolders.values());
                //int size = arrFolder.size();
                //int current = 0;
                // while (current < size) {
                // Log.d("--", "current:" + current + "   size: " + size);
                Folder obj = listFolders.get(folderID);
                //List<String> subFolderKeys = new ArrayList<String>();
                String token = "FDC2A0FB867DF24B96A74CB30C0344EC9E357010DE191B79ABC9C7C52F129DBD8B82674F758CA4F6FF805CCB9E6F997FE33C7AAB5CAC53C89E3F09B2EABFE9F6519790B9DFAB446956635C485D5504F6E7EBA2E3D73C36B443394B22AF93957C304FE64632DB4558E869074B43170F4591C5BEE96B5C9DC04FD9AAF7D84E1FACDAB7C79A0E089757C5B6B064332ACCA2ED7575A6F5CD0519D2C65C037575E0ABE1D02937897E53A1F8D9EA6909C4A98C113A46A6EFE20825233F8804219CC125";
                if (obj.hasSubFolders) {
                    //JSONObject objkeys = new JSONObject(obj.folderkey);
                    String query = URLEncoder.encode(folderID, "utf-8");
                    String link1 = getString(R.string.Link) + "Services/Tasks/Folder/GetSubFolders.do?folderKey=" + query;
                    HttpPost post1 = new HttpPost(link1);
                    post1.setHeader("Cookie", ".ASPXAUTH=" + token + ";");
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
