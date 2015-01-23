package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.RequestAdapter;
import it.polimi.frontend.util.RequestLoader;
import it.polimi.frontend.util.RequestLoader.OnRequestLoadedListener;
import java.util.List;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RequestList extends ListFragment implements OnRequestLoadedListener{

	OnRequestSelectedListener mListener;
	public final static String ID="RequestListFragmentID";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//		new TestInsertTask().execute();
		// new Query().execute();
		
		RequestLoader.getInstance().addListener(this);
		List<Request> requests = RequestLoader.getInstance().getRequests();
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			//RequestLoader.getInstance().loadRequest();
		}
		mListener = ParentFragmentUtil.getParent(this, OnRequestSelectedListener.class);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onRequestSelected(position, (Request) getListAdapter().getItem(position));
	}

	/**
	 * Interfaccia che deve implementare il master (fragment o activity) per ricevere dati sulla 
	 * richiesta selezionata.
	 * */
	public interface OnRequestSelectedListener {
		public void onRequestSelected(int position, Request request);
	}

	@Override
	public void onRequestLoaded(List<Request> requests) {
		System.out.println("Ho ricevuto requests:");
		if(requests==null)
			System.out.println("Requests è nullo");
		else
			setRequestAdapter(requests);		
	}

	public void setRequestAdapter(List<Request> requests){
		System.out.println("Setto le richieste");
		List<Request> reqs = requests;
		Context c = getActivity();
		if(c==null){ 
			System.out.println("Il context è null ma noi bariamo");
			c = MyApplication.getContext();
		}
		else 
			System.out.println("Tutto ok inizializzo l'adapter");
		RequestAdapter adapter = new RequestAdapter(c,0,reqs);
		setListAdapter(adapter);
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(
                activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_NONE);
    }
	
}