package com.flycode.jasonfit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flycode.jasonfit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by acerkinght on 3/14/17.
 */

public class IntroFragment extends Fragment {
    private static final String EXTRA_BACKGROUND_RESOURCE = "extraBackgroundResource";

    @BindView(R.id.image) View view;

    private Unbinder unbinder;

    public static IntroFragment initialize(int backgroundResource) {
        IntroFragment introFragment = new IntroFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_BACKGROUND_RESOURCE, backgroundResource);
        introFragment.setArguments(arguments
        );

        return introFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View introView = inflater.inflate(R.layout.fragment_intro, container, false);

        unbinder = ButterKnife.bind(this, introView);

        int backgroundResource = getArguments().getInt(EXTRA_BACKGROUND_RESOURCE);

        view.setBackgroundColor(getResources().getColor(backgroundResource));

        return introView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}
