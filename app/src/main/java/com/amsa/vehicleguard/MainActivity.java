package com.amsa.vehicleguard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.StrictMath.abs;
//import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final int REQUEST_CALL = 1;
    private EditText mEditTextNumber;
    private EditText et_email, et_phone;
    private Button set_bt,btn,stop_bt;

    private String callingNumber="01521579898";
    private String senderEmail="walenxet@gmail.com";
    private String senderPass="89133803amsa";
    private String receiverEmail="u1404092@student.cuet.ac.bd";
    private String sub="test";
    private String msg="mtest";
    ServiceReceiver serviceReceiver;

    private static final String TAG = "MainActivity";
    public static long firstTime;
    //    public static double v1=0.0;
    public final static long interval=100;///ms
    public static double valdiff=3.01;

    private SensorManager sensorManager;
    private Sensor mMagno,mGyro,mAcce,mPressure,mTemp,mLight,mHumi;
    TextView xMagnoValue, yMagnoValue, zMagnoValue,xGyroValue, yGyroValue, zGyroValue,xAcceValue, yAcceValue, zAcceValue,lightValue,pressureValue,humiValue,tempValue;

    private static boolean shake;
    public static boolean xMagno,yMagno,zMagno,xAcce,yAcce,zAcce,xGyro,yGyro,zGyro,temp,press,light,hum;
    public static double xMagnoV1,yMagnoV1,zMagnoV1,xAcceV1,yAcceV1,zAcceV1,xGyroV1,yGyroV1,zGyroV1,tempV1,pressV1,lightV1,humV1;
    private static String email,phoneNo,status="off";

    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /////permission for sending SMS
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        xMagnoValue = (TextView) findViewById(R.id.xMagnoValue);
        yMagnoValue = (TextView) findViewById(R.id.yMagnoValue);
        zMagnoValue = (TextView) findViewById(R.id.zMagnoValue);

        xAcceValue = (TextView) findViewById(R.id.xAcceValue);
        yAcceValue = (TextView) findViewById(R.id.yAcceValue);
        zAcceValue = (TextView) findViewById(R.id.zAcceValue);

        xGyroValue = (TextView) findViewById(R.id.xGyroValue);
        yGyroValue = (TextView) findViewById(R.id.yGyroValue);
        zGyroValue = (TextView) findViewById(R.id.zGyroValue);

        lightValue = (TextView) findViewById(R.id.lightValue);
        tempValue = (TextView) findViewById(R.id.tempValue);
        humiValue = (TextView) findViewById(R.id.humiValue);
        pressureValue = (TextView) findViewById(R.id.pressureValue);

        mEditTextNumber = findViewById(R.id.edit_text_number);

        firstTime = Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        Log.d(TAG, "onCreate: Initializing Sensor Services");

        et_email=(EditText)findViewById(R.id.et_Email);
        et_phone=(EditText)findViewById(R.id.et_Number);
        set_bt=(Button)findViewById(R.id.bt_Set);
        btn=(Button)findViewById(R.id.bt_Start);
        stop_bt=(Button)findViewById(R.id.bt_Stop);
        ///////////////////////////Database handling
        SharedPreferences sharedPreferences=getSharedPreferences("Email_Phone",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Email") && sharedPreferences.contains("Phone")) {
            phoneNo = sharedPreferences.getString("Phone", null);
            email = sharedPreferences.getString("Email", null);
            et_email.setHint(email);
            et_phone.setHint(phoneNo);
        }
        SharedPreferences sharedPreferences1=getSharedPreferences("Status",Context.MODE_PRIVATE);
        if(sharedPreferences1.contains("Status") ) {
            status = sharedPreferences1.getString("Status", null);
            if (status != null) {
                if (status.equals("on")) {
//                    start_bt.setClickable(false);
//                    stop_bt.setClickable(true);
                    btn.setText("Stop");
                    Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                } else {
//                    start_bt.setClickable(true);
//                    stop_bt.setClickable(false);
                    btn.setText("Start");
                    Toast.makeText(getApplicationContext(), "off"+status, Toast.LENGTH_SHORT).show();
                }
            }
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("Status",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(email==null || phoneNo==null){
                    Toast.makeText(getApplicationContext(), "Set Email and Phone no. first!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (status != null) {
                        if (status.equals("on")) {
                            editor.putString("Status", "off");
                            editor.commit();
                            btn.setText("Start");
                            status = "off";
                            Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_SHORT).show();
                        } else {
                            editor.putString("Status", "on");
                            editor.commit();
                            btn.setText("Stop");
                            status = "on";
                            Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                et_phone.onEditorAction(EditorInfo.IME_ACTION_DONE);


//                start_bt.setClickable(false);
//                stop_bt.setClickable(true);
//                Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_LONG).show();
            }
        });
//        stop_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences sharedPreferences=getSharedPreferences("Status",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.putString("Status","off");
//                editor.commit();
//                start_bt.setClickable(true);
//                stop_bt.setClickable(false);
//                Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_LONG).show();
//            }
//        });
        set_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=et_email.getText().toString();
                String takenPhoneNo=et_phone.getText().toString();
                if(takenPhoneNo.startsWith("+88")){
                    takenPhoneNo=takenPhoneNo.substring(3);
                }
                String PhoneNo=takenPhoneNo;
                if (Email.isEmpty() || PhoneNo.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Fill all filed", Toast.LENGTH_LONG).show();
                } else {
                    if(!(Email.contains(".") && Email.contains("@"))){Toast.makeText(getApplicationContext(), "Enter Valid Email", Toast.LENGTH_LONG).show();}
                    if (PhoneNo.length() < 11 || (PhoneNo.startsWith("+") && PhoneNo.length() < 14) || PhoneNo.length() >= 14 || PhoneNo.contains("!") || PhoneNo.contains("@") || PhoneNo.contains("#") || PhoneNo.contains("$") || PhoneNo.contains("%") || PhoneNo.contains("^") || PhoneNo.contains("&") || PhoneNo.contains("*") || PhoneNo.contains("(") || PhoneNo.contains(")") || PhoneNo.contains("_") || PhoneNo.contains("-") || PhoneNo.contains("/") || PhoneNo.contains("?") || PhoneNo.contains(">") || PhoneNo.contains("<") || PhoneNo.contains(",") || PhoneNo.contains(".") || PhoneNo.contains("\"") || PhoneNo.contains("|") || PhoneNo.contains("{") || PhoneNo.contains("}") || PhoneNo.contains(";") || PhoneNo.contains("]") || PhoneNo.contains("[") || PhoneNo.contains(":")) {
                        Toast.makeText(getApplicationContext(), "Enter Valid phone no", Toast.LENGTH_LONG).show();
                    }else {
                        SharedPreferences sharedPreferences=getSharedPreferences("Email_Phone", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Email",Email);
                        editor.putString("Phone",PhoneNo);
                        editor.commit();
                        email=Email;
                        phoneNo=PhoneNo;
//                        Toast.makeText(getApplicationContext(), "set"+phoneNo, Toast.LENGTH_LONG).show();
                        et_email.setText("");
                        et_phone.setText("");
                        et_email.setHint(email);
                        et_phone.setHint(phoneNo);
                    }
                }
            }
        });/////////////////////////////////////

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            xMagnoValue.setText("Magno Not Supported");
            yMagnoValue.setText("Magno Not Supported");
            zMagnoValue.setText("Magno Not Supported");
        }

        mAcce = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mAcce, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            xAcceValue.setText("Acce Not Supported");
            yAcceValue.setText("Acce Not Supported");
            zAcceValue.setText("Acce Not Supported");
        }

        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            xGyroValue.setText("Gyro Not Supported");
            yGyroValue.setText("Gyro Not Supported");
            zGyroValue.setText("Gyro Not Supported");
        }

        mTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            tempValue.setText("Temperature Not Supported");
        }

        mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            pressureValue.setText("Pressure Not Supported");
        }

        mHumi = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mHumi, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            humiValue.setText("Humidity Not Supported");
        }

        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            lightValue.setText("Light Not Supported");
        }

        ///////////////////////Gps
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                t.setText(" ");
//                t.append("\n Lat: " + location.getLongitude() + "\n Lon: " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);

//            float g=sensorEvent.values[0];
            xMagnoValue.setText("xMagnoValue: " + sensorEvent.values[0]);
            yMagnoValue.setText("yMagnoValue: " + sensorEvent.values[1]);
            zMagnoValue.setText("zMagnoValue: " + sensorEvent.values[2]);

            if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[0]);
                if((v2-xMagnoV1 >= valdiff) || (xMagnoV1-v2 >= valdiff)){
                    shake=true;
                    xMagnoV1=abs(sensorEvent.values[0]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                System.out.println("xxxx"+xMagnoV1);
            }/*else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[1]);
                if((v2-yMagnoV1 >= valdiff) || (yMagnoV1-v2 >= valdiff)){
//                    yMagno=true;
                    shake=true;
                    yMagnoV1=abs(sensorEvent.values[1]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[2]);
                if((v2-zMagnoV1 >= valdiff) || (zMagnoV1-v2 >= valdiff)){
//                    zMagno=true;
                    shake=true;
                    xMagnoV1=abs(sensorEvent.values[2]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }*/

        }else if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);

            xAcceValue.setText("xAcceValue: " + sensorEvent.values[0]);
            yAcceValue.setText("yAcceValue: " + sensorEvent.values[1]);
            zAcceValue.setText("zAcceValue: " + sensorEvent.values[2]);

            if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[0]);
                if((v2-xAcceV1 >= valdiff) || (xAcceV1-v2 >= valdiff)){
                    shake=true;
                    xAcceV1=abs(sensorEvent.values[0]);
                }
//                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//            }else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                v2 = abs(sensorEvent.values[1]);
                if((v2-yAcceV1 >= valdiff) || (yAcceV1-v2 >= valdiff)){
                    shake=true;
                    yAcceV1=abs(sensorEvent.values[1]);
                }
//                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//            }else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                v2 = abs(sensorEvent.values[2]);
                if((v2-zAcceV1 >= valdiff) || (zAcceV1-v2 >= valdiff)){
                    shake=true;
                    zAcceV1=abs(sensorEvent.values[2]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }


        }else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);

            xGyroValue.setText("xGyroValue: " + sensorEvent.values[0]);
            yGyroValue.setText("yGyroValue: " + sensorEvent.values[1]);
            zGyroValue.setText("zGyroValue: " + sensorEvent.values[2]);

            if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[0]);
                if((v2-xGyroV1 >= valdiff) || (xGyroV1-v2 >= valdiff)){
                    shake=true;
                    xGyroV1=abs(sensorEvent.values[0]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[1]);
                if((v2-yGyroV1 >= valdiff) || (yGyroV1-v2 >= valdiff)){
                    shake=true;
                    yGyroV1=abs(sensorEvent.values[1]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }else if(Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()))-firstTime >=interval){
                double v2 = abs(sensorEvent.values[2]);
                if((v2-zGyroV1 >= valdiff) || (zGyroV1-v2 >= valdiff)){
                    shake=true;
                    zGyroV1=abs(sensorEvent.values[2]);
                }
                firstTime=Long.parseLong(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            }

        }else if(sensor.getType() == Sensor.TYPE_LIGHT){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]);
            lightValue.setText("lightValue: " + sensorEvent.values[0]);

        }else if(sensor.getType() == Sensor.TYPE_PRESSURE){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]);
            pressureValue.setText("pressureValue: " + sensorEvent.values[0]);

        }else if(sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]);
            tempValue.setText("temperatureValue: " + sensorEvent.values[0]);

        }else if(sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0]);
            humiValue.setText("humidityValue: " + sensorEvent.values[0]);
        }


        if(shake==true && phoneNo!=null && status!="off"){
            makePhoneCall();
            Toast.makeText(MainActivity.this,"Shaken!!!"+shake,Toast.LENGTH_LONG).show();
            shake=false;
        }
    }


    private void makePhoneCall() {
//        String number = mEditTextNumber.getText().toString();
        String number = phoneNo;
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    ////////mailing

    public void sendMail() {

        String mail = receiverEmail;
        String message = msg;
        String subject = sub;
        String SenderEmail = senderEmail;
        String SenderPass = senderPass;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message,SenderEmail,SenderPass);

        javaMailAPI.execute();
        Toast.makeText(getApplicationContext()," called",Toast.LENGTH_LONG).show();
//

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


//    private boolean checkInternetConnection() {
//        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//        // ARE WE CONNECTED TO THE NET
//        if (conMgr.getActiveNetworkInfo() != null
//                && conMgr.getActiveNetworkInfo().isAvailable()
//                && conMgr.getActiveNetworkInfo().isConnected()) {
//
//            return true;
//
//            /* New Handler to start the Menu-Activity
//             * and close this Splash-Screen after some seconds.*/
////            new Handler().postDelayed(new Runnable() {
////                public void run() {
////                    /* Create an Intent that will start the Menu-Activity. */
////                    Intent mainIntent = new Intent(TheEvoStikLeagueActivity.this, IntroActivity.class);
////                    TheEvoStikLeagueActivity.this.startActivity(mainIntent);
////                    TheEvoStikLeagueActivity.this.finish();
////                }
////            }, SPLASH_DISPLAY_LENGHT);
//        } else {
//            return false;
//
////            Intent connectionIntent = new Intent(TheEvoStikLeagueActivity.this, HomeActivity.class);
////            TheEvoStikLeagueActivity.this.startActivity(connectionIntent);
////            TheEvoStikLeagueActivity.this.finish();
//        }
public boolean isNetworkConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
}

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit!!")
                .setMessage("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel",null)
                .setCancelable(false);
        AlertDialog alert=builder.create();
        alert.show();
    }


    public String getEmail() {
        return email;
    }

    public String getPhoneNo() { return phoneNo; }

    public String getStatus() {
        return status;
    }
}