package com.nilesh.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.nilesh.popularmovies.databinding.ActivityErrorBinding;

/**
 * Created by Nilesh on 06/03/2018.
 */

public class ErrorScreen extends AppCompatActivity {

    ActivityErrorBinding mBindingError;
    private static final String mMessageStringExtra = "ErrorMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        mBindingError = DataBindingUtil.setContentView(this, R.layout.activity_error);

        String errorText = getIntent().getStringExtra(mMessageStringExtra);
        mBindingError.tvError.setText(errorText);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.error_string);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

    }

}
