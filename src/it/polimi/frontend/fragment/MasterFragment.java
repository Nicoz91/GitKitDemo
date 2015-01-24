package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterFragment extends Fragment implements RequestList.OnRequestSelectedListener, RequestDetail.OnUserSectionClickedListener{

	private boolean twoPane;
	private static View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//PRIMA POSSIBILE SOLUZIONE (A problema di exception al cambio di tab)
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_master,container, false);
			if (view.findViewById(R.id.detail_container) != null) {
				// The detail container view will be present only in the
				// large-screen layouts (res/values-large and
				// res/values-sw600dp). If this view is present, then the
				// activity should be in two-pane mode.
				twoPane = true;
				
				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				((RequestList) getChildFragmentManager()
						.findFragmentById(R.id.request_list))
						.setActivateOnItemClick(true);
			} else {
				getChildFragmentManager().beginTransaction()
				.replace(R.id.container,new RequestList(RequestList.ALL_REQUEST, null),RequestList.ID)
				.commit();
			}
		} catch (InflateException e) {
			// map is already there, just return view as it is
		}
		return view;
		/*SECONDA SOLUZIONE POSSIBILE
	    if(savedInstanceState==null)
	    	View rootView = inflater.inflate(R.layout.fragment_master,container, false);
		if (rootView.findViewById(R.id.request_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			//((RequestList) getChildFragmentManager()
			//		.findFragmentById(R.id.request_list))
			//		.setActivateOnItemClick(true);
		} else {
			getChildFragmentManager().beginTransaction()
			.replace(R.id.container,new RequestList(),RequestList.ID)
			.commit();
		}
		return rootView;
		 */
		/* SITUAZIONE INIZIALE
		View rootView = inflater.inflate(R.layout.fragment_master,container, false);
		if (rootView.findViewById(R.id.request_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			//((RequestList) getChildFragmentManager()
			//		.findFragmentById(R.id.request_list))
			//		.setActivateOnItemClick(true);
		} else {
			getChildFragmentManager().beginTransaction()
			.replace(R.id.container,new RequestList(),RequestList.ID)
			.commit();
		}
		return rootView;*/
	}

	@Override
	public void onRequestSelected(int position, Request request) {
		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			DetailContainerFragment detailContFrag = new DetailContainerFragment(request);
			getChildFragmentManager().beginTransaction()
			.replace(R.id.detail_container, detailContFrag, DetailContainerFragment.ID).commit();
			System.out.println("Dentro onRequestSelected. Dovrei aver creato e settato il DetailFragment");
		} else {
			// In single-pane mode, simply start the detail fragment
			// for the selected item ID.
			RequestDetail fragment = new RequestDetail(request);
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
			.add(R.id.container,fragment,FeedbackDetail.ID)
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
