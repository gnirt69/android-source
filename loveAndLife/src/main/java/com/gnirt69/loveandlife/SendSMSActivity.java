package com.gnirt69.loveandlife;/**
 * Created by NgocTri on 9/13/2015.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.gnirt69.loveandlife.model.Common;

import java.util.ArrayList;

public class SendSMSActivity extends Activity implements View.OnClickListener {
    private Button btnOpenAddress;
    private Button btnSendSms;
    private EditText txtPhoneNo;
    private EditText txtSmsContent;
    private String quoteContent = null;
    private ProgressDialog mProgressDialog;
    private RadioButton radBtCoDau;
    private RadioButton radKoDau;
    private RadioGroup radGrpSendSms;
    private BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    private boolean sendErrFlag = false;
    private int messageCount = 0;
    private int receiveResultCnt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_sms_activity);
        btnOpenAddress = (Button)findViewById(R.id.btnOpenAddress);
        btnOpenAddress.setOnClickListener(this);
        btnSendSms = (Button)findViewById(R.id.btnSendSms);
        txtPhoneNo = (EditText)findViewById(R.id.txtPhoneNumber);
        txtSmsContent = (EditText)findViewById(R.id.txtSmsContent);
        radBtCoDau = (RadioButton)findViewById(R.id.radCoDau);
        radKoDau = (RadioButton)findViewById(R.id.radKhongDau);
        radGrpSendSms = (RadioGroup)findViewById(R.id.radGrpSendSms);
        radGrpSendSms.setOnCheckedChangeListener(radioChangedListener);
        ActionBar bar = getActionBar();
        //bar.setHomeAsUpIndicator(R.drawable.back);
        bar.setDisplayHomeAsUpEnabled(true);
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        quoteContent = getIntent().getExtras().getString("QuoteContent");
        txtSmsContent.setText(quoteContent);


    }

    public void btnSendSms_click(View v) {
        TaskSendSms threadSendSms = new TaskSendSms();
        threadSendSms.execute(txtPhoneNo.getText().toString(), txtSmsContent.getText().toString());
    }

    public void sendSMS(String phoneNumber,String message) {
        SmsManager smsManager = SmsManager.getDefault();


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        messageCount = parts.size();

        Common.writeLogCat("SendSMSActivity", "Message Count: " + messageCount);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < messageCount; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
    }
    @Override
    public void onResume() {

        super.onResume();
        // ---when the SMS has been sent---
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Common.writeLogCat("SendSMSActivity","BroadcastReceiver:result="+getResultCode());

                receiveResultCnt++;
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Only show toast message send success in latest received time
                        if( (receiveResultCnt == messageCount) && (false == sendErrFlag)) {
                            Toast.makeText(getBaseContext(), "SMS sent",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        sendErrFlag = true;
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        sendErrFlag =true;
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        sendErrFlag = true;
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        sendErrFlag = true;
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "N/A", Toast.LENGTH_SHORT).show();
                        sendErrFlag = true;
                        break;
                }
                if(receiveResultCnt == messageCount) {
                    receiveResultCnt = 0;
                    sendErrFlag = false;
                }
            }
        };

        // ---when the SMS has been delivered---
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        //sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }
    private OnCheckedChangeListener radioChangedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            boolean checked = ((RadioButton) findViewById(checkedId)).isChecked();
            switch (checkedId) {
                case R.id.radCoDau:
                    txtSmsContent.setText(quoteContent);
                    break;
                case R.id.radKhongDau:
                    txtSmsContent.setText(truncateVietnameseDigit(quoteContent));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            txtPhoneNo.setText(cursor.getString(column));
        }
    }

    public class TaskSendSms extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(SendSMSActivity.this,
                    "Đang gửi tin nhắn...", "Xin đợi...", true);
        }

        @Override
        protected Void doInBackground(String... params) {
            sendSMS(params[0],params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Press back button in action bar
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                return true;
            default:
                break;
        }

        return true;
    }

    public static String truncateVietnameseDigit(String input) {

        if (input == "") {
            return "";
        }

        input = input.replace("à", "a");
        input = input.replace("ạ", "a");
        input = input.replace("ả", "a");
        input = input.replace("á", "a");
        input = input.replace("ã", "a");

        input = input.replace("ă", "a");
        input = input.replace("ằ", "a");
        input = input.replace("ắ", "a");
        input = input.replace("ặ", "a");
        input = input.replace("ẳ", "a");
        input = input.replace("ẵ", "a");

        input = input.replace("ầ", "a");
        input = input.replace("ậ", "a");
        input = input.replace("ẫ", "a");
        input = input.replace("ấ", "a");
        input = input.replace("ẩ", "a");
        input = input.replace("â", "a");

        input = input.replace("đ", "d");

        input = input.replace("è", "e");
        input = input.replace("ẹ", "e");
        input = input.replace("ẽ", "e");
        input = input.replace("é", "e");
        input = input.replace("ẻ", "e");

        input = input.replace("ề", "e");
        input = input.replace("ệ", "e");
        input = input.replace("ễ", "e");
        input = input.replace("ế", "e");
        input = input.replace("ể", "e");
        input = input.replace("ê", "e");

        input = input.replace("ì", "i");
        input = input.replace("ị", "i");
        input = input.replace("ĩ", "i");
        input = input.replace("ỉ", "i");
        input = input.replace("í", "i");

        input = input.replace("ò", "o");
        input = input.replace("ọ", "o");
        input = input.replace("õ", "o");
        input = input.replace("ó", "o");
        input = input.replace("ỏ", "o");

        input = input.replace("ô", "o");
        input = input.replace("ồ", "o");
        input = input.replace("ộ", "o");
        input = input.replace("ỗ", "o");
        input = input.replace("ổ", "o");
        input = input.replace("ố", "o");

        input = input.replace("ơ", "o");
        input = input.replace("ờ", "o");
        input = input.replace("ợ", "o");
        input = input.replace("ỡ", "o");
        input = input.replace("ớ", "o");
        input = input.replace("ở", "o");

        input = input.replace("ù", "u");
        input = input.replace("ụ", "u");
        input = input.replace("ũ", "u");
        input = input.replace("ú", "u");
        input = input.replace("ủ", "u");

        input = input.replace("ừ", "u");
        input = input.replace("ự", "u");
        input = input.replace("ữ", "u");
        input = input.replace("ứ", "u");
        input = input.replace("ử", "u");
        input = input.replace("ư", "u");

        input = input.replace("ỳ", "y");
        input = input.replace("ỵ", "y");
        input = input.replace("ỹ", "y");
        input = input.replace("ỷ", "y");
        input = input.replace("ý", "y");

        input = input.replace("À", "A");
        input = input.replace("Ạ", "A");
        input = input.replace("Ả", "A");
        input = input.replace("Á", "A");
        input = input.replace("Ã", "A");

        input = input.replace("Ă", "A");
        input = input.replace("Ằ", "A");
        input = input.replace("Á", "A");
        input = input.replace("Ặ", "A");
        input = input.replace("Ả", "A");
        input = input.replace("Ẵ", "A");

        input = input.replace("Ầ", "A");
        input = input.replace("Ậ", "A");
        input = input.replace("Ẫ", "A");
        input = input.replace("Ấ", "A");
        input = input.replace("Ẩ", "A");
        input = input.replace("Â", "A");

        input = input.replace("Đ", "D");

        input = input.replace("È", "E");
        input = input.replace("Ẹ", "E");
        input = input.replace("Ẽ", "E");
        input = input.replace("É", "E");
        input = input.replace("Ẻ", "E");

        input = input.replace("Ề", "E");
        input = input.replace("Ệ", "E");
        input = input.replace("Ễ", "E");
        input = input.replace("Ế", "E");
        input = input.replace("Ể", "E");
        input = input.replace("Ê", "E");

        input = input.replace("Ì", "I");
        input = input.replace("Ị", "I");
        input = input.replace("Ĩ", "I");
        input = input.replace("Ỉ", "I");
        input = input.replace("Í", "I");

        input = input.replace("Ò", "O");
        input = input.replace("Ọ", "O");
        input = input.replace("Õ", "O");
        input = input.replace("Ó", "O");
        input = input.replace("Ỏ", "O");

        input = input.replace("Ô", "O");
        input = input.replace("Ồ", "O");
        input = input.replace("Ộ", "O");
        input = input.replace("Ỗ", "O");
        input = input.replace("Ổ", "O");
        input = input.replace("Ố", "O");

        input = input.replace("Ơ", "O");
        input = input.replace("Ờ", "O");
        input = input.replace("Ợ", "O");
        input = input.replace("Ỡ", "O");
        input = input.replace("Ớ", "O");
        input = input.replace("Ở", "O");

        input = input.replace("Ù", "U");
        input = input.replace("Ụ", "U");
        input = input.replace("Ũ", "U");
        input = input.replace("Ú", "U");
        input = input.replace("Ủ", "U");

        input = input.replace("Ừ", "U");
        input = input.replace("Ự", "U");
        input = input.replace("Ữ", "U");
        input = input.replace("Ứ", "U");
        input = input.replace("Ử", "U");
        input = input.replace("Ư", "U");

        input = input.replace("Ỳ", "Y");
        input = input.replace("Ỵ", "Y");
        input = input.replace("Ỹ", "Y");
        input = input.replace("Ỷ", "Y");
        input = input.replace("Ý", "Y");
        return input;
    }
}
