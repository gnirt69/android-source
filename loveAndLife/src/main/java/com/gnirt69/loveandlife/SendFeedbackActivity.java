package com.gnirt69.loveandlife;/**
 * Created by NgocTri on 9/7/2015.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gnirt69.loveandlife.model.Common;

import java.util.List;

public class SendFeedbackActivity extends Activity {
    private EditText txtFeedbackContent;
    private Button btnSendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_feedback_activity);
        txtFeedbackContent = (EditText) findViewById(R.id.txtFeedbackContent);
        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Góp ý cho chúng tôi");
    }

    public void btnSendFeedBack_Click(View view) {
        if (this.isValidContent(txtFeedbackContent.getText().toString())) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{Common.MY_EMAIL_ADDRESS});
            // email.putExtra(Intent.EXTRA_CC, new String[]{ to});
            // email.putExtra(Intent.EXTRA_BCC, new String[]{to});
            email.putExtra(Intent.EXTRA_SUBJECT, "[Love&Life] Ý kiến phản hồi");
            email.putExtra(Intent.EXTRA_TEXT, txtFeedbackContent.getText().toString());
            email.setType("message/rfc822");

            //Check gmail app install or not
            final PackageManager pm = this.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(email, 0);
            ResolveInfo best = null;
            for(final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                    best = info;
                }
            if (best != null) {
                Common.writeLogCat("SENDFEEDBACK", "Gmail app installed");
                email.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                this.startActivityForResult(email, 1);
            } else {
                Common.writeLogCat("SENDFEEDBACK", "Gmail app do not installed");
                Intent i = Intent.createChooser(email, "Gửi email");
                startActivityForResult(i, 1);
            }

        } else {
            txtFeedbackContent.setError(Html.fromHtml("Hãy nhập nội dung góp ý!"));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Common.writeLogCat("Email send result ", String.valueOf(requestCode));
        Toast.makeText(SendFeedbackActivity.this,
                "Cảm ơn bạn đã góp ý cho sản phẩm!",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    public boolean isValidContent(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Press back button in action bar
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                return true;
            default:
                return true;
        }
    }
}
