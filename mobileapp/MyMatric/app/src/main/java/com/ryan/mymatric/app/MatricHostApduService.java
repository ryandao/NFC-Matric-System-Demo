package com.ryan.mymatric.app;

import android.annotation.TargetApi;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by ryandao on 3/29/14.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MatricHostApduService extends HostApduService {

  @Override
  public byte[] processCommandApdu(byte[] bytes, Bundle bundle) {
    return bytes;
  }

  @Override
  public void onDeactivated(int i) {

  }
}
