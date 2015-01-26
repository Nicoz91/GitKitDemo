package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.ParentFragmentUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class InsertFeedback extends Fragment implements OnClickListener, OnRatingBarChangeListener {

	private int evaluation=3;
	private EditText commentET;
	private User fromUser, toUser;
	private OnFeedbackSentListener mListener;
	
	public InsertFeedback(User fromUser, User toUser){
		this.fromUser=fromUser;
		this.toUser=toUser;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_insert_feedback,
				container, false);
		RatingBar evaluationRB = (RatingBar) rootView.findViewById(R.id.valutazione);
		evaluationRB.setOnRatingBarChangeListener(this);
		commentET = (EditText) rootView.findViewById(R.id.description);
		Button send = (Button) rootView.findViewById(R.id.send);
		send.setOnClickListener(this);
		mListener = ParentFragmentUtil.getParent(this, OnFeedbackSentListener.class);
		return rootView;
	}
	
	public interface OnFeedbackSentListener{
		public void onFeedbackSent(Feedback feedback, User from, User to);//TODO user in fb o esterni?
	}

	@Override
	public void onClick(View v) {
		Feedback fb = new Feedback();
		fb.setEvaluation(evaluation);
		fb.setDescription(commentET.getEditableText().toString());
		//fb.setFrom(fromUser);
		//fb.setTo(toUser); TODO, servono?
		mListener.onFeedbackSent(fb, fromUser, toUser);
		
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		evaluation = (int) Math.round(rating);
	}
}
