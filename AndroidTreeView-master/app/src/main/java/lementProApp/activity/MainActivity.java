package lementProApp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import lementProApp.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    String strCookie = ".ASPXAUTH";
    String strExpire = "expires";
    String strPath = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared = getSharedPreferences(Login.SharedPrefs, MODE_PRIVATE);
        String cookieval = (shared.getString(strCookie, ""));
        if(cookieval == null || cookieval.equals("")){
            SharedPreferences.Editor editor = shared.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(MainActivity.this, Login.class);
            MainActivity.this.startActivity(i);
        }
        final LinkedHashMap<String, Class<?>> listItems = new LinkedHashMap<>();
        listItems.put("Tasks", TaskFolders.class);
        listItems.put("Documents", DocumentFolders.class);
        listItems.put("Projects", ProjectFolders.class);
        //listItems.put("2d scrolling", TwoDScrollingFragment.class);


        final List<String> list = new ArrayList(listItems.keySet());
        final ListView listview = (ListView) findViewById(R.id.listview);
        final SimpleArrayAdapter adapter = new SimpleArrayAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Class<?> clazz = //listItems.values().toArray(new Class<?>[]{})[position];
                try {
                    if (position == 0) {
                        Intent i = new Intent(MainActivity.this, TaskFolders.class);
                        MainActivity.this.startActivity(i);
                    } else if (position == 1) {
                        Intent i = new Intent(MainActivity.this, DocumentFolders.class);
                        MainActivity.this.startActivity(i);
                    } else if (position == 2) {
                        Intent i = new Intent(MainActivity.this, ProjectFolders.class);
                        MainActivity.this.startActivity(i);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private class SimpleArrayAdapter extends ArrayAdapter<String> {
        public SimpleArrayAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}