package com.example.truongnguyen.soundrecorder.fragment;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


import com.example.truongnguyen.soundrecorder.MySharedPreferences;

import com.example.truongnguyen.soundrecorder.activities.BuildConfig;
import com.example.truongnguyen.soundrecorder.activities.R;
import com.example.truongnguyen.soundrecorder.activities.SettingActivity;

import androidx.annotation.Nullable;

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        CheckBoxPreference highQualityPref=(CheckBoxPreference)findPreference(getResources().getString(R.string.pref_high_quality_key)) ;
        highQualityPref.setChecked(MySharedPreferences.getPrefHighQuality(getActivity()));
        highQualityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
               MySharedPreferences.setPrefHighQuality(getActivity(),(boolean)newValue);

                return true;
            }
        });
        Preference aboutPref=findPreference(getString(R.string.pref_about_key));
        aboutPref.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
               LicemsesFragment licemsesFragment=new LicemsesFragment();
               licemsesFragment.show(((SettingActivity)getActivity()).getSupportFragmentManager().beginTransaction(),"dialog_licenses");

                return true;
            }
        });
    }
}
