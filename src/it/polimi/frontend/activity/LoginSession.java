package it.polimi.frontend.activity;

import android.content.SharedPreferences;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

public class LoginSession {
	
	private static GitkitUser user;
	private static IdToken idToken;
	private static SharedPreferences prefs;
	private static String deviceId;
	private static String provider;
	
	public static int getNotNumber() {
		int notNumber = 1;

		if (prefs.contains("notNumber")){
		     notNumber = prefs.getInt("notNumber", 1);
		}
		return notNumber;
	}

	public static void setNotNumber(int notNumber){
		prefs.edit().putInt("notNumber", notNumber).commit();
	}

	public static String getDeviceId() {
		return deviceId;
	}

	public static void setDeviceId(String deviceId) {
		LoginSession.deviceId = deviceId;
	}

	public static GitkitUser getUser() {
		return user;
	}

	public static void setUser(GitkitUser user) {
		LoginSession.user = user;
	}

	public static String getProvider() {
		return provider;
	}

	public static void setProvider(String provider) {
		LoginSession.provider = provider;
	}

	public static void setPrefs(SharedPreferences prefes) {
		//TODO Aggiungere MODO PRIVATO
		prefs = prefes;
		if (prefs.contains("gituser")) {
		     String gituser = prefs.getString("gituser", "");
			 user = GitkitUser.fromJsonString(gituser);
		}
		if (prefs.contains("token")) {
		     String token = prefs.getString("token", "");
			 idToken = IdToken.parse(token);
		}
		if (prefs.contains("device")){
		     deviceId = prefs.getString("device", "");
		}
		
		if (prefs.contains("provider")){
		     provider = prefs.getString("provide", "");
		}
	}
	
	public static void setStringUser(String gituser){
		prefs.edit().putString("gituser", gituser).commit();
	}
	
	public static void setStringProvider(String provider){
		prefs.edit().putString("provider", provider).commit();
	}
	
	public static void setStringToken(String token){
		prefs.edit().putString("token", token).commit();
	}
	
	public static void setStringDevice(String device){
		prefs.edit().putString("device", device).commit();
	}

	public static IdToken getIdToken() {
		return idToken;
	}

	public static void setIdToken(IdToken idToken) {
		LoginSession.idToken = idToken;
	}
	
	public static void reset(){
		user = null;
		idToken = null;
		prefs.edit().remove("gituser").commit();
		prefs.edit().remove("token").commit();
		prefs.edit().remove("provider").commit();
		prefs.edit().remove("device").commit();
		prefs.edit().remove("notNumber").commit();
	}
}
