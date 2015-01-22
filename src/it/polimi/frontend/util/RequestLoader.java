package it.polimi.frontend.util;
import it.polimi.appengine.entity.manager.Manager;
import it.polimi.appengine.entity.manager.model.Key;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.CloudEndpointUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class RequestLoader {

	private List<Request> requests;
	private List<OnRequestLoadedListener> listeners;
	private static RequestLoader instance;

	private RequestLoader(){
		this.requests = new ArrayList<Request>();
		this.listeners = new ArrayList<OnRequestLoadedListener>();
	}

	public static RequestLoader getInstance(){
		if (instance==null){
			instance = new RequestLoader();
		}
		return instance;
	}

	public void loadRequest(){
		System.out.println("Mi connetto per scaricare le richieste...");
		new RequestRetrieverTask().execute();
	}

	public List<Request> getRequests(){
		return requests;
		//TODO sarebbe più "safe" passare una copia
		//return requests.clone();
	}

	public void addListener(OnRequestLoadedListener listener){
		listeners.add(listener);
	}

	public interface OnRequestLoadedListener{
		public void onRequestLoaded(List<Request> requests);
	}

	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class RequestRetrieverTask extends AsyncTask<Void, Void, ArrayList<Request>> {

		@Override
		protected ArrayList<Request> doInBackground(Void... params) {
			Manager.Builder endpointBuilder = new Manager.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);
			Manager endpoint = endpointBuilder.build();
			ArrayList<Request> result = new ArrayList<Request>();
			ArrayList<User> users = new ArrayList<User>();

			System.out.println("Sto per ricostruire le request");
			try {
				users = (ArrayList<User>) endpoint.listUser().execute().getItems();
				try{
					for(User u : users){
						ArrayList<Request> a = (ArrayList<Request>) u.getRequests();
						if(a!=null && a.size()>0){
							for(Request r : a){
								System.out.println("La chiave è: "+r.getId());
								Request req = endpoint.getRequest(r.getId()).execute();
								if(req==null) System.out.println("Non ho trovato nulla");
								r.setOwner(u);}
							
							result.addAll(a);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Like no tomorrow");}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Requests null");
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Request> result) {
			requests=result;

			for (OnRequestLoadedListener l : listeners){
				l.onRequestLoaded(requests);
			}

		}
	}
}
