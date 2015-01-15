package it.polimi.frontend.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.appengine.entity.requestendpoint.model.User;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.LoginSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RequestList extends ListFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	/**
	 * AsyncTask for calling Mobile Assistant API for checking into a place (e.g., a store)
	 */
	//TODO Modificare per task di recupero Requests
	private class TestInsertTask extends AsyncTask<Void, Void, Request> {

		/**
		 * Calls appropriate CloudEndpoint to indicate that user checked into a place.
		 *
		 * @param params the place where the user is checking in.
		 */
		@Override
		protected Request doInBackground(Void... params) {
			Request req = new Request();
			req.setDescription("Descrizione di prova");
			req.setTitle("Titolo di prova");
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
				// TODO Auto-generated catch block
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
