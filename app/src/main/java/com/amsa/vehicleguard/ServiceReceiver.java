package com.amsa.vehicleguard;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

public class ServiceReceiver extends BroadcastReceiver {

    private String callingNumber = "01521579898";
    private String senderEmail = "alensamsa3@gmail.com";
    private String senderPass = "89133803%%amsa";
//    private String receiverEmail = "u1404092@student.cuet.ac.bd";
    private String receiverEmail = new MainActivity().getEmail();
    private String sub = "gh";
    private String msg = "gh";
    private static boolean sMail = false;
    private static boolean rang = false;
    private LocationManager locationManager;
    private LocationListener listener;
    private Context context;
    public static String lat = "Lat: ";
    public static String lon = "lon: ";
    public static String incomingNumber;
//    public static String ownerPhoneNumber="+8801521579898";
    public static String ownerPhoneNumber=new MainActivity().getPhoneNo();
    public static String status=new MainActivity().getStatus();


    @Override
    public void onReceive(final Context context, Intent intent) {
        //////////////////////////////////////////////
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                t.setText(" ");
//                t.append("\n Lat: " + location.getLongitude() + "\n Lon: " + location.getLatitude());
//                lat=(String) location.getLatitude()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    double d = location.getLatitude();
                    lat = String.valueOf(d);
                    lon = String.valueOf(location.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
            }
        };
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
//                        , 10);
//            }
//            return;
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        } else {
//        Toast.makeText(context,"low APi",Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);

        ///////////////////////////////////////////

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        tm.listen(new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingnumber) {
                super.onCallStateChanged(state, incomingnumber);
//                System.out.println("incomingNumber : "+incomingNumber);
                incomingNumber = incomingnumber.trim();
                Toast.makeText(context,"dd "+incomingNumber,Toast.LENGTH_SHORT).show();


            }
        }, PhoneStateListener.LISTEN_CALL_STATE);

        int state = tm.getCallState();
        if (state == TelephonyManager.CALL_STATE_RINGING) {

            rang = true;
            Toast.makeText(context, " vggf", Toast.LENGTH_SHORT).show();
            Toast.makeText(context,""+receiverEmail+ownerPhoneNumber,Toast.LENGTH_SHORT).show();
        }
        if(ownerPhoneNumber!=null && receiverEmail!=null && status !="off"){
            if (state == TelephonyManager.CALL_STATE_IDLE && rang == true && /*incomingNumber.equals(ownerPhoneNumber)*/!incomingNumber.contains(ownerPhoneNumber)) {
    //            sMail=true;
                Toast.makeText(context, " Idle", Toast.LENGTH_SHORT).show();
                String mail = receiverEmail;
                String message = lat + "," + lon;
                String subject = sub;
                String SenderEmail = senderEmail;
                String SenderPass = senderPass;
                boolean it = false;
                it = isInternetConnected(context);
    //            it=true;
                if (it == true) {
                    //Send Mail
                    JavaMailAPI javaMailAPI = new JavaMailAPI(context, mail, subject, message, SenderEmail, SenderPass);
                    javaMailAPI.execute();
                    Toast.makeText(context, " true", Toast.LENGTH_SHORT).show();
                } else {
                    ///////////send SMS
                    Toast.makeText(context, " false", Toast.LENGTH_SHORT).show();
                    SmsManager mySmsManager = SmsManager.getDefault();
                    mySmsManager.sendTextMessage(ownerPhoneNumber, null, message, null, null);
                }

                rang = false;
            }
    }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 10:
//                configure_button();
//                break;
//            default:
//                break;
//        }
//    }



///////////////////////internet connection checking
    public boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = false;
        if (activeNetwork != null) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                status = "Wifi enabled";
                status=true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                status = "Mobile data enabled";
                status = true;
            }
        } else {
//            status = "No internet is available";
            status = false;
        }
        return status;
    }



}
