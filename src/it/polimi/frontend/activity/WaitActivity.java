package it.polimi.frontend.activity;

import it.polimi.frontend.util.ConnectionHandler;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class WaitActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait);
		showDialog(getString(R.string.waiting));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(ConnectionHandler.isConnected())
			super.onBackPressed();
		else{
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("Reason", "Exit");
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finish();
			//super.onBackPressed();
		}
	}

	private ProgressDialog mProgressDialog;
	protected void showDialog(String message) {
		try{
			setProgressDialog(message);
			if(this!=null && !this.isFinishing())
				mProgressDialog.show();
		}catch(Exception e){}
	}

	protected void hideDialog() {
		try{
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}catch(Exception e){}
	}

	private void setProgressDialog(String message) {
		if(mProgressDialog!=null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getString(R.string.wait));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage(message);
	}


}
