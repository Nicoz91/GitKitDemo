package it.polimi.frontend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.os.AsyncTask;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

public class Storage {
	private static Storage instance;
	private final static String APP_KEY = "9r7xcnhwd8krsip";
	private final static String APP_SECRET = "qq6r2nsgakr4u8b";
	private final static String APP_TOKEN = "LScJK1rz1BQAAAAAAAAAG7mSTlO5ZPG5qwj7enYV21SsG0UuFejvHNmogO8W8L6e";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	private OnImageLoadedListener listener;
	
	private Storage(){
		new Init().execute();
	}

	private class Init extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
				while(!isDropboxLinked()){
				//System.out.println("Link non effettuato");
				//mDBApi.getSession().finishAuthentication();
				
				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession session = new AndroidAuthSession(appKeys);
				mDBApi = new DropboxAPI<AndroidAuthSession>(session);
				mDBApi.getSession().setOAuth2AccessToken(APP_TOKEN);
				}
				try {
					System.out.println("Linked to: "+mDBApi.accountInfo().displayName);
				} catch (DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return null;
		}
	}


	public OnImageLoadedListener getListener() {
		return listener;
	}

	public void setListener(OnImageLoadedListener listener) {
		this.listener = listener;
	}



	public interface OnImageLoadedListener{
		public void onImageLoading();
		public void onImageLoaded(String path);
		public void onImageProgress(long bytes, long total);
	}

	public boolean isDropboxLinked() {
		return mDBApi != null && (mDBApi.getSession().isLinked() || mDBApi.getSession().authenticationSuccessful());
	}

	public static Storage getInstance(){
		if(instance==null)
			instance = new Storage();
		return instance;
	}

	public void uploadFile(String path,String name){
		new UploadFile(path,name).execute();
	}

	private class UploadFile extends AsyncTask<Void, Void, String> {
		private String path;
		private String name;

		public UploadFile(String path,String name){
			this.path = path;
			this.name = name;
		}

		@Override
		protected void onPreExecute() {
			if(listener!=null)
				listener.onImageLoading();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				return uploadFile(path,name);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private String uploadFile(String path,String name) throws DropboxException, FileNotFoundException{
			File file = new File(path);
			FileInputStream inputStream = new FileInputStream(file);
		//	Entry response = mDBApi.putFileOverwrite("/"+name, inputStream,file.length(), null);
			Entry response = mDBApi.putFile("/"+name, inputStream,file.length(), null,new ProgressListener(){

				@Override
				public void onProgress(long b, long t) {
					listener.onImageProgress(b, t);					
				}
				
			});
			DropboxLink link = mDBApi.share(response.path);
			return getShareURL(link.url);
		}

		private String getShareURL(String strURL) {
			URLConnection conn = null;
			String redirectedUrl = null;
			try {
				URL inputURL = new URL(strURL);
				conn = inputURL.openConnection();
				conn.connect();

				InputStream is = conn.getInputStream();
				System.out.println("Redirected URL: " + conn.getURL());
				redirectedUrl = conn.getURL().toString();
				is.close();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			redirectedUrl = redirectedUrl.substring(0, redirectedUrl.length()-1)+1;
			return redirectedUrl;
		}

		@Override
		protected void onPostExecute(String	result) {
			if(listener!=null)
				listener.onImageLoaded(result);
			super.onPostExecute(result);
		}
	}

}
