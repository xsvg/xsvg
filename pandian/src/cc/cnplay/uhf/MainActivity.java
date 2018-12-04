package cc.cnplay.uhf;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends Activity implements LoaderCallbacks<Cursor> {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mHostnameView;
	private View mProgressView;
	private View mLoginFormView;

	private LoginDbHelper loginDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			// Set up the login form.
			mUsernameView = (EditText) findViewById(R.id.username);
			mHostnameView = (EditText) findViewById(R.id.hostname);
			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView textView,
								int id, KeyEvent keyEvent) {
							if (id == R.id.login || id == EditorInfo.IME_NULL) {
								attemptLogin();
								return true;
							}
							return false;
						}
					});

			Button mSignInButton = (Button) findViewById(R.id.username_sign_in_button);
			mSignInButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
			});

			mLoginFormView = findViewById(R.id.login_form);
			mProgressView = findViewById(R.id.login_progress);
			initForm();
		} catch (Throwable ex) {
			UIHelper.ToastMessage(getApplicationContext(), ex.getMessage(),
					60000);
		}
	}

	private void initForm() {
		try {
			loginDbHelper = new LoginDbHelper(getApplicationContext());
			App.login = loginDbHelper.findOne();
			if (App.login == null) {
				App.login = new Login();
				App.login.setHostname(App.HOSTNAME);
				App.login.setUsername("admin");
				App.login.setPassword("");
			}
			mUsernameView.setText(App.login.getUsername());
			mHostnameView.setText(App.login.getHostname());
			//mPasswordView.setText(App.login.getPassword());
			App.login.setPassword("");
		} catch (Throwable ex) {
			UIHelper.ToastMessage(getApplicationContext(), ex.getMessage(),
					60000);
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		App.login.setUsername(mUsernameView.getText().toString());
		App.login.setPassword(mPasswordView.getText().toString());
		App.login.setHostname(mHostnameView.getText().toString());
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(App.login.getPassword())
				&& !isPasswordValid(App.login.getPassword())) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(App.login.getUsername())) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		cursor.moveToFirst();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		UserLoginTask() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject json = new JSONObject();
				json.put("username", App.login.getUsername());
				json.put("password", App.login.getPassword());
				String ret = HttpUtils.postJSON(App.url("/loginByApp"),
						json.toString(), null);
				if (ret != null) {
					json = new JSONObject(ret);
					if (json.getInt("code") == 200) {
						JSONObject data = json.getJSONObject("rows");
						App.login.setAcctoken(data.getString("accessToken"));
						App.login.setReftoken(data.getString("refreshToken"));
						loginDbHelper.save(App.login);
						return true;
					} else {
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}
				} else {
					mPasswordView
							.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}
			} catch (Throwable ex) {
				mPasswordView.setError(ex.getMessage());
				mPasswordView.requestFocus();
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			if (success) {
				finish();
				loginDbHelper.save(App.login);
				Intent intent = new Intent(MainActivity.this,
						UHFMainActivity.class);
				MainActivity.this.startActivity(intent);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
