package com.amazonaws.smartdevicelink;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Map;

/**
 * Created by sanjoyg on 9/25/15.
 */
public class MqttSettingsActivity extends Activity {

    private static final String TAG = "MqttSettingsActivity";

    private static HelloFordApplication helloFordApplication;
    private static AppLinkService serviceInstance = AppLinkService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helloFordApplication = (HelloFordApplication) getApplication();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            helloFordApplication.getMqttConnection().connectToMqttServer(helloFordApplication);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
        finish();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mqttconnectionsettings);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        private void updatePreference(Preference preference) {
            if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                String key = preference.getKey();
                String newValue = editTextPreference.getText();

                if (key.equals("emulatorIP")) {
                    serviceInstance.setEmulatorIP(newValue);
                }
                if (key.equals("clientID")) {
                    helloFordApplication.getMqttConnection().setClientId(newValue);
                }
                else if (key.equals("server")) {
                    helloFordApplication.getMqttConnection().setServer(newValue);
                }
                else if (key.equals("port")) {
                    helloFordApplication.getMqttConnection().setPort(Integer.parseInt(newValue));
                }
                else if (key.equals("keystoreFile")) {
                    helloFordApplication.getMqttConnection().setKeystoreFile(newValue);
                }
                else if (key.equals("passphrase")) {
                    helloFordApplication.getMqttConnection().setPassphrase(newValue);
                }
                else if (key.equals("timeout")) {
                    helloFordApplication.getMqttConnection().setKeepAlive(Integer.parseInt(newValue));
                }
                else if (key.equals("keepAlive")) {
                    helloFordApplication.getMqttConnection().setKeepAlive(Integer.parseInt(newValue));
                }

                editTextPreference.setSummary(newValue);
            }
            else if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;

                String key = preference.getKey();
                if (key.equals("cleanSession")) {
                    helloFordApplication.getMqttConnection().setCleanSession(checkBoxPreference.isChecked());
                }
                else if (key.equals("useSsl")) {
                    helloFordApplication.getMqttConnection().setUseSsl(checkBoxPreference.isChecked());
                }

                checkBoxPreference.setChecked(checkBoxPreference.isChecked());
            }
            else if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key));
            serviceInstance.reset();
        }
    }
}
