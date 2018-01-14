package com.example.user.check_mate.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText userNameText;
    EditText emailAddressText;
    EditText passwordText;
    EditText confirmPasswordText;
    Button registerButton;
    BackEndFunc backEndFunc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backEndFunc= FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getWindow().setBackgroundDrawableResource(R.drawable.background);
        userNameText=findViewById(R.id.userNameTextView);
        emailAddressText=findViewById(R.id.emailAddressTextView);
        passwordText=findViewById(R.id.passwordTextView);
        confirmPasswordText=findViewById(R.id.confirmPasswordTextView);
        registerButton=findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=userNameText.getText().toString();
                String emailAddress=emailAddressText.getText().toString();
                String password=passwordText.getText().toString();
                String confirmPassword=confirmPasswordText.getText().toString();

                if(userName==null || userName.length()==0)inputWarningDialog("user name cannot be empty");
                else if(!emailValidator(emailAddress))inputWarningDialog("invalid email address!");
                else if(password==null||password.length()<8)inputWarningDialog("password must contain 8 characters");
                else if(confirmPassword==null|| confirmPassword.length()==0)inputWarningDialog("cannot leave confirm password empty!");
                else if(!confirmPassword.equals(password))inputWarningDialog("your passwords don't match");
                else
                {
                    //Person person=new Person(userName,password,emailAddress);
                    try {
                        //backEndFunc.addPerson(person);
                        Intent intent=new Intent(RegisterActivity.this,GetStartedActivity.class);
                        intent.putExtra("userName",userName);
                        finish();
                        startActivity(intent);
                    } catch (Exception e) {
                        inputWarningDialog(e.getMessage());
                    }
                }
            }
        });


    }
    public void inputWarningDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Invalid input!");
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
