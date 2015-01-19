package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.requestendpoint.model.Request;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import android.support.v7.app.ActionBarActivity;

public class RequestDetail extends Fragment {

	public static final String ID="RequestDetailFragmentID";
	private Request request;
	private TextView ownerTV;

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
		if (request!=null){
			System.out.println("Sono nel dettaglio e il nome dell'owner è "+request.getOwner().getName());
			ownerTV=(TextView)rootView.findViewById(R.id.ownerLabel);
			ownerTV.setText(request.getOwner().getName());
		}
		return rootView;
	}
	
	public void setRequest(Request request){
		this.request=request;
		refresh();
	}
	
	private void refresh(){
		ownerTV.setText(request.getOwner().getName());
	}
}
