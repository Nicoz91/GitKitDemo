package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.Manager;
import it.polimi.appengine.entity.manager.model.GeoPt;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
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
		// new Query().execute();
		
		RequestLoader.getInstance().addListener(this);
		List<Request> requests = RequestLoader.getInstance().getRequests();
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			//RequestLoader.getInstance().loadRequest();
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
		if(requests==null)
			System.out.println("Requests è nullo");
		else
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
		else 
			System.out.println("Tutto ok inizializzo l'adapter");
		RequestAdapter adapter = new RequestAdapter(c,0,reqs);
		setListAdapter(adapter);
	}


	/**
	 * AsyncTask for retrieving the list of places (e.g., stores) and updating the
	 * corresponding results list.
	 */
	private class Query extends AsyncTask<Void, Void, User> {

		@Override
		protected User doInBackground(Void... params) {

			System.out.println("Inizio la query");
			Manager.Builder endpointBuilder = new Manager.Builder(
					AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);

			endpointBuilder = CloudEndpointUtils.updateBuilder(endpointBuilder);


			User result = null;

			Manager endpoint = endpointBuilder.build();

//			try {
//				result = endpoint.getUserByEmail("test@test.com").execute();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				result = null;
//			}
			return result;
		}

		@Override
		@SuppressWarnings("null")
		protected void onPostExecute(User result) {
			System.out.println("Ho ricevuto l'utente: "+result.getName());
			return;
		}

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
			
			Manager.Builder reqBuilder = new Manager.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
					null);
			reqBuilder = CloudEndpointUtils.updateBuilder(reqBuilder);
			Manager reqEndpoint = reqBuilder.build();
			User u = new User();
			try {
				u = reqEndpoint.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u.getRequests()==null)
				u.setRequests(new ArrayList<Request>());
			
			Request req = new Request();
			req.setDescription("Vicino alla casa");
			req.setTitle("Ripasso Soft Computing");
			GeoPt geo = new GeoPt();
			geo.setLatitude(45.38766f);
			geo.setLongitude(9.22514f);
			req.setPlace(geo);
			u.getRequests().add(req);

			Request r = null;
			try {
				reqEndpoint.updateUser(u).execute();
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