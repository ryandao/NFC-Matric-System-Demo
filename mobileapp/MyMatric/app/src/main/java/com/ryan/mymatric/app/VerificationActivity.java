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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryandao on 3/10/14.
 */
public class VerificationActivity extends Activity {
  String verificationNumber;
  EditText mEdit;
  Button mButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.verification);
    mEdit = (EditText)findViewById(R.id.verification_number);
    mButton = (Button)findViewById(R.id.submit_button);
  }

  public void sendVerification(View view) {
    mButton.setEnabled(false);
    new PostDataTask().execute("http://hw2-cs3213.herokuapp.com/confirm.json");
  }

  public void returnToPrevious(View view) {
    onBackPressed();
  }

  public void goToBeam() {
  }

  private class PostDataTask extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... urls) {
      // Create a new HttpClient and Post Header
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(urls[0]);
      BasicResponseHandler handler = new BasicResponseHandler();

      try {
        // Post data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        verificationNumber = mEdit.getText().toString();
        nameValuePairs.add(new BasicNameValuePair("verification_number", verificationNumber));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);

        if (response.getStatusLine().getStatusCode() == 200) {
          return getUIDFromResponse(handler.handleResponse(response));
        } else {
          return null;
        }
      } catch (ClientProtocolException e) {
        mEdit.setError("Something wrong happened :(");
        return null;
      } catch (IOException e) {
        return null;
      }
    }

    protected void onPostExecute(String uid) {
      if (uid != null) {
        // Persist the authentication status
        AuthenticationManager.getInstance().createNewAuthentication(uid);
        // Start the BeamActivity Activity
        goToBeam();
      } else {
        // Show error
        mEdit.setError("Verification fails. Please try again.");
        mButton.setEnabled(true);
      }
    }

    private String getUIDFromResponse(String responseBody) {
      try {
        JSONObject json = new JSONObject(responseBody);
        return json.getString("uid");
      } catch (JSONException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
}
