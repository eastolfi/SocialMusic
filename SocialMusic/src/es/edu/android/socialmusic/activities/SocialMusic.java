package es.edu.android.socialmusic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import es.edu.android.socialmusic.R;

public class SocialMusic extends Activity {
	private static final String PREF_USER_LOGED = "userLoged";
	private static final String PREF_USER_MAIL = "userMail";
	SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_music);
		
		preferences = getSharedPreferences(PREF_USER_LOGED, Context.MODE_PRIVATE);
		
		TextView txt = (TextView) findViewById(R.id.textView1);
		txt.setText(preferences.getString(PREF_USER_MAIL, "Anonimo"));
	}
	
	private void goToLoginScreen() {
		Intent i = new Intent(getApplicationContext(), SocialLogin.class);
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
		if (item.getItemId() == R.id.menu_logout) {
			Editor edit = preferences.edit();
			edit.remove(PREF_USER_MAIL);
			edit.commit();
			goToLoginScreen();
		}
		return true;
//		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
