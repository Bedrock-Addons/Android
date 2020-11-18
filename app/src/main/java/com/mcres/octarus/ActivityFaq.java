package com.mcres.octarus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.mcres.octarus.R;
import com.mcres.octarus.utils.Tools;

import java.util.Objects;

public class ActivityFaq extends AppCompatActivity {
    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivityFaq.class);
        activity.startActivity(i);
    }

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text6;
    private TextView text7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        initComponent();
    }

    private void initComponent() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorBackground);
        Tools.setSystemBarLight(this);


        text1 = findViewById(R.id.textfaq1);
        text2 = findViewById(R.id.textfaq2);
        text3 = findViewById(R.id.textfaq3);
        text4 = findViewById(R.id.textfaq4);
        text5 = findViewById(R.id.textfaq5);
        text6 = findViewById(R.id.textfaq6);
        text7 = findViewById(R.id.textfaq7);

        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);
        text4.setVisibility(View.GONE);
        text5.setVisibility(View.GONE);
        text6.setVisibility(View.GONE);
        text7.setVisibility(View.GONE);

        btToggleClick(findViewById(R.id.dropdown1));

    }

    public void btToggleClick(View view) {
        if (view instanceof ImageView) {
            ImageView img = (ImageView) view;

            if (img.getId() == R.id.dropdown1) {
                if (view.isSelected()) {
                    text1.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);

                } else {
                    text1.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown2) {
                if (img.isSelected()) {
                    text2.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text2.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown3) {
                if (img.isSelected()) {
                    text3.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text3.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown4) {
                if (img.isSelected()) {
                    text4.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text4.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown5) {
                if (img.isSelected()) {
                    text5.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text5.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown6) {
                if (img.isSelected()) {
                    text6.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text6.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());

                ////
            } else if (img.getId() == R.id.dropdown7) {
                if (img.isSelected()) {
                    text7.setVisibility(View.GONE);
                    img.setImageResource(R.drawable.ic_arrow_drop_down);
                } else {
                    text7.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_arrow_dropdown_up);
                }
                img.setSelected(!img.isSelected());
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}