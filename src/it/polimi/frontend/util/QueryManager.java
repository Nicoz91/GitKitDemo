package it.polimi.frontend.util;
import it.polimi.appengine.entity.manager.Manager;
import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.CloudEndpointUtils;
import it.polimi.frontend.activity.GCMIntentService;
import it.polimi.frontend.activity.LoginSession;
import it.polimi.frontend.activity.MainActivity;
import it.polimi.frontend.activity.MyApplication;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.identitytoolkit.demo.messageEndpoint.MessageEndpoint;
import com.google.identitytoolkit.demo.messageEndpoint.model.CollectionResponseMessageData;
import com.google.identitytoolkit.demo.messageEndpoint.model.MessageData;

public class QueryManager {

	private List<User> users;
	private List<Feedback> feedbacks;
	private List<Request> requests;
	private List<OnRequestLoadedListener> listeners;
	private List<OnActionListener> actionListeners;
	private ArrayList<String> notification;
	private Manager manager;
	private MessageEndpoint message;
	private static QueryManager instance;
	private User user;

	private QueryManager(){
		Storage.getInstance();
		this.users = new ArrayList<User>();
		this.requests = new ArrayList<Request>();
		this.feedbacks = new ArrayList<Feedback>();
		this.listeners = new ArrayList<OnRequestLoadedListener>();
		this.actionListeners = new ArrayList<OnActionListener>();
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

	public void removeListener(OnRequestLoadedListener listener){
		try{
			this.listeners.remove(listener);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void loadRequest(){
		//		System.out.println("Mi connetto per scaricare le richieste...");
		AsyncTask<Void, Void,  ArrayList<Request>> myTask = new LoadDataTask();
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
		    myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
		    myTask.execute();
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

	public void getUserByEmail(String email){
		new QueryUser(email).execute();
	}

	public boolean updateUserDevices(){
		try {
			return new UpdateUserDevice().execute().get();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void notifyListener(){
		System.out.println("NOTIFICO I LISTENER PER AGGIORNARE LE VIEW");
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


	public void insertUser(User user){
		new InsertUser(user).execute();
	}

	public void updateUser(){
		new UpdateUser().execute();
	}

	public void insertRequest(Request request){
		new InsertRequest(request).execute();
	}

	public ArrayList<Request> getUserPartecipation(){
		ArrayList<Request> partecipation = new ArrayList<Request>();
		for(Request r : requests){
			if(r.getPartecipants()!=null)
				if(r.getPartecipants().contains(user.getId()))
					partecipation.add(r);
		}
		System.out.println("Size delle requests: "+partecipation.size());

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
		new JoinRequest(r).execute();
	}

	public void removeJoinRequest(Request r){
		new RemoveJoinRequest(r).execute();
	}

	public void removeOwnerRequest(Request r){
		new RemoveOwnerRequest(r).execute();
		for(int i=0;i<user.getRequests().size();i++){
			if(user.getRequests().get(i).getId().equals(r.getId())){
				user.getRequests().remove(i);
				break;
			}
		}
		notifyListener();

	}



	public void insertFeedback(Feedback f){
		new InsertFeedback(f).execute();
	}

	public ArrayList<String> getNotification(){
		if(notification==null){
			notification = new ArrayList<String>();
			new NotificationTask().execute();
		}
		return notification;

	}
	
	public void clearNotification(){
		notification = null;
	}

	public void queryRequest(String tag){
		new FilterTask(tag).execute();
	}

	public void advancedQuery(String tag, DateTime startA, DateTime startB, DateTime endA, DateTime endB,int maxPA, int maxPB){
		new AdvancedQuery(tag,startA,startB,endA,endB,maxPA,maxPB);
	}

	public void registerDevice(){
		new RegisterDevice().execute();
	}

	public void sortRequest(){
		Collections.sort(requests, new Comparator<Request>(){
			@Override
			public int compare(Request r1, Request r2) {
				if(r1.getStart().getValue()<r2.getStart().getValue())
					return 1;
				else if (r1.getStart().getValue()>r2.getStart().getValue())
					return -1;
				else return 0;
			}

		});
	}

	private class RegisterDevice extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			GCMIntentService.register(MyApplication.getContext());
			while(!updateUserDevices());
			return null;
		}
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
		public void onRequestLoading();
		public void onRequestLoaded(List<Request> requests);
	}

	public void addActionListener(OnActionListener listener){
		OnActionListener remove = null;
		for(OnActionListener l : actionListeners){
			if(l.getClass().equals(listener.getClass()))
				remove = l;
		}
		if(remove!=null)
			actionListeners.remove(remove);
		actionListeners.add(listener);

	}
	public interface OnActionListener{
		public static final int JOIN=0, CANCEL_JOIN=1,GET_USER=2,INSERT_USER=3,
				UPDATE_USER=4, INSERT_REQUEST=5, REMOVE_REQUEST=6, INSERT_FEEDBACK=7, NOTIFICATION=8;
		public void onPerformingAction(int action);
		public void onActionPerformed(Object result,int action);
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
				for(String device : devices){
					message.notify(notify, device,to.getId()).execute();
					System.out.println("Sto per chiamare la query...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}


	}

	private boolean checkEmail(User u , String email){
		boolean check = false;
		if(u.getPwAccount()!=null && u.getPwAccount().equals(LoginSession.getUser().getEmail()) )
			check = true;
		if(u.getGmailAccount()!=null && u.getGmailAccount().equals(LoginSession.getUser().getEmail()) )
			check = true;
		if(u.getFbAccount()!=null && u.getFbAccount().equals(LoginSession.getUser().getEmail()) )
			check = true;
		return check;
	}



	//Classi che effettuano le query
	private class LoadDataTask extends AsyncTask<Void, Void,  ArrayList<Request>> {


		@Override
		protected void onPreExecute() {	
			System.out.println("PRE EXECUTE");
			for(OnRequestLoadedListener l: listeners)
				l.onRequestLoading();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Request> doInBackground(Void... params) {
			System.out.println("Sto scaricando gli utenti");
			ArrayList<Request> requests = new ArrayList<Request>();
			try {
				users = (ArrayList<User>) manager.listUser().execute().getItems();
				try{
					System.out.println("# utenti: "+users.size());
					for(User u : users){
						//						System.out.println("UTENTE: "+u);
						if(checkEmail(u,LoginSession.getUser().getEmail())){
							user = u;
							System.out.println("STO SETTANDO LO USER!!!!");
						}
						ArrayList<Request> ownerReq = (ArrayList<Request>) u.getRequests();
						ArrayList<Feedback> sentFeed = (ArrayList<Feedback>) u.getSentFb();
						if(sentFeed!=null && sentFeed.size()>=0){
							//							System.out.println("SIZE DEI FEED INVIATI: "+sentFeed.size());
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
								Calendar now = Calendar.getInstance();
								if(r.getStart().getValue()>now.getTimeInMillis()){
									System.out.println("Setto "+r.getTitle()+" attuale");
									r.setPastRequest(false);
								}
								else{
									System.out.println("Setto "+r.getTitle()+" passato");
									r.setPastRequest(true);
								}
								if(r.getTitle() == null)
									r.setTitle("Nessun Titolo");
								if(r.getDescription()==null)
									r.setDescription("Nessuna Descrizione");
								if(r.getType()==null)
									r.setType("");
								r.setOwner(u);
								if(r.getPartecipants()==null)
									r.setPartecipants(new ArrayList<Long>());}
							if(checkEmail(u,LoginSession.getUser().getEmail()) ) 
								;//continue;
							else
								requests.addAll(ownerReq);
						}

					}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Like no tomorrow");}

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Requests null");
			}
			sortRequest();
			return (ArrayList<Request>)requests;
		}

		@Override
		protected void onPostExecute(ArrayList<Request> result) {
			//			System.out.println("NOTIFICO A TUTTI!("+listeners.size()+")");
			if(result!=null)
				requests = result;
			else
				requests = new ArrayList<Request>();
			OnRequestLoadedListener remove = null;
			for (OnRequestLoadedListener l : listeners){
				l.onRequestLoaded(requests);
				if(l.getClass().equals(MainActivity.class))
					remove = l;
			}
			listeners.remove(remove);
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
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.GET_USER);
			super.onPreExecute();
			//showDialog();
		}

		@Override
		protected User doInBackground(Void... params) {
			//			System.out.println("Inizio la query");
			User result = null;
			try {
				result = manager.getUserByEmail(email).execute();
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(User result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.GET_USER);
			super.onPostExecute(result);
		}




	}
	private class UpdateUserDevice extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			//			System.out.println("Modifico la lista dei device");
			User u = null;
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			if(u==null){
				//				System.out.println("L'utente è null quindi torno false");
				return false;
			}
			if(LoginSession.getDeviceId()==null){
				//				System.out.println("Deviceid in LoginSession è null quindi torno false");
				return false;
			}
			if(u.getDevices()==null)
				u.setDevices(new ArrayList<String>());
			for(String dev : u.getDevices())
				if(dev.equals(LoginSession.getDeviceId()))
					return true;
			u.getDevices().add(LoginSession.getDeviceId());
			try {
				manager.updateUser(u).execute();
			} catch (Exception e) {
				System.out.println("Eccezione quindi torno null");
				e.printStackTrace();
				return false;
			}
			System.out.println("Update dello user device effettuato con successo.");
			return true;
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
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.INSERT_USER);
			super.onPreExecute();
		}

		@Override
		protected User doInBackground(Void... params) {
			//			System.out.println("Creo un nuovo utente");
			User result = null;
			try {
				result = manager.insertUser(u).execute();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return result;
		}

		@Override
		protected void onPostExecute(User result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.INSERT_USER);
			super.onPostExecute(result);
		}

	}
	private class InsertRequest extends AsyncTask<Void, Void, Request> {
		public Request r;

		public InsertRequest(Request r){
			this.r = r;
		}

		@Override
		protected void onPreExecute() {
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.INSERT_REQUEST);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Request result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.INSERT_REQUEST);
			super.onPostExecute(result);
			notifyListener();
		}

		@Override
		protected Request doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
			if(u.getRequests()==null)
				u.setRequests(new ArrayList<Request>());
			u.getRequests().add(r);

			try {
				u = manager.updateUser(u).execute();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			boolean set = false;
			for(Request req : u.getRequests()){
				if(compareRequest(req,r)){
					r.setId(req.getId());
					set = true;
					System.out.println("Ho settato l'id giusto");
				}
			}
			if(!set)
				return null;

			r.setOwner(user);
			addRequest(user,r);
			return r;
		}



		private boolean compareRequest(Request r1,Request r2){
			try{
				if(r1.getTitle().equals(r2.getTitle()) && r1.getDescription().equals(r2.getDescription()))
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

	private class JoinRequest extends AsyncTask<Void, Void, Boolean> {
		public Request r;

		public JoinRequest(Request r){
			this.r = r;
		}

		@Override
		protected void onPreExecute() {
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.JOIN);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			User u = new User();
			Request newReq = new Request();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
				newReq = manager.getRequest(r.getId()).execute();
				if(newReq!=null)
					System.out.println("Ho caricato la richiesta corretta e aggiornata");
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			if(newReq.getPartecipants()!=null && newReq.getPartecipants().size()>=newReq.getMaxPartecipants()){
				System.out.println("Non puoi partecipare");
				return false;
			}
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());

			r.setPartecipants(newReq.getPartecipants());

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

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			r.setOwner(oldOwner);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.JOIN);
			notifyListener();
			super.onPostExecute(result);
		}

	}
	private class RemoveJoinRequest extends AsyncTask<Void, Void, Boolean> {
		public Request r;

		public RemoveJoinRequest(Request r){
			this.r = r;
		}

		@Override
		protected void onPreExecute() {

			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.CANCEL_JOIN);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
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

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			r.setOwner(oldOwner);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.CANCEL_JOIN);
			notifyListener();
			super.onPostExecute(result);
		}
	}
	private class RemoveOwnerRequest extends AsyncTask<Void, Void, Boolean> {
		public Request r;

		public RemoveOwnerRequest(Request r){
			this.r = r;
		}
		
		@Override
		protected void onPreExecute() {
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.REMOVE_REQUEST);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			//			if(u==null)
			//				System.out.println("Utente null");
			//			else
			//				System.out.println("Sto modificando: "+u.getName());

			if(u.getRequests()==null){
				//				System.out.println("Request è null quindi non modifico nulla");
				return false;
				}

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
						new PushMessage("L'utente "+ user.getName()+" "+user.getSurname()+" ha cancellato la richiesta a cui partecipavi.",us).execute();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.REMOVE_REQUEST);
			notifyListener();
			super.onPostExecute(result);
		}
	}
	private class InsertFeedback extends AsyncTask<Void, Void, Boolean> {

		public Feedback f;

		public InsertFeedback(Feedback f){
			this.f = f;
		}
		
		@Override
		protected void onPreExecute() {
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.INSERT_FEEDBACK);
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			//			System.out.println("Doing in background");
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
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

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			adjustUserFeedback(f);
			f.setFrom(user);
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			for(OnActionListener l: actionListeners)
				l.onActionPerformed(result,OnActionListener.INSERT_FEEDBACK);
			notifyListener();
			super.onPostExecute(result);
		}
	}


	private class UpdateUser extends AsyncTask<Void, Void, User> {

		@Override
		protected void onPreExecute() {
			for(OnActionListener l: actionListeners)
				l.onPerformingAction(OnActionListener.UPDATE_USER);
			super.onPreExecute();
		}

		@Override
		protected User doInBackground(Void... params) {
			User u = new User();
			try {
				u = manager.getUserByEmail(LoginSession.getUser().getEmail()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				u = null;
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

			} catch (Exception e) {
				e.printStackTrace();
				u = null;
			}
			//			System.out.println("Dovrei aver aggiornato correttamente l'utente");
			return u;
		}

		@Override
		protected void onPostExecute(User result) {
			for(OnActionListener l: actionListeners){
				l.onActionPerformed(result,OnActionListener.UPDATE_USER);
			}
			//			notifyListener();
			super.onPostExecute(result);
		}
	}

	private class NotificationTask extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			CollectionResponseMessageData not = null;
			try {
				not = message.getNotification(user.getId()).execute();
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
			ArrayList<String> notification = new ArrayList<String>();
			if(not!=null && not.getItems()!=null && not.getItems().size()>0)
				for(MessageData md : not.getItems())
					notification.add(md.getMessage());
			return notification;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			notification = result;
			for(OnActionListener l: actionListeners){
				l.onActionPerformed(result,OnActionListener.NOTIFICATION);
			}
			super.onPostExecute(result);
		}
	}
}