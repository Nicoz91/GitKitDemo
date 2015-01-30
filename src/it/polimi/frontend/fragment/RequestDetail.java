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
import java.util.List;

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
//import android.support.v7.app.ActionBarActivity;

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
		if(request.getPartecipants()==null || !request.getPartecipants().contains((QueryManager.getInstance().getCurrentUser().getId())))
			join.setText("Partecipa");
		else
			join.setText("Cancella");

		join.setOnClickListener(this);
		LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.userSection);
		if (mode==OWNER_REQUEST){
			ll.setVisibility(View.GONE);
			join.setVisibility(View.GONE);
			}
		else
			ll.setOnClickListener(this);

		if (request!=null){
			if (request.getOwner()!= null && request.getOwner().getPhotoURL()!=null)
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
			userPartecipant = (ListView) rootView.findViewById(R.id.partecipantsList);

		}
		//registrazione del parente in ascolto
		listener = ParentFragmentUtil.getParent(this, OnUserClickedListener.class);
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
	public interface OnUserClickedListener {
		public void onUserClicked(User user, String requestId);
	}

	@Override
	public void onClick(View v) {
		System.out.println("Sono entrato nell'onclick");
		switch (v.getId()) {
		case R.id.userSection:
			listener.onUserClicked(request.getOwner(),request.getId());
			break;
		case R.id.joinReq:
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
			break;
		case R.id.showPartecipant:
			System.out.println("Sono entrato nell'onclick di showPartecipant");
			if (sendFb.getText().toString().equals("Mostra Partecipanti")){
				sendFb.setText("Nascondi Partecipanti");
				userPartecipant.setVisibility(View.VISIBLE);

				userPartecipant.setOnItemClickListener(this);

				List<User> users = QueryManager.getInstance().getUserFromRequest(request);
				Context c = getActivity();
				if(c==null){ 
					System.out.println("Il context è null ma noi bariamo");
					c = MyApplication.getContext();
				}
				else 
					System.out.println("Tutto ok inizializzo l'adapter");
				userPartecipant.setAdapter(new UserAdapter(c,0,users));
			}else{
				userPartecipant.setVisibility(View.GONE);
				sendFb.setText("Mostra Partecipanti");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		listener.onUserClicked((User) userPartecipant.getAdapter().getItem(position),request.getId());
	}
}
