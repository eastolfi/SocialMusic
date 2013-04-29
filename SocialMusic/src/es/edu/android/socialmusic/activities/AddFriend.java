package es.edu.android.socialmusic.activities;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.adapters.MyUserListAdapter;
import es.edu.android.socialmusic.entities.User;

public class AddFriend extends Activity {
	private final String HOST_PROD = "http://social-music.herokuapp.com/";
	private final String HOST_DESA = "http://socialmusic.eastolfi.c9.io/";
	Context ctx;
	EditText txtUser;
	ListView lstUsers;
	Button btSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ctx = this;
		
		txtUser = (EditText) findViewById(R.id.txtUserToAdd);
		lstUsers = (ListView) findViewById(R.id.lstUsersMatch);
		
		btSearch = (Button) findViewById(R.id.btSearchUserToAdd);
		btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (txtUser.getText().toString().isEmpty()) {
					txtUser.setError("Este campo es obligatorio.");
				}
				else {
					new AsyncTask<String, Void, String>() {
						ProgressDialog dialog = new ProgressDialog(ctx);
						
						@Override
						protected void onPreExecute() {
							dialog.setMessage("Obteniendo lista de usuarios...");
							dialog.setIndeterminate(true);
							dialog.setCancelable(false);
							dialog.show();
						}
						
						@Override
						protected String doInBackground(String... params) {
							String result = null;
							try {
								HttpClient client = new DefaultHttpClient();
								String uri = HOST_PROD + "usuarios/" + params[0]; 
								HttpGet get = new HttpGet(uri);
								HttpResponse response = client.execute(get);
								result = EntityUtils.toString(response.getEntity());
								
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return result;
						}

						@Override
						protected void onPostExecute(String result) {
							if (result != null) {
								try {
									JSONArray jArray = new JSONArray(result);
									
									ArrayList<User> users = new ArrayList<User>();
									for (int i=0; i<jArray.length(); i++) {
										JSONObject jObj = jArray.getJSONObject(i);
										User user = new User();
										user.nombre = jObj.getString("nombre");
										user.apellidos = jObj.getString("apellidos");
										user.id = jObj.getString("_id");
										users.add(user);
									}
									
									MyUserListAdapter mAdapter = new MyUserListAdapter(
													ctx, R.layout.simple_user_list_element, users);
									lstUsers.setAdapter(mAdapter);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							dialog.cancel();
						}
						
					}.execute(txtUser.getText().toString());
				}
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_friend, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
