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

/**
 * Created by ryandao on 4/1/14.
 */
public class AuthenticationActivity extends Activity {
  EditText mEdit;
  Button mButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.authentication);
    mEdit = (EditText) findViewById(R.id.matric_number);
    mButton = (Button) findViewById(R.id.request_button);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mButton.setEnabled(true);
  }

  public void requestAccess(View view) {
    mButton.setEnabled(false);
    new PostDataTask().execute("http://hw2-cs3213.herokuapp.com/verify.json");
  }

  public void goToVerify() {
    startActivity(new Intent(this, VerificationActivity.class));
  }

  private class PostDataTask extends AsyncTask<String, Void, Boolean> {
    protected Boolean doInBackground(String... urls) {
      // Create a new HttpClient and Post Header
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(urls[0]);

      try {
        // Post data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("matric_number", mEdit.getText().toString()));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        // Execute HTTP Post Request
        HttpResponse response = httpclient.execute(httppost);
        return (response.getStatusLine().getStatusCode() == 200);
      } catch (ClientProtocolException e) {
        mEdit.setError("Something wrong happened :(");
        return false;
      } catch (IOException e) {
        return false;
      }
    }

    protected void onPostExecute(Boolean result) {
      if (result) {
        // Start the Verify Activity
        goToVerify();
      } else {
        // Show error
        mEdit.setError("Cannot verify your Matric number");
        mButton.setEnabled(true);
      }
    }
  }
}
