package com.hatn.learnarduino;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    private EditText edtEmailReg, edtPassReg;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        widget();

        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegisterUser();
            }
        });
    }

    private void widget(){
        edtEmailReg = (EditText) findViewById(R.id.edt_mail);
        edtPassReg = (EditText) findViewById(R.id.edt_pass_reg);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        loading = new ProgressDialog(this);
    }

    private void submitRegisterUser(){
        String email = edtEmailReg.getText().toString().trim();
        String pass = edtPassReg.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Please enter pass...", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setTitle("Create New Account");
            loading.setMessage("Please wait, while we are creating new account for you...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();

            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_id = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                                Map newPost = new HashMap();
                                newPost.put("ex", 0);

                                current_user_id.setValue(newPost);

                                SendUserToLoginActivity();
                                Toast.makeText(RegisterActivity.this, "Create account successfully...", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToLoginActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
