package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.UserAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RequestDetail extends Fragment implements OnClickListener, OnItemClickListener{

	public static final String ID="RequestDetailFragmentID";
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int mode=0;
	private Request request;
	private ImageView profileImg;
	private OnUserClickedListener listener;
	private Button join,sendFb;
	private ListView userPartecipant;
	private String MOSTRA,NASCONDI;
	private String CANCELLA_PART,PARTECIPA;
	private String CANCELLA_REQ;
	private String NON_SPECIFICATO;
	private String LASCIA_FEED;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RequestDetail() {

	}

	public RequestDetail(Request request, int mode){
		this.request=request;
		this.mode=mode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_request_detail,
				container, false);
		MOSTRA=getString(R.string.showPartecipants);
		NASCONDI=getString(R.string.hidePartecipants);
		CANCELLA_PART=getString(R.string.cancelPartecipation);
		PARTECIPA=getString(R.string.join);
		CANCELLA_REQ=getString(R.string.cancelRequest);
		NON_SPECIFICATO=getString(R.string.not_specified);
		LASCIA_FEED = getString(R.string.insertFeedback);
		profileImg = (ImageView) rootView.findViewById(R.id.profileImg);
		join = (Button) rootView.findViewById(R.id.joinReq);
		sendFb = (Button) rootView.findViewById(R.id.showPartecipant);
		if(mode!=OWNER_REQUEST){
			if(request.getPastRequest()){
				if(alreadyInsertFeed())
					join.setVisibility(View.GONE);
				else
					join.setText(LASCIA_FEED);
			}else{
				if(request.getPartecipants()==null || !request.getPartecipants().contains((QueryManager.getInstance().getCurrentUser().getId())))
					join.setText(PARTECIPA);
				else
					join.setText(CANCELLA_PART);
			}
		} else
			join.setText(CANCELLA_REQ);
		join.setOnClickListener(this);
		sendFb.setOnClickListener(this);
		LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.userSection);
		if (mode==OWNER_REQUEST){
			ll.setVisibility(View.GONE);
			//join.setVisibility(View.GONE);
		}
		else
			ll.setOnClickListener(this);

		if (request!=null){
			//Photo
			if (request.getOwner()!= null && request.getOwner().getPhotoURL()!=null)
				new ProfileImageTask().execute(request.getOwner().getPhotoURL());
			//Name and surname
			((TextView)rootView.findViewById(R.id.ownerLabel))
			.setText(request.getOwner().getName()+" "+request.getOwner().getSurname());
			//Age and sex
			String subText="";
			if (request.getOwner().getGender())
				subText+=getString(R.string.detailMale);
			else
				subText+=getString(R.string.detailFemale);
			if (request.getOwner().getBday()!=null){
				Calendar dob = Calendar.getInstance();	
				dob.setTimeInMillis(request.getOwner().getBday().getValue());
				Calendar now = Calendar.getInstance();
				subText+=getAge(dob,now)+getString(R.string.detailYears);
			} else 
				subText+=NON_SPECIFICATO;
			((TextView)rootView.findViewById(R.id.ageSexLabel)).setText(subText);
			//Titolo richiesta
			((TextView)rootView.findViewById(R.id.title))
			.setText(request.getTitle());
			//Start date
			if(request.getStart()!=null){
				Calendar start = Calendar.getInstance();
				start.setTimeInMillis(request.getStart().getValue());
				String dataInizio = start.get(Calendar.DAY_OF_MONTH)+" "+
						start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
						+" "+start.get(Calendar.YEAR);
				((TextView)rootView.findViewById(R.id.date))
				.setText(dataInizio);
			} else
				((TextView)rootView.findViewById(R.id.date))
				.setText(NON_SPECIFICATO);
			//Duration
			if(request.getEnd()!=null){
				long durationMs = request.getStart().getValue()-request.getEnd().getValue();
				long duration = durationMs / 1000;
				long h = duration / 3600;
				long m = (duration - h * 3600) / 60;
				long s = duration - (h * 3600 + m * 60);
				String durationValue;
				if (h == 0)
					durationValue = m+"m:"+s+"s";
				else 
					durationValue = h+"h:"+m+"m:"+s+"s";
				((TextView)rootView.findViewById(R.id.duration))
				.setText(durationValue);
			} else 
				((TextView)rootView.findViewById(R.id.duration))
				.setText(NON_SPECIFICATO);
			//Tipo richiesta
			if (request.getType()!=null)
				((TextView)rootView.findViewById(R.id.type))
				.setText(request.getType());
			else
				((TextView)rootView.findViewById(R.id.type))
				.setText(NON_SPECIFICATO);
			//N° max partecipanti richiesta
			if (request.getMaxPartecipants()!=0)
				((TextView)rootView.findViewById(R.id.max))
				.setText(request.getType());
			else
				((TextView)rootView.findViewById(R.id.max))
				.setText(NON_SPECIFICATO);
			//Descrizione
			((TextView)rootView.findViewById(R.id.descriptionLabel))
			.setText(request.getDescription());
			userPartecipant = (ListView) rootView.findViewById(R.id.partecipantsList);
			userPartecipant.setOnItemClickListener(this);

		}
		//registrazione del parente in ascolto
		listener = ParentFragmentUtil.getParent(this, OnUserClickedListener.class);
		return rootView;
	}

	private boolean alreadyInsertFeed(){
		User owner = request.getOwner();
		if (owner!=null){
			List<Feedback> feedbacks = owner.getReceivedFb();
			//feedback di prova per visualizzazione
			if (feedbacks==null)
				feedbacks= new ArrayList<Feedback>();

			for(Feedback f : feedbacks){
				if(f.getFrom().equals(QueryManager.getInstance().getCurrentUser()) && f.getToId().equals(owner.getId()) && f.getRequest().equals(request.getId()))
					return true;
			}
			return false;
		}
		return false;
	}

	public void setRequest(Request request){
		this.request=request;
		refresh();
	}

	private void refresh(){
		//TODO
	}


	/**
	 * Interfaccia che deve implementare il master (fragment o activity) per mostrare i 
	 * feedback dello user (in modo diverso a seconda del dispositivo).
	 * */
	public interface OnUserClickedListener {
		public void onUserClicked(User user, Request request);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userSection:
			listener.onUserClicked(request.getOwner(),request);
			break;
		case R.id.joinReq:
			if(mode!=OWNER_REQUEST){
				if(join.getText().equals(LASCIA_FEED)){
					listener.onUserClicked(request.getOwner(),request);
				}else{
				if(request.getPartecipants()==null || !request.getPartecipants().contains((QueryManager.getInstance().getCurrentUser().getId())) ){
					QueryManager.getInstance().joinRequest(request);
					//new JoinTask().execute(true);
					join.setText(CANCELLA_PART);
				}
				else{
					QueryManager.getInstance().removeJoinRequest(request);
					//new JoinTask().execute(false);
					join.setText(PARTECIPA);
				}
				}
			}else{
				QueryManager.getInstance().removeOwnerRequest(request);
			}
			break;
		case R.id.showPartecipant:
			if (sendFb.getText().toString().equals(MOSTRA)){
				sendFb.setText(NASCONDI);
				userPartecipant.setVisibility(View.VISIBLE);
				List<User> users = QueryManager.getInstance().getUserFromRequest(request);
				if (users!=null && users.size()>0)
					getView().findViewById(R.id.empty).setVisibility(View.GONE);
				else
					getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
				Context c = getActivity();
				if(c==null){ 
					//					System.out.println("Il context è null ma noi bariamo");
					c = MyApplication.getContext();
				}
				userPartecipant.setAdapter(new UserAdapter(c,0,users));
			}else{
				userPartecipant.setVisibility(View.GONE);
				getView().findViewById(R.id.empty).setVisibility(View.GONE);
				sendFb.setText(MOSTRA);
			}
			break;
		default:
			break;
		}
	}

	private int getAge(Calendar dob, Calendar now){
		int age=0;
		age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if ((dob.get(Calendar.MONTH) > now.get(Calendar.MONTH))
				|| (dob.get(Calendar.MONTH) == now.get(Calendar.MONTH) && dob.get(Calendar.DAY_OF_MONTH) > now
				.get(Calendar.DAY_OF_MONTH))) {
			age--;
		}
		return age;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		listener.onUserClicked((User) userPartecipant.getAdapter().getItem(position),request);
	}


	private class ProfileImageTask extends AsyncTask<String, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(String... arg) {
			try {
				byte[] result = HttpUtils.get(arg[0]);
				return BitmapFactory.decodeByteArray(result, 0, result.length);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null && profileImg!=null) {
				profileImg.setImageBitmap(bitmap);
			}
		}
	}

	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	private ProgressDialog mProgressDialog;
	protected void showDialog() {
		if (mProgressDialog == null) {
			setProgressDialog();
		}
		mProgressDialog.show();
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog() {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle("Attendi...");
		mProgressDialog.setMessage("Sto scaricando...");
	}
}
