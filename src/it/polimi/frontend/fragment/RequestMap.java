package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnRequestLoadedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RequestMap extends Fragment implements OnRequestLoadedListener, OnMapReadyCallback, OnInfoWindowClickListener,RequestDetail.OnUserSectionClickedListener {

	static final LatLng CASA_STUDENTE = new LatLng(45.4766, 9.22414);
	private GoogleMap map;
	private Map<Marker,Request> markers;
	private boolean twoPane=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_request_map,
				container, false);
		((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
		markers = new HashMap<Marker,Request>();
		twoPane = getResources().getBoolean(R.bool.isTablet);
		return rootView;
	}

	private void setRequestMark(List<Request> requests){
		System.out.println("Setto le posizioni sulla mappa");
		System.out.println("Size: "+requests.size());
		for(Request r:requests){
			if(r.getPlace()!=null){
				System.out.println("setto "+r.getPlace().getLatitude());
				Marker m = map.addMarker(new MarkerOptions().position(new LatLng(r.getPlace().getLatitude(), r.getPlace().getLongitude()))
					.title(r.getTitle())
					.snippet(r.getDescription()));
				markers.put(m,r);
			}
		}
	}

	@Override
	public void onRequestLoaded(List<Request> requests) {
		if(requests!=null && requests.size()>0)
		setRequestMark(requests);
		
	}

	@Override
	public void onMapReady(GoogleMap arg) {
		// TODO Auto-generated method stub
		this.map=arg;
		QueryManager.getInstance().addListener(this);
		List<Request> requests = QueryManager.getInstance().getRequests();
		if(requests!=null && requests.size()>0 ){
			setRequestMark(requests);	
		}
		else{
			//RequestLoader.getInstance().loadRequest();
		}
		
		map.addMarker(new MarkerOptions().position(CASA_STUDENTE)
				.title("Casa dello studente")
				.snippet("Tutti alla casa"));

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CASA_STUDENTE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		map.setOnInfoWindowClickListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker m) {
		if(twoPane){
			DetailContainerFragment detailContFragment = new DetailContainerFragment(markers.get(m),MasterFragment.ALL_REQUEST);
			Fragment mapFragment=getChildFragmentManager().findFragmentById(R.id.map);

			getChildFragmentManager().beginTransaction()
			.hide(mapFragment)
			.addToBackStack(DetailContainerFragment.ID)
			.add(R.id.mapContainer,detailContFragment,DetailContainerFragment.ID)
			.commit();
		} else {
			RequestDetail fragment = new RequestDetail(markers.get(m),MasterFragment.ALL_REQUEST);
			Fragment mapFragment=getChildFragmentManager().findFragmentById(R.id.map);

			getChildFragmentManager().beginTransaction()
			.hide(mapFragment)
			.addToBackStack(RequestDetail.ID)
			.add(R.id.mapContainer,fragment,RequestDetail.ID)
			.commit();
		}
	}

	@Override
	public void onUserSectionClicked(User owner) {
		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			//getChildFragmentManager().beginTransaction()
			//.replace(R.id.feedback_list_container, fragment).commit();
			
			/*Se ne dovrebbe occupare il DetailContainerFragment, quindi non fa nulla*/
		} else {
			// In single-pane mode, simply start the detail fragment
			// for the selected item ID.
			FeedbackDetail fragment = new FeedbackDetail(owner);
			Fragment reqDetail=getChildFragmentManager().findFragmentByTag(RequestDetail.ID);

			getChildFragmentManager().beginTransaction()
			.hide(reqDetail)
			.addToBackStack(FeedbackDetail.ID)
			.add(R.id.mapContainer,fragment,FeedbackDetail.ID)
			.commit();

			getChildFragmentManager().addOnBackStackChangedListener(
					new FragmentManager.OnBackStackChangedListener() {
						public void onBackStackChanged() {
							//TODO
							// Update your UI here.
						}
					});
		}
	}

}
