package com.example.wifitest;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;

import android.content.Context;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.List;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements LocationListener {

    WifiManager wifiManager;

    WifiConfiguration apConfig;
    Method setWifiApEnabled;
    Method getWifiApConfiguration;
    Method setWifiApConfiguration;
    Method getWifiApState;

    Button button1, button2, button3, button4;
    static TextView text1;

    BroadcastReceiver receiver;

    double x;
    double y;

    static boolean isDone;
    static String comment;

    //위치정보 객체
    LocationManager lm = null;
    //위치정보 장치 이름
    String provider = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = (TextView)findViewById(R.id.textView);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button3);

        button2.setEnabled(false);
        button3.setEnabled(false);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

/*
        Method[] methods = wifiManager.getClass().getDeclaredMethods();

        for (Method method: methods)
        {
            if (method.getName().equals("setWifiApEnabled"))
            {
                setWifiApEnabled = method;
            }
            else if(method.getName().equals("getWifiApConfiguration"))
            {
                getWifiApConfiguration = method;
            }
            else if(method.getName().equals("setWifiApConfiguration"))
            {
                setWifiApConfiguration = method;
            }
            else if(method.getName().equals("getWifiApState"))
            {
                getWifiApState = method;
            }
        }
*/

        try {
            setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);

        } catch(Exception ex) {
            setWifiApEnabled = null;
        }

        try {
            getWifiApConfiguration = wifiManager.getClass().getMethod("getWifiApConfiguration");
        } catch(Exception ex) {
            getWifiApConfiguration = null;
        }

        try {
            setWifiApConfiguration = wifiManager.getClass().getMethod("setWifiApConfiguration",
                    WifiConfiguration.class);
        } catch(Exception ex) {
            setWifiApConfiguration = null;
        }

        try {
            getWifiApState = wifiManager.getClass().getMethod("getWifiApState");
        } catch(Exception ex) {
            getWifiApState = null;
        }

        if(receiver == null)
        {
            receiver = new WiFiScanReceiver(this);
        }

        registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /**위치정보 객체를 생성한다.*/
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        /** 현재 사용가능한 위치 정보 장치 검색*/
        //위치정보 하드웨어 목록
        Criteria c = new Criteria();
        //최적의 하드웨어 이름을 리턴받는다.
        c.setAccuracy(Criteria.NO_REQUIREMENT);
        c.setPowerRequirement(Criteria.NO_REQUIREMENT);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        provider = lm.getBestProvider(c, true);

        // 최적의 값이 없거나, 해당 장치가 사용가능한 상태가 아니라면,
        //모든 장치 리스트에서 사용가능한 항목 얻기
        if(provider == null || !lm.isProviderEnabled(provider)){
            // 모든 장치 목록
            List<String> list = lm.getAllProviders();

            for(int i = 0; i < list.size(); i++){
                //장치 이름 하나 얻기
                String temp = list.get(i);

                //사용 가능 여부 검사
                if(lm.isProviderEnabled(temp)){
                    provider = temp;
                    break;
                }
            }
        }// (end if)위치정보 검색 끝

        /**마지막으로  조회했던 위치 얻기*/
        Location location;

        /*
        if (lm != null) {
            lm.requestLocationUpdates(lm.GPS_PROVIDER, 0, 0, this);
            location = lm .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                x = location.getLatitude();
                y = location.getLongitude();
            } else {
                android.widget.Toast.makeText( this, "Location Null", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
        */

        /**마지막으로  조회했던 위치 얻기*/
        location = lm.getLastKnownLocation(provider);

        if(provider == null){
            android.widget.Toast.makeText(this, "Provide가 null", android.widget.Toast.LENGTH_SHORT).show();
        }else{
            if(location == null){
                android.widget.Toast.makeText(this, "사용가능한 위치 정보 제공자가 없습니다.", android.widget.Toast.LENGTH_SHORT).show();
            }else{
                //최종 위치에서 부터 이어서 GPS 시작...
                onLocationChanged(location);

            }
        }

    }

    public void onButton1Click(View view)
    {
        /*
        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = "TBACKUP";
        netConfig.hiddenSSID = true;

        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        netConfig.allowedKeyManagement.set(4);  // WifiConfiguration.KeyMgmt.WPA2_PSK
//        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.preSharedKey = "1234567890";

        try {

            if(setWifiApEnabled != null)
            {
                if(wifiManager.isWifiEnabled() == true)
                {
                    wifiManager.setWifiEnabled(false);
                }

                boolean ret;

                setWifiApEnabled.invoke(wifiManager, null, false);

//                ret = (Boolean)setWifiApConfiguration.invoke(wifiManager, netConfig);
//                text1.append("setWifiApConfiguration: " + ret + "\n");

                ret = (Boolean)setWifiApEnabled.invoke(wifiManager, netConfig, true);

                text1.append("setWifiApEnabled: " + ret + "\n");
            }

        }
        catch (Exception ex)
        {
            text1.append("setWifiApEnabled Error: " + ex.toString() + "\n");
        }

        try {
            if(getWifiApConfiguration != null)
            {
                apConfig = (WifiConfiguration)getWifiApConfiguration.invoke(wifiManager);

                text1.append("SSID: " + apConfig.SSID + "\n");
                text1.append("hiddenSSID: " + apConfig.hiddenSSID + "\n");
                text1.append("preSharedKey: " + apConfig.preSharedKey + "\n");
            }
        }
        catch (Exception ex)
        {
            text1.append("getWifiApConfiguration Error: " + ex.toString() + "\n");
        }*/
        text1.setText("");
        text1.append("----------------- Test Begin --------------\n");
        text1.append("mac address: " + getMacAddress(this) + "\n");
        text1.append("device model: " + Build.MODEL + "\n");
        text1.append("current time: " + getCurrentTime() + "\n");
        text1.append("latitude = " + x + ", longitude = " + y + "\n");
        text1.append("address = " + getAddress(x, y) +"\n");
        text1.append("--------------------------------------------\n");

        button2.setEnabled(true);
        button1.setEnabled(false);
        button3.setEnabled(true);
    }

    public void onButton2Click(View view)
    {

/*
        text1.setText("");

        text1.append("isWifiEnabled: " + wifiManager.isWifiEnabled() + "\n");
        text1.append("getWifiState: " + wifiManager.getWifiState() + "\n");

        try {
            if(getWifiApConfiguration != null)
            {
                apConfig = (WifiConfiguration)getWifiApConfiguration.invoke(wifiManager);

                text1.append("SSID: " + apConfig.SSID + "\n\n");
                text1.append("hiddenSSID: " + apConfig.hiddenSSID + "\n\n");
                text1.append("preSharedKey: " + apConfig.preSharedKey + "\n\n");
            }
        }
        catch (Exception ex)
        {

        }
*/

/*
        text1.append("WiFi AP Scanning ...\n");
        wifiManager.startScan();
*/

        if( wifiManager.isWifiEnabled() == false ) {
            try {
                if(getWifiApState != null) {
                    int state = (Integer)getWifiApState.invoke(wifiManager);

                    if(state == 13) // WIFI_AP_STATE_ENABLED
                    {
                        if(setWifiApEnabled != null)
                        {
                            setWifiApEnabled.invoke(wifiManager, null, false);
                        }
                    }
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.startScan();
        }

        /*
        while(wifiManager.isWifiEnabled() != true)
        {
            try {
                Thread.sleep(300);
            } catch (Exception ex) {
            }
        }
        */

/*
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            text1.append(config.SSID + ", " + config.preSharedKey + "\n");
        }
*/
        WifiConfiguration netConfig = new WifiConfiguration();

//        netConfig.SSID = "\"SKPMSD1\"";
        netConfig.SSID = "\"TBACKUP\"";
        netConfig.hiddenSSID = true;

        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

//        netConfig.preSharedKey = "\"mobiledev#1\"";
        netConfig.preSharedKey = "\"1234567890\"";

        int net = wifiManager.addNetwork(netConfig);
        wifiManager.enableNetwork(net, true);
        wifiManager.reconnect();


    }

    public void onButton3Click(View view) {
        wifiManager.setWifiEnabled(false);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Really want to end this test?");
        alert.setMessage("Type extra comment (Optional)");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                button1.setEnabled(true);
                button2.setEnabled(false);
                button3.setEnabled(false);

                String value = input.getText().toString();

                com.example.wifitest.MainActivity.isDone = true;
                com.example.wifitest.MainActivity.comment = value.toString();
                TextView text1 = com.example.wifitest.MainActivity.text1;

                text1.append("------------------ Test End ---------------\n");
                text1.append("mac address: " + getMacAddress(com.example.wifitest.MainActivity.this) + "\n");
                text1.append("current time: " + getCurrentTime() + "\n");
                text1.append("latitude = " + x + ", longitude = " + y + "\n");
                text1.append("address = " + getAddress(x, y) +"\n");
                text1.append("comment [" + com.example.wifitest.MainActivity.comment +"]\n");
                text1.append("--------------------------------------------\n");
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        com.example.wifitest.MainActivity.isDone = false;
                    }
                });

        alert.show();

        /*
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        text1.append("getConnectionInfo: " + wInfo.toString() + "\n\n");

        int ip = wifiManager.getDhcpInfo().gateway;
        String address = String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

        text1.append("Gateway: " + address + "\n");
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public String getMacAddress(android.content.Context context) {
        WifiManager wimanager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "와이파이가 안되거나 맥어드레스가 없는경우";//device has no macaddress or wifi is disabled
        }
        return macAddress;
    }

    public String getCurrentTime() {
        long now = System.currentTimeMillis();
        return String.valueOf(now);
    }

    public String getCurrentLocation() {
        return String.valueOf(x*10000 + y);
    }

    /** 이 화면이 불릴 때, 일시정지 해제 처리*/
    @Override
    public void onResume(){
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onResume();

        //위치정보 객체에 이벤트 연결
        lm.requestLocationUpdates(provider, 500, 1, this);
    }
    /** 다른 화면으로 넘어갈 때, 일시정지 처리*/
    @Override
    public void onPause(){
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onPause();

        //위치정보 객체에 이벤트 해제
        lm.removeUpdates(this);
    }

    /** 위치가 변했을 경우 호출된다.*/
    @Override
    public void onLocationChanged(Location location) {
        // 위도, 경도
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        x = location.getLatitude();
        y = location.getLongitude();
    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    /** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
    public String getAddress(double lat, double lng){
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, java.util.Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        try{
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(list == null){
            //Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }

        if(list.size() > 0){
            Address addr = list.get(0);
            address = addr.getCountryName() + " "
                    + addr.getPostalCode() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare() + " "
                    + addr.getFeatureName();
        }

        return address;



    }

}
