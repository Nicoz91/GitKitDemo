package it.polimi.frontend.fragment;

import java.util.Calendar;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.DateSlider.labeler.TimeLabeler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class InsertRequest extends Fragment{

	static final int DEFAULTDATESELECTOR_ID = 0;
	public final static String ID="InsertRequestFragmentID";
	//private GoogleMap map;
	private EditText title;
	private EditText description;
	private EditText tag;
	private EditText start;
	private EditText end;
	private EditText max;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_insert_request,
				container, false);
		title = (EditText) rootView.findViewById(R.id.insert_title_edit); 
		description = (EditText)rootView.findViewById(R.id.insert_description_edit);
		tag = (EditText) rootView.findViewById(R.id.insert_tag_edit);
		start = (EditText) rootView.findViewById(R.id.insert_start_edit);
		start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getActivity().showDialog(0);
			}
		});
		end = (EditText) rootView.findViewById(R.id.insert_end_edit);
		end.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getActivity().showDialog(1);
			}
		});
		max = (EditText) rootView.findViewById(R.id.insert_max_edit);
		return rootView;
	}

	public void onStartDateSelected(Calendar selectedDate){
		int minute = selectedDate.get(Calendar.MINUTE) /
				TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
		start.setText(String.format("%n%te. %tB %tY%n%tH:%02d",
				selectedDate, selectedDate, selectedDate, selectedDate, minute));
	}
	
	public void onEndDateSelected(Calendar selectedDate){
		int minute = selectedDate.get(Calendar.MINUTE) /
				TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
		end.setText(String.format("%n%te. %tB %tY%n%tH:%02d",
				selectedDate, selectedDate, selectedDate, selectedDate, minute));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.insert, menu);
		menu.findItem(R.id.insert_next).setVisible(true);
		menu.findItem(R.id.insert_cancel).setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.insert_next:
//			System.out.println("Vado avanti");
			return true;
		case R.id.insert_cancel:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void assignAttribute(Request req){
		req.setTitle(title.getText().toString());
		req.setDescription(description.getText().toString());
		req.setType(tag.getText().toString());
		req.setMaxPartecipants(Integer.getInteger(max.getText().toString()));	
	}
}
