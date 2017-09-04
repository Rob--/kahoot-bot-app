package io.github.rob__.kahootbot.Settings;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rob__.kahootbot.R;

public class SettingsFragment extends Fragment{

    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.constraintLayout)
    ConstraintLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, v);

    final ConstraintSet first = new ConstraintSet();
        final ConstraintSet last = new ConstraintSet();

        last.clone(getContext(), R.layout.fragment_settings);
        first.clone(layout);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Kahoot", "Click!");

                TransitionManager.beginDelayedTransition(layout);
                last.applyTo(layout);
            }
        });

        return v;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}
