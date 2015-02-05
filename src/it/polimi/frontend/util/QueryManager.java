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
import com.google.api.client.util.DateTime;
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

	public void loadRequest(){
		//		System.out.println("Mi connetto per scaricare le richieste...");
		new LoadDataTask().execute();
	}

	public List<Request> getRequests(){
		if(requests == null)
			return new ArrayList<Request>();
		return requests;
	}

	public List<User> getUsers() {
		if(users == null)
			return new ArrayList<User>();
		return users;
	}

	public List<Feedback> getFeedback() {
		if(feedbacks == null)
			return new ArrayList<Feedback>();
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

	public void notifyListener(){
		//		System.out.println("NOTIFICO I LISTENER PER AGGIORNARE LE VIEW");
		for (OnRequestLoadedListener l : listeners){
			l.onRequestLoaded(requests);
		}
	}

	public void notifyListener(ArrayList<Request> req){
		//		System.out.println("NOTIFICO I LISTENER PER AGGIORNARE LE VIEW");
		for (OnRequestLoadedListener l : listeners){
			l.onRequestLoaded(req);
		}
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
		this.user = user;
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

	public void insertRequest(Request request){
		try {
			new InsertRequest(request).execute().get();
			notifyListener();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		//		if(r==null){System.out.println("R è null... strano");}
		//		else{System.out.println("R id:"+r.getId());}

		try {
			new JoinRequest(r).execute().get();
			notifyListener();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeJoinRequest(Request r){

		//		if(r==null){System.out.println("R è null... strano");}
		//		else{System.out.println("R id:"+r.getId());}

		try {
			new RemoveJoinRequest(r).execute().get();
			notifyListener();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeOwnerRequest(Request r){
		//
		//		if(r==null){System.out.println("R è null... strano");}
		//		else{System.out.println("R id:"+r.getId());}
		try {
			new RemoveOwnerRequest(r).execute().get();
			for(int i=0;i<user.getRequests().size();i++){
				if(user.getRequests().get(i).getId().equals(r.getId())){
					user.getRequests().remove(i);
					break;
				}

			}
			notifyListener();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void insertFeedback(Feedback f){
		//		System.out.println("Provo ad inserire un feed");

		try {
			new InsertFeedback(f).execute().get();
			notifyListener();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void queryRequest(String tag){
		new FilterTask(tag).execute();
	}
	
	public void advancedQuery(String tag, DateTime startA, DateTime startB, DateTime endA, DateTime endB,int maxPA, int maxPB){
		new AdvancedQuery(tag,startA,startB,endA,endB,maxPA,maxPB);
	}

	private class FilterTask extends AsyncTask<Void, Void, ArrayList<Request>> {
		private String tag;
		public FilterTask(String tag){
			this.tag = tag;
		}
		@Override
		protected ArrayList<Request> doInBackground(Void... arg0) {
			ArrayList<Request> result = new ArrayList<Request>();
			for(Request r : requests){
				if(	r.getTitle().toLowerCase().contains(tag.toLowerCase())		
							|| r.getDescription().contains(tag)
							|| r.getType().contains(tag)
						){
					result.add(r);
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Request> result) {
			notifyListener(result);
		}
	}
	
	private class AdvancedQuery extends AsyncTask<Void, Void, ArrayList<Request>> {
		private String tag;
		private DateTime startA,startB,endA,endB;
		private int maxPA;
		private int maxPB;

		public AdvancedQuery(String tag, DateTime startA, DateTime startB,DateTime endA, DateTime endB, int maxPA,int maxPB) {
			super();
			this.tag = tag;
			this.startA = startA;
			this.startB = startB;
			this.endA = endA;
			this.endB = endB;
			this.maxPA = maxPA;
			this.maxPB = maxPB;
			
		}

		@Override
		protected ArrayList<Request> doInBackground(Void... arg0) {
			ArrayList<Request> result = new ArrayList<Request>();
			ArrayList<Request> toRemove = new ArrayList<Request>();
			result.addAll(requests);
			
			for(Request r : result){
				boolean remove = false;
				if(!(	r.getTitle().toLowerCase().contains(tag.toLowerCase())		
							|| r.getDescription().contains(tag)
							|| r.getType().contains(tag)
						)){
					remove = true; 
				}
				if(startA!=null && startB!=null)
				if(!(r.getStart().getValue()>startA.getValue() && r.getStart().getValue()<startB.getValue() ))
					remove = true;
				
				if(endA!=null && endB!=null)
				if(!(r.getEnd().getValue()>endA.getValue() && r.getEnd().getValue()<endB.getValue() ))
					remove = true;
				
				if(!(r.getMaxPartecipants()>maxPA && r.getMaxPartecipants()<maxPB))
					remove = true;
				
				if(remove) toRemove.add(r);
			}
			result.removeAll(toRemove);
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Request> result) {
			notifyListener(result);
		}
	}

	public void addListener(OnRequestLoadedListener listener){
		listeners.add(listener);
		//		System.out.println("Ho aggiunto un listener: "+listener.getClass().toString());
	}

	public interface OnRequestLoadedListener{
		public void onRequestLoaded(List<Request> requests);
	}

	public static void destroy (){
		instance=null;
	}

	private void adjustUserFeedback(Feedback f){
		for(User u1 : users){
			//			System.out.println("Feed id: "+f.getToId()+" == User id:"+u1.getId()+" ?");
			if(f.getToId().equals(u1.getId())){
				f.setTo(u1);
				if(u1.getReceivedFb()==null)
					u1.setReceivedFb(new ArrayList<Feedback>());										
				//				System.out.println("Assegno all'utente "+u1.getName()+" il feed"+f.getDescription());
				u1.getReceivedFb().add(f);
			}
		}
	}

	private User getUserFromId(Long id){
		for(User u: users)
			if(u.getId().equals(id))
				return u;
		return null;
	}

	private class PushMessage extends AsyncTask<Void, Void, Void> {

		private String notify;
		private User to;

		public PushMessage(String message,User user){
			super();
			this.notify = message;
			this.to  = user;
		}

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<String> devices = (ArrayList<String>) to.getDevices();
			if(devices == null) return null;
			try {
				for(String device : devices)
					message.notify(notify, device).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}


	}


	//Classi che effettuano le query
	private class LoadDataTask extends AsyncTask<Void, Void,  ArrayList<Request>> {

		@Override
		protected ArrayList<Request> doInBackground(Void... params) {
			System.out.println("Sto scaricando gli utenti");
			requests = new ArrayList<Request>();
			try {
				users = (ArrayList<User>) manager.listUser().execute().getItems();
				try{
					System.out.println("# utenti: "+users.size());
					for(User u : users){
						//						System.out.println("UTENTE: "+u);

						if(u.getPwAccount().equals(LoginSession.getUser().getEmail())) 
							user = u;
						ArrayList<Request> ownerReq = (ArrayList<Request>) u.getRequests();
						ArrayList<Feedback> sentFeed = (ArrayList<Feedback>) u.getSentFb();
						if(sentFeed!=null && sentFeed.size()>=0){
							System.out.println("SIZE DEI FEED INVIATI: "+sentFeed.size());
							for(Feedback f: sentFeed){
								f.setFrom(u);
								adjustUserFeedback(f);
							}
						}
						else{
							//							System.out.println("I feed sono vuoti per l'utente: "+u);
						}
						if(ownerReq!=null && ownerReq.size()>0){
							for(Request r : ownerReq){
								//								Request req = manager.getRequest(r.getId()).execute();
								//								if(req==null) System.out.println("Non ho trovato nulla");
								//								else System.out.println("Title: "+req.getTitle());
								if(r.getTitle() == null)
									r.setTitle("Nessun Titolo");
								if(r.getDescription()==null)
									r.setDescription("Nessuna Descrizione");
								if(r.getType()==null)
									r.setType("");
								r.setOwner(u);
								if(r.getPartecipants()==null)
									r.setPartecipants(new ArrayList<Long>());}
							if(u.getPwAccount().equals(LoginSession.getUser().getEmail())) 
								;//continue;
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
			//			System.out.println("NOTIFICO A TUTTI!("+listeners.size()+")");
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
			//			System.out.println("Inizio la query");
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
			//			System.out.println("Modifico la lista dei device");
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
			//			System.out.println("Creo un nuovo utente");
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
				u = manager.updateUser(u).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(Request req : u.getRequests()){
				if(compareRequest(req,r)){
					r.setId(req.getId());
					System.out.println("Ho settato l'id giusto");
				}
			}
			r.setOwner(user);
			addRequest(user,r);
			return r;
		}

		private boolean compareRequest(Request r1,Request r2){
			try{
				if(r1.getTitle().equals(r2.getTitle()))
					return true;
				else 
					return false;
			}catch(Exception e){
				return false;
			}
		}

		private void addRequest(User u, Request r){
			if(u.getRequests()==null)
				u.setRequests(new ArrayList<Request>());
			u.getRequests().add(r);
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
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());

			if(u.getJoinedReq()==null)
				u.setJoinedReq(new ArrayList<String>());
			if(r.getPartecipants()==null)
				r.setPartecipants(new ArrayList<Long>());
			r.getPartecipants().add(u.getId());
			//			System.out.println("I partecipant della R che sta per esser aggiunta "+ r.getPartecipants());
			u.getJoinedReq().add(r.getId());
			User oldOwner = r.getOwner();
			r.setOwner(null);
			try {
				manager.updateUser(u).execute();
				manager.updateRequest(r).execute();
				new PushMessage(user.getName()+ " "+ user.getSurname() + " partecipa alla richiesta "+r.getTitle(),oldOwner).execute();
				//				message.notify("Notifica", u.getDevices().get(1)).execute();

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
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());

			if(u.getJoinedReq()==null){
				u.setJoinedReq(new ArrayList<String>());
				//				System.out.println("Torno null");
				//return null;
			}

			if(r.getPartecipants()==null)
				r.setPartecipants(new ArrayList<Long>());
			//r.getPartecipants().remove(u.getId());
			//			boolean removed = false;
			for(int i=0;i<r.getPartecipants().size();i++){
				if(r.getPartecipants().get(i).longValue()==(user.getId())){
					//					removed = true;
					r.getPartecipants().remove(i);
				}
			}		
			//			if(removed)	System.out.println("RIMOSSO CON SUCCESSO DALLA REQUEST");
			//			else	System.out.println("NON E' STATO TROVATO NELLA REQUEST!!");
			//			System.out.println("Il nuovo size e': "+r.getPartecipants().size());
			//			removed = false;
			for(int i=0;i<u.getJoinedReq().size();i++){
				if(u.getJoinedReq().get(i).equals(r.getId())){
					u.getJoinedReq().remove(i);
					//					removed = true;

				}
			}
			//			if(removed)	System.out.println("RIMOSSO CON SUCCESSO DALLO USER");
			//			else	System.out.println("NON E' STATO TROVATO NELLO USER!!");
			User oldOwner = r.getOwner();
			r.setOwner(null);
			try {
				manager.updateUser(u).execute();
				manager.updateRequest(r).execute();
				new PushMessage(user.getName()+ " "+ user.getSurname() + " non partecipa più alla richiesta "+r.getTitle(),oldOwner).execute();

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
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());

			if(u.getRequests()==null){
				//				System.out.println("Request è null quindi non modifico nulla");
				return null;}

			for(int i=0;i<u.getRequests().size();i++){
				if(u.getRequests().get(i).getId().equals(r.getId())){
					u.getRequests().remove(i);
					break;
				}

			}

			//u.getRequests().remove(r.getId());

			try {
				manager.updateUser(u).execute();
				ArrayList<User> partecipant = getUserFromRequest(r);
				if(partecipant!=null)
					for(User us : partecipant)
						new PushMessage("Lutente "+ user.getName()+" "+user.getSurname()+" ha cancellato la richiesta a cui partecipavi.",us).execute();
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
			//			System.out.println("Doing in background");
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());
			if(u.getSentFb()==null)
				u.setSentFb(new ArrayList<Feedback>());
			u.getSentFb().add(f);
			//			System.out.println("Persisting?");
			try {
				manager.updateUser(u).execute();
				if(f.getTo()!=null)
					new PushMessage("Lutente "+ user.getName()+" "+user.getSurname()+" ti ha lasciato un feedback.",f.getTo()).execute();
				else
					new PushMessage("Lutente "+ user.getName()+" "+user.getSurname()+" ti ha lasciato un feedback.",getUserFromId(f.getToId())).execute();

			} catch (IOException e) {
				e.printStackTrace();
			}
			adjustUserFeedback(f);
			f.setFrom(user);
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
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());
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
			//			System.out.println("Dovrei aver aggiornato correttamente l'utente");
			return u;
		}
	}
}