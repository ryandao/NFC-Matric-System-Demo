package com.ryan.mymatric.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
  AuthenticationManager authenticationManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Init the AuthenticationManager and check authentication state
    authenticationManager = AuthenticationManager.getInstance().initialize(getApplicationContext());

    if (authenticationManager.isAuthenticated()) {
      startActivity(new Intent(this, BeamActivity.class));
    } else {
      startActivity(new Intent(this, AuthenticationActivity.class));
    }

    finish();
  }
}
