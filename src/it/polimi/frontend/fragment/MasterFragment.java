package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnRequestLoadedListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterFragment extends Fragment implements OnRequestLoadedListener, RequestList.OnRequestSelectedListener, RequestDetail.OnUserClickedListener{

	private boolean twoPane;
	private static View view;
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int mode;

	public MasterFragment(){
		this.mode=0;
	}
	public MasterFragment(int mode){
		this.mode=mode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//PRIMA POSSIBILE SOLUZIONE (A problema di exception al cambio di tab)
		QueryManager.getInstance().addListener(this);
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_master,container, false);
			//Ora sarà masterFragment il listener del loader e passerà i risultati al RequestList
			List<Request> requests = new ArrayList<Request>();
			RequestList requestListFragment = null;
			switch (mode) {
			case OWNER_REQUEST:
				requests = QueryManager.getInstance().getCurrentUser().getRequests();
				System.out.println("Dovrei aver recuperato le richieste dell'owner");
				break;
			case JOINED_REQUEST:
				String mail=null;
				if (QueryManager.getInstance().getCurrentUser()!=null){
					User current =QueryManager.getInstance().getCurrentUser();
					mail = current.getPwAccount() != null ? 
							current.getPwAccount()
							: current.getGmailAccount() != null ?
									current.getGmailAccount() 
									: current.getFbAccount() != null ?
											current.getFbAccount()
											: null;
					requests = QueryManager.getInstance().getUserPartecipation(mail);
				}
				else
					requests = new ArrayList<Request>();
				break;
			default: //Caso ALL_REQUEST + tutti gli altri possibili
				//TODO
				requests = QueryManager.getInstance().getRequests();
				break;
			}
			requestListFragment = new RequestList(requests, mode);
			// CASO TABLET:
			if (view.findViewById(R.id.detail_container) != null) {
				// The detail container view will be present only in the
				// large-screen layouts (res/values-large and
				// res/values-sw600dp). If this view is present, then the
				// activity should be in two-pane mode.
				twoPane = true;
				getChildFragmentManager().beginTransaction()
				.replace(R.id.request_list_container,requestListFragment,RequestList.ID)
				.commit();
				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				//requestListFragment.setActivateOnItemClick(true);
			} else { //CASO SMARTPHONE:
				getChildFragmentManager().beginTransaction()
				.replace(R.id.container,requestListFragment,RequestList.ID)
				.commit();
			}
		} catch (InflateException e) {
			// is already there, just return view as it is
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onRequestSelected(int position, Request request) {
		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			DetailContainerFragment detailContFrag = new DetailContainerFragment(request,mode);
			getChildFragmentManager().beginTransaction()
			.replace(R.id.detail_container, detailContFrag, DetailContainerFragment.ID).commit();
		} else {
			// In single-pane mode, simply start the detail fragment
			// for the selected item ID.
			switch (mode) {
			case OWNER_REQUEST:
				//UserList userListFragment = new UserList
				break;
			case JOINED_REQUEST:
				break;

			default://ALL_REQUEST				
				break;
			}
			RequestDetail fragment = new RequestDetail(request,mode);
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
	public void onUserClicked(User user,String requestId) {
		if (!twoPane) {
			// In single-pane mode, simply start the detail fragment
			// for the selected item ID.
			FeedbackDetail fragment = new FeedbackDetail(user,this.mode,requestId);
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
		} else {
			/*Se ne dovrebbe occupare il DetailContainerFragment, quindi non fa nulla*/
		}
	}
	@Override
	public void onRequestLoaded(List<Request> requests) {
		System.out.println("Ho caricato: "+requests.size());
		RequestList requestListFragment = (RequestList)getChildFragmentManager().findFragmentByTag(RequestList.ID);
		switch (mode) {
		case OWNER_REQUEST:
			//TODO
			break;
		case JOINED_REQUEST:
			//TODO
			break;
		default: //Caso ALL_REQUEST + tutti gli altri possibili
			if (requestListFragment!=null)
				requestListFragment.setRequestAdapter(requests);
			break;
		}
	}

}
