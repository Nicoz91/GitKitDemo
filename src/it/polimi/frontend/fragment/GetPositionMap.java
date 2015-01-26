package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.GeoPt;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetPositionMap extends Fragment{

	private MarkerOptions marker;
	private GoogleMap map;
	private LatLng position;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LocationManager locationmanager;
		
		String context=Context.LOCATION_SERVICE;
		locationmanager=(LocationManager) this.getActivity().getSystemService(context);
		String provider=LocationManager.NETWORK_PROVIDER;
		Location location= locationmanager.getLastKnownLocation(provider);
		Double lat=location.getLatitude();
        Double lon=location.getLongitude();
        System.out.println("Lat: "+lat+ " Lon: "+lon);
		LatLng pos = new LatLng(lat,lon);
		View rootView = inflater.inflate(R.layout.fragment_position_map,
				container, false);
		map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapPosition)).getMap();

		marker = new MarkerOptions().draggable(true).position(pos)
				.title("Casa dello studente")
				.snippet("Tutti alla casa");

		map.addMarker(marker);

		map.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onMarkerDragEnd(Marker arg0) {
				// TODO Auto-generated method stub
				map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
				position = arg0.getPosition();
				
			}
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
			}
		});

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(100), 2000, null);


		return rootView;
	}

	public void setPosition(Request req){
		GeoPt g = new GeoPt();
		g.setLatitude((float)position.latitude);
		g.setLongitude((float)position.longitude);
		req.setPlace(g);
	}


}
