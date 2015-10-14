package com.gnirt69.loveandlife;

import java.sql.SQLException;
import java.util.List;

import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.model.Common;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.TextView;

public class ShareEmailActivity extends Activity {

    private DatabaseHelper dbHelper = null;
    private EditText txtSendAddress = null;
    private String[] storyData = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_email_activity);
        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Chia sẻ qua email");
        txtSendAddress = (EditText) findViewById(R.id.txtSendAddress);
        int storyId = getIntent().getExtras().getInt("StoryId");
        dbHelper = new DatabaseHelper(getApplicationContext());
        storyData = dbHelper.getNameAndContentAndAuthorById(storyId);
        dbHelper.close();
        if (null != storyData) {

        } else {
            Common.writeLogCat("SHARE EMAIL ", "Get info ERR");
        }
    }

    public void btnSendEmail_Click(View view) {
        if (txtSendAddress.getText().toString().trim().length() < 5
                || !this.isValidEmail(txtSendAddress.getText().toString())) {

            txtSendAddress.setError(Html.fromHtml("Email không hợp lệ!"));

            return;
        }
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{txtSendAddress
                .getText().toString()});
        // email.putExtra(Intent.EXTRA_CC, new String[]{ to});
        // email.putExtra(Intent.EXTRA_BCC, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, "[Love&Life]" + storyData[0]);
        email.putExtra(Intent.EXTRA_TEXT, storyData[0] + "\n\n" + storyData[1]
                + "\n---" + storyData[2] + "---" + Common.FOOTER_SHARE);

        email.setType("message/rfc822");
        //Intent i = Intent.createChooser(email, "Gửi");
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Common.writeLogCat("Email send result ", String.valueOf(requestCode));
        finish();
    }

    /**
     * check validate email input
     *
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
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
            case R.id.menuItemShareStoryToFace:
                break;
            default:
                break;
        }

        return true;
    }
}
