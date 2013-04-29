package es.edu.android.socialmusic.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;

import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.adapters.MySongListAdapter;

public class SocialMusic extends Activity {
	// Constants
	private static final String PREF_USER_LOGED = "userLoged";
	private static final String PREF_USER_MAIL = "userMail";
	private static final String PREF_USER_REMEMBER = "userRemember";
	private final String API_KEY = "QFOJ7UZX0SSIZCSXG";
	// Utils
	Context ctx;
	private EchoNestAPI enAPI;
	SharedPreferences preferencesUser;
//	private Spotify spfAPI;
	// UI references
	ListView currentSongs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_music);
		
		ctx = this;
		enAPI = new EchoNestAPI(API_KEY);
		
		preferencesUser = getSharedPreferences(PREF_USER_LOGED, Context.MODE_PRIVATE);
		if (!preferencesUser.contains(PREF_USER_MAIL)) {
			goToLoginScreen();
		}
		
		TextView txt = (TextView) findViewById(R.id.textView1);
		txt.setText(preferencesUser.getString(PREF_USER_MAIL, "Anonimo"));

		currentSongs = (ListView) findViewById(R.id.lstCurrentSongs);
		TextView getCurrentSongs = (TextView) findViewById(R.id.getCurrentSongs);
		getCurrentSongs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, List<Song>>() {
					ProgressDialog pDialog = new ProgressDialog(ctx);
					@Override
					protected void onPreExecute() {
						// TODO Loading icon
						pDialog.setMessage("Cargando canciones más escuchadas...");
						pDialog.setIndeterminate(true);
						pDialog.setCancelable(false);
						pDialog.show();
					}
					@Override
					protected List<Song> doInBackground(Void... params) {
						try {
							SongParams p = new SongParams();
							p.add("sort", "song_hotttnesss-desc");
							p.add("bucket", "song_hotttnesss");
							List<Song> hotttestSongs = enAPI.searchSongs(p);
							
							return hotttestSongs;
						} catch (EchoNestException e) {
							e.printStackTrace();
							return null;
						}
					}

					@Override
					protected void onPostExecute(List<Song> result) {
						if (result != null) {
							List<Song> songs = new ArrayList<Song>();
							for (Song song : result) {
								if (!songs.contains(song)) songs.add(song);
							}
							MySongListAdapter songAdapter = new MySongListAdapter(ctx, R.layout.simple_list_element, songs);
							currentSongs.setAdapter(songAdapter);
						}
						pDialog.cancel();
					}

					
				}.execute();
			}
		});
	}
	
	private void goToLoginScreen() {
		Intent i = new Intent(getApplicationContext(), SocialLogin.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.social_music, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.menu_logout:
			Editor edit = preferencesUser.edit();
			edit.remove(PREF_USER_MAIL);
			edit.commit();
			goToLoginScreen();
			
			break;
		case R.id.menu_add_friend:
			i = new Intent(ctx, AddFriend.class);
			startActivity(i);
			
			break;
		case R.id.menu_search_song:
			i = new Intent(ctx, SearchSong.class);
			startActivity(i);
			
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		if (!preferencesUser.getBoolean(PREF_USER_REMEMBER, false)) {
			Editor edit = preferencesUser.edit();
			edit.remove(PREF_USER_LOGED);
			edit.remove(PREF_USER_MAIL);
			edit.commit();
		}
		
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	
}
