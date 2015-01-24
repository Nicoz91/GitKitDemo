package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.FeedbackAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
public class FeedbackDetail extends Fragment {

	public static final String ID = "FeedbackDetailFragmentID";
	private User owner;
	private ListView feedbackLV;
	
	public FeedbackDetail(User owner){
		this.owner=owner;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback_detail,
				container, false);
		this.feedbackLV = (ListView)rootView.findViewById(R.id.feedbackList);
		if (owner!=null){
			((TextView)rootView.findViewById(R.id.feedbackLabel)).setText("Nome del'owner"+owner.getName());
			List<Feedback> feedbacks = owner.getReceivedFb();
			//feedback di prova per visualizzazione
			if (feedbacks==null)
				feedbacks= new ArrayList<Feedback>();
			Feedback f = new Feedback();
			User u = new User();
			u.setName("Primo");
			u.setSurname("Reviewer");
			f.setFrom(u);
			f.setEvaluation(3);
			f.setDescription("Questa persona fa schifo. Ma comunque gli do 3 stelle.");
			feedbacks.add(f);
			Context c = getActivity();
			if(c==null){ 
				System.out.println("Il context è null ma noi bariamo");
				c = MyApplication.getContext();
			}
			else 
				System.out.println("Tutto ok inizializzo l'adapter");
			FeedbackAdapter fba = new FeedbackAdapter(c,0,feedbacks);
			feedbackLV.setAdapter(fba);
		}else
			((TextView)rootView.findViewById(R.id.feedbackLabel)).setText("Nessun Nome dell'owner");
		System.out.println("Sono dentro l'onCreateView del FeedbackDetail Fragment");
		return rootView;
	}
	
}
