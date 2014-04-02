package com.ryan.mymatric.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

/**
 * Created by ryandao on 3/10/14.
 */
public class BeamActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {
  NfcAdapter mNfcAdapter;
  TextView mInfoText;
  ImageView mNFCImage;
  String uid;

  private static final int MESSAGE_SENT = 1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.uid = AuthenticationManager.getInstance().getUID();

    // Check for availability of Host Card Emulation
    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
      runHCE();
    } else {
      Log.i("Matric", "HCE not available");
      runBeam();
    }
  }

  private void runHCE() {

  }

  private void runBeam() {
    setContentView(R.layout.beam);
    mInfoText = (TextView) findViewById(R.id.textView);
    mNFCImage = (ImageView) findViewById(R.id.imageView);
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    if (mNfcAdapter == null) {
      mInfoText.setText(R.string.nfc_info_no);
      mNFCImage.setVisibility(View.GONE);
    } else if (mNfcAdapter.isEnabled()) {
      // Register callback to set NDEF message
      mNfcAdapter.setNdefPushMessageCallback(this, this);
      // Register callback to listen for message-sent success
      mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    } else {
      mInfoText.setText(R.string.nfc_info_disabled);
      mNFCImage.setImageResource(R.drawable.nfc_no);
    }
  }

  /**
   * Implementation for the CreateNdefMessageCallback interface
   */
  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    Time time = new Time();
    time.setToNow();
    String text = ("Reader communication demo");

    NdefMessage msg = new NdefMessage(
      new NdefRecord[] {
        createMimeRecord(uid, text.getBytes())
      }
    );

    return msg;
  }

  /**
   * Implementation for the OnNdefPushCompleteCallback interface
   */
  @Override
  public void onNdefPushComplete(NfcEvent arg0) {
    // A handler is needed to send messages to the activity when this
    // callback occurs, because it happens from a binder thread
    mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
  }

  /** This handler receives a message from onNdefPushComplete */
  private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MESSAGE_SENT:
          Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
          break;
      }
    }
  };

  @Override
  public void onResume() {
    super.onResume();
    // Check to see that the Activity started due to an Android BeamActivity
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
      processIntent(getIntent());
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    // onResume gets called after this to handle the intent
    setIntent(intent);
  }

  /**
   * Parses the NDEF Message from the intent and prints to the TextView
   */
  void processIntent(Intent intent) {
    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
            NfcAdapter.EXTRA_NDEF_MESSAGES);
    // only one message sent during the beam
    NdefMessage msg = (NdefMessage) rawMsgs[0];
    // record 0 contains the MIME type, record 1 is the AAR, if present
    mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
  }

  /**
   * Creates a custom MIME type encapsulated in an NDEF record
   *
   * @param mimeType
   */
  public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
    byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
    NdefRecord mimeRecord = new NdefRecord(
            NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
    return mimeRecord;
  }
}