package io.github.rob__.kahootbot.Start;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.rob__.kahootbot.CustomListeners;
import io.github.rob__.kahootbot.Kahoot;
import io.github.rob__.kahootbot.Logger.Logger;
import io.github.rob__.kahootbot.R;

public class StartFragment extends Fragment implements StartView {

    @BindView(R.id.background)
    ConstraintLayout layout;

    @BindView(R.id.etGameCode)
    EditText etGameCode;

    @BindView(R.id.btnStart)
    Button btnStart;

    @BindView(R.id.btnStop)
    Button btnStop;

    @OnClick(R.id.btnStart) void start() {
        presenter.startFloodClicked();
    }

    @OnClick(R.id.btnStop) void stop() {
        presenter.stopFloodClicked();
    }

    StartPresenter presenter;
    CustomListeners.KahootListeners kahootListeners;
    CustomListeners.LoggerListener loggerListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);

        ButterKnife.bind(this, v);

        presenter = new StartPresenter(this);

        final ValueAnimator animator = ValueAnimator.ofArgb(
                Color.parseColor("#45a3e5"),
                Color.parseColor("#33cccc"),
                Color.parseColor("#66bf39"),
                Color.parseColor("#ffa602"),
                Color.parseColor("#eb670f"),
                Color.parseColor("#ff3355"),
                Color.parseColor("#864cbf")
        );
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layout.setBackgroundColor((int) valueAnimator.getAnimatedValue());
            }
        });
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(20000);
        animator.start();

        return v;
    }

    public static StartFragment newInstance() {
        return new StartFragment();
    }

    public void setKahootListeners(CustomListeners.KahootListeners listeners) {
        this.kahootListeners = listeners;
    }

    public void setLoggerListener(CustomListeners.LoggerListener listener) {
        this.loggerListener = listener;
    }

    @Override
    public void startFloodClicked() {
        final String code = etGameCode.getText().toString();

        if (code.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "You need to enter a valid game code.", Toast.LENGTH_SHORT).show();
            return;
        }

        /* hide the keyboard */
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getActivity().getCurrentFocus();
        if(currentFocus != null){
            manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        Kahoot.gameExists(code, new CustomListeners.GameListener() {
            @Override
            public void gameExists(boolean exists) {
                if (!exists) {
                    Toast.makeText(getActivity().getApplicationContext(), "Game with code #" + code + " does not exist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("Kahoot", "Starting flood...");

                etGameCode.setEnabled(false);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);

                kahootListeners.floodStarted();
                Logger.addLog("Flood starting for game #" + code);

                presenter.startFlood(code);
            }
        });
    }

    @Override
    public void stopFloodClicked() {
        etGameCode.setEnabled(true);
        btnStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);

        kahootListeners.floodStopped();
        Logger.addLog("Flood stopped for game #" + etGameCode.getText().toString());
    }

}
