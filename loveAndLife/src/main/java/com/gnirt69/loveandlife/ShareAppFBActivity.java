package com.gnirt69.loveandlife;/**
 * Created by NgocTri on 9/7/2015.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.model.Common;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ShareAppFBActivity extends Activity {
    private Button btnShareLink;
    ProgressDialog mProgressDialog;
    private LoginButton loginBtn;
    // private Button postImageBtn;
    private DatabaseHelper dbHelper = null;
    private TextView userName;
    private UiLifecycleHelper uiHelper;

    private static final List<String> PERMISSIONS = Arrays
            .asList("publish_actions");

    private String link = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_app_fb_activity);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        userName = (TextView) findViewById(R.id.txt_user_name);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Chia sẻ ứng dụng");
        loginBtn = (LoginButton) findViewById(R.id.fb_login_button_2);
        loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
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
        btnShareLink = (Button) findViewById(R.id.btnShareAppFb);
        btnShareLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //shareLink(link);
                shareLinkAppToFacebook(Common.LINK_APP_IN_PLAY_STORE);
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
        btnShareLink.setEnabled(isEnabled);
    }


    /**
     * Share facebook using facebook SDK ver 2.2
     */
    public void shareLink(String link) {
        if (checkPermissions()) {
            Bundle params = new Bundle();
            //params.putString("caption", "caption");
            params.putString("message", "message");
            //params.putString("link", "link_url");
            //params.putString("picture", "picture_url");
            //params.putString("link", link);
            mProgressDialog = ProgressDialog.show(ShareAppFBActivity.this,
                    "Đang xử lý...", "Xin chờ...", true);
            Request request = new Request(Session.getActiveSession(), "me/feed", params, HttpMethod.POST);
            request.setCallback(new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if (response.getError() == null) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ShareAppFBActivity.this,
                                "Share link thành công!",
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

    /**
     * Share facebook using facebook SDK ver 3.0
     */
    public void shareLinkAppToFacebook(String link) {
        if (checkPermissions()) {
            mProgressDialog = ProgressDialog.show(ShareAppFBActivity.this,
                    "Đang xử lý...", "Xin chờ...", true);
            Request request = Request.newStatusUpdateRequest(
                    Session.getActiveSession(), link,
                    new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            if (response.getError() == null) {
                                mProgressDialog.dismiss();
                                Toast.makeText(ShareAppFBActivity.this,
                                        "Chia sẻ link thành công!",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
            Bundle params = request.getParameters();
            //params.putString("caption", "caption");
            //params.putString("message", "message");
            //params.putString("link", "link_url");
            //params.putString("picture", "picture_url");
            params.putString("link", link);
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
        if (s != null)
            s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                    this, PERMISSIONS));
    }

    @Override
    public void onResume() {
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