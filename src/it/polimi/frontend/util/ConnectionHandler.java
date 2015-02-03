package it.polimi.frontend.util;

import it.polimi.frontend.activity.MyApplication;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHandler {
	private static ConnectionHandler instance;
	private ConnectivityManager cm;
	
	private ConnectionHandler(){
		cm =(ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static ConnectionHandler getInstance(){
		if(instance ==	null ){
			instance = new ConnectionHandler();
		}
		return instance;
	}
	
	public boolean isConnected(){
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

}
