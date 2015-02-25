package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.TextValidator;
import it.polimi.frontend.util.DateSlider.labeler.TimeLabeler;

import java.util.Calendar;
import java.util.TimeZone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;


public class InsertRequest extends Fragment{

	static final int DEFAULTDATESELECTOR_ID = 0;
	public final static String ID="InsertRequestFragmentID";
	//private GoogleMap map;
	private EditText title;
	private EditText description;
	//private EditText tag;
	private Spinner tag;
	private EditText start;
	private EditText end;
	private EditText max;
	private Calendar startDate;
	private Calendar endDate;
	private boolean valid;
	private String EMPTY_VALIDATOR;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		EMPTY_VALIDATOR=getString(R.string.emptyValidator);
		View rootView = inflater.inflate(R.layout.fragment_insert_request,
				container, false);
		title = (EditText) rootView.findViewById(R.id.insert_title_edit); 
		title.addTextChangedListener(new TextValidator(title){
			@Override
			public void validate(TextView textView, String text) {
				if(text!=null && !text.equals("")){
					valid=true;
				} else {
					valid=false;
					textView.setError(EMPTY_VALIDATOR);
				}
			}
		});

		description = (EditText)rootView.findViewById(R.id.insert_description_edit);
		description.addTextChangedListener(new TextValidator(description){
			@Override
			public void validate(TextView textView, String text) {
				if(text!=null && !text.equals("")){
					valid=true;
				} else {
					valid=false;
					textView.setError(EMPTY_VALIDATOR);
				}
			}
		});/*Vecchio codice con EditText
		tag = (EditText) rootView.findViewById(R.id.insert_tag_edit);
		tag.addTextChangedListener(new TextValidator(tag){
			@Override
			public void validate(TextView textView, String text) {
				if(text!=null && !text.equals("")){
					valid=true;
				} else {
					valid=false;
					textView.setError("Inserisci almeno un tag per cercare la richiesta!");
				}
			}
		});*/
		tag = (Spinner) rootView.findViewById(R.id.insert_tag_spin);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.tag_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		tag.setAdapter(adapter);

		Calendar now = Calendar.getInstance();
		start = (EditText) rootView.findViewById(R.id.insert_start_edit);
		start.setText(String.format("%n%te. %tB %tY%n%tH:%02d",
				now, now, now, now, now.get(Calendar.MINUTE) /
				TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL));
		startDate = now;
		start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				getActivity().showDialog(0);
			}
		});
		end = (EditText) rootView.findViewById(R.id.insert_end_edit);
		end.setText(String.format("%n%te. %tB %tY%n%tH:%02d",
				now, now, now, now, now.get(Calendar.MINUTE) /
				TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL));
		endDate = now;
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
		startDate = selectedDate;
		//onEndDateSelected(startDate);TODO perchè aggiorna la var., l'edittext, ma se clicco
		//sull'edit text nel dialog la data è ancora la precedente?
		int minute = selectedDate.get(Calendar.MINUTE) /
				TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
		start.setText(String.format("%n%te. %tB %tY%n%tH:%02d",
				selectedDate, selectedDate, selectedDate, selectedDate, minute));

	}

	public void onEndDateSelected(Calendar selectedDate){
		endDate = selectedDate;
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

	public boolean assignAttribute(Request req){

		//if(title.getText().equals("") || description.getText().equals("") || tag.getText().equals(""))
		if(title.getText().equals("") || description.getText().equals("") || tag.getSelectedItem().toString().equals(""))
			return false;
		if(startDate.getTimeInMillis()>endDate.getTimeInMillis()){
			Toast.makeText(MyApplication.getContext(),getString(R.string.startEndValidator),Toast.LENGTH_SHORT).show();
			return false;
		}
		req.setTitle(title.getText().toString());
		req.setDescription(description.getText().toString());
		//req.setType(tag.getText().toString());
		req.setType(tag.getSelectedItem().toString());
		req.setStart(new DateTime(startDate.getTimeInMillis()));
		req.setEnd(new DateTime(endDate.getTimeInMillis()));
		if (max.getText().toString()==null || max.getText().toString().equals(""))
			req.setMaxPartecipants(0);
		else
			req.setMaxPartecipants(Integer.parseInt(max.getText().toString()));	

		return valid;
	}
}
