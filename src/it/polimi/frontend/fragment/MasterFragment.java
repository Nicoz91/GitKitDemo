package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterFragment extends Fragment implements RequestList.OnRequestSelectedListener{

	private boolean twoPane;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.master_fragment,
				container, false);
		if (rootView.findViewById(R.id.request_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;
			/*
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((RequestList) getChildFragmentManager()
					.findFragmentById(R.id.request_list))
					.setActivateOnItemClick(true);*/
		} else {
			getChildFragmentManager().beginTransaction()
			.replace(R.id.container,new RequestList(),RequestList.ID)
			.commit();
		}
		return rootView;
	}

	@Override
	public void onRequestSelected(int position, Request request) {
		RequestDetail fragment = new RequestDetail(request);
		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			getChildFragmentManager().beginTransaction()
					.replace(R.id.request_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail fragment
			// for the selected item ID.
			Fragment reqList=getChildFragmentManager().findFragmentByTag(RequestList.ID);
			
			getChildFragmentManager().beginTransaction()
			.hide(reqList)
			.addToBackStack(RequestDetail.ID)
			.add(R.id.container,fragment,RequestDetail.ID)
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
