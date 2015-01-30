package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.RequestAdapter;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RequestList extends ListFragment /*implements OnRequestLoadedListener*/{

	OnRequestSelectedListener mListener;
	public final static String ID="RequestListFragmentID";
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int listMode=0;
	private List<Request> requests;

	public RequestList(){
	}
	
	public RequestList(List<Request> reqs, int mode){
		requests=reqs;
		this.listMode=mode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//		new TestInsertTask().execute();
		// new Query().execute();
		/*
		RequestLoader.getInstance().addListener(this);
		List<Request> requests=null;

		switch (listMode) {
		case ALL_REQUEST:
			requests = RequestLoader.getInstance().getRequests();			
			break;
		case OWNER_REQUEST:
			if (owner!=null)
				requests = owner.getRequests();	
			else
				System.out.println("RequestList: owner NULL");
			break;
		case JOINED_REQUEST:
			if (owner!=null){
				List<String> keys = owner.getJoinedReq();
				requests = new ArrayList<Request>();
				List<Request> allReqs = RequestLoader.getInstance().getRequests();
				//Pseudo JOIN nested loop a mano in locale
				if (allReqs!=null && allReqs.size()>0 && keys!=null && keys.size()>0)
					for (Request r: allReqs)
						for(String k: keys)
							if(r.getId().equals(k)){
								requests.add(r);
								continue;
							}
			}
			else
				System.out.println("RequestList: owner NULL");			
			break;
		default:
			requests = RequestLoader.getInstance().getRequests();
			break;
		}
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			//RequestLoader.getInstance().loadRequest();
		}*/
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			setRequestAdapter(new ArrayList<Request>());
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

	//@Override
	public void onRequestLoaded(List<Request> requests) {
		System.out.println("Ho ricevuto requests:");
		if(requests==null)
			System.out.println("Requests è nullo");
		else
			setRequestAdapter(requests);		
	}

	public void setRequestAdapter(List<Request> requests){
		System.out.println("Setto le richieste");
		List<Request> reqs;
		if(requests == null)
			reqs = new ArrayList<Request>();
		else
			reqs= requests;
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