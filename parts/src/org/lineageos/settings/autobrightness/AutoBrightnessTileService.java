package org.lineageos.settings.autobrightness;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import org.lineageos.settings.R;

public class AutoBrightnessTileService extends TileService {
    private static final String TAG = "AutoBrightnessTileSvc";

    private Context context;
    private Tile tile;

    private static final int MODE_MANUAL = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL; // 0
    private static final int MODE_AUTOMATIC = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; // 1

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    private int getCurrentMode() {
        return Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                MODE_MANUAL
        );
    }

    private boolean isAutoEnabled() {
        return getCurrentMode() == MODE_AUTOMATIC;
    }

    private void setAutoEnabled(boolean enabled) {
        int mode = enabled ? MODE_AUTOMATIC : MODE_MANUAL;
        boolean ok = Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
        if (!ok) {
            Log.w(TAG, "Failed to write SCREEN_BRIGHTNESS_MODE");
        }
    }

    private void updateTileView() {
        if (tile == null) tile = getQsTile();

        boolean autoOn = isAutoEnabled();

        tile.setLabel(getString(R.string.auto_brightness_title));
        String subtitle = autoOn ? getString(R.string.on) : getString(R.string.off);
        tile.setContentDescription(subtitle);
        tile.setSubtitle(subtitle);

        try {
            int iconRes = autoOn ? R.drawable.icon_brightness_auto_on : R.drawable.icon_brightness_auto_off;
            tile.setIcon(Icon.createWithResource(context, iconRes));
        } catch (Exception e) {
            Log.w(TAG, "Failed to set icon", e);
        }

        tile.setState(autoOn ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);

        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        updateTileView();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean currentlyAuto = isAutoEnabled();
        setAutoEnabled(!currentlyAuto);
        updateTileView();
    }
}
