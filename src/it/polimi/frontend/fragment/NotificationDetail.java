package it.polimi.frontend.fragment;

import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.QueryManager;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
public class NotificationDetail extends Fragment{

	public static final String ID = "NotificationDetailFragmentID";
	private ListView notificationLV;
	private ArrayList<String> notification;
	private ArrayAdapter<String> adapter;

	public NotificationDetail(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

}
