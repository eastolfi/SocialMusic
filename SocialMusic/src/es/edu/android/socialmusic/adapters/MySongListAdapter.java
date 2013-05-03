package es.edu.android.socialmusic.adapters;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

import com.echonest.api.v4.Song;

import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.config.Config;
import es.edu.android.socialmusic.constants.IConstantsCode;
import es.edu.android.socialmusic.entities.MySong;

public class MySongListAdapter extends ArrayAdapter<MySong> {
	private static final String HOST = Config.HOST_PROD;
	private static final String PREF_USER_LOGED = "userLoged";
	private static final String PREF_USER_MAIL = "userMail";
	Context mContext;
	int mLayoutResourceId;
	List<MySong> data = null;
	SharedPreferences prefs;

	public MySongListAdapter(Context context, int layoutResourceId, List<MySong> lst) {
		super(context, layoutResourceId, lst);
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		data = lst;
		prefs = context.getSharedPreferences(PREF_USER_LOGED, Context.MODE_PRIVATE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SongHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			
			holder = new SongHolder();
			holder.txtSong = (TextView) row.findViewById(R.id.txtSongTitle);
			holder.txtArtist = (TextView) row.findViewById(R.id.txtArtistTitle);
			holder.btnSongFav = (ImageButton) row.findViewById(R.id.btnSongFav);
			holder.btnSongAdd = (ImageButton) row.findViewById(R.id.btnSongAdd);
			holder.btnSongPlay = (ImageButton) row.findViewById(R.id.btnSongPlay);
			
			row.setTag(holder);
		}
		else {
			holder = (SongHolder) row.getTag();
		}
		
		String userMail = prefs.getString(PREF_USER_MAIL, "");
		MySong song = data.get(position);
		holder.txtSong.setText(song.song);
		holder.txtArtist.setText(song.artist);
		holder.id = song.id;
		
		MyOnClickListener myClick = new MyOnClickListener(mContext, holder, userMail);
		holder.btnSongFav.setOnClickListener(myClick);
		holder.btnSongAdd.setOnClickListener(myClick);
		holder.btnSongPlay.setOnClickListener(myClick);
		
		return row;
	}

	
	static class SongHolder {
		TextView txtSong;
		TextView txtArtist;
		String id;
		ImageButton btnSongFav;
		ImageButton btnSongAdd;
		ImageButton btnSongPlay;
	}
	
	static class MyOnClickListener implements OnClickListener {
		SongHolder mHolder;
		String mUserMail;
		Context mContext;
		String type = "";
		
		public MyOnClickListener(Context context, SongHolder holder, String userMail) {
			mContext = context;
			mHolder = holder;
			mUserMail = userMail;
		}
		
		@Override
		public void onClick(View v) {
			JSONObject jObj = new JSONObject();
			try {
				jObj.put("titulo", mHolder.txtSong.getText().toString());
				jObj.put("artista", mHolder.txtArtist.getText().toString());
				jObj.put("echoNestID", mHolder.id);
				jObj.put("userMail", mUserMail);
				if (v.getId() == mHolder.btnSongFav.getId()) {
					type = IConstantsCode.TYPE_SONG_NEWS_FAV;
				}
				else if (v.getId() == mHolder.btnSongAdd.getId()) {
					type = IConstantsCode.TYPE_SONG_NEWS_ADD;
				}
				else if (v.getId() == mHolder.btnSongPlay.getId()) {
					type = IConstantsCode.TYPE_SONG_NEWS_PLAY;
				}
				jObj.put("type", type);
				
				new AsyncTask<JSONObject, Void, String>() {
					ProgressDialog dialog = new ProgressDialog(mContext);
					@Override
					protected void onPreExecute() {
						if (type.equals(IConstantsCode.TYPE_SONG_NEWS_FAV))
							dialog.setMessage(IConstantsCode.VAL_SONG_NEWS_FAV);
						else if (type.equals(IConstantsCode.TYPE_SONG_NEWS_ADD))
							dialog.setMessage(IConstantsCode.VAL_SONG_NEWS_ADD);
						else if (type.equals(IConstantsCode.TYPE_SONG_NEWS_PLAY))
							dialog.setMessage(IConstantsCode.VAL_SONG_NEWS_PLAY);
							
						dialog.setIndeterminate(true);
						dialog.setCancelable(false);
						dialog.show();
					}
					@Override
					protected String doInBackground(JSONObject... params) {
						String result = null;
						try {
							HttpClient client = new DefaultHttpClient();
							String uri = HOST + "noticias/canciones/add";
							HttpPost post = new HttpPost(uri);
							post.setHeader("accept", "application/json");
							post.setHeader("content-type", "application/json");
							post.setEntity(new StringEntity(params[0].toString()));
							HttpResponse response = client.execute(post);
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
						if (result != null && result.equals(IConstantsCode.SUCCESS_CODE_ADD_SONG_NEWS)) {
							Toast.makeText(mContext, "Noticia creada correctamente", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(mContext, "Fallo al crear la noticia", Toast.LENGTH_SHORT).show();
						}
						dialog.cancel();
					}
				}.execute(jObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//TODO Click on element -> view detail + 'cache'; onBack -> listView from 'cache'
}
