package it.polimi.frontend.activity;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.fragment.GetPositionMap;
import it.polimi.frontend.fragment.InsertRequest;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.DateSlider.DateSlider;
import it.polimi.frontend.util.DateSlider.DateTimeSlider;
import it.polimi.frontend.util.DateSlider.labeler.TimeLabeler;
public class RequestActivity extends ActionBarActivity {
	
	private InsertRequest insert;
	private GetPositionMap map;
	private Menu menu;
	private Request req;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		showDialog(0);
	}
	

    @Override
    protected Dialog onCreateDialog(int id) {
        final Calendar c = Calendar.getInstance();
    	return new DateTimeSlider(this,mDateTimeSetListener,c);
    }
    

    private DateSlider.OnDateSetListener mDateTimeSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
                // update the dateText view with the corresponding date
                int minute = selectedDate.get(Calendar.MINUTE) /
                        TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
//                dateText.setText(String.format("The chosen date and time:%n%te. %tB %tY%n%tH:%02d",
//                        selectedDate, selectedDate, selectedDate, selectedDate, minute));
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
			menu.findItem(R.id.insert_next).setVisible(false);
			menu.findItem(R.id.insert_cancel).setVisible(false);
			menu.findItem(R.id.insert_back).setVisible(true);
			menu.findItem(R.id.insert_done).setVisible(true);
			insert.assignAttribute(req);
			fragmentTransaction.replace(R.id.insRequestContainer, map);
			fragmentTransaction.commit();
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
			map.setPosition(req);
			QueryManager.getInstance().insertRequest(req);
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void assignAttribute(){
		
	}

}
