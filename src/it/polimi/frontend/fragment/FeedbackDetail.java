package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class FeedbackDetail extends Fragment {

	public static final String ID = "FeedbackDetailFragmentID";
	private User owner;
	
	public FeedbackDetail(User owner){
		this.owner=owner;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback_detail,
				container, false);
		if (owner!=null)
			((TextView)rootView.findViewById(R.id.feedbackLabel)).setText("Nome del'owner"+owner.getName());
		else
			((TextView)rootView.findViewById(R.id.feedbackLabel)).setText("Nessun Nome dell'owner");
		System.out.println("Sono dentro l'onCreateView del FeedbackDetail Fragment");
		return rootView;
	}
	
}
