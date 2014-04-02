package com.ryan.mymatric.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by ryandao on 4/1/14.
 * This singleton class manages the authentication state of the application.
 * It persists the encrypted user id once the user has authenticated the app.
 */
public class AuthenticationManager {
  SharedPreferences preferences;
  Context context;
  Editor editor;

  private static AuthenticationManager mInstance;

  // Sharedpref file name
  private static final String PREF_NAME = "MyMatric";

  public static AuthenticationManager getInstance() {
    if (mInstance == null) {
      mInstance = new AuthenticationManager();
    }

    return mInstance;
  }

  public AuthenticationManager initialize(Context context) {
    this.context = context;
    this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    this.editor = preferences.edit();
    return this;
  }

  public void createNewAuthentication(String uid) {
    editor.putString("uid", uid);
    editor.commit();
  }

  public String getUID() {
    return preferences.getString("uid", null);
  }

  public boolean isAuthenticated() {
    return preferences.getString("uid", null) != null;
  }
}
