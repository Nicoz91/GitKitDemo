package it.polimi.frontend.util;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.appengine.entity.userendpoint.Userendpoint;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.LoginSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class QueryManager {

	private Object queryResult;
	private List<OnQueryListener> listeners;
	private static QueryManager instance;
	
	private QueryManager(){
		this.queryResult = new Object();
		this.listeners = new ArrayList<OnQueryListener>();
	}
	
	public static QueryManager getInstance(){
		if (instance==null){
			instance = new QueryManager();
		}
		return instance;
	}
	
	public void getUserByEmail(String email){
		new QueryUser(email).execute();
	}
	
	public Object getResult(){
		return queryResult;
		//TODO sarebbe più "safe" passare una copia
		//return requests.clone();
	}
	
	public void addListener(OnQueryListener listener){
		listeners.add(listener);
	}
	
	public interface OnQueryListener{
		public void onQueryLoaded(Object result);
	}
	
	
	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class QueryUser extends AsyncTask<Void, Void, it.polimi.appengine.entity.userendpoint.model.User> {
		
		private String email;
		
		public QueryUser(String email){
			super();
			this.email = email;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected it.polimi.appengine.entity.userendpoint.model.User doInBackground(Void... params) {
			System.out.println("Inizio la query");
			Userendpoint.Builder endpointBuilder = new Userendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			it.polimi.appengine.entity.userendpoint.model.User result;
			Userendpoint endpoint = endpointBuilder.build();
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
		protected void onPostExecute(it.polimi.appengine.entity.userendpoint.model.User result) {
			queryResult=result;
			for (OnQueryListener l : listeners){
				l.onQueryLoaded(result);
			}
		}

	}
	
}
