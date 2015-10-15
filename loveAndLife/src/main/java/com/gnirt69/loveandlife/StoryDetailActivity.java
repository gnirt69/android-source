package com.gnirt69.loveandlife;

import java.io.FileOutputStream;

import com.gnirt69.loveandlife.bll.DatabaseHelper;
import com.gnirt69.loveandlife.model.Common;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class StoryDetailActivity extends Activity {

    private DatabaseHelper dbHelper = null;
    private TextView tvStoryName;
    private TextView tvStoryContent;
    private TextView tvAuthor;
    private int storyId;
    private NumberPicker numPickerFontSize = null;
    private RadioGroup radioGroup = null;
    private RadioButton radioBtColorBlack = null;
    //private RadioButton radioBtColorBlue = null;
    private RadioButton radioBtColorRed = null;
    private RadioButton radioBtColorDefault = null;
    //private Button btnCloseDialog = null;
    private Dialog dialog = null;
    static final String COLOR_DEFAULT = "#006386";
    static final String COLOR_RED = "#FA0259";
    static final String COLOR_BLUE = "#0213F7";
    static final String COLOR_BLACK = "#000000";
    private boolean isFavouriteStory = false;
    private Menu menu;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_detail_activity);
        dbHelper = new DatabaseHelper(getApplicationContext());
        // Init view
        tvStoryName = (TextView) findViewById(R.id.tv_storyDetail_name);
        tvStoryContent = (TextView) findViewById(R.id.tv_storyDetail_content);
        tvAuthor = (TextView) findViewById(R.id.tv_storyDetail_author);
        ActionBar bar = getActionBar();
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }
        // Show back button on action bar
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getIntent().getExtras().getString("StoryName"));
        String searchResult[] = null;
        storyId = getIntent().getExtras().getInt("StoryId");
        dialog = new Dialog(StoryDetailActivity.this);
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                saveValueSetting(Common.configValueFontColor,
                        Common.configValueFontSize);
            }
        });
        applySetingValue();
        searchResult = dbHelper.getNameAndContentAndAuthorById(storyId);
        if (null != searchResult) {
            tvStoryName.setText(searchResult[0]);
            tvStoryContent.setText(searchResult[1]);
            if (!"Sưu tầm".equals(searchResult[2])) {
                tvAuthor.setText("--" + searchResult[2] + "--");
            } else {
                tvAuthor.setText("");
            }
        }
        //Check favourite story by id
        isFavouriteStory = dbHelper.isFavouriteStory(storyId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_detail_story_menu, menu);
        this.menu = menu;
        if (true == isFavouriteStory) {
            setOptionTitle(R.id.menuItemFavourite, "Bỏ yêu thích");
        } else {
            setOptionTitle(R.id.menuItemFavourite, "Thêm vào yêu thích");
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
            case R.id.menuItemShareStoryToFace:
                Intent iFace = new Intent(getApplicationContext(),
                        ShareFacebookActivity.class);
                iFace.putExtra("StoryId", storyId);

                startActivityForResult(iFace, 1);
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.menuItemShareStoryToEmail:
                Intent iEmail = new Intent(getApplicationContext(),
                        ShareEmailActivity.class);
                iEmail.putExtra("StoryId", storyId);

                startActivityForResult(iEmail, 1);
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.menuItemSetting:
                clickItemMenuSetting();
                break;
            case R.id.menuItemFavourite:
                //Add favourite
                if (false == isFavouriteStory) {

                    dbHelper.addFavouriteStory(storyId);
                    Toast.makeText(StoryDetailActivity.this, "Đã thêm vào danh mục yêu thích", Toast.LENGTH_SHORT).show();
                    setOptionTitle(R.id.menuItemFavourite, "Bỏ yêu thích");
                } else { //Remove favourite
                    dbHelper.deleteFavouriteStory(storyId);
                    Toast.makeText(StoryDetailActivity.this, "Đã xóa khỏi danh mục yêu thích", Toast.LENGTH_SHORT).show();
                    setOptionTitle(R.id.menuItemFavourite, "Thêm vào yêu thích");
                }
                isFavouriteStory = !isFavouriteStory;
                break;
            default:
                break;
        }

        return true;
    }

    private void clickItemMenuSetting() {
        Common.writeLogCat("OPEN SETING DIALOG", "COLOR = "
                + Common.configValueFontColor + ",FONTSIZE="
                + Common.configValueFontSize);
        // final Dialog dialog = new Dialog(StoryDetailActivity.this);
        dialog.setContentView(R.layout.display_setting_dialog);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setGravity(Gravity.CENTER);
        title.setText("Cài đặt hiển thị");
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);
        title.setBackgroundColor(Color.parseColor("#f2822c"));
        numPickerFontSize = (NumberPicker) dialog
                .findViewById(R.id.numberPickerFontSize);
        //Hide virtual keyboard focus to number picker
        numPickerFontSize.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        radioGroup = (RadioGroup) dialog.findViewById(R.id.radGrpFontColor);
        radioBtColorBlack = (RadioButton) dialog
                .findViewById(R.id.radioFontColorBlack);
//        radioBtColorBlue = (RadioButton) dialog
//                .findViewById(R.id.radioFontColorBlue);
        radioBtColorRed = (RadioButton) dialog
                .findViewById(R.id.radioFontColorRed);
        radioBtColorDefault = (RadioButton) dialog.findViewById(R.id.radioFontColorDefault);
        if (COLOR_DEFAULT.equals(Common.configValueFontColor)) {
            radioBtColorDefault.setChecked(true);
        } else if (COLOR_RED.equals(Common.configValueFontColor)) {
            radioBtColorRed.setChecked(true);
        } else if (COLOR_BLACK.equals(Common.configValueFontColor)) {
            radioBtColorBlack.setChecked(true);
        } else {
            //radioBtColorBlue.setChecked(true);
        }
        //btnCloseDialog = (Button) dialog.findViewById(R.id.btnCloseDialog);
        numPickerFontSize.setMaxValue(40);
        numPickerFontSize.setMinValue(15);
        numPickerFontSize.setValue(Common.configValueFontSize);
        numPickerFontSize.setWrapSelectorWheel(true);
        numPickerFontSize.setFocusable(false);
        numPickerFontSize
                .setOnValueChangedListener(new OnValueChangeListener() {

                    @Override
                    public void onValueChange(NumberPicker picker,
                                              int oldVal, int newVal) {
                        tvStoryContent.setTextSize(newVal);
                        tvAuthor.setTextSize(newVal);
                        tvStoryName.setTextSize(newVal + 4);
                        Common.configValueFontSize = newVal;
                        // saveValueSeting(Common.configValueFontColor,
                        // newVal);
                    }
                });

//        btnCloseDialog.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        radioGroup
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        boolean checked = ((RadioButton) dialog
                                .findViewById(checkedId)).isChecked();
                        String currentColor = null;
                        switch (checkedId) {
                            case R.id.radioFontColorDefault:
                                if (true == checked) {
                                    setTextViewColor(COLOR_DEFAULT);
                                    currentColor = COLOR_DEFAULT;
                                }
                                break;
                            case R.id.radioFontColorBlack:
                                if (true == checked) {
                                    setTextViewColor(COLOR_BLACK);
                                    currentColor = COLOR_BLACK;
                                }
                                break;
//                            case R.id.radioFontColorBlue:
//                                if (true == checked) {
//                                    setTextViewColor(COLOR_BLUE);
//                                    currentColor = COLOR_BLUE;
//                                }
//                                break;
                            case R.id.radioFontColorRed:
                                if (true == checked) {
                                    setTextViewColor(COLOR_RED);
                                    currentColor = COLOR_RED;
                                }
                                break;

                            default:
                                break;
                        }
                        Common.configValueFontColor = currentColor;
                    }
                });
        dialog.show();
    }

    private void setTextViewColor(String color_code) {
        tvAuthor.setTextColor(Color
                .parseColor(color_code));
        tvStoryContent.setTextColor(Color
                .parseColor(color_code));
        tvStoryName.setTextColor(Color
                .parseColor(color_code));
    }

    private void applySetingValue() {
        setTextViewColor(Common.configValueFontColor);
        tvAuthor.setTextSize(Common.configValueFontSize);
        tvStoryContent.setTextSize(Common.configValueFontSize);
        tvStoryName.setTextSize(Common.configValueFontSize + 4);
    }

    private void saveValueSetting(String color, int fontSize) {
        SharedPreferences settings = getSharedPreferences(Common.SETTING_SHAREREFER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Common.SETTING_SESSION_FONTCOLOR, color);
        editor.putInt(Common.SETTING_SESSION_FONTSIZE, fontSize);
        editor.commit();
        Common.writeLogCat("StoryDetailActivity","Save setting value: color="+color + ",fontSize=" + fontSize);
    }

    private void setOptionTitle(int id, String title) {
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }
}
