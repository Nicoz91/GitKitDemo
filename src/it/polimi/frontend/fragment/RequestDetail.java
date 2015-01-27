package it.polimi.frontend.fragment;

import java.io.IOException;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.fragment.RequestList.OnRequestSelectedListener;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.QueryManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.support.v7.app.ActionBarActivity;

public class RequestDetail extends Fragment implements View.OnClickListener{

	public static final String ID="RequestDetailFragmentID";
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int mode=0;
	private Request request;
	private ImageView profileImg;
	private OnUserSectionClickedListener listener;
	private Button join;
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
		profileImg = (ImageView) rootView.findViewById(R.id.profileImg);
		join = (Button) rootView.findViewById(R.id.joinReq);
		System.out.println("Mio ID: "+QueryManager.getInstance().getCurrentUser().getId());
		System.out.println("Partecipants: "+request.getPartecipants());
		if(request.getPartecipants()==null || !request.getPartecipants().contains((QueryManager.getInstance().getCurrentUser().getId())))
			join.setText("Partecipa");
		else
			join.setText("Cancella");
		
		join.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(request.getPartecipants()==null || !request.getPartecipants().contains((QueryManager.getInstance().getCurrentUser().getId())) ){
					QueryManager.getInstance().joinRequest(request);
					//QueryManager.getInstance().insertFeedback();
					join.setText("Cancella");
				}
				else{
					QueryManager.getInstance().removeJoinRequest(request);
					//QueryManager.getInstance().insertFeedback();
					join.setText("Partecipa");
				}
			}

		});
		((LinearLayout)rootView.findViewById(R.id.userSection)).setOnClickListener(this);
		if (request!=null){
			new ProfileImageTask().execute(request.getOwner().getPhotoURL());
			((TextView)rootView.findViewById(R.id.ownerLabel))
			.setText(request.getOwner().getName()+" "+request.getOwner().getSurname());
			if (request.getOwner().getBday()!=null){
				((TextView)rootView.findViewById(R.id.ageSexLabel)).setText(request.getOwner().getBday().toString());
			} else {
				((TextView)rootView.findViewById(R.id.ageSexLabel)).setText("Età non specificata.");
			}
			((TextView)rootView.findViewById(R.id.titleLabel))
			.setText(request.getTitle());
			((TextView)rootView.findViewById(R.id.descriptionLabel))
			.setText(request.getDescription());
		}
		//registrazione del parente in ascolto
		listener = ParentFragmentUtil.getParent(this, OnUserSectionClickedListener.class);
		return rootView;
	}

	public void setRequest(Request request){
		this.request=request;
		refresh();
	}

	private void refresh(){
		//TODO
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
			if (bitmap != null) {
				profileImg.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * Interfaccia che deve implementare il master (fragment o activity) per mostrare i 
	 * feedback dello user (in modo diverso a seconda del dispositivo).
	 * */
	public interface OnUserSectionClickedListener {
		public void onUserSectionClicked(User owner);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userSection:
			listener.onUserSectionClicked(request.getOwner());
			break;

		default:
			break;
		}
	}
}
