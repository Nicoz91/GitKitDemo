package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.FeedbackAdapter;
import it.polimi.frontend.util.QueryManager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
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
	private LinearLayout sendFbForm;
	private Request request;

	public interface OnFeedbackSentListener{
		public void onFeedbackSent(Feedback feedback);//TODO
	}

	public FeedbackDetail(){

	}

	public FeedbackDetail(User owner, int mode, Request request){
		this.owner=owner;
		this.mode=mode;
		this.request=request;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback_detail,
				container, false);
		this.sendFbForm= (LinearLayout)rootView.findViewById(R.id.sendFbForm);
		if (mode==ALL_REQUEST){
			sendFbForm.setVisibility(View.GONE);
			rootView.findViewById(R.id.fbNotAllowed).setVisibility(View.VISIBLE);
		}
		((Button) rootView.findViewById(R.id.send)).setOnClickListener(this);
		((RatingBar)rootView.findViewById(R.id.valutazione)).setOnRatingBarChangeListener(this);
		this.feedbackLV = (ListView)rootView.findViewById(R.id.feedbackList);
		feedbackLV.setEmptyView(rootView.findViewById(R.id.empty));
		if (owner!=null){
			if(owner.getId().equals(QueryManager.getInstance().getCurrentUser().getId())){
				sendFbForm.setVisibility(View.GONE);
				rootView.findViewById(R.id.fbNotAllowed).setVisibility(View.VISIBLE);
			}
			List<Feedback> feedbacks = owner.getReceivedFb();
			//feedback di prova per visualizzazione
			if (feedbacks==null)
				feedbacks= new ArrayList<Feedback>();
			if(!request.getPastRequest()){
				sendFbForm.setVisibility(View.GONE);
				rootView.findViewById(R.id.fbNotAllowed).setVisibility(View.VISIBLE);
			} else {
				for(Feedback f : feedbacks){
					if(f.getFrom().equals(QueryManager.getInstance().getCurrentUser()) && f.getToId().equals(owner.getId()) && f.getRequest().equals(request.getId())){
						sendFbForm.setVisibility(View.GONE);
						rootView.findViewById(R.id.fbNotAllowed).setVisibility(View.VISIBLE);
					}/* else {
						rootView.findViewById(R.id.fbNotAllowed).setVisibility(View.GONE);
					}*/
				}
			}
			Context c = getActivity();
			if(c==null){ 
				c = MyApplication.getContext();
			}
			FeedbackAdapter fba = new FeedbackAdapter(c,0,feedbacks);
			feedbackLV.setAdapter(fba);
		}else
			System.out.println("Nessun Nome dell'owner");
		return rootView;
	}

	@Override
	public void onClick(View v) {
		sendFbForm.setVisibility(View.GONE);
		getView().findViewById(R.id.fbNotAllowed).setVisibility(View.VISIBLE);
		Feedback fb = new Feedback();
		fb.setEvaluation(evaluation);
		fb.setDescription(((EditText)getView().findViewById(R.id.description)).getEditableText().toString());
		fb.setToId(owner.getId());
		fb.setRequest(request.getId());
		//mListener.onFeedbackSent(fb); //TODO pensare se convenga farlo inserire al parent
		QueryManager.getInstance().insertFeedback(fb); //...o qui direttamente
		if (owner!=null){
			List<Feedback> feedbacks = owner.getReceivedFb();
			//feedback di prova per visualizzazione
			if (feedbacks==null)
				feedbacks= new ArrayList<Feedback>();
			Context c = getActivity();
			if(c==null){ 
				//				System.out.println("Il context è null ma noi bariamo");
				c = MyApplication.getContext();
			}
			FeedbackAdapter fba = new FeedbackAdapter(c,0,feedbacks);
			feedbackLV.setAdapter(fba);
		}
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		evaluation = (int) Math.round(rating);
	}
}
