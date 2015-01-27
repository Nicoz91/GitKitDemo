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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
public class FeedbackDetail extends Fragment implements OnClickListener, OnRatingBarChangeListener{

	public static final String ID = "FeedbackDetailFragmentID";
	private User owner;
	private ListView feedbackLV;
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int mode=0;
	private int evaluation=3;
	private EditText commentET;
	private User fromUser, toUser;
	private OnFeedbackSentListener mListener;

	public interface OnFeedbackSentListener{
		public void onFeedbackSent(Feedback feedback);//TODO
	}
	
	public FeedbackDetail(User owner, int mode){
		this.owner=owner;
		this.mode=mode;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback_detail,
				container, false);
		this.feedbackLV = (ListView)rootView.findViewById(R.id.feedbackList);
		if (owner!=null){
			List<Feedback> feedbacks = owner.getReceivedFb();
			//feedback di prova per visualizzazione
			if (feedbacks==null)
				feedbacks= new ArrayList<Feedback>();
			Feedback f = new Feedback();
			User u = new User();
			u.setName("Primo");
			u.setSurname("Reviwer");
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
			System.out.println("Nessun Nome dell'owner");
		System.out.println("Sono dentro l'onCreateView del FeedbackDetail Fragment");
		return rootView;
	}

	@Override
	public void onClick(View v) {
		Feedback fb = new Feedback();
		fb.setEvaluation(evaluation);
		fb.setDescription(commentET.getEditableText().toString());
		//fb.setFrom(fromUser);
		//fb.setTo(toUser); TODO, servono?
		mListener.onFeedbackSent(fb);
		
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		evaluation = (int) Math.round(rating);
	}
}
