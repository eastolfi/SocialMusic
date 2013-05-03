package es.edu.android.socialmusic.activities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import es.edu.android.socialmusic.R;
import es.edu.android.socialmusic.config.Config;
import es.edu.android.socialmusic.constants.IConstantsCode;

/**
 * Actividad encargada del login y registro en la aplicacion
 * 
 * @author e.astolfi
 */
public class SocialLogin extends Activity {
	// Constants
	private final String HOST = Config.HOST_PROD;
	private static final String ACTION_LOGIN = "login";
	private static final String ACTION_REGISTER = "register";
	private static final String PREF_USER_LOGED = "userLoged";
	private static final String PREF_USER_MAIL = "userMail";
	private static final String PREF_USER_REMEMBER = "userRemember";
	// Utils
	private UserLoginTask mAuthTask = null;
	private SharedPreferences preferences;
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mPasswordRepeat;
	private String mNombre;
	private String mApellidos;
	// UI references
	private ViewSwitcher layoutSwitcher;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordRepeatView;
	private EditText mNombreView;
	private EditText mApellidosView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_login_register);
		
		/******* Events ********/
		// Declaramos los eventos para Views
		final OnEditorActionListener mPasswordEditorListener = new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin(ACTION_LOGIN);
					return true;
				}
				return false;
			}
		};
		final OnEditorActionListener mPasswordRepeatEditorListener = new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin(ACTION_REGISTER);
					return true;
				}
				return false;
			}
		};
		
		/***** Switcher *****/
		// Obtenemos el Switcher y registramos los eventos de los enlaces que lo activaran
		layoutSwitcher = (ViewSwitcher) findViewById(R.id.layoutSwitcher);	//TODO Add animation
		findViewById(R.id.txtToRegister).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Mostramos el formulario de registro
				mEmailView = (EditText) findViewById(R.id.emailReg);
				mPasswordView = (EditText) findViewById(R.id.passwordReg);
				mPasswordView.setOnEditorActionListener(mPasswordEditorListener);
				mNombreView = (EditText) findViewById(R.id.nombreReg);
				mApellidosView = (EditText) findViewById(R.id.apellidosReg);
				mLoginFormView = findViewById(R.id.registration_form);
				layoutSwitcher.showNext();
			}
		});
		findViewById(R.id.txtToLogin).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Mostramos el formulario de login
				mEmailView = (EditText) findViewById(R.id.email);
				mPasswordView = (EditText) findViewById(R.id.password);
				mPasswordView.setOnEditorActionListener(mPasswordEditorListener);
				mLoginFormView = findViewById(R.id.login_form);
				layoutSwitcher.showPrevious();
			}
		});
		/********************/
		
		// Obtenemos todos los campos del formulario y registramos los eventos necesarios
		mEmailView = (EditText) findViewById(R.id.email);
		
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(mPasswordEditorListener);
		
		mPasswordRepeatView = (EditText) findViewById(R.id.passwordRepeat);
		mPasswordRepeatView.setOnEditorActionListener(mPasswordRepeatEditorListener);
		
		mNombreView = (EditText) findViewById(R.id.nombreReg);
		mApellidosView = (EditText) findViewById(R.id.apellidosReg);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin(ACTION_LOGIN);
			}
		});
		findViewById(R.id.register_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptLogin(ACTION_REGISTER);
			}
		});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 * @param action 
	 */
	public void attemptLogin(String action) {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mPasswordRepeatView.setError(null);
		mNombreView.setError(null);
		mApellidosView.setError(null);
		

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		if (action.equals(ACTION_REGISTER)) {
			mPasswordRepeat = mPasswordRepeatView.getText().toString();
			mNombre = mNombreView.getText().toString();
			mApellidos = mApellidosView.getText().toString();
		}

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (action.equals(ACTION_REGISTER)) {
			if (mPasswordRepeat.isEmpty()) {
				mPasswordRepeatView.setError(getString(R.string.error_field_required));
				focusView = mPasswordRepeatView;
				cancel = true;
			}
			else if (!mPasswordRepeat.equals(mPassword)) {
				mPasswordRepeatView.setError(getString(R.string.error_password_not_match));
				focusView = mPasswordRepeatView;
				cancel = true;
			}
			if (mNombre.isEmpty()) {
				mNombreView.setError(getString(R.string.error_field_required));
				focusView = mNombreView;
				cancel = true;
			}
			else if (mNombre.length() < 3) {
				mNombreView.setError(getString(R.string.error_invalid_name));
				focusView = mNombreView;
				cancel = true;
			}
			
			if (mApellidos.isEmpty()) {
				mApellidosView.setError(getString(R.string.error_field_required));
				focusView = mApellidosView;
				cancel = true;
			}
		}
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}
		

		if (cancel) {
			// There was an error; don't attempt login and focus the first form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(action);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			/*************/ // Ocultamos el teclado
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			/*************/
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			// Mostramos la animacion
			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			//Ocultamos el formulario
			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Sub clase asincrona para el login / registro 
	 *
	 * @author e.astolfi
	 */
	public class UserLoginTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String response = IConstantsCode.ERROR_CODE_RESPONSE_NULL;
			try {
				// Simulate network access. ???????????????
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return IConstantsCode.ERROR_CODE_SIMULATE_NETWORK;
			}
			
			try {
				HttpClient client = new DefaultHttpClient();
				HttpUriRequest req;
				// Preparamos la request para el login
				if (params[0].equals(ACTION_LOGIN)) {
					String uri = HOST + "login/" + mEmail + "/" + mPassword;
					req = new HttpGet(uri);
				}
				// Preparamos la request para el registro
				else {
					String uri = HOST + "register";
					HttpPost post = new HttpPost(uri);
					
					JSONObject jNewUser = new JSONObject();
					jNewUser.put("nombre", mNombre);
					jNewUser.put("apellidos", mApellidos);
					jNewUser.put("correo", mEmail);
					jNewUser.put("password", mPassword);
					post.setEntity(new StringEntity(jNewUser.toString()));
					post.setHeader("accept", "application/json");
					post.setHeader("content-type", "application/json");
					req = post;
				}
				// Efectuamos la llamada de login / registro
				HttpResponse execute = client.execute(req);
				response = EntityUtils.toString(execute.getEntity());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(final String response) {
			mAuthTask = null;
			showProgress(false);

			// Dependiendo del codigo devuelto, redirigimos a la pantalla de login o mostramos el error
			if (response.equals(IConstantsCode.SUCCESS_CODE_LOGIN)) {
				goToMainScreen(false);
			}
			else if (response.equals(IConstantsCode.SUCCESS_CODE_REGISTER)) {
				goToMainScreen(false);
			}
			else if (response.equals(IConstantsCode.ERROR_CODE_LOGIN_FAILED)) {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
			else if (response.equals(IConstantsCode.ERROR_CODE_EXISTING_USER)) {
				mEmailView.setError(getString(R.string.error_used_email));
				mEmailView.requestFocus();
			}
			else {
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	/**
	 * Redirige a la pantalla principal
	 * 
	 * @param alredyLoged
	 */
	private void goToMainScreen(boolean alredyLoged) {
		// Si no se ha logeado aun (viene del registro)
		if (!alredyLoged) {
			preferences = getSharedPreferences(PREF_USER_LOGED, Context.MODE_PRIVATE);
			Editor edit = preferences.edit();
			edit.putString(PREF_USER_MAIL, mEmail);
			CheckBox ck = (CheckBox) findViewById(R.id.checkRecordar);
			edit.putBoolean(PREF_USER_REMEMBER, ck.isChecked());
			edit.commit();
		}
		// Lanzamos la actividad
		Intent i = new Intent(getApplicationContext(), SocialMusic.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}
	
	@Override
	public void onBackPressed() {
		finish();	//TODO history_remove
	}
}
