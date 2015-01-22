package it.polimi.frontend.activity;

import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class SettingsActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener{

	private Calendar data;
	
	private EditText nameET;
	private EditText surnameET;
	private EditText pwAccountET;
	private EditText gmailAccountET;
	private EditText fbAccountET;
	private EditText bDayET;
	private boolean male;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		nameET = (EditText)findViewById(R.id.name);
		surnameET = (EditText)findViewById(R.id.surname);
		pwAccountET = (EditText)findViewById(R.id.pwAccount);
		gmailAccountET = (EditText)findViewById(R.id.gmailAccount);
		fbAccountET = (EditText)findViewById(R.id.fbAccount);
		bDayET = (EditText)findViewById(R.id.bDay);
	}

	public void onRadioButtonClicked(View view){
		switch (view.getId()) {
		case R.id.radio_male:
			male = true;
			break;
		case R.id.radio_female:
			male = false;
			break;
		default:
			break;
		}
	}

	public void showDatePickerDialog(View v){

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);


		if (data!=null){
			year = data.get(Calendar.YEAR);
			month = data.get(Calendar.MONTH);
			day = data.get(Calendar.DAY_OF_MONTH);
		}

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dpd= new DatePickerDialog(this, this, year, month, day);
		dpd.show();
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		data = Calendar.getInstance();
		data.set(year, monthOfYear, dayOfMonth);
		bDayET.setText(data.get(Calendar.DAY_OF_MONTH)+" "+
				data.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
				+" "+data.get(Calendar.YEAR));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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

}
