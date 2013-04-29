package es.edu.android.socialmusic.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;

import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.adapters.MySongListAdapter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchSong extends Activity {
	Context ctx;
	private final String API_KEY = "QFOJ7UZX0SSIZCSXG";
	private EchoNestAPI enAPI;
	ListView lstSongs;
	EditText txtSongNameView, txtArtistView;
	Button btSearch;
	TextView labelHelp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_song);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ctx = this;
		enAPI = new EchoNestAPI(API_KEY);
		
		txtSongNameView = (EditText) findViewById(R.id.txtSongName);
		txtArtistView = (EditText) findViewById(R.id.txtArtistName);
		lstSongs = (ListView) findViewById(R.id.lstSongs);
		labelHelp = (TextView) findViewById(R.id.txtSongSearchHelp);
		
		btSearch = (Button) findViewById(R.id.btSearchSong);
		btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String txtSongName = txtSongNameView.getText().toString();
				String txtArtistName = txtArtistView.getText().toString();
				
				if (txtSongName.isEmpty()) {
					txtSongNameView.setError("Este campo es obligatorio");
					return;
				}
				
				new AsyncTask<String, Void, List<Song>>() {
					ProgressDialog pDialog = new ProgressDialog(ctx);
					@Override
					protected void onPreExecute() {
						pDialog.setMessage("Buscando canciones...");
						pDialog.setIndeterminate(true);
						pDialog.setCancelable(false);
						pDialog.show();
					}
					
					
					@Override
					protected List<Song> doInBackground(String... params) {
						String song = params[0];
						String artist = params[1];
						try {
							SongParams p = new SongParams();
							p.setTitle(song);
							if (!artist.isEmpty()) p.setArtist(artist);//p.setArtist("parkway drive");
							
							List<Song> songs = enAPI.searchSongs(p);
							Log.d("s", "?");
							return songs;
						} catch (EchoNestException e) {
							e.printStackTrace();
							return null;
						}
					}
					
					@Override
					protected void onPostExecute(List<Song> result) {
						MySongListAdapter songAdapter;
						if (result != null) {
							songAdapter = new MySongListAdapter(ctx, R.layout.simple_list_element, (ArrayList<Song>) result);
							lstSongs.setAdapter(songAdapter);
						}
						
						if (txtArtistView.getText().toString().isEmpty()) {
							labelHelp.setText("¿No has encontrado tu canción? Prueba a añadir un artista.");
							labelHelp.setVisibility(View.VISIBLE);
						}
						else {
							labelHelp.setVisibility(View.GONE);
						}
						pDialog.cancel();
					}
					
				}.execute(txtSongName, txtArtistName);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_song, menu);
		
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
