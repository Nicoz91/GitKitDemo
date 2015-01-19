package it.polimi.frontend.fragment;

import java.io.IOException;

import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
//import android.support.v7.app.ActionBarActivity;

public class RequestDetail extends Fragment {

	public static final String ID="RequestDetailFragmentID";
	private Request request;
	private ImageView profileImg;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public RequestDetail() {
	}

	public RequestDetail(Request request){
		this.request=request;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//TODO per gestire l'actionbar con API10 serve libreria opensource ActionBarSherlock
		View rootView = inflater.inflate(R.layout.request_detail_fragment,
				container, false);
		profileImg = (ImageView) rootView.findViewById(R.id.profileImg);
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
}
