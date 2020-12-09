package com.amsa.vehicleguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    private EditText et_email, et_phone;
    private Button set_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        et_email=(EditText)findViewById(R.id.et_Email);
        et_phone=(EditText)findViewById(R.id.et_Number);
        set_bt=(Button)findViewById(R.id.bt_Set);

        SharedPreferences sharedPreferences=getSharedPreferences("Email_Phone",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Email") && sharedPreferences.contains("Phone")) {
            String phone = sharedPreferences.getString("phone", null);
            String email = sharedPreferences.getString("Email", null);
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            intent.putExtra("Email", email);//////sending profileId to home
            intent.putExtra("Phone", phone);//////sending profileId to ho
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        set_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=et_email.getText().toString();
                String takenPhoneNo=et_phone.getText().toString();
                if(takenPhoneNo.startsWith("+88")){
                    takenPhoneNo=takenPhoneNo.substring(3);
                }
                final String phoneNo=takenPhoneNo;
                if (email.isEmpty() || phoneNo.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Fill all filed", Toast.LENGTH_LONG).show();
                } else {
                    if (phoneNo.length() < 11 || (phoneNo.startsWith("+") && phoneNo.length() < 14) || phoneNo.length() >= 14 || phoneNo.contains("!") || phoneNo.contains("@") || phoneNo.contains("#") || phoneNo.contains("$") || phoneNo.contains("%") || phoneNo.contains("^") || phoneNo.contains("&") || phoneNo.contains("*") || phoneNo.contains("(") || phoneNo.contains(")") || phoneNo.contains("_") || phoneNo.contains("-") || phoneNo.contains("/") || phoneNo.contains("?") || phoneNo.contains(">") || phoneNo.contains("<") || phoneNo.contains(",") || phoneNo.contains(".") || phoneNo.contains("\"") || phoneNo.contains("|") || phoneNo.contains("{") || phoneNo.contains("}") || phoneNo.contains(";") || phoneNo.contains("]") || phoneNo.contains("[") || phoneNo.contains(":")) {
                        Toast.makeText(getApplicationContext(), "Enter Valid phone no", Toast.LENGTH_LONG).show();
                    }else {
                        SharedPreferences sharedPreferences=getSharedPreferences("Email_Phone", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Email",email);
                        editor.putString("Phone",phoneNo);
                        editor.commit();
                        et_email.setHint(email);
                        et_phone.setHint(phoneNo);
                    }
                }
            }
        });


    }
}
