package com.flycode.jasonfit.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.fragment.IntroFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends AppCompatActivity {
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.indicator) CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);

        viewPager.setAdapter(new IntroPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager);
    }

    @OnClick(R.id.proceed)
    public void onProceed() {
        startActivity(new Intent(this, StartActivity.class));
    }

    private class IntroPagerAdapter extends FragmentPagerAdapter {
        private final int[] BACKGROUND_RESOURCES = new int[] {
                R.color.colorPrimaryDark,
                R.color.colorBurgundy,
                R.color.text_black_grey
        };

        public IntroPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return BACKGROUND_RESOURCES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return IntroFragment.initialize(BACKGROUND_RESOURCES[position]);
        }
    }
}
