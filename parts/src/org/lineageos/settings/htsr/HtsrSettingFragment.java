package org.lineageos.settings.htsr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreferenceCompat;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.FileUtils;

public class HtsrSettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String HTSR_PATH = "/sys/devices/virtual/touch/touch_dev/bump_sample_rate";
    private static final String PREF_NAME = "htsr_prefs";
    private static final String KEY_LAST_STATE = "last_state";
    private static final int STATE_OFF = 0;
    private static final int STATE_ON = 1;

    private static final String KEY_PREF_ENABLE = "htsr_enable";

    private SwitchPreferenceCompat mSwitchPref;

    private SharedPreferences getPrefs() {
        return getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void saveState(int state) {
        getPrefs().edit().putInt(KEY_LAST_STATE, state).apply();
    }

    private int loadSavedState() {
        return getPrefs().getInt(KEY_LAST_STATE, STATE_OFF);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_htsr_setting);

        Preference p = findPreference(KEY_PREF_ENABLE);
        if (p instanceof SwitchPreferenceCompat) {
            mSwitchPref = (SwitchPreferenceCompat) p;
            mSwitchPref.setPersistent(false);
            mSwitchPref.setOnPreferenceChangeListener(this);
        }

        int current = FileUtils.readLineInt(HTSR_PATH);
        if (current < 0) {
            current = loadSavedState();
            try {
                FileUtils.writeLine(HTSR_PATH, current);
            } catch (Exception ignored) {}
        }
        updateUiFromState(current);
    }

    @Override
    public void onResume() {
        super.onResume();
        int current = FileUtils.readLineInt(HTSR_PATH);
        if (current < 0) {
            current = loadSavedState();
        }
        updateUiFromState(current);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!KEY_PREF_ENABLE.equals(preference.getKey())) return false;

        boolean enabled = (newValue instanceof Boolean) && (Boolean) newValue;
        int newState = enabled ? STATE_ON : STATE_OFF;

        try {
            FileUtils.writeLine(HTSR_PATH, newState);
            int readBack = FileUtils.readLineInt(HTSR_PATH);
            if (readBack != newState) {
                Toast.makeText(getActivity(), R.string.write_failed, Toast.LENGTH_SHORT).show();
                updateUiFromState(loadSavedState());
                return false; 
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.write_failed, Toast.LENGTH_SHORT).show();
            updateUiFromState(loadSavedState());
            return false;
        }

        saveState(newState);
        updateUiFromState(newState);
        return true;
    }

    private void updateUiFromState(int state) {
        if (mSwitchPref == null) return;
        mSwitchPref.setChecked(state == STATE_ON);

        if (state == STATE_ON) {
            mSwitchPref.setSummary(getString(R.string.on));
        } else if (state == STATE_OFF) {
            mSwitchPref.setSummary(getString(R.string.off));
        } else {
            mSwitchPref.setSummary(getString(R.string.unknown));
        }
    }
}
