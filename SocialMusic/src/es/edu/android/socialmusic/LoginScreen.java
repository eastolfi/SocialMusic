package es.edu.android.socialmusic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		
//		Button bt = (Button) findViewById(R.id.btGoMain);
//		bt.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				Intent i = new Intent(LoginScreen.this, MainScreen.class);
////				i.setAction(Intent.ACTION_MAIN);
////				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
////						   Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
////						   Intent.FLAG_ACTIVITY_NO_HISTORY);
////				startActivity(i);
//				Editor ed = getSharedPreferences("logPrefs", 0).edit();
//				ed.putBoolean("isLoged", true);
//				ed.commit();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login_screen, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
