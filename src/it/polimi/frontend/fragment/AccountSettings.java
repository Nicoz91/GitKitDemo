package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.LoginSession;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.activity.TabbedActivity;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.TextValidator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;

public class AccountSettings extends Fragment implements OnClickListener, DatePickerDialog.OnDateSetListener{

	public final static String ID="AccountSettingsFragmentID";

	private Calendar data;

	private EditText nameET,surnameET,accountET[],bDayET;
	private TextView nameTV,surnameTV,accountTV[],bDayTV;
	private RadioButton maleRB;
	private RadioButton femaleRB;
	private boolean male;
	private Menu menu;
	private boolean editMode=false;
	private User user;
	private final static int PW=0,GMAIL=1,FB=2;
	private boolean accountType[]={false,false,false};
	private boolean regMode=false, valid=true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_account_settings,
				container, false);
		setHasOptionsMenu(true);
		user = QueryManager.getInstance().getCurrentUser();
		if(user == null){
			user = new User();
			user.setPwAccount(LoginSession.getUser().getEmail());
			user.setGender(true);
			regMode = true;
		}
		initializeView(rootView);

		return rootView;
	}

	private void initializeView(View rootView){
		accountET = new EditText[3];
		accountTV = new TextView[3];
		//Setup EditText
		nameET = (EditText) rootView.findViewById(R.id.name);
		nameET.setText(user.getName());
		nameET.addTextChangedListener(new TextValidator(nameET){
			@Override
			public void validate(TextView textView, String text) {
				if(text!=null && !text.equals("")){
					valid=true;
				} else {
					valid=false;
					textView.setError("Il nome non può essere vuoto.");
				}
			}
		});
		surnameET = (EditText) rootView.findViewById(R.id.surname);
		surnameET.setText(user.getSurname());
		surnameET.addTextChangedListener(new TextValidator(surnameET){
			@Override
			public void validate(TextView textView, String text) {
				if(text!=null && !text.equals("")){
					valid=true;
				} else {
					valid=false;
					textView.setError("Il cognome non può essere vuoto.");
				}
			}
		});
		//Setup degli account visibili
		if (user.getPwAccount()!=null && !user.getPwAccount().equals(""))
			accountType[PW]=true;
		else {
			((TableRow)rootView.findViewById(R.id.pwRow)).setVisibility(View.GONE);
		}
		if (user.getGmailAccount()!=null && !user.getGmailAccount().equals(""))
			accountType[GMAIL]=true;
		else {
			((TableRow)rootView.findViewById(R.id.gmailRow)).setVisibility(View.GONE);
		}
		if (user.getFbAccount()!=null && !user.getFbAccount().equals(""))
			accountType[FB]=true;
		else {
			((TableRow)rootView.findViewById(R.id.fbRow)).setVisibility(View.GONE);
		}
		accountET[PW] = (EditText) rootView.findViewById(R.id.pwAccount);
		accountET[PW].setText(user.getPwAccount());
		accountET[GMAIL] = (EditText) rootView.findViewById(R.id.gmailAccount);
		accountET[GMAIL].setText(user.getGmailAccount());
		accountET[FB] = (EditText) rootView.findViewById(R.id.fbAccount);
		accountET[FB].setText(user.getFbAccount());
		for (int i=0; i<accountET.length;i++){
			if (accountType[i])
				accountET[i].addTextChangedListener(new TextValidator(accountET[i]){
					@Override
					public void validate(TextView textView, String text) {
						if(text!=null && !text.equals("") && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
							valid=true;
						} else {
							valid=false;
							textView.setError("Mail non valida.");
						}
					}
				});
		}

		//Setup TextView
		nameTV = (TextView) rootView.findViewById(R.id.nameTV);
		nameTV.setText(user.getName());
		surnameTV = (TextView) rootView.findViewById(R.id.surnameTV);
		surnameTV.setText(user.getSurname());
		accountTV[PW] = (TextView) rootView.findViewById(R.id.pwAccountTV);
		accountTV[PW].setText(user.getPwAccount());
		accountTV[GMAIL] = (TextView) rootView.findViewById(R.id.gmailAccountTV);
		accountTV[GMAIL].setText(user.getGmailAccount());
		accountTV[FB] = (TextView) rootView.findViewById(R.id.fbAccountTV);
		accountTV[FB].setText(user.getFbAccount());

		//Setup bDay
		bDayET = (EditText) rootView.findViewById(R.id.bDay);
		bDayTV = (TextView) rootView.findViewById(R.id.bDayTV);
		if (user.getBday()!=null){
			data = Calendar.getInstance();
			data.setTimeInMillis(user.getBday().getValue());
			bDayET.setText(data.get(Calendar.DAY_OF_MONTH)+" "+
					data.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
					+" "+data.get(Calendar.YEAR));
			bDayTV.setText(data.get(Calendar.DAY_OF_MONTH)+" "+
					data.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
					+" "+data.get(Calendar.YEAR));
		}
		bDayET.setOnClickListener(this);
		bDayET.addTextChangedListener(new TextValidator(bDayET){
			@Override
			public void validate(TextView textView, String text) {
				if(data!=null && data.after(Calendar.getInstance())){
					valid=false;
					textView.setError("Non puoi venire dal futuro.");
				} else {
					valid=true;
				}
			}
		});

		//Setup gender
		maleRB = (RadioButton) rootView.findViewById(R.id.radio_male);
		femaleRB = (RadioButton) rootView.findViewById(R.id.radio_female);
		maleRB.setOnClickListener(this);
		femaleRB.setOnClickListener(this);
		if (user.getGender())
			maleRB.performClick();
		else 
			femaleRB.performClick();

		//Setup NonEditable Mode
		if(regMode)
			editMode = true;
		else
			editMode=false;

		editable(editMode);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
		this.menu=menu;
		//
		if(!regMode){
			menu.findItem(R.id.editAccount).setVisible(true);
			menu.findItem(R.id.saveAccount).setVisible(false);
		} else {
			menu.findItem(R.id.editAccount).setVisible(false);
			menu.findItem(R.id.saveAccount).setVisible(true);
		}
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
			if(regMode){
				registerUser();

				QueryManager.getInstance().loadRequest();
				startActivity(new Intent(getActivity(), TabbedActivity.class));
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
				this.getActivity().finish();
				return false;
			}
			if (hasChanged()){
				if (valid){
					menu.findItem(R.id.editAccount).setVisible(true);
					menu.findItem(R.id.saveAccount).setVisible(false);
					editMode=false;
					editable(editMode);
					updateUser();
					initializeView(getView());
				} else 
					Toast.makeText(getActivity().getApplicationContext(), "Qualcosa non va nei campi inseriti.",
							Toast.LENGTH_SHORT).show();
			} else{
				Toast.makeText(getActivity().getApplicationContext(), "Nulla è cambiato.",
						Toast.LENGTH_SHORT).show();
				menu.findItem(R.id.editAccount).setVisible(true);
				menu.findItem(R.id.saveAccount).setVisible(false);
				editMode=false;
				editable(editMode);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void registerUser(){
		updateUser();
		ArrayList<String> devices = new ArrayList<String>();
		devices.add(LoginSession.getDeviceId());
		user.setDevices(devices);
		user  = QueryManager.getInstance().insertUser(user);
		Toast.makeText(getActivity().getApplicationContext(), "Registrazione effettuata.",
				Toast.LENGTH_SHORT).show();

	}

	private void updateUser(){
		user.setName(nameET.getEditableText().toString());
		user.setSurname(surnameET.getEditableText().toString());
		user.setPwAccount(accountET[PW].getEditableText().toString());
		user.setGmailAccount(accountET[GMAIL].getEditableText().toString());
		user.setFbAccount(accountET[FB].getEditableText().toString());
		user.setGender(male);
		user.setBday(new DateTime(data.getTimeInMillis()));
		if(!regMode){
			QueryManager.getInstance().updateUser();
			Toast.makeText(getActivity().getApplicationContext(), "Dati utente aggiornati correttamente.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean hasChanged(){
		boolean changed=false;
		if (nameET.getEditableText().toString().equals(nameTV.getText().toString())
				&& surnameET.getEditableText().toString().equals(surnameTV.getText().toString())
				&& accountET[PW].getEditableText().toString().equals(accountTV[PW].getText().toString())
				&& accountET[GMAIL].getEditableText().toString().equals(accountTV[GMAIL].getText().toString())
				&& accountET[FB].getEditableText().toString().equals(accountTV[FB].getText().toString())
				&& male==user.getGender()
				&& bDayET.getEditableText().toString().equals(bDayTV.getText().toString()))
			changed=false;
		else
			changed=true;
		return changed;
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
		if(!editMode)
			bDayTV.setText(data.get(Calendar.DAY_OF_MONTH)+" "+
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
		if (editable){
			nameTV.setVisibility(View.GONE);
			nameET.setVisibility(View.VISIBLE);
			surnameTV.setVisibility(View.GONE);
			surnameET.setVisibility(View.VISIBLE);
			bDayTV.setVisibility(View.GONE);
			bDayET.setVisibility(View.VISIBLE);
			for(int i=0; i<accountType.length;i++){
				if (accountType[i]){//solo se è tra i tipi di account visibili
					accountTV[i].setVisibility(View.GONE);
					accountET[i].setVisibility(View.VISIBLE);
				}
			}
		} else {
			nameTV.setVisibility(View.VISIBLE);
			nameET.setVisibility(View.GONE);
			surnameTV.setVisibility(View.VISIBLE);
			surnameET.setVisibility(View.GONE);
			bDayTV.setVisibility(View.VISIBLE);
			bDayET.setVisibility(View.GONE);
			for(int i=0; i<accountType.length;i++){
				if (accountType[i]){//solo se è tra i tipi di account visibili
					accountTV[i].setVisibility(View.VISIBLE);
					accountET[i].setVisibility(View.GONE);
				}
			}
		}
		maleRB.setClickable(editable);
		femaleRB.setClickable(editable);
	}

}
