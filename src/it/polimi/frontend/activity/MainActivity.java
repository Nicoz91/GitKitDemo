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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

import it.polimi.appengine.entity.manager.Manager;
import it.polimi.appengine.entity.manager.model.GeoPt;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.RequestLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
		//Provo a registrare l'id così lo trovo già salvato in shared dopo
		try {
			GCMIntentService.register(MyApplication.getContext());
		} catch (Exception e) {
			System.out.println("Impossibile registrare l'app");
			//TODO ricominciare fino a che non viene registrata!
		}
		//Controllo se è attiva una sessione
		if(session!=null && sessionToken!=null){
			//showProfilePage(sessionToken,session);
		}

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

				//Controllo se l'utente ha già inserito i dati obbligatori
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

	// Step 3: Override the onActivityResult method.
	// When a result is returned to this activity, it is maybe intended for GitkitClient. Call
	// GitkitClient.handleActivityResult to check the result. If the result is for GitkitClient,
	// the method returns true to indicate the result has been consumed.
	//

	private boolean checkRegistration(GitkitUser user){
		//Se l'utente è già salvato allora associo il device
		User u = checkUser(user);
		if(u!=null){
			try {

				ArrayList<String> dev = (ArrayList<String>) u.getDevices();
				if(dev==null || !dev.contains(LoginSession.getDeviceId()) ){
					System.out.println("Faccio l'update su: "+u.getId());
					u = (new UpdateUserDevice(u).execute()).get();
				}

				//				System.out.println("Faccio l'update su: "+u.getId());
				//				u = (new UpdateUserDevice(u).execute()).get();
				//				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;

		}
		else
			return false;
	}

	private User checkUser(GitkitUser user){
		User u = null;
		try {
			u = new QueryUser(user.getEmail()).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}

	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class UpdateUserDevice extends AsyncTask<Void, Void, User> {

		private User u;

		public UpdateUserDevice(User u){
			super();
			this.u = u;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Modifico la lista dei device");
			Manager.Builder endpointBuilder = new Manager.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			User result = null;
			Manager endpoint = endpointBuilder.build();
			if(u.getDevices()==null)
				u.setDevices(new ArrayList<String>());
			u.getDevices().add(LoginSession.getDeviceId());
			try {
				result = endpoint.updateUser(u).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

	}


	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class InsertUser extends AsyncTask<Void, Void, User> {

		private User u;

		public InsertUser(User u){
			super();
			this.u = u;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Creo un nuovo utente");
			Manager.Builder endpointBuilder = new Manager.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			User result = null;
			Manager endpoint = endpointBuilder.build();
			try {
				result = endpoint.insertUser(u).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

	}

	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class QueryUser extends AsyncTask<Void, Void, User> {

		private String email;

		public QueryUser(String email){
			super();
			this.email = email;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Inizio la query");
			Manager.Builder endpointBuilder = new Manager.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			User result = null;
			Manager endpoint = endpointBuilder.build();
			try {
				result = endpoint.getUserByEmail(email).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		@Override
		@SuppressWarnings("null")
		protected void onPostExecute(User result) {
			if(result!=null)
				System.out.println("Ho ricevuto l'utente: "+result.getName());
			else
				System.out.println("Nessun utente ricevuto, devo crearlo.");
			hideDialog();
			return;
		}

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
		User u = new User();
		u.setName("John"+Math.random()*100);
		u.setSurname("A"+Math.random()*100);
		u.setPwAccount(user.getEmail());
		ArrayList<String> dev = new ArrayList<String>();
		dev.add(LoginSession.getDeviceId());
		u.setDevices(dev);
		if(u.getRequests()==null)
			u.setRequests(new ArrayList<Request>());
		Request req = new Request();
		req.setDescription("Richiesta iniziale");
		req.setTitle("Test"+Math.random()*100);
		GeoPt geo = new GeoPt();
		geo.setLatitude(45.38766f);
		geo.setLongitude(9.22514f);
		req.setPlace(geo);
		u.getRequests().add(req);
		
		System.out.println("Provo a registrare l'utente");
		try {
			(new InsertUser(u).execute()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Registrazione effettuata con successo");
		findViewById(R.id.sign_out).setOnClickListener(this);
		
		findViewById(R.id.sign_out).setOnClickListener(this);
		startActivity(new Intent(this, TabbedActivity.class));
	}

	private void showProfilePage(IdToken idToken, GitkitUser user) {
		RequestLoader.getInstance().loadRequest();
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

	//TODO REFACTOR
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
