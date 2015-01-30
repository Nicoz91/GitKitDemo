package it.polimi.frontend.util;
import it.polimi.appengine.entity.manager.Manager;
import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.LoginSession;
import it.polimi.frontend.activity.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.identitytoolkit.demo.messageEndpoint.MessageEndpoint;

public class QueryManager {

	private List<User> users;
	private List<Feedback> feedbacks;
	private List<Request> requests;
	private List<OnRequestLoadedListener> listeners;
	private Manager manager;
	private MessageEndpoint message;
	private static QueryManager instance;
	private ProgressDialog mProgressDialog;
	private User user;
	/** 
	 * Metodi per mostrare o meno il progressDialog
	 * */
	protected void showDialog() {
		if (mProgressDialog == null) {
			setProgressDialog();
		}
		mProgressDialog.show();
	}

	protected void hideDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void setProgressDialog() {
		mProgressDialog = new ProgressDialog(MyApplication.getContext());
		mProgressDialog.setTitle("Attendi...");
		mProgressDialog.setMessage("Sto scaricando...");
	}



	private QueryManager(){
		this.users = new ArrayList<User>();
		this.requests = new ArrayList<Request>();
		this.feedbacks = new ArrayList<Feedback>();
		this.listeners = new ArrayList<OnRequestLoadedListener>();
		Manager.Builder managerBuilder = new Manager.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
		managerBuilder = CloudEndpointUtils.updateBuilder(managerBuilder);
		this.manager = managerBuilder.build();
		MessageEndpoint.Builder messageBuilder = new MessageEndpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
		messageBuilder = CloudEndpointUtils.updateBuilder(messageBuilder);
		this.message = messageBuilder.build();
	}

	public static QueryManager getInstance(){
		if (instance==null){
			instance = new QueryManager();
		}
		return instance;
	}

	public void loadData(){
		new LoadDataTask().execute();
	}

	public void loadRequest(){
		System.out.println("Mi connetto per scaricare le richieste...");
		new LoadDataTask().execute();
	}

	public List<Request> getRequests(){
		return requests;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Feedback> getFeedback() {
		return feedbacks;
	}

	public User getUserByEmail(String email){
		User u = null;
		try {
			u = new QueryUser(email).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}

	public User updateUserDevices(User user){
		User u = null;

		try {
			u= new UpdateUserDevice(user).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;

	}

	public User insertUser(User user){
		User u = null;

		try {
			u= new InsertUser(user).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;

	}
	
	public void updateUser(){
		try {
			new UpdateUser().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Request insertRequest(Request request){
		Request r = null;
		try {
			r = new InsertRequest(request).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requests.add(r);
		System.out.println("Aggiorno la view");
		System.out.println("Size: "+listeners.size());
		for (OnRequestLoadedListener l : listeners){
			System.out.println("Classe: "+l.getClass().toString());
			l.onRequestLoaded(requests);
		}
		return r;
	}

	public ArrayList<Request> getUserPartecipation(){

		ArrayList<Request> partecipation = new ArrayList<Request>();
		for(Request r : requests){
			if(r.getPartecipants()!=null)
				if(r.getPartecipants().contains(user.getId()))
					partecipation.add(r);
		}
		return partecipation;
	}

	public User getCurrentUser(){
		return user;
	}

	public ArrayList<User> getUserFromRequest(Request r){
		ArrayList<User> ret = new ArrayList<User>();
		for(User u : this.users){
			if(r.getPartecipants()!=null)
				if(r.getPartecipants().contains(u.getId()))
					ret.add(u);		
		}
		return ret;
	}

	public void joinRequest(Request r){
		if(r==null){System.out.println("R è null... strano");}
		else{System.out.println("R id:"+r.getId());}
		try {
			new JoinRequest(r).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeJoinRequest(Request r){

		if(r==null){System.out.println("R è null... strano");}
		else{System.out.println("R id:"+r.getId());}
		try {
			new RemoveJoinRequest(r).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeOwnerRequest(Request r){

		if(r==null){System.out.println("R è null... strano");}
		else{System.out.println("R id:"+r.getId());}
		try {
			new RemoveOwnerRequest(r).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void insertFeedback(Feedback f){
		System.out.println("Provo ad inserire un feed");

		try {
			new InsertFeedback(f).execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addListener(OnRequestLoadedListener listener){
		listeners.add(listener);
		System.out.println("Ho aggiunto un listener: "+listener.getClass().toString());
	}

	public interface OnRequestLoadedListener{
		public void onRequestLoaded(List<Request> requests);
	}

	public static void destroy (){
		instance=null;
	}

	//Classi che effettuano le query
	private class LoadDataTask extends AsyncTask<Void, Void,  ArrayList<Request>> {

		@Override
		protected ArrayList<Request> doInBackground(Void... params) {
			System.out.println("Sto per ricostruire le request");
			try {
				users = (ArrayList<User>) manager.listUser().execute().getItems();
				try{
					for(User u : users){
						System.out.println("Id degli utenti: "+u.getId());
						if(u.getPwAccount().equals(LoginSession.getUser().getEmail())) 
							user = u;

						ArrayList<Request> ownerReq = (ArrayList<Request>) u.getRequests();
						ArrayList<Feedback> sentFeed = (ArrayList<Feedback>) u.getSentFb();
						if(sentFeed!=null && sentFeed.size()>0){
							System.out.println("SIZE DEI FEED INVIATI: "+sentFeed.size());
							for(Feedback f: sentFeed){
								f.setFrom(u);
								for(User u1 : users){
									if(f.getToId().equals(u1.getId())){
										f.setTo(u1);
										if(u1.getReceivedFb()==null)
											u1.setReceivedFb(new ArrayList<Feedback>());
										System.out.println("Assegno all'utente "+u1.getName()+" il feed"+f.getDescription());
										u1.getReceivedFb().add(f);
									}
								}

							}
						}
						if(ownerReq!=null && ownerReq.size()>0){
							for(Request r : ownerReq){
								//								Request req = manager.getRequest(r.getId()).execute();
								//								if(req==null) System.out.println("Non ho trovato nulla");
								//								else System.out.println("Title: "+req.getTitle());
								r.setOwner(u);
								if(r.getPartecipants()==null)
									r.setPartecipants(new ArrayList<Long>());}
							if(u.getPwAccount().equals(LoginSession.getUser().getEmail())) 
								continue;
							else
								requests.addAll(ownerReq);
						}

					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Like no tomorrow");}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Requests null");
			}
			return (ArrayList<Request>)requests;
		}

		@Override
		protected void onPostExecute(ArrayList<Request> result) {
			System.out.println("NOTIFICO A TUTTI!("+listeners.size()+")");
			if(result!=null)
				requests = result;
			for (OnRequestLoadedListener l : listeners){
				l.onRequestLoaded(requests);
			}
		}
	}
	private class QueryUser extends AsyncTask<Void, Void, User> {

		private String email;

		public QueryUser(String email){
			super();
			this.email = email;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Inizio la query");
			User result = null;
			try {
				result = manager.getUserByEmail(email).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		@Override
		@SuppressWarnings("null")
		protected void onPostExecute(User result) {
			if(result!=null)
				System.out.println("Ho ricevuto l'utente: "+result.getName());
			else
				System.out.println("Nessun utente ricevuto, devo crearlo.");
			//hideDialog();
			return;
		}

	}
	private class UpdateUserDevice extends AsyncTask<Void, Void, User> {

		private User u;

		public UpdateUserDevice(User u){
			super();
			this.u = u;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//	showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Modifico la lista dei device");
			User result = null;
			if(u.getDevices()==null)
				u.setDevices(new ArrayList<String>());
			u.getDevices().add(LoginSession.getDeviceId());
			try {
				result = manager.updateUser(u).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

	}
	private class InsertUser extends AsyncTask<Void, Void, User> {

		private User u;

		public InsertUser(User u){
			super();
			this.u = u;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			System.out.println("Creo un nuovo utente");
			User result = null;
			try {
				result = manager.insertUser(u).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = null;
			}
			return result;
		}

	}
	private class InsertRequest extends AsyncTask<Void, Void, Request> {
		public Request r;

		public InsertRequest(Request r){
			this.r = r;
		}

		@Override
		protected Request doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u.getRequests()==null)
				u.setRequests(new ArrayList<Request>());
			u.getRequests().add(r);

			try {
				manager.updateUser(u).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return r.setOwner(u);
		}



	}
	private class JoinRequest extends AsyncTask<Void, Void, User> {
		public Request r;

		public JoinRequest(Request r){
			this.r = r;
		}

		@Override
		protected User doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u==null)
				System.out.println("Utente null");
			else
				System.out.println("Sto modificando: "+u.getName());

			if(u.getJoinedReq()==null)
				u.setJoinedReq(new ArrayList<String>());
			if(r.getPartecipants()==null)
				r.setPartecipants(new ArrayList<Long>());
			r.getPartecipants().add(u.getId());
			System.out.println("I partecipant della R che sta per esser aggiunta "+ r.getPartecipants());
			u.getJoinedReq().add(r.getId());
			User oldOwner = r.getOwner();
			r.setOwner(null);
			try {
				manager.updateUser(u).execute();
				manager.updateRequest(r).execute();
				message.notify("Notifica", u.getDevices().get(0)).execute();
				message.notify("Notifica", u.getDevices().get(1)).execute();

			} catch (IOException e) {
				e.printStackTrace();
			}
			r.setOwner(oldOwner);
			return u;
		}



	}
	private class RemoveJoinRequest extends AsyncTask<Void, Void, User> {
		public Request r;

		public RemoveJoinRequest(Request r){
			this.r = r;
		}

		@Override
		protected User doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u==null)
				System.out.println("Utente null");
			else
				System.out.println("Sto modificando: "+u.getName());

			if(u.getJoinedReq()==null){
				u.setJoinedReq(new ArrayList<String>());
				System.out.println("Torno null");
				//return null;
			}

			if(r.getPartecipants()==null)
				r.setPartecipants(new ArrayList<Long>());
			//r.getPartecipants().remove(u.getId());
			boolean removed = false;
			for(int i=0;i<r.getPartecipants().size();i++){
				if(r.getPartecipants().get(i).longValue()==(user.getId())){
					removed = true;
					r.getPartecipants().remove(i);
				}
			}		
			if(removed)	System.out.println("RIMOSSO CON SUCCESSO DALLA REQUEST");
			else	System.out.println("NON E' STATO TROVATO NELLA REQUEST!!");
			System.out.println("Il nuovo size e': "+r.getPartecipants().size());
			removed = false;
			for(int i=0;i<u.getJoinedReq().size();i++){
				if(u.getJoinedReq().get(i).equals(r.getId())){
					u.getJoinedReq().remove(i);
					removed = true;

				}
			}
			if(removed)	System.out.println("RIMOSSO CON SUCCESSO DALLO USER");
			else	System.out.println("NON E' STATO TROVATO NELLO USER!!");
			User oldOwner = r.getOwner();
			r.setOwner(null);
			try {
				manager.updateUser(u).execute();
				manager.updateRequest(r).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			r.setOwner(oldOwner);
			return u;
		}
	}
	private class RemoveOwnerRequest extends AsyncTask<Void, Void, User> {
		public Request r;

		public RemoveOwnerRequest(Request r){
			this.r = r;
		}

		@Override
		protected User doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u==null)
				System.out.println("Utente null");
			else
				System.out.println("Sto modificando: "+u.getName());

			if(u.getRequests()==null)
				return null;
			if(!u.getRequests().contains(r.getId()))
				return null;

			u.getRequests().remove(r.getId());

			try {
				manager.updateUser(u).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return u;
		}
	}
	private class InsertFeedback extends AsyncTask<Void, Void, Feedback> {

		public Feedback f;

		public InsertFeedback(Feedback f){
			this.f = f;
		}

		@Override
		protected Feedback doInBackground(Void... params) {
			System.out.println("Doing in background");
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u==null)
				System.out.println("Utente null");
			else
				System.out.println("Sto modificando: "+u.getName());
			if(u.getSentFb()==null)
				u.setSentFb(new ArrayList<Feedback>());
			u.getSentFb().add(f);
			System.out.println("Persisting?");
			try {
				manager.updateUser(u).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return f;
		}
	}
	
	private class UpdateUser extends AsyncTask<Void, Void, User> {

		@Override
		protected User doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(u==null)
				System.out.println("Utente null");
			else
				System.out.println("Sto modificando: "+u.getName());
			u.setName(user.getName());
			u.setSurname(user.getSurname());
			u.setBday(user.getBday());
			u.setPhotoURL(user.getPhotoURL());
			u.setGender(user.getGender());
			try {
				manager.updateUser(u).execute();

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Dovrei aver aggiornato correttamente l'utente");
			return u;
		}
	}
}