/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polimi.frontend.activity;

import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import it.polimi.frontend.activity.R;

import java.io.IOException;

/**
 * Gitkit Demo
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	private GitkitClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		LoginSession.setPrefs(PreferenceManager.getDefaultSharedPreferences(this));
		GitkitUser session = LoginSession.getUser();
		IdToken sessionToken = LoginSession.getIdToken();
		if(session!=null && sessionToken!=null){
			showProfilePage(sessionToken,session);
		}
		else
		{
			// Step 1: Create a GitkitClient.
			// The configurations are set in the AndroidManifest.xml. You can also set or overwrite them
			// by calling the corresponding setters on the GitkitClient builder.
			//
			//    Intent intent = new Intent(this, RegisterActivity.class);
			//    startActivity(intent);

			client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
				// Implement the onSignIn method of GitkitClient.SignInCallbacks interface.
				// This method is called when the sign-in process succeeds. A Gitkit IdToken and the signed
				// in account information are passed to the callback.

				@Override
				public void onSignIn(IdToken idToken, GitkitUser user) {

					//Salvo i dati ricevuti nelle shared preferences
					LoginSession.setUser(user);
					LoginSession.setStringUser(user.toString());
					LoginSession.setIdToken(idToken);
					LoginSession.setStringToken(idToken.getTokenString());
					if(checkRegistration(user))
						showProfilePage(idToken, user);
					else
						showRegistrationPage(user);
					// Now use the idToken to create a session for your user.
					// To do so, you should exchange the idToken for either a Session Token or Cookie
					// from your server.
					// Finally, save the Session Token or Cookie to maintain your user's session.
				}

				// Implement the onSignInFailed method of GitkitClient.SignInCallbacks interface.
				// This method is called when the sign-in process fails.
				@Override
				public void onSignInFailed() {
					Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_LONG).show();
				}
			}).build();

			showSignInPage();
		}

	}

	// Step 3: Override the onActivityResult method.
	// When a result is returned to this activity, it is maybe intended for GitkitClient. Call
	// GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
	// the method returns true to indicate the result has been consumed.
	//

	private boolean checkRegistration(GitkitUser user){
		if(checkUser(user)){
			try {
				GCMIntentService.register(MyApplication.getContext());
			} catch (Exception e) {
				System.out.println("Impossibile registrare l'app");
				//TODO ricominciare fino a che non viene registrata!
			}
			return true;

		}
		else
			return false;
	}

	private boolean checkUser(GitkitUser user){
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (!client.handleActivityResult(requestCode, resultCode, intent)) {
			super.onActivityResult(requestCode, resultCode, intent);
		}

	}



	// Step 4: Override the onNewIntent method.
	// When the app is invoked with an intent, it is possible that the intent is for GitkitClient.
	// Call GitkitClient.handleIntent to check it. If the intent is for GitkitClient, the method
	// returns true to indicate the intent has been consumed.

	@Override
	protected void onNewIntent(Intent intent) {
		if(client!=null){
			if (!client.handleIntent(intent)) {
				super.onNewIntent(intent);
				setIntent(intent);
			}
		}
		setIntent(intent);
	}



	private void showSignInPage() {
		setContentView(R.layout.welcome);
		Button button = (Button) findViewById(R.id.sign_in);
		button.setOnClickListener(this);
	}

	private void showRegistrationPage(GitkitUser user){
		setContentView(R.layout.profile);
		showAccount(user);
		findViewById(R.id.sign_out).setOnClickListener(this);
	}

	private void showProfilePage(IdToken idToken, GitkitUser user) {
		setContentView(R.layout.profile);
		showAccount(user);
		findViewById(R.id.sign_out).setOnClickListener(this);

		startActivity(new Intent(this, TabbedActivity.class));
	}


	// Step 5: Respond to user actions.
	// If the user clicks sign in, call GitkitClient.startSignIn() to trigger the sign in flow.
	// If the user clicks sign out, call GitkitClient.signOut() to clear state.
	// If the user clicks manage account, call GitkitClient.manageAccount() to show manage
	// account UI.

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.sign_in) {
			client.startSignIn();
		} else if (v.getId() == R.id.sign_out) {
			LoginSession.reset();
			showSignInPage();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		Intent i = getIntent();
		if (i!=null && i.getStringExtra("ActivityName")!=null && i.getStringExtra("ActivityName").equals("TabbedActivity")){
			LoginSession.reset();
			showSignInPage();
		}
	}

	private void showAccount(GitkitUser user) {  
		((TextView) findViewById(R.id.account_email)).setText(user.getEmail());

		if (user.getDisplayName() != null) {
			((TextView) findViewById(R.id.account_name)).setText(user.getDisplayName());
		}

		if (user.getPhotoUrl() != null) {
			final ImageView pictureView = (ImageView) findViewById(R.id.account_picture);
			new AsyncTask<String, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(String... arg) {
					try {
						byte[] result = HttpUtils.get(arg[0]);
						return BitmapFactory.decodeByteArray(result, 0, result.length);
					} catch (IOException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap bitmap) {
					if (bitmap != null) {
						pictureView.setImageBitmap(bitmap);
					}
				}
			}.execute(user.getPhotoUrl());
		}
	}
	
	private ProgressDialog mProgressDialog;
	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	protected void showDialog() {
		if (mProgressDialog == null) {
			setProgressDialog();
		}
		mProgressDialog.show();
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Attendi...");
		mProgressDialog.setMessage("Sto scaricando...");
	}
}
