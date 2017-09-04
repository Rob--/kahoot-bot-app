package io.github.rob__.kahootbot.Logger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rob__.kahootbot.R;

public class LoggerFragment extends Fragment implements LoggerView {

    @BindView(R.id.tvLogs)
    TextView tvLogs;

    public LoggerPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logger, container, false);

        ButterKnife.bind(this, v);

        presenter = new LoggerPresenter(this);

        tvLogs.setText(Logger.getLogs());

        return v;
    }

    public static LoggerFragment newInstance() {
        return new LoggerFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Kahoot", "resumed");
        tvLogs.setText(Logger.getLogs());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Kahoot", "started");
        tvLogs.setText(Logger.getLogs());
    }

    @Override
    public void addLog(String message) {
        Log.d("Kahoot", "Adding log to textview");
        tvLogs.setText(Logger.getLogs());
    }

}
