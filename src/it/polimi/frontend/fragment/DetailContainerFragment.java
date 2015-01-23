package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailContainerFragment extends Fragment implements RequestDetail.OnUserSectionClickedListener{

	public static final String ID="DetailContainerFragmentID";
	private Request request;
	
	public DetailContainerFragment(Request request){
		this.request=request;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_detail_container,
				container, false);
		//Inizializza dettaglio della richiesta
		RequestDetail fragment = new RequestDetail(request);
		getChildFragmentManager().beginTransaction()
		.replace(R.id.request_detail_container, fragment,RequestDetail.ID).commit();
		System.out.println("Dentro DetailContainerFragment. Dovrei aver creato RequestDetail.");
		return rootView;
	}

	@Override
	public void onUserSectionClicked(User owner) {
		FeedbackDetail fragment = new FeedbackDetail(owner);
		getChildFragmentManager().beginTransaction()
			.replace(R.id.feedback_list_container, fragment,FeedbackDetail.ID).commit();
		System.out.println("Dentro DetailContainerFragment. Dovrei aver creato FeedbackDetail");
	}
}
