package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.requestendpoint.Requestendpoint;
import it.polimi.appengine.entity.requestendpoint.model.CollectionResponseRequest;
import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.RequestLoader;
import it.polimi.frontend.util.RequestLoader.OnRequestLoadedListener;

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

public class RequestMap extends Fragment implements OnRequestLoadedListener {

	static final LatLng CASA_STUDENTE = new LatLng(45.4766, 9.22414);
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.request_map_fragment,
				container, false);
		map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
		
		RequestLoader.getInstance().addListener(this);
		List<Request> requests = RequestLoader.getInstance().getRequests();
		if(requests!=null && requests.size()>0 ){
			setRequestMark(requests);	
		}
		else{
			RequestLoader.getInstance().loadRequest();
		}
		
		map.addMarker(new MarkerOptions().position(CASA_STUDENTE)
				.title("Casa dello studente")
				.snippet("Tutti alla casa"));

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CASA_STUDENTE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		return rootView;
	}

	private void setRequestMark(List<Request> requests){
		System.out.println("Setto le posizioni sulla mappa");
		System.out.println("Size: "+requests.size());
		for(Request r:requests){
			if(r.getPlace()!=null){
				System.out.println("setto "+r.getPlace().getLatitude());
				map.addMarker(new MarkerOptions().position(new LatLng(r.getPlace().getLatitude(), r.getPlace().getLongitude()))
					.title(r.getTitle())
					.snippet(r.getDescription()));
			}
		}
	}

	@Override
	public void onRequestLoaded(List<Request> requests) {
		setRequestMark(requests);
		
	}

}
