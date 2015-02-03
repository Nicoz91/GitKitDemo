package it.polimi.frontend.fragment;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.MyApplication;
import it.polimi.frontend.util.ParentFragmentUtil;
import it.polimi.frontend.util.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UserList extends ListFragment {

	private List<User> users;
	private OnUserClickedListener mListener;
	
	public UserList(List<User> users){
		this.users=users;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(users!=null && users.size()>0 ){
			setUserAdapter(users);
		}
		else{
//			System.out.println("Le richieste sono nulle? Le sto ancora caricando?");
			setUserAdapter(new ArrayList<User>());
			//RequestLoader.getInstance().loadRequest();
		}
		mListener = ParentFragmentUtil.getParent(this, OnUserClickedListener.class);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onUserClicked(position, (User) getListAdapter().getItem(position));
	}
	
	public void setUserAdapter(List<User> users){
		List<User> user;
		if(users == null)
			user = new ArrayList<User>();
		else
			user= users;
		Context c = getActivity();
		if(c==null){ 
//			System.out.println("Il context è null ma noi bariamo");
			c = MyApplication.getContext();
		}
		else {
//			System.out.println("Tutto ok inizializzo l'adapter");
			}
		UserAdapter adapter = new UserAdapter(c,0,user);
		setListAdapter(adapter);
	}
	
	public interface OnUserClickedListener{
		public void onUserClicked(int position, User user);
	}
	
}
