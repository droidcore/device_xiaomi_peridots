package org.lineageos.settings.htsr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.FileUtils;

public class HtsrTileService extends TileService {

    private static final String HTSR_PATH = "/sys/devices/virtual/touch/touch_dev/bump_sample_rate";
    private static final String PREF_NAME = "htsr_prefs";
    private static final String KEY_LAST_STATE = "last_state";
    private static final int STATE_OFF = 0;
    private static final int STATE_ON = 1;

    private SharedPreferences getPrefs() {
        return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void saveState(int state) {
        getPrefs().edit().putInt(KEY_LAST_STATE, state).apply();
    }

    private int loadSavedState() {
        return getPrefs().getInt(KEY_LAST_STATE, STATE_OFF);
    }

    private boolean writeStateToSysfs(int state) {
        try {
            FileUtils.writeLine(HTSR_PATH, state);
            int readBack = FileUtils.readLineInt(HTSR_PATH);
            return (readBack == state);
        } catch (Exception e) {
            return false;
        }
    }

    private void applyState(int state) {
        boolean ok = writeStateToSysfs(state);
        if (ok) {
            saveState(state);
            updateUI(state);
        } else {
            Tile tile = getQsTile();
            if (tile != null) {      
                updateUI(loadSavedState());
            }

            try {
                Toast.makeText(this, R.string.write_failed, Toast.LENGTH_SHORT).show();
            } catch (Exception ignored) {}
        }
    }

    private void updateUI(int state) {
        Tile tile = getQsTile();
        if (tile == null) return;

        tile.setLabel(getString(R.string.htsr_title));
        try {
            tile.setIcon(Icon.createWithResource(this, R.drawable.icon_htsr));
        } catch (Exception ignored) {}

        if (state == STATE_ON) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setSubtitle(getString(R.string.on));
        } else if (state == STATE_OFF) {
            tile.setState(Tile.STATE_INACTIVE);
            tile.setSubtitle(getString(R.string.off));
        } else {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.setSubtitle(getString(R.string.unknown));
        }
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        int current = FileUtils.readLineInt(HTSR_PATH);
        if (current < 0) {
            current = loadSavedState();
            writeStateToSysfs(current);
        }
        updateUI(current);
    }

    @Override
    public void onClick() {
        super.onClick();
        int current = FileUtils.readLineInt(HTSR_PATH);
        if (current < 0) {
            current = loadSavedState();
        }
        int newState = (current == STATE_ON) ? STATE_OFF : STATE_ON;
        applyState(newState);
    }
}
