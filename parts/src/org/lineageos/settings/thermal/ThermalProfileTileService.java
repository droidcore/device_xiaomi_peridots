/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.thermal;

import android.content.Context;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.FileUtils;

public class ThermalProfileTileService extends TileService {

    private static final String THERMAL_PROFILE_PATH = "/sys/class/thermal/thermal_message/sconfig";
    private static final String PREF_NAME = "thermal_profile_prefs";
    private static final String KEY_LAST_PROFILE = "last_profile";

    private static final int THERMAL_PROFILE_DEFAULT = 0;
    private static final int THERMAL_PROFILE_MBATTERY = 1;
    private static final int THERMAL_PROFILE_MPERFORMANCE = 6;
    private static final int THERMAL_PROFILE_MGAME = 19;

    private void saveProfile(int profile) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_LAST_PROFILE, profile).apply();
    }

    private int loadSavedProfile() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_LAST_PROFILE, THERMAL_PROFILE_DEFAULT);
    }

    private void applyProfile(int profile) {
        FileUtils.writeLine(THERMAL_PROFILE_PATH, profile);
        saveProfile(profile);
        updateUI(profile);
    }

    private void updateUI(int profile) {
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setLabel(getString(R.string.thermalprofile_title));
            String subtitle;
            switch (profile) {
                case THERMAL_PROFILE_DEFAULT:
                    subtitle = getString(R.string.thermalprofile_default);
                    break;
                case THERMAL_PROFILE_MBATTERY:
                    subtitle = getString(R.string.thermalprofile_battery);
                    break;
                case THERMAL_PROFILE_MPERFORMANCE:
                    subtitle = getString(R.string.thermalprofile_performance);
                    break;
                case THERMAL_PROFILE_MGAME:
                    subtitle = getString(R.string.thermalprofile_game);
                    break;
                default:
                    subtitle = getString(R.string.thermalprofile_unknown);
            }
            tile.setSubtitle(subtitle);
            tile.setState(Tile.STATE_ACTIVE);
            tile.updateTile();
        }
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        int currentProfile = FileUtils.readLineInt(THERMAL_PROFILE_PATH);
        if (currentProfile < 0) {
            currentProfile = loadSavedProfile();
            FileUtils.writeLine(THERMAL_PROFILE_PATH, currentProfile);
        }
        updateUI(currentProfile);
    }

    @Override
    public void onClick() {
        super.onClick();
        int currentProfile = FileUtils.readLineInt(THERMAL_PROFILE_PATH);
        if (currentProfile < 0)
            currentProfile = loadSavedProfile();

        int newProfile;
        switch (currentProfile) {
            case THERMAL_PROFILE_DEFAULT:
                newProfile = THERMAL_PROFILE_MBATTERY;
                break;
            case THERMAL_PROFILE_MBATTERY:
                newProfile = THERMAL_PROFILE_MPERFORMANCE;
                break;
            case THERMAL_PROFILE_MPERFORMANCE:
                newProfile = THERMAL_PROFILE_MGAME;
                break;
            default:
                newProfile = THERMAL_PROFILE_DEFAULT;
                break;
        }

        applyProfile(newProfile);
    }
}
