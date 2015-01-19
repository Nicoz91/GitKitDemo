package it.polimi.frontend.util;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.Request;
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
	
	public RequestLoader getInstance(){
		if (instance==null){
			instance = new RequestLoader();
		}
		return instance;
	}
	
	public void loadRequest(){
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
	private class RequestRetrieverTask extends AsyncTask<Void, Void, CollectionResponseRequest> {

		@Override
		protected CollectionResponseRequest doInBackground(Void... params) {


			Requestendpoint.Builder endpointBuilder = new Requestendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);

			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);


			CollectionResponseRequest result;

			Requestendpoint endpoint = endpointBuilder.build();
			System.out.println("Sto per recuperare requests");
			try {
				result = endpoint.listRequest().execute();
			} catch (IOException e) {
				e.printStackTrace();
				result = null;
				System.out.println("Requests null");
			}
			return result;
		}
		
		@Override
	    protected void onPostExecute(CollectionResponseRequest result) {
			requests=result.getItems();
			for (OnRequestLoadedListener l : listeners){
				l.onRequestLoaded(requests);
			}
		}
	}
}
