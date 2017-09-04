package io.github.rob__.kahootbot;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.audiofx.BassBoost;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import dalvik.annotation.TestTarget;
import io.github.rob__.kahootbot.Logger.Logger;
import io.github.rob__.kahootbot.Logger.LoggerFragment;
import io.github.rob__.kahootbot.Menu.DrawerAdapter;
import io.github.rob__.kahootbot.Menu.DrawerItem;
import io.github.rob__.kahootbot.Menu.SimpleItem;
import io.github.rob__.kahootbot.Settings.SettingsFragment;
import io.github.rob__.kahootbot.Start.StartFragment;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, CustomListeners.KahootListeners {

    @BindView(R.id.tvStatus)
    TextView tvStatus;

    @BindView(R.id.list)
    RecyclerView list;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private int POS_START = 0;
    private int POS_SETTINGS = 1;
    private int POS_LOGGER = 2;

    SlidingRootNav slidingRootNav;

    Fragment[] fragments = new Fragment[] {
            StartFragment.newInstance(),
            SettingsFragment.newInstance(),
            LoggerFragment.newInstance()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        screenTitles = getScreenTitles();
        screenIcons = getScreenIcons();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer)
                .inject();

        // Need to bind after we build our drawer as the layout hasn't been created yet
        ButterKnife.bind(this);

        // called to set status to inactive
        this.floodStopped();

        DrawerAdapter adapter = new DrawerAdapter(getDrawerItems());
        adapter.setListener(this);

        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_START);
    }

    private String[] getScreenTitles() {
        return getResources().getStringArray(R.array.screenNames);
    }

    private Drawable[] getScreenIcons() {
        TypedArray array = getResources().obtainTypedArray(R.array.screenIcons);
        Drawable[] icons = new Drawable[array.length()];
        for (int i = 0; i < array.length(); i++) {
            icons[i] = ContextCompat.getDrawable(this, array.getResourceId(i, 0));
        }

        array.recycle();
        return icons;
    }

    private ArrayList<DrawerItem> getDrawerItems() {
        ArrayList<DrawerItem> list = new ArrayList<>();

        for (int i = 0; i < screenTitles.length; i++) {
            list.add(new SimpleItem(screenIcons[i], screenTitles[i], this));
        }

        return list;
    }

    @Override
    public void onItemSelected(int position) {
        Fragment selectedScreen = fragments[position];

        if (position == POS_START) {
            ((StartFragment) selectedScreen).setKahootListeners(this);
        }

        showFragment(selectedScreen);
        slidingRootNav.closeMenu(true);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void floodStarted() {
        tvStatus.setText(getString(R.string.active));
        tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.active));
    }

    @Override
    public void floodStopped() {
        tvStatus.setText(getString(R.string.inactive));
        tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.inactive));
    }
}
