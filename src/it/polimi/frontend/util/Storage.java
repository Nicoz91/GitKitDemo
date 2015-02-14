package it.polimi.frontend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

public class Storage {
	private static Storage instance;
	private final static String APP_KEY = "9r7xcnhwd8krsip";
	private final static String APP_SECRET = "qq6r2nsgakr4u8b";
	private final static String APP_TOKEN = "LScJK1rz1BQAAAAAAAAAG7mSTlO5ZPG5qwj7enYV21SsG0UuFejvHNmogO8W8L6e";
	private DropboxAPI<AndroidAuthSession> mDBApi;
	
	private Storage(){
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		mDBApi.getSession().setOAuth2AccessToken(APP_TOKEN);
		try {
		if(isDropboxLinked())
			System.out.println("Link effettuato");
		else
			System.out.println("Link non effettuato");
		//mDBApi.getSession().finishAuthentication();

			System.out.println("Linked to: "+mDBApi.accountInfo().displayName);
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
	}
	
	public boolean isDropboxLinked() {
	    return mDBApi != null && (mDBApi.getSession().isLinked() || mDBApi.getSession().authenticationSuccessful());
	}
	
	public static Storage getInstance(){
		if(instance==null)
			instance = new Storage();
		return instance;
	}
	
	public String uploadFile(String path,String name) throws DropboxException, FileNotFoundException{
		File file = new File(path);
		FileInputStream inputStream = new FileInputStream(file);
		Entry response = mDBApi.putFile("/"+name, inputStream,
		                                file.length(), null, null);
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
}
