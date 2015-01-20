package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.GeoPt;
import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.appengine.entity.requestendpoint.model.User;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.LoginSession;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.TabbedActivity;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.RequestAdapter;
import it.polimi.frontend.util.RequestLoader;
import it.polimi.frontend.util.RequestLoader.OnRequestLoadedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class RequestList extends ListFragment implements OnRequestLoadedListener{

	OnRequestSelectedListener mListener;
	public final static String ID="RequestListFragmentID";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//		new TestInsertTask().execute();
		RequestLoader.getInstance().addListener(this);
		List<Request> requests = RequestLoader.getInstance().getRequests();
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			RequestLoader.getInstance().loadRequest();
		}
		mListener = ParentFragmentUtil.getParent(this, OnRequestSelectedListener.class);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onRequestSelected(position, (Request) getListAdapter().getItem(position));
	}

	/**
	 * Interfaccia che deve implementare il master (fragment o activity) per ricevere dati sulla 
	 * richiesta selezionata.
	 * */
	public interface OnRequestSelectedListener {
		public void onRequestSelected(int position, Request request);
	}

	@Override
	public void onRequestLoaded(List<Request> requests) {
		System.out.println("Ho ricevuto requests:");
		if(requests==null) System.out.println("Requests è nullo");
		setRequestAdapter(requests);		
	}

	public void setRequestAdapter(List<Request> requests){
		System.out.println("Setto le richieste");
		List<Request> reqs = requests;
		Context c = getActivity();
		if(c==null){ 
			System.out.println("Il context è null ma noi bariamo");
			c = MyApplication.getContext();
		}
		else System.out.println("Tutto ok inizializzo l'adapter");
		RequestAdapter adapter = new RequestAdapter(c,0,reqs);
		setListAdapter(adapter);
	}

	/**
	 * AsyncTask for calling Mobile Assistant API for checking into a place (e.g., a store)
	 */
	private class TestInsertTask extends AsyncTask<Void, Void, Request> {
		/**
		 * Calls appropriate CloudEndpoint to indicate that user checked into a place.
		 *
		 * @param params the place where the user is checking in.
		 */
		@Override
		protected Request doInBackground(Void... params) {
			Request req = new Request();
			req.setDescription("Vicino alla casa");
			req.setTitle("Ripasso Soft Computing");
			GeoPt geo = new GeoPt();
			geo.setLatitude(45.48666f);
			geo.setLongitude(9.32414f);
			req.setPlace(geo);
			User user = new User();
			// Set the ID of the store where the user is.
			// This would be replaced by the actual ID in the final version of the code.
			user.setName(LoginSession.getUser().getDisplayName());
			user.setSurname("Pasticcio");
			user.setPwAccount(LoginSession.getUser().getEmail());
			req.setOwner(user);
			Requestendpoint.Builder reqBuilder = new Requestendpoint.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
					null);
			reqBuilder = CloudEndpointUtils.updateBuilder(reqBuilder);
			Requestendpoint reqEndpoint = reqBuilder.build();
			Request r = null;
			try {
				r = reqEndpoint.insertRequest(req).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return r;
		}

		@Override
		protected void onPostExecute(Request result) {
			if (result!=null){
				ArrayList<String> reqTitles = new ArrayList<String>();
				reqTitles.add(result.getTitle());
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, reqTitles);
				setListAdapter(adapter);
			}
		}
	}
}