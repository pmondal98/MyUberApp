package com.example.myuberapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegister extends AppCompatActivity {

    private TextView tvdl,tvregister;
    private EditText etemail,etpassword;
    private Button btnlogin,btnregister;

    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;
    DatabaseReference ref;

    Driver driver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        mAuth=FirebaseAuth.getInstance();
        driver=new Driver();

        loadingbar=new ProgressDialog(this);

        tvdl=findViewById(R.id.tvdl);
        tvregister=findViewById(R.id.tvregister);
        etemail=findViewById(R.id.etemail);
        etpassword=findViewById(R.id.etpassword);
        btnlogin=findViewById(R.id.btnlogin);
        btnregister=findViewById(R.id.btnregister);

        btnregister.setVisibility(View.INVISIBLE);
        btnregister.setEnabled(false);

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnlogin.setVisibility(View.INVISIBLE);
                tvregister.setVisibility(View.INVISIBLE);

                tvdl.setText("REGISTER DRIVER");

                btnregister.setVisibility(View.VISIBLE);
                btnregister.setEnabled(true);
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email=etemail.getText().toString().trim();
                final String password=etpassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etemail.setError("Please enter email id");
                    etemail.requestFocus();
                }
                else if (password.isEmpty()) {
                    etpassword.setError("Please enter password");
                    etpassword.requestFocus();
                }
                else if (!(email.isEmpty() && password.isEmpty())) {

                    loadingbar.setTitle("Driver Registration");
                    loadingbar.setMessage("Please wait...while we register your details");
                    loadingbar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginRegister.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveData(email, password);

                                Toast.makeText(DriverLoginRegister.this, "Customer Successfully Registered...Login to proceed further", Toast.LENGTH_SHORT).show();

                                loadingbar.dismiss();

                                btnregister.setVisibility(View.INVISIBLE);
                                btnregister.setEnabled(false);

                                btnlogin.setVisibility(View.VISIBLE);
                                btnlogin.setEnabled(true);
                                tvregister.setVisibility(View.VISIBLE);
                                tvregister.setEnabled(true);
                                tvdl.setText("LOGIN DRIVER");

                            } else {
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Log.i("ERROR", e.getMessage());
                                Toast.makeText(DriverLoginRegister.this, "Customer Registration Failed....Please try again later", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(DriverLoginRegister.this, "Error Occured....please try again later", Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email=etemail.getText().toString().trim();
                final String password=etpassword.getText().toString().trim();

                if(email.isEmpty())
                {
                    etemail.setError("Please enter email id");
                    etemail.requestFocus();
                }
                else if(password.isEmpty())
                {
                    etpassword.setError("Please enter password");
                    etpassword.requestFocus();
                }
                else if(!(email.isEmpty() && password.isEmpty()))
                {
                    loadingbar.setTitle("Driver Login");
                    loadingbar.setMessage("Please wait...while we login your details");
                    loadingbar.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginRegister.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(DriverLoginRegister.this, "Login Unsuccessful....please try again later", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                //startActivity(new Intent(CustomerLoginRegister.this,CustomerMapActivity.class));
                                //finish();
                                //loadingbar.dismiss();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(DriverLoginRegister.this, "Error Occured....please try again later", Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }
        });
    }

    private void saveData(String email, String password) {

        driver.setEmail(email);
        driver.setPassword(password);

        ref= FirebaseDatabase.getInstance().getReference().child("DRIVERS").child(mAuth.getCurrentUser().getUid());

        ref.setValue(driver);

        FirebaseAuth.getInstance().signOut();

    }
}