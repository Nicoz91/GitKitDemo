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

import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.ConnectionHandler;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnActionListener;
import it.polimi.frontend.util.QueryManager.OnRequestLoadedListener;


public class MainActivity extends FragmentActivity implements OnClickListener,OnActionListener,OnRequestLoadedListener {

	private GitkitClient client;
	private ConnectionHandler ch;
	private boolean signing;
	private GitkitUser session;
	private IdToken sessionToken;
	private boolean notification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QueryManager.getInstance().addActionListener(this);
		notification = false;
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.welcome);
		ch = new ConnectionHandler();
		registerReceiver(ch,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		//Carico i cookie
		LoginSession.setPrefs(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()));
		session = LoginSession.getUser();
		sessionToken = LoginSession.getIdToken();

		//Istanzio il gitkit client
		client = GitkitClient.newBuilder(this, new GitkitClient.SignInCallbacks() {
			public void onSignIn(IdToken idToken, GitkitUser user) {		
				//Verifico se l'utente è stato registrato nel database
				signing = true;
				session = user;
				sessionToken = idToken;
				System.out.println("Cerco l'utente");
				QueryManager.getInstance().getUserByEmail(user.getEmail());
			}
			@Override
			public void onSignInFailed() {
				Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_LONG).show();
			}
		}).build();
		signing = false;
		//Controllo se è presente la connessione
		if(!ConnectionHandler.isConnected()){
			startActivity(new Intent(this, WaitActivity.class));
			return;
		}

		//Controllo se è attiva una sessione
		if(session!=null)
			QueryManager.getInstance().getUserByEmail(session.getEmail());
		else
			showSignInPage();
	}

	// Step 3: Override the onActivityResult method.
	// When a result is returned to this activity, it is maybe intended for GitkitClient. Call
	// GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
	// the method returns true to indicate the result has been consumed.
	//

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
		QueryManager.getInstance().addActionListener(this);
		setContentView(R.layout.welcome);
		Button button = (Button) findViewById(R.id.sign_in);
		button.setOnClickListener(this);
	}

	private void showRegistrationPage(){
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private void showProfilePage() {
		QueryManager.getInstance().addListener(this);
		System.out.println("CARICO LE REQUEST");
		QueryManager.getInstance().loadRequest();
		//		startActivity(new Intent(this, TabbedActivity.class));
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
			session = null;
			sessionToken = null;
		}
	}
	
	

	@Override
	protected void onPause() {
		hideDialog();
		super.onPause();
	}

	@Override
	public void onResume(){
		super.onResume();
		//		session = null;
		//		sessionToken = null;
		//		signing = false;

		Intent i = getIntent();
		if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Logout")){
			signing = false;
			LoginSession.reset();
			QueryManager.destroy();
			showSignInPage();
			return;
		} else if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Exit")){
			QueryManager.destroy();
			this.finish();
			return;
		}else if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Registered")){
			showProfilePage();
			return;
		}else if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Network")){
			if(session!=null)
				QueryManager.getInstance().getUserByEmail(session.getEmail());
			else
				showSignInPage();
			return;
		}else if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Notification")){
			if(session!=null && QueryManager.getInstance().getCurrentUser()!=null){
				notification = true;
				Intent in = new Intent(this, TabbedActivity.class);
				in.putExtra("Reason", "Notification");
				startActivity(in);
			}
			notification = true;
			return;
		}
		if(session!=null && QueryManager.getInstance().getCurrentUser()!=null)
			startActivity(new Intent(this, TabbedActivity.class));
		else
			showSignInPage();
	}

	@Override
	protected void onDestroy() {
		QueryManager.destroy();
		if(ch!=null)
			this.unregisterReceiver(ch);
		super.onDestroy();
	}

	@Override
	public void onPerformingAction(int action) {
		if(action == OnActionListener.GET_USER)
			showDialog(getString(R.string.serverConnection));
	}

	@Override
	public void onActionPerformed(Object result, int action) {
		if(action == OnActionListener.GET_USER){
			User u = (User)result;
			if(!signing){
				if(u == null){
					Toast.makeText(MainActivity.this, getString(R.string.serverError), Toast.LENGTH_LONG).show();
					return;
				}

				if(u!=null && u.getName()!=null && !u.getName().equals("")){
					QueryManager.getInstance().registerDevice();
				}

				hideDialog();
				if(session!=null && sessionToken!=null && u!=null && u.getName()!=null && !u.getName().equals("")){
					showProfilePage();
				}else
					showSignInPage();
				return;
			}else{
				try {	
					if(u == null){
						Toast.makeText(MainActivity.this, getString(R.string.serverError), Toast.LENGTH_LONG).show();
						return;
					}

					hideDialog();
					if(u!=null && u.getName()!=null && !u.getName().equals(""))
						showProfilePage();
					else
						showRegistrationPage();

					//Salvo i dati ricevuti nelle shared preferences
					LoginSession.setUser(session);
					LoginSession.setStringUser(session.toString());
					LoginSession.setIdToken(sessionToken);
					LoginSession.setStringToken(sessionToken.getTokenString());
					if(session.getIdProvider()!=null){
						LoginSession.setStringProvider(session.getIdProvider().name());
						LoginSession.setProvider(session.getIdProvider().name());
					}
					else{
						LoginSession.setStringProvider("");
						LoginSession.setProvider("");
					}
					return;
				} catch (Exception e) {
					Toast.makeText(MainActivity.this, getString(R.string.serverError), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}
	}

	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	private ProgressDialog mProgressDialog;
	protected void showDialog(String message) {
		
		setProgressDialog(message);
		if(this!=null && !this.isFinishing())
			mProgressDialog.show();
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog(String message) {
		if(mProgressDialog!=null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getString(R.string.wait));
		mProgressDialog.setMessage(message);
	}

	@Override
	public void onRequestLoading() {
		showDialog(getString(R.string.loadingRequests));
	}

	@Override
	public void onRequestLoaded(List<Request> requests) {
		if(notification){
			notification = true;
			Intent in = new Intent(this, TabbedActivity.class);
			in.putExtra("Reason", "Notification");
			startActivity(in);
		}else
		startActivity(new Intent(this, TabbedActivity.class));
		hideDialog();

	}      


}
