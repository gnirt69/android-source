package com.gnirt69.loveandlife;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.model.Common;

public class ShareFacebookActivity extends Activity {

    ProgressDialog mProgressDialog;
    private LoginButton loginBtn;
    // private Button postImageBtn;
    private Button updateStatusBtn;
    private DatabaseHelper dbHelper = null;
    private TextView userName;
    private UiLifecycleHelper uiHelper;

    private static final List<String> PERMISSIONS = Arrays
            .asList("publish_actions");

    private String message = null;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.share_facebook_actitivy);

        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Chia sẻ lên Facebook");
        int storyId = getIntent().getExtras().getInt("StoryId");
        if(-1 == storyId) { //From QuoteListActivity
            message = getIntent().getExtras().getString("QuoteContent");
        } else { //From StoryDetailActivity
            String[] storyData = null;
            dbHelper = new DatabaseHelper(getApplicationContext());
            storyData = dbHelper.getNameAndContentAndAuthorById(storyId);
            if (null != storyData) {
                message = storyData[0] + "\n\n" + storyData[1] + "\n\n"
                        + Common.FOOTER_SHARE;
            } else {
                Common.writeLogCat("SHARE FACEBOOK ", "Get info ERR");
            }
        }
        userName = (TextView) findViewById(R.id.user_name);
        //Text of login button to when login and logout is set in String.xml
        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    userName.setText("Xin chào, " + user.getName());
                    //Request permission post to facebook
                    if (false == checkPermissions()) {
                        requestPermissions();
                    }
                } else {
                    userName.setText("Bạn chưa đăng nhập!");
                }
            }
        });

		/*
         * postImageBtn = (Button) findViewById(R.id.post_image);
		 * postImageBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { postImage(); } });
		 */

        updateStatusBtn = (Button) findViewById(R.id.update_status);
        updateStatusBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                postStatusMessage();
            }
        });

        buttonsEnabled(false);
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                buttonsEnabled(true);
                Common.writeLogCat("FacebookShare", "Facebook session opened");
            } else if (state.isClosed()) {
                buttonsEnabled(false);
                Common.writeLogCat("FacebookShare", "Facebook session closed");
            }
        }
    };

    public void buttonsEnabled(boolean isEnabled) {
        // postImageBtn.setEnabled(isEnabled);
        updateStatusBtn.setEnabled(isEnabled);
    }

    public void postImage() {
        if (checkPermissions()) {
            mProgressDialog = ProgressDialog.show(ShareFacebookActivity.this,
                    "Đang xử lý...", "Xin chờ...", true);
            Bitmap img = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
            Request uploadRequest = Request.newUploadPhotoRequest(
                    Session.getActiveSession(), img, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ShareFacebookActivity.this,
                                    "Đăng ảnh thành công", Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
            uploadRequest.executeAsync();
        } else {
            requestPermissions();
        }
    }

    public void postStatusMessage() {
        Common.writeLogCat("FacebookShare", "postStatusMessage");
        if (checkPermissions()) {
            mProgressDialog = ProgressDialog.show(ShareFacebookActivity.this,
                    "Đang xử lý...", "Xin chờ...", true);
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), message,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            if (response.getError() == null) {
                                mProgressDialog.dismiss();
                                Toast.makeText(ShareFacebookActivity.this,
                                        "Đã đăng bài lên Facebook thành công!",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
            request.executeAsync();
        } else {
            requestPermissions();
        }
    }

    public boolean checkPermissions() {
        Session s = Session.getActiveSession();
        boolean bRet = false;
        if (s != null) {
            bRet = s.getPermissions().contains("publish_actions");
        } else {
            bRet = false;
        }
        Common.writeLogCat("FacebookShare", "CheckPermissions return " + bRet);
        return bRet;
    }

    public void requestPermissions() {
        Common.writeLogCat("FacebookShare", "requestPermissions");
        Session s = Session.getActiveSession();
        if (s != null) {
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                    this, PERMISSIONS));
        }else{
            Common.writeLogCat("FacebookShare", "getActiveSession null");
        }
    }

    @Override
    public void onResume() {
        Common.writeLogCat("FacebookShare", "onResume: LoginText=" + loginBtn.getText().toString());
        super.onResume();
        uiHelper.onResume();
        buttonsEnabled(Session.getActiveSession().isOpened());
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
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