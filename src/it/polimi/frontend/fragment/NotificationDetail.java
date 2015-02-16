package it.polimi.frontend.fragment;

import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnActionListener;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
public class NotificationDetail extends Fragment implements OnActionListener{

	public static final String ID = "NotificationDetailFragmentID";
	private ListView notificationLV;
	private ArrayList<String> notification;
	private ArrayAdapter<String> adapter;

	public NotificationDetail(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		QueryManager.getInstance().addActionListener(this);
		View rootView = inflater.inflate(R.layout.fragment_notification_detail,
				container, false);
		this.notificationLV = (ListView)rootView.findViewById(R.id.notificationList);
		notificationLV.setEmptyView(rootView.findViewById(R.id.empty));
		
		notification = QueryManager.getInstance().getNotification();
		
		if(notification == null)
			notification = new ArrayList<String>();
		Context c = getActivity();
		if(c==null){ 
			c = MyApplication.getContext();
		}
        adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, notification);
		notificationLV.setAdapter(adapter);

		return rootView;
	}

	@Override
	public void onPerformingAction(int action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionPerformed(Object result, int action) {
		if(action == OnActionListener.NOTIFICATION){
			ArrayList<String> not = (ArrayList<String>) result;
			if(not == null)
				Toast.makeText(MyApplication.getContext(),"Impossibile contattare il server...",Toast.LENGTH_SHORT).show();				
			else{
				notification = not;
				Context c = getActivity();
				if(c==null){ 
					c = MyApplication.getContext();
				}
		        adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, notification);
				notificationLV.setAdapter(adapter);
			}
				
		}
		
	}

}
