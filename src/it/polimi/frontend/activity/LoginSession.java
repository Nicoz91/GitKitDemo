package it.polimi.frontend.activity;

import android.content.SharedPreferences;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

public class LoginSession {
	
	private static GitkitUser user;
	private static IdToken idToken;
	private static SharedPreferences prefs;
	
	public static GitkitUser getUser() {
		return user;
	}

	public static void setUser(GitkitUser user) {
		LoginSession.user = user;
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
	}
	
	public static void setStringUser(String gituser){
		prefs.edit().putString("gituser", gituser).commit();
	}
	
	public static void setStringToken(String token){
		prefs.edit().putString("token", token).commit();
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
	}
}
