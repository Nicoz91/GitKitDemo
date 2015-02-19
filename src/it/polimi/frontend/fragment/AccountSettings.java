package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.LoginSession;
import it.polimi.frontend.activity.MainActivity;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.activity.R;
import it.polimi.frontend.util.QueryManager;
import it.polimi.frontend.util.QueryManager.OnActionListener;
import it.polimi.frontend.util.Storage;
import it.polimi.frontend.util.Storage.OnImageLoadedListener;
import it.polimi.frontend.util.TextValidator;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.identitytoolkit.IdProvider;
import com.squareup.picasso.Picasso;

public class AccountSettings extends Fragment implements OnClickListener, DatePickerDialog.OnDateSetListener,OnActionListener, OnImageLoadedListener{

	public final static String ID="AccountSettingsFragmentID";
	private Calendar data;
	private EditText nameET,surnameET,accountET[],bDayET;
	private TextView nameTV,surnameTV,accountTV[],bDayTV;
	private RadioButton maleRB;
	private RadioButton femaleRB;
	private ImageView profileIV;
	private boolean male;
	private Menu menu;
	private boolean editMode=false;
	private User user;
	private final static int PW=0,GMAIL=1,FB=2;
	private boolean accountType[]={false,false,false};
	private boolean regMode=false, valid=true, linkingAllowed=false;
	private String photoURL="";
	private Uri currImageURI;
	private String EMPTY_VALIDATOR;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		QueryManager.getInstance().addActionListener(this);
		Storage.getInstance().setListener(this);
		EMPTY_VALIDATOR=getString(R.string.emptyValidator);
		View rootView;
		rootView = inflater.inflate(R.layout.fragment_account_settings,
				container, false);
		setHasOptionsMenu(true);
		user = QueryManager.getInstance().getCurrentUser();
		if(user == null){
			user = new User();
			try{
				String s = LoginSession.getUser().getDisplayName();
				String name = s.substring(0, s.lastIndexOf(' ')) ;
				String surname =  s.substring(s.lastIndexOf(' '),s.length()) ;
				user.setName(name);
				user.setSurname(surname);
				user.setPhotoURL(LoginSession.getUser().getPhotoUrl());
			}catch(Exception e){
				e.printStackTrace();
			}
			if(LoginSession.getProvider()==null)
				user.setPwAccount(LoginSession.getUser().getEmail());
			else if (LoginSession.getProvider().equalsIgnoreCase(IdProvider.GOOGLE.name()))
				user.setGmailAccount(LoginSession.getUser().getEmail());
			else if(LoginSession.getProvider().equalsIgnoreCase(IdProvider.FACEBOOK.name()))
				user.setFbAccount(LoginSession.getUser().getEmail());
			else user.setPwAccount(LoginSession.getUser().getEmail());
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
					textView.setError(EMPTY_VALIDATOR);
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
					textView.setError(EMPTY_VALIDATOR);
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
							textView.setError(getString(R.string.mailValidator));
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
		else{
			data = Calendar.getInstance();
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
					textView.setError(getString(R.string.bDayValidator));
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

		//Setup Image
		profileIV = (ImageView) rootView.findViewById(R.id.account_picture);
		profileIV.setOnClickListener(this);
		profileIV.setOnTouchListener(new OnTouchListener() {
			//Se in editMode, si comporterà al click come un button
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editMode){
					ImageView view;
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						view = (ImageView) v;
						try{
							//overlay is black with transparency of 0x77 (119)
							view.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);
							view.invalidate();
						}catch(Exception e){e.printStackTrace();}
						//TODO inserendo qua il dialog funziona ma non sono sicuro sia modo corretto
						//showGallery();
						//showProfileURLDialog(v);
						break;
					case MotionEvent.ACTION_UP:
						//Inserendo il performClick non dovrebbe essere necessario chiamare i metodi qua dentro.
						v.performClick();
						break;
					case MotionEvent.ACTION_CANCEL: 
						view = (ImageView) v;
						v.performClick();
						try{
							//clear the overlay
							view.getDrawable().clearColorFilter();
							view.invalidate();
						}catch(Exception e){e.printStackTrace();}
						break;
					}
					return true;
				} else return true;
			}
		});
		if (user.getPhotoURL()!=null){
			photoURL = user.getPhotoURL();
			try{
				Picasso.with(getActivity()).load(photoURL).into(profileIV);
			}catch(Exception e){}
		}
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
			menu.findItem(R.id.cancel).setVisible(true);
			editMode=true;
			editable(editMode);
			return true;
		case R.id.saveAccount:
			if(regMode){
				registerUser();
				return false;
			}
			if (hasChanged()){
				if (valid){
					updateUser();
					//					menu.findItem(R.id.editAccount).setVisible(true);
					//					menu.findItem(R.id.saveAccount).setVisible(false);
					//					editMode=false;
					//					editable(editMode);
					//					initializeView(getView());
				} else 
					Toast.makeText(MyApplication.getContext(), getString(R.string.generalValidator),
							Toast.LENGTH_SHORT).show();
			} else{
				Toast.makeText(MyApplication.getContext(), getString(R.string.noChangeValidator),
						Toast.LENGTH_SHORT).show();
				menu.findItem(R.id.editAccount).setVisible(true);
				menu.findItem(R.id.saveAccount).setVisible(false);
				menu.findItem(R.id.cancel).setVisible(false);
				editMode=false;
				editable(editMode);
			}
			return true;
		case R.id.cancel:
			Toast.makeText(MyApplication.getContext(), getString(R.string.noChangeValidator),
					Toast.LENGTH_SHORT).show();
			menu.findItem(R.id.editAccount).setVisible(true);
			menu.findItem(R.id.saveAccount).setVisible(false);
			menu.findItem(R.id.cancel).setVisible(false);
			editMode=false;
			editable(editMode);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void registerUser(){
		updateUser();
		if(LoginSession.getProvider()!=null)
			System.out.println(LoginSession.getUser().getIdProvider());
		QueryManager.getInstance().insertUser(user);
	}

	private void updateUser(){
		user.setName(nameET.getEditableText().toString());
		user.setSurname(surnameET.getEditableText().toString());
		user.setPwAccount(accountET[PW].getEditableText().toString());
		user.setGmailAccount(accountET[GMAIL].getEditableText().toString());
		user.setFbAccount(accountET[FB].getEditableText().toString());
		user.setGender(male);
		user.setBday(new DateTime(data.getTimeInMillis()));
		user.setPhotoURL(photoURL);
		if(!regMode){
			QueryManager.getInstance().updateUser();
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
				&& bDayET.getEditableText().toString().equals(bDayTV.getText().toString())
				&& ( (!photoURL.equals("") && photoURL.equals(user.getPhotoURL()))
						|| (photoURL.equals("") && user.getPhotoURL()==null)) )
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
		case R.id.account_picture:
			showGallery();
			//showProfileURLDialog(v);
			break;
		default:
			break;
		}
	}

	private void showGallery(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.choosePicture)), 1 );
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 

		if (resultCode == getActivity().RESULT_OK) {

			if (requestCode == 1) {

				// currImageURI is the global variable I'm using to hold the content:// URI of the image
				currImageURI = data.getData();
				String realUri = this.getRealPathFromURI(currImageURI);
				Storage.getInstance().uploadFile(realUri,"I"+LoginSession.getUser().getEmail()+"Image.png");
			}
		}
	}

	// And to convert the image URI to the direct file system path of the image file
	private String getRealPathFromURI(Uri contentUri) {

		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = getActivity().managedQuery( contentUri,
				proj, // Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	//Non dovrebbe servire più, teoricamente
	public void showProfileURLDialog(View v){
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Foto Profilo");
		alert.setMessage("Inserisci l'URL di un'immagine che vorresti come immagine del profilo.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String photoUrl = input.getEditableText().toString();
				if (photoUrl!=null && !photoUrl.equals("")
						&& android.util.Patterns.WEB_URL.matcher(photoUrl).matches()){
					photoURL = photoUrl;
					Picasso.with(getActivity()).load(photoURL).into(profileIV);
				} else
					Toast.makeText(MyApplication.getContext(), "Immagine non aggiornata perchè non hai inserito un URL corretto.",
							Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				Toast.makeText(MyApplication.getContext(), "Aggiornamento immagine cancellato.",
						Toast.LENGTH_SHORT).show();
			}
		});

		alert.show();
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
				if (accountType[i] && linkingAllowed){//solo se è tra i tipi di account visibili
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
				if (accountType[i] && linkingAllowed){//solo se è tra i tipi di account visibili
					accountTV[i].setVisibility(View.VISIBLE);
					accountET[i].setVisibility(View.GONE);
				}
			}
		}
		maleRB.setClickable(editable);
		femaleRB.setClickable(editable);
		profileIV.setClickable(editable);
	}

	@Override
	public void onImageLoading() {
		String message = getString(R.string.performingAction);
		showDialog(message,true);

	}

	@Override
	public void onImageLoaded(String path) {
		hideDialog();
		if(path==null)
			Toast.makeText(MyApplication.getContext(), getString(R.string.imageError),Toast.LENGTH_SHORT).show();
		else{
			photoURL = path;
			Picasso.with(getActivity()).load(photoURL).into(profileIV);
		}
	}

	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	private ProgressDialog mProgressDialog;
	protected void showDialog(String message,boolean progress) {
		if(getActivity()!=null && !getActivity().isFinishing()){
			setProgressDialog(message,progress);
			mProgressDialog.show();
		}
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog(String message, boolean progress) {
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(getString(R.string.wait));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage(message);
		if(progress){
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
	}      

	@Override
	public void onImageProgress(long bytes, long total) {
		int percent = (int)(100.0*(double)bytes/total + 0.5);
		mProgressDialog.setProgress(percent);

	}



	@Override
	public void onPerformingAction(int action) {
		if(action == OnActionListener.INSERT_USER || action == OnActionListener.UPDATE_USER){
			showDialog(getString(R.string.performingAction),false);
		}

	}

	@Override
	public void onActionPerformed(Object result, int action) {
		if(action == OnActionListener.INSERT_USER || action == OnActionListener.UPDATE_USER){
			hideDialog();
			if(action == OnActionListener.INSERT_USER){
				User u = (User)result;
				if(u==null){
					Toast.makeText(MyApplication.getContext(), getString(R.string.registrationError),Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(MyApplication.getContext(), getString(R.string.registrationCompleted),
						Toast.LENGTH_SHORT).show();
				QueryManager.getInstance().registerDevice();
				QueryManager.getInstance().loadRequest();
				//Fai partire la main activity
				Intent i = new Intent(getActivity(), MainActivity.class);
				i.putExtra("Reason", "Network");
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);				
				//
				//startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
				this.getActivity().finish();
			}

			if(action == OnActionListener.UPDATE_USER){
				User u = (User)result;
				if(u==null){
					Toast.makeText(MyApplication.getContext(), getString(R.string.serverError),Toast.LENGTH_SHORT).show();
					return;
				}
				menu.findItem(R.id.editAccount).setVisible(true);
				menu.findItem(R.id.saveAccount).setVisible(false);
				menu.findItem(R.id.cancel).setVisible(false);
				editMode=false;
				editable(editMode);
				Toast.makeText(MyApplication.getContext(), getString(R.string.userUpdateCompleted),
						Toast.LENGTH_SHORT).show();
				if(getActivity()!=null && !getActivity().isFinishing()){
				initializeView(getView());}
				//MODIFICA QUI
			}


		}

	}
}
