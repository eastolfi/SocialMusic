package es.edu.android.socialmusic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainScreen extends Activity {
	SharedPreferences prefs;
	Button btLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = getSharedPreferences("logPrefs", Context.MODE_PRIVATE);
		if (prefs.getString("logUsername", null) == null) {
			initLoginScreen();
		}
		else {
			initMainScreen();			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_screen, menu);
		return true;
	}
	
	private void initMainScreen() {
		setContentView(R.layout.activity_main_screen);
		((TextView) findViewById(R.id.txtUserLoged)).setText(
			"Bienvenid@ " + prefs.getString("logUsername", "Anónimo"));
	}
	
	private void initLoginScreen() {
		setContentView(R.layout.activity_login_screen);
		
		btLogin = (Button) findViewById(R.id.btnLogin);
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logIn();
			}
		});
		
		/********* TABS **********/
		Resources res = getResources();
		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();
		TabSpec spec = tabs.newTabSpec("tabLogin");
		spec.setContent(R.id.tabLogin);
		spec.setIndicator("", res.getDrawable(R.drawable.ic_tab_login));
		tabs.addTab(spec);
		spec=tabs.newTabSpec("tabRegister");
		spec.setContent(R.id.tabRegister);
		spec.setIndicator("", res.getDrawable(R.drawable.ic_tab_register));
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
		/*************************/
	}
	
	private void logIn() {
		Resources res = getResources();
		TextView txtUser = (TextView) findViewById(R.id.txtLoginUsername);
		TextView txtPswd = (TextView) findViewById(R.id.txtLoginPswd);
		if (txtUser.getText().toString().isEmpty()) {
			txtUser.setError("El usuario es obligatorio.", res.getDrawable(android.R.drawable.ic_dialog_alert));
		}
		else if (txtPswd.getText().toString().isEmpty()) {
			txtPswd.setError("La contraseña es obligatoria.", res.getDrawable(android.R.drawable.ic_dialog_alert));
		}
		else {
			if (txtUser.getText().toString().toLowerCase().equals("admin") && txtPswd.getText().toString().equals("123")) {
				Editor ed = getSharedPreferences("logPrefs", 0).edit();
				ed.putString("logUsername", txtUser.getText().toString());
				ed.commit();				
				initMainScreen();
			}
		}
		
	}
}
