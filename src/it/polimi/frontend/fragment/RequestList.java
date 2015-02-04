package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.RequestAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RequestList extends Fragment implements OnItemClickListener,SwipeRefreshLayout.OnRefreshListener/*implements OnRequestLoadedListener*/{

	OnRequestSelectedListener mListener;
	public final static String ID="RequestListFragmentID";
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int listMode=0;
	private List<Request> requests;
	private ListView requestList;
	private SwipeRefreshLayout swipeRefresh;

	public RequestList(){
	}

	public RequestList(List<Request> reqs, int mode){
		requests=reqs;
		this.listMode=mode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_request_list,
				container, false);
		swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
		swipeRefresh.setOnRefreshListener(this);
		requestList = (ListView) rootView.findViewById(R.id.requestList);
		requestList.setOnItemClickListener(this);
		//Per attivare swipeRefresh solo se in cima alla lista 
		requestList.setOnScrollListener(new AbsListView.OnScrollListener() {  
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int topRowVerticalPosition = 
						(requestList == null || requestList.getChildCount() == 0) ? 
								0 : requestList.getChildAt(0).getTop();
				swipeRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
			}
		});
		requestList.setEmptyView(rootView.findViewById(R.id.empty));
		if(requests!=null && requests.size()>0 ){
			setRequestAdapter(requests);	
		}
		else{
			//			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			setRequestAdapter(new ArrayList<Request>());
			//RequestLoader.getInstance().loadRequest();
		}

		mListener = ParentFragmentUtil.getParent(this, OnRequestSelectedListener.class);
		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> list, View v, int position, long id) {
		mListener.onRequestSelected(position, (Request) requestList.getAdapter().getItem(position));
	}

	/**
	 * Interfaccia che deve implementare il master (fragment o activity) per ricevere dati sulla 
	 * richiesta selezionata.
	 * */
	public interface OnRequestSelectedListener {
		public void onRequestSelected(int position, Request request);
	}

	public void setRequestAdapter(List<Request> requests){
		//		System.out.println("Setto le richieste");
		if (swipeRefresh!=null)
			swipeRefresh.setRefreshing(false);
		List<Request> reqs;
		if(requests == null)
			reqs = new ArrayList<Request>();
		else
			reqs= requests;
		Context c = getActivity();
		if(c==null){ 
			//			System.out.println("Il context è null ma noi bariamo");
			c = MyApplication.getContext();
		}
		RequestAdapter adapter = new RequestAdapter(c,0,reqs,this.listMode);
		requestList.setAdapter(adapter);
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		if (requestList!=null)
			requestList.setChoiceMode(
					activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
							: ListView.CHOICE_MODE_NONE);
	}

	@Override
	public void onRefresh() {
		QueryManager.getInstance().loadRequest();
	}


}