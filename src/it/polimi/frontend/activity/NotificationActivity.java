package it.polimi.frontend.activity;

import android.support.v7.app.ActionBarActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class NotificationActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(12345);
		LoginSession.setNotNumber(0);
	}


}
