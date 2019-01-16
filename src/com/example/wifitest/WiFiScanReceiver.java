package com.example.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;

import java.util.List;


/**
 * Created by skplanet on 13. 5. 23.
 */
public class WiFiScanReceiver extends BroadcastReceiver {

    MainActivity main;

    public WiFiScanReceiver(MainActivity main) {
        super();
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        List<ScanResult> results = main.wifiManager.getScanResults();

        main.text1.append("wifi scanned (" + results.size() + " results)\n");

        /*
        for (ScanResult result : results)
        {
            main.text1.append(result.SSID + "\r\n");
        }
        */
    }

}
