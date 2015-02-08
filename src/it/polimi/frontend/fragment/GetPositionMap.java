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
				View rootView = inflater.inflate(R.layout.fragment_position_map,
				container, false);

		map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapPosition)).getMap();
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				if(marker==null){
					marker = new MarkerOptions().draggable(true).position(
							new LatLng(point.latitude, point.longitude)).title("Posizione Richiesta");
					map.addMarker(marker);
					position = new LatLng(point.latitude,point.longitude);
				}
				else{
					marker = new MarkerOptions().draggable(true).position(
							new LatLng(point.latitude, point.longitude)).title("Posizione Richiesta");
				}

			}
		});

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

		LocationManager locationmanager;
        String context=Context.LOCATION_SERVICE;
        locationmanager=(LocationManager) getActivity().getSystemService(context);
        String provider=LocationManager.NETWORK_PROVIDER;
        Location location= locationmanager.getLastKnownLocation(provider);
        
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.4766, 9.22414), 15));
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


		return rootView;
	}

	public boolean setPosition(Request req){
		if(position==null)
			return false;
		GeoPt g = new GeoPt();
		g.setLatitude((float)position.latitude);
		g.setLongitude((float)position.longitude);
		req.setPlace(g);
		return true;
	}


}
