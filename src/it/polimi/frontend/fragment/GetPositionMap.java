package it.polimi.frontend.fragment;

import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetPositionMap extends Fragment{

	static final LatLng CASA_STUDENTE = new LatLng(45.4766, 9.22414);
	private GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_position_map,
				container, false);
		map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapPosition)).getMap();
		
		map.addMarker(new MarkerOptions().position(CASA_STUDENTE)
				.title("Casa dello studente")
				.snippet("Tutti alla casa"));
		
		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CASA_STUDENTE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		return rootView;
	}


}
