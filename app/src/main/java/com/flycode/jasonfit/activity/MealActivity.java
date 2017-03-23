package com.flycode.jasonfit.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.flycode.jasonfit.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
    }

    private CharSequence attributedSequence(String sourceString) {
        Pattern pattern = Pattern.compile("\\$");
        Matcher matcher = pattern.matcher(sourceString);
        CharSequence attributedSequence = "";
        int stringStartIndex = 0;

        while (matcher.find()) {
            int sectionStartIndex = matcher.start();
            attributedSequence = TextUtils.concat(attributedSequence, sourceString.substring(stringStartIndex, sectionStartIndex));

            matcher.find();

            int sectionEndIndex = matcher.start() + 1;
            SpannableString spannableString = new SpannableString(sourceString.substring(sectionStartIndex, sectionEndIndex).replaceAll("\\$", ""));
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    startActivity(new Intent(MealActivity.this, StartActivity.class));
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            }, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            attributedSequence = TextUtils.concat(attributedSequence, spannableString);

            stringStartIndex = sectionEndIndex;
        }

        return attributedSequence;
    }
}
