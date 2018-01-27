package com.hionthefly.user.check_mate.controller;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hionthefly.user.check_mate.R;
import com.hionthefly.user.check_mate.model.backend.BackEndFunc;
import com.hionthefly.user.check_mate.model.backend.DataSourceType;
import com.hionthefly.user.check_mate.model.backend.FactoryMethod;
import com.hionthefly.user.check_mate.model.entities.Message;

public class ContactActivity extends AppCompatActivity {

    Spinner spinnerOptions;
    EditText editTextMessage;
    Button buttonSend;
    BackEndFunc backEndFuncForFirebase= FactoryMethod.getBackEndFunc(DataSourceType.DATA_INTERNET);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getSupportActionBar().setTitle(R.string.contact_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        spinnerOptions=(Spinner)findViewById(R.id.spinnerOptions);
        editTextMessage=(EditText)findViewById(R.id.editTextMessage);
        buttonSend=(Button)findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m=editTextMessage.getText().toString();
                if(m==null || m.length()<=0)
                {
                    inputWarningDialog(getString(R.string.cant_leave_message_empty));
                    return;
                }
                else {
                    Message message=new Message();
                    message.setUserId(Me.ME.get_id());
                    message.setMessage(m);
                    String spinnerSelection=spinnerOptions.getSelectedItem().toString();
                    switch (spinnerSelection){

                        case "דוח על שגיאה":
                        {
                            spinnerSelection="Report Error";
                            break;
                        }
                        case "יש לך ראיונות לשתף":
                        {
                            spinnerSelection="Have any ideas to share";
                            break;
                        }
                        case "סתם שלום":
                        {
                            spinnerSelection="Just say hi";
                            break;
                        }
                    }
                    backEndFuncForFirebase.sendMessage(message,spinnerSelection);
                    finish();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void inputWarningDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
        builder.setTitle(getString(R.string.invalid_input)).setIcon(R.drawable.ic_warning);
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
