package es.edu.android.socialmusic.adapters;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.entities.User;


public class MyUserListAdapter extends ArrayAdapter<User> {
	private final String HOST_PROD = "http://social-music.herokuapp.com/";
	private final String HOST_DESA = "http://socialmusic.eastolfi.c9.io/";
	private static final String PREF_USER_LOGED = "userLoged";
	private static final String PREF_USER_MAIL = "userMail";
	Context mContext;
	int mLayoutResourceId;
	List<User> data = null;
	UserHolder holder;
	SharedPreferences preferencesUser;
	
	public MyUserListAdapter(Context context, int textViewResourceId, List<User> lst) {
		super(context, textViewResourceId, lst);
		
		mContext = context;
		mLayoutResourceId = textViewResourceId;
		data = lst;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.simple_user_list_element, parent, false);
			
			holder = new UserHolder();
			holder.txtNombre = (TextView) row.findViewById(R.id.txtFriendName);
			holder.txtApellidos = (TextView) row.findViewById(R.id.txtFriendSName);
			holder.btAddFriend = (ImageButton) row.findViewById(R.id.btnFriendAdd);
			
			row.setTag(holder);
		}
		else {
			holder = (UserHolder) row.getTag();
		}
		
		User user = data.get(position);
		holder.txtNombre.setText(user.nombre);
		holder.txtApellidos.setText(user.apellidos);
		holder.id = user.id;
		holder.btAddFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<String, Void, String>() {
					ProgressDialog dialog = new ProgressDialog(mContext);
					@Override
					protected void onPreExecute() {
						dialog.setMessage("Se está enviando tu petición...");
						dialog.setIndeterminate(true);
						dialog.setCancelable(false);
						dialog.show();
					}
					@Override
					protected String doInBackground(String... params) {
						String result = null;
						try {
							HttpClient client = new DefaultHttpClient();
							String uri = HOST_PROD + "usuarios/friends";
							HttpPost post = new HttpPost(uri);
							
							JSONObject jObj = new JSONObject();
							preferencesUser = mContext.getSharedPreferences(PREF_USER_LOGED, Context.MODE_PRIVATE);
							String userMail = preferencesUser.getString(PREF_USER_MAIL, null);
							
							if (userMail == null) return null;
							
							jObj.put("userMail", userMail);
							jObj.put("friendID", holder.id);
							
							post.setEntity(new StringEntity(jObj.toString()));
							post.setHeader("accept", "application/json");
							post.setHeader("content-type", "application/json");
							
							HttpResponse response = client.execute(post);
							result = EntityUtils.toString(response.getEntity());
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return result;
					}
					
					@Override
					protected void onPostExecute(String result) {
						if (result != null && result.equals("999")) {
							Toast.makeText(mContext, "Se ha enviado tu petición", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(mContext, "Error al enviar tu petición", Toast.LENGTH_SHORT).show();
						}
						dialog.cancel();
					}
				}.execute(holder.id);
			}
		});
		
		return row;
	}
	
	static class UserHolder {
		TextView txtNombre;
		TextView txtApellidos;
		ImageButton btAddFriend;
		String id;
	}
}
