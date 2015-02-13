package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.fragment.RequestDetail.OnRequestDeletedListener;
import it.polimi.frontend.util.ParentFragmentUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailContainerFragment extends Fragment implements RequestDetail.OnUserClickedListener, RequestDetail.OnRequestDeletedListener{

	public static final String ID="DetailContainerFragmentID";
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int mode=0;
	private Request request;
	private OnRequestDeletedListener deletionListener;
	
	public DetailContainerFragment(){
		
	}
	
	public DetailContainerFragment(Request request,int mode){
		this.request=request;
		this.mode=mode;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail_container,
				container, false);
		//Inizializza dettaglio della richiesta
		RequestDetail fragment = new RequestDetail(request,mode);
		getChildFragmentManager().beginTransaction()
		.replace(R.id.request_detail_container, fragment,RequestDetail.ID).commit();
		deletionListener = ParentFragmentUtil.getParent(this, OnRequestDeletedListener.class);
		return rootView;
	}

	@Override
	public void onUserClicked(User user,Request request) {
		FeedbackDetail fragment = new FeedbackDetail(user,this.mode,request);
		getChildFragmentManager().beginTransaction()
			.replace(R.id.feedback_list_container, fragment,FeedbackDetail.ID).commit();
	}

	@Override
	public void onRequestDeleted(Request request) {
		//Rimbalza semplicemente la richiesta a MasterFragment
		deletionListener.onRequestDeleted(request);
	}
}
