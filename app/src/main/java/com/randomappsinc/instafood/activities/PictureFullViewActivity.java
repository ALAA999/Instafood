package com.randomappsinc.instafood.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.adapters.PictureFullViewGalleryAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureFullViewActivity extends AppCompatActivity {

    public static final String IMAGE_URLS_KEY = "imageUrls";
    public static final String POSITION_KEY = "position";

    @BindView(R.id.pictures_pager) ViewPager mPicturesPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_full_view_activity);
        ButterKnife.bind(this);

        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra(IMAGE_URLS_KEY);
        PictureFullViewGalleryAdapter adapter =
                new PictureFullViewGalleryAdapter(getSupportFragmentManager(), imageUrls);
        mPicturesPager.setOffscreenPageLimit(2);
        mPicturesPager.setAdapter(adapter);
        mPicturesPager.setCurrentItem(getIntent().getIntExtra(POSITION_KEY, 0));
    }

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
