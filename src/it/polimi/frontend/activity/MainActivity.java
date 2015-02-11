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

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.ConnectionHandler;
import it.polimi.frontend.util.QueryManager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private GitkitClient client;
	private ConnectionHandler ch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ch = new ConnectionHandler();
		registerReceiver(ch,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		//Carico i cookie
		LoginSession.setPrefs(PreferenceManager.getDefaultSharedPreferences(this));
		GitkitUser session = LoginSession.getUser();
		IdToken sessionToken = LoginSession.getIdToken();
		
		//		if(!ConnectionHandler.getInstance().isConnected())
		//			Toast.makeText(MainActivity.this, "Problemi di connettività. Controllare la connessione!", Toast.LENGTH_LONG).show();


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
				//Controllo se l'utente ha già inserito i dati obbligatori

				if(checkRegistration(user))
					showProfilePage(idToken, user);
				else
					showRegistrationPage(user);

				//Salvo i dati ricevuti nelle shared preferences
				LoginSession.setUser(user);
				LoginSession.setStringUser(user.toString());
				LoginSession.setIdToken(idToken);
				LoginSession.setStringToken(idToken.getTokenString());
				if(user.getIdProvider()!=null){
					LoginSession.setStringProvider(user.getIdProvider().name());
					LoginSession.setProvider(user.getIdProvider().name());
				}
				else{
					LoginSession.setStringProvider("");
					LoginSession.setProvider("");
				}
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

		//Controllo se è presente la connessione
		if(!ConnectionHandler.isConnected()){
			startActivity(new Intent(this, WaitActivity.class));
			return;
		}
		System.out.println("Effettuo la query");
		//	QueryManager.getInstance().getUserByEmail(session.getEmail());
		System.out.println("Fine query");
		//Controllo se è attiva una sessione
		if(session!=null && sessionToken!=null && QueryManager.getInstance().getUserByEmail(session.getEmail())!=null){
			showProfilePage(sessionToken,session);
		}else
			showSignInPage();
	}

	// Step 3: Override the onActivityResult method.
	// When a result is returned to this activity, it is maybe intended for GitkitClient. Call
	// GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
	// the method returns true to indicate the result has been consumed.
	//

	private boolean checkRegistration(GitkitUser user){
		//Se l'utente è già salvato allora associo il device
		User u = checkUser(user);
		if(u!=null && u.getName()!=null && !u.getName().equals("")){
//			//Provo a registrare l'id così lo trovo già salvato in shared dopo
//			try {
//				GCMIntentService.register(MyApplication.getContext());
//			} catch (Exception e) {
//				System.out.println("Impossibile registrare l'app");
//				//TODO ricominciare fino a che non viene registrata!
//			}
//			ArrayList<String> dev = (ArrayList<String>) u.getDevices();
//			if(dev==null || !dev.contains(LoginSession.getDeviceId()) ){
//				//				System.out.println("Faccio l'update su: "+u.getId());
//				u = QueryManager.getInstance().updateUserDevices(u);
//				if(u!=null)
//					return true;
//				else 
//					return false;
			QueryManager.getInstance().registerDevice();
			return true;

		}
		else
			return false;
	}

	private User checkUser(GitkitUser user){
		User u = null;
		u = QueryManager.getInstance().getUserByEmail(user.getEmail());
		return u;
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
		startActivity(new Intent(this, SettingsActivity.class));
	}

	private void showProfilePage(IdToken idToken, GitkitUser user) {
		System.out.println("CARICO LE REQUEST");
		QueryManager.getInstance().loadRequest();
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
		if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Logout")){
			LoginSession.reset();
			QueryManager.destroy();
			showSignInPage();
		} else if (i!=null && i.getStringExtra("Reason")!=null && i.getStringExtra("Reason").equals("Exit")){
			QueryManager.destroy();
			this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		QueryManager.destroy();
		if(ch!=null)
			this.unregisterReceiver(ch);
		super.onDestroy();
	}



}
