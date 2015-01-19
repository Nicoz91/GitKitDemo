package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.R;

import java.io.IOException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

public class RequestMap extends Fragment {

	static final LatLng CASA_STUDENTE = new LatLng(45.4766, 9.22414);
	private List<Request> requests;
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.request_map_fragment,
				container, false);
		new RequestRetrieverTask().execute();
		map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
		Marker casa = map.addMarker(new MarkerOptions().position(CASA_STUDENTE)
				.title("Casa dello studente")
				.snippet("Tutti alla casa"));

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CASA_STUDENTE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		return rootView;
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
			if (result!=null){
				requests=result.getItems();

			} else
				System.out.println("Anche in onPostExecute result è null");
		}
	}

}
