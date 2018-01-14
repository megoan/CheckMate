package com.example.user.check_mate.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.check_mate.R;
import com.example.user.check_mate.model.backend.BackEndFunc;
import com.example.user.check_mate.model.backend.FactoryMethod;
import com.example.user.check_mate.model.backend.SelectedDataSource;
import com.example.user.check_mate.model.entities.Person;

public class LoginActivity extends AppCompatActivity {
    EditText userNameEditText;
    EditText passwordEditText;
    BackEndFunc backEndFunc;
    Person person=new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        backEndFunc= FactoryMethod.getBackEndFunc(SelectedDataSource.dataSourceType);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // getWindow().setBackgroundDrawableResource(R.drawable.background);
        userNameEditText=findViewById(R.id.userNameTextView);
        passwordEditText=findViewById(R.id.passwordTextView);
        Button logInButton=findViewById(R.id.loginButton);
        TextView registerTextView=findViewById(R.id.registerTextView);
        TextView getPasswordTextView=findViewById(R.id.getPasswordTextView);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=userNameEditText.getText().toString();
                String password=passwordEditText.getText().toString();
                if(userName==null || userName.length()==0)inputWarningDialog("can't leave user name empty!");
                else if(password==null || password.length()==0)inputWarningDialog("can't leave password empty");
                else{
                    Person person =backEndFunc.getPerson(userName);
                    if(LoginActivity.this.person ==null)inputWarningDialog("this user name does not exist!");
                    else
                    {
                        if(password.equals(true))
                        {
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("userName",userName);
                            finish();
                            startActivity(intent);
                        }
                        else {
                            inputWarningDialog("wrong password!");
                        }
                    }

                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void inputWarningDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
}
