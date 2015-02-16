package it.polimi.frontend.activity;

import java.util.Calendar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.fragment.GetPositionMap;
import it.polimi.frontend.fragment.InsertRequest;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnActionListener;
import it.polimi.frontend.util.DateSlider.DateSlider;
import it.polimi.frontend.util.DateSlider.DateTimeSlider;
public class RequestActivity extends ActionBarActivity implements OnActionListener {

	private InsertRequest insert;
	private GetPositionMap map;
	private Menu menu;
	private Request req;
	private Calendar start; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QueryManager.getInstance().addActionListener(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		insert = new InsertRequest();
		map = new GetPositionMap();
		req = new Request();
		fragmentTransaction.add(R.id.insRequestContainer, insert);
		//		fragmentTransaction.add(R.id.insRequestContainer, fragment2);
		fragmentTransaction.commit();
	}

	public void getDate(EditText t){

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		switch(id){
		case 0:	return new DateTimeSlider(this,startTime,c);
		default :
			if(start==null)
				return new DateTimeSlider(this,endTime,c);
			else
				return new DateTimeSlider(this,endTime,start);

		}

	}


	private DateSlider.OnDateSetListener startTime =
			new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			insert.onStartDateSelected(selectedDate);
			insert.onEndDateSelected(selectedDate);
			start = selectedDate;
		}
	};

	private DateSlider.OnDateSetListener endTime =
			new DateSlider.OnDateSetListener() {
		public void onDateSet(DateSlider view, Calendar selectedDate) {
			insert.onEndDateSelected(selectedDate);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.insert, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		int id = item.getItemId();
		switch (id) {
		case R.id.insert_cancel:
			this.finish();
			return true;
		case R.id.insert_next:
			if(insert.assignAttribute(req)){
				menu.findItem(R.id.insert_next).setVisible(false);
				menu.findItem(R.id.insert_cancel).setVisible(false);
				menu.findItem(R.id.insert_back).setVisible(true);
				menu.findItem(R.id.insert_done).setVisible(true);
				fragmentTransaction.replace(R.id.insRequestContainer, map);
				fragmentTransaction.commit();
			}
			return true;
		case R.id.insert_back:
			menu.findItem(R.id.insert_next).setVisible(true);
			menu.findItem(R.id.insert_cancel).setVisible(true);
			menu.findItem(R.id.insert_back).setVisible(false);
			menu.findItem(R.id.insert_done).setVisible(false);
			fragmentTransaction.replace(R.id.insRequestContainer, insert);
			fragmentTransaction.commit();
			return true;
		case R.id.insert_done:

			if(map.setPosition(req)){
				QueryManager.getInstance().insertRequest(req);
			}
			else 
				Toast.makeText(MyApplication.getContext(),"Inserisci una posizione sulla mappa",Toast.LENGTH_SHORT).show();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void assignAttribute(){

	}

	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	private ProgressDialog mProgressDialog;
	protected void showDialog(String message) {
		
		setProgressDialog(message);
		if(this!=null && !this.isFinishing())
			mProgressDialog.show();
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog(String message) {
		if(mProgressDialog!=null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Attendi...");
		mProgressDialog.setMessage(message);
	}
	
	@Override
	public void onPerformingAction(int action) {
		if(action == OnActionListener.INSERT_REQUEST){
			showDialog("Stiamo completando la tua richiesta...");
		}
		
	}

	@Override
	public void onActionPerformed(Object result, int action) {
		hideDialog();
		if(action == OnActionListener.INSERT_REQUEST){
			Request r = (Request)result;
			if(r==null)
				Toast.makeText(MyApplication.getContext(),"Si è verificato un errore durante l'operazione...",Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(MyApplication.getContext(),"Richiesta inserita correttamente.",Toast.LENGTH_SHORT).show();
			this.finish();
		}
		
		
	}

}
