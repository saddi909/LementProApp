package lementProApp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lementProApp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class Login extends ActionBarActivity {
    public static final String SharedPrefs = "Cookie" ;
    SharedPreferences sharedpreferences;
    TextView txtErrorMessage;
    boolean isSuccess = false;
    String strResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE);
        txtErrorMessage = (TextView) findViewById(R.id.txtErrorMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        return true;
    }

    public void btnLoginClickListener(final View v) {
        txtErrorMessage.setVisibility(View.GONE);
        (new AsyncLogin()).execute(getString(R.string.Link) + "Services/Authentication/Login.do");
    }
    private class AsyncLogin extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... param) {
            try {
                EditText txtLogin = (EditText) findViewById(R.id.txtLogin);
                EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
                /*List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                DefaultHttpClient client = new DefaultHttpClient();
                CookieStore cookieStore = new BasicCookieStore();
                client.setCookieStore(cookieStore);
                HttpPost post = new HttpPost(param[0]);
                pairs.add(new BasicNameValuePair("loginName", txtLogin.getText().toString()));
                pairs.add(new BasicNameValuePair("password", txtPassword.getText().toString()));
                post.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse httpResponse = client.execute(post);
                int connectionStatusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d("--", "Connection code: " + connectionStatusCode + " for request: " + param[0]);
                */
                    URL url;
                    HttpURLConnection connection = null;
                    try {

                        //Create connection
                        url = new URL(param[0]);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("loginName", txtLogin.getText().toString());
                        connection.setRequestProperty("password", txtPassword.getText().toString());

                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        String urlParameters =
                                "loginName=" + URLEncoder.encode(txtLogin.getText().toString(), "UTF-8") +
                                        "&password=" + URLEncoder.encode(txtPassword.getText().toString(), "UTF-8");
                        //Send request
                        DataOutputStream wr = new DataOutputStream(
                                connection.getOutputStream());
                        wr.writeBytes(urlParameters);
                        wr.flush();
                        wr.close();

                        //Get Response
                        InputStream is = connection.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuffer response = new StringBuffer();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        strResponse = response.toString();
                        JSONObject obj = new JSONObject(strResponse);
                        if (obj.getString("isSuccess").equals("true")){
                            isSuccess = true;
                            Log.v("Response:", strResponse);
                        //String aa = connection.getHeaderField("Set-Cache");
                        // Log.v("Response:", aa);
                        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                            String val = header.getValue().toString();
                            if (header.getKey() != null) {
                                String key = header.getKey().toString();
                                Log.i("Headers", "Headers : " + header.getKey() + "=" + val);
                                if (key.equals("Set-Cookie")) {
                                    String cookieSplit[] = val.split(";");
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    String nameValue[] = cookieSplit[0].split("=");
                                    editor.putString(nameValue[0].substring(1), nameValue[1]);
                                    nameValue = cookieSplit[1].split("=");
                                    editor.putString(nameValue[0], nameValue[1]);
                                    nameValue = cookieSplit[2].split("=");
                                    editor.putString(nameValue[0], nameValue[1].substring(0,nameValue[1].length()-1));
                                    editor.commit();
                                }
                            }
                        }
                    }else{
                          isSuccess = false;
                        }
                        }catch(Exception e){

                            e.printStackTrace();
                            return null;

                        }finally{

                            if (connection != null) {
                                connection.disconnect();
                            }
                        }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if(isSuccess){
                Intent i = new Intent(Login.this, MainActivity.class);
                Login.this.startActivity(i);
            }else{
                txtErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
