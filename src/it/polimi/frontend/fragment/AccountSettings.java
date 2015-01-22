package it.polimi.frontend.fragment;

import it.polimi.frontend.activity.R;

import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

public class AccountSettings extends Fragment implements OnClickListener, DatePickerDialog.OnDateSetListener{

	public final static String ID="AccountSettingsFragmentID";

	private Calendar data;

	private EditText nameET;
	private EditText surnameET;
	private EditText pwAccountET;
	private EditText gmailAccountET;
	private EditText fbAccountET;
	private EditText bDayET;
	private RadioButton maleRB;
	private RadioButton femaleRB;
	private boolean male;
	private Menu menu;
	private boolean editMode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_account_settings,
				container, false);
		setHasOptionsMenu(true);
		nameET = (EditText) rootView.findViewById(R.id.name);
		surnameET = (EditText) rootView.findViewById(R.id.surname);
		pwAccountET = (EditText) rootView.findViewById(R.id.pwAccount);
		gmailAccountET = (EditText) rootView.findViewById(R.id.gmailAccount);
		fbAccountET = (EditText) rootView.findViewById(R.id.fbAccount);
		bDayET = (EditText) rootView.findViewById(R.id.bDay);
		maleRB = (RadioButton) rootView.findViewById(R.id.radio_male);
		femaleRB = (RadioButton) rootView.findViewById(R.id.radio_female);
		editMode=false;
		editable(editMode);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
		this.menu=menu;
		menu.findItem(R.id.editAccount).setVisible(true);
		menu.findItem(R.id.saveAccount).setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.editAccount:
			menu.findItem(R.id.editAccount).setVisible(false);
			menu.findItem(R.id.saveAccount).setVisible(true);
			editMode=true;
			editable(editMode);
			return true;
		case R.id.saveAccount:
			menu.findItem(R.id.editAccount).setVisible(true);
			menu.findItem(R.id.saveAccount).setVisible(false);
			editMode=false;
			editable(editMode);
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
		DatePickerDialog dpd= new DatePickerDialog(getActivity(), this, year, month, day);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.radio_male:
			male = true;
			break;
		case R.id.radio_female:
			male = false;
			break;
		case R.id.bDay:
			showDatePickerDialog(v);
			break;
		default:
			break;
		}
	}

	private void editable(boolean editable){
		nameET.setFocusable(editable);
		nameET.setFocusableInTouchMode(editable);
		nameET.setClickable(editable);
		surnameET.setFocusable(editable);
		surnameET.setFocusableInTouchMode(editable);
		surnameET.setClickable(editable);
		pwAccountET.setFocusable(editable);
		pwAccountET.setFocusableInTouchMode(editable);
		pwAccountET.setClickable(editable);
		gmailAccountET.setFocusable(editable);
		gmailAccountET.setFocusableInTouchMode(editable);
		gmailAccountET.setClickable(editable);
		fbAccountET.setFocusable(editable);
		fbAccountET.setFocusableInTouchMode(editable);
		fbAccountET.setClickable(editable);
		bDayET.setFocusable(editable);
		bDayET.setFocusableInTouchMode(!editable);
		bDayET.setClickable(editable);
		maleRB.setClickable(editable);
		femaleRB.setClickable(editable);
	}

}
