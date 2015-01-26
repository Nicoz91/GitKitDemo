package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class InsertRequest extends Fragment{

	public final static String ID="InsertRequestFragmentID";
	//private GoogleMap map;
	private EditText title;
	private EditText description;
	private EditText tag;
	private EditText start;
	private EditText end;
	private EditText max;
	private View root;
	private Menu menu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_insert_request,
				container, false);
		this.root = rootView;

		title = (EditText) rootView.findViewById(R.id.insert_title_edit); 
		description = (EditText)rootView.findViewById(R.id.insert_description_edit);
		tag = (EditText) rootView.findViewById(R.id.insert_tag_edit);
		start = (EditText) rootView.findViewById(R.id.insert_start_edit);
		end = (EditText) rootView.findViewById(R.id.insert_end_edit);
		max = (EditText) rootView.findViewById(R.id.insert_max_edit);
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.insert, menu);
		this.menu=menu;
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
			System.out.println("Vado avanti");
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
