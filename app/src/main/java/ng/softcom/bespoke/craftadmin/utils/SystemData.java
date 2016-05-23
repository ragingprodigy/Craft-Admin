package ng.softcom.bespoke.craftadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by oladapo on 12/3/15.
 * as part of com.omonayajo.edusocial.model in CALogin Demo
 */
public class SystemData {

    public static final String PREF_FILE = "es_Shared_Prefs";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    public SystemData(Context ctx) {
        settings = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    /**
     * Write a String Value to Shared Preferences
     *
     * @param value Value to Store
     * @param forKey Key to store it under
     */
    public void writeString(String value, String forKey) {

        editor = settings.edit();
        this.editor.putString(forKey, value);
        this.editor.apply();
    }

    /**
     * Get a String Value from Shared Preferences
     */
    public String getString(String forKey) {

        return this.settings.getString(forKey, "");

    }

    /**
     * Write an Int to Shared Preferences
     * @param value Value to Write
     * @param forKey Key to write it to
     */
    public void writeInt(int value, String forKey) {

        editor = settings.edit();
        this.editor.putInt(forKey, value);
        this.editor.apply();

    }

    /**
     * Get an Int Value for Shared Preferences
     *
     * @param forKey Key to retrieve
     * @return int
     */
    public int getInt(String forKey) {

        return this.settings.getInt(forKey, -1);

    }

    /**
     * Write a Boolean Value to Shared Preferences
     *
     * @param value Value to Store
     * @param forKey Key to Store Under
     */
    public void writeBoolean(Boolean value, String forKey) {

        editor = settings.edit();
        this.editor.putBoolean(forKey, value);
        this.editor.apply();

    }

    /**
     * Get a Boolean Value from Shared Preferences
     *
     * @param forKey Key the Value is Stored Under
     * @param defaultValue default Value
     * @return Boolean
     */
    public Boolean getBoolean(String forKey, Boolean defaultValue) {

        return this.settings.getBoolean(forKey, defaultValue);

    }

    /**
     * Clear all Data in SharedPreferences
     */
    public void clearData() {

        editor = settings.edit();
        editor.clear();
        editor.apply();

    }

    /**
     * Remove Value For the Specified Key
     */
    public void remove(String key) {

        editor = settings.edit();
        editor.remove(key);
        editor.apply();

    }

}
