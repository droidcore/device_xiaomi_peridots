package org.lineageos.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.lineageos.settings.utils.FileUtils;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String THERMAL_PROFILE_PATH = "/sys/class/thermal/thermal_message/sconfig";
    private static final String THERMAL_PREF_NAME = "thermal_profile_prefs";
    private static final String KEY_LAST_PROFILE = "last_profile";
    private static final int DEFAULT_PROFILE = 0;

    private static final String HTSR_PATH = "/sys/devices/virtual/touch/touch_dev/bump_sample_rate";
    private static final String HTSR_PREF_NAME = "htsr_prefs";
    private static final String KEY_LAST_HTSR = "last_state";
    private static final int HTSR_DEFAULT = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences thermalPrefs = context.getSharedPreferences(THERMAL_PREF_NAME, Context.MODE_PRIVATE);
            int lastProfile = thermalPrefs.getInt(KEY_LAST_PROFILE, DEFAULT_PROFILE);
            FileUtils.writeLine(THERMAL_PROFILE_PATH, lastProfile);

            SharedPreferences htsrPrefs = context.getSharedPreferences(HTSR_PREF_NAME, Context.MODE_PRIVATE);
            int lastHtsr = htsrPrefs.getInt(KEY_LAST_HTSR, HTSR_DEFAULT);
            FileUtils.writeLine(HTSR_PATH, lastHtsr);
        }
    }
}
