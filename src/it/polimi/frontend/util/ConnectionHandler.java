package it.polimi.frontend.util;

import it.polimi.frontend.activity.MainActivity;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.TabbedActivity;
import it.polimi.frontend.activity.WaitActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHandler extends BroadcastReceiver {
	
	private boolean state;
	
	public ConnectionHandler(){
		state = isConnected();
	}
	

	public static boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	
    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                              activeNetwork.isConnected();
        return isConnected;
    }
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("Ho ricevuto il cambio di connettività");
		if(isConnected(context) && !state)
		{
			state = true;
			Intent i = new Intent(context, MainActivity.class);
			i.putExtra("Reason", "Network");
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
			//QueryManager.getInstance().loadRequest();

		}
		if(!isConnected(context) && state){
			state = false;
			context.startActivity(new Intent(context, WaitActivity.class));
		}
//        if(isConnected(context)) Toast.makeText(context, "Connected.", Toast.LENGTH_LONG).show();
//        else Toast.makeText(context, "Lost connect.", Toast.LENGTH_LONG).show();
		
	}

}
