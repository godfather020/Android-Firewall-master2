package com.example.firewallminor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rule implements Comparable<Rule> {
    public PackageInfo info;
    public String name;
    public boolean system;
    public boolean disabled;
    public boolean wifi_blocked;
    public boolean other_blocked;
    public boolean changed;
    static int i =0;

    private Rule(PackageInfo info, boolean wifi_blocked, boolean other_blocked, boolean changed, Context context) {
        PackageManager pm = context.getPackageManager();
        this.info = info;
        this.name = info.applicationInfo.loadLabel(pm).toString();
        this.system = ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);

        int setting = pm.getApplicationEnabledSetting(info.packageName);
        if (setting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
            this.disabled = !info.applicationInfo.enabled;
        else
            this.disabled = (setting != PackageManager.COMPONENT_ENABLED_STATE_ENABLED);

        this.wifi_blocked = wifi_blocked;
        this.other_blocked = other_blocked;
        this.changed = changed;
    }

    public static List<Rule> getRules(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences wifi = context.getSharedPreferences("wifi", Context.MODE_PRIVATE);
        SharedPreferences other = context.getSharedPreferences("other", Context.MODE_PRIVATE);

        String[] pac = context.getResources().getStringArray(R.array.PackageNames);
        boolean wlWifi = prefs.getBoolean("whitelist_wifi", false);
        boolean wlOther = prefs.getBoolean("whitelist_other", false);

        List<Rule> listRules = new ArrayList<>();
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {

            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){

                    boolean blWifi = wifi.getBoolean(info.packageName, false);
                    boolean blOther = other.getBoolean(info.packageName, false);
                    boolean changed = (blWifi != wlWifi || blOther != wlOther);
                    listRules.add(new Rule(info, blWifi, blOther, changed, context));
            }
            else {

                for (String s : pac) {

                    if (info.packageName.equals(s)) {

                        boolean blWifi = wifi.getBoolean(s, true);
                        boolean blOther = other.getBoolean(s, true);
                        boolean changed = (blWifi != wlWifi || blOther != wlOther);
                        listRules.add(new Rule(info, blWifi, blOther, changed, context));
                    }
                }
            }
        }

        //for (int j = 0; j< listRules.size(); j++){
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)){

            for (String s : pac) {

                /*if (info.packageName == s) {

                    boolean blWifi = wifi.getBoolean(s, false);
                    boolean blOther = other.getBoolean(s, false);
                    boolean changed = (blWifi != wlWifi || blOther != wlOther);
                    listRules.add(new Rule(info, blWifi, blOther, changed, context));
                }
                else {

                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                        boolean blWifi = wifi.getBoolean(s, true);
                        boolean blOther = other.getBoolean(s, true);
                        boolean changed = (blWifi != wlWifi || blOther != wlOther);
                        listRules.add(new Rule(info, blWifi, blOther, changed, context));
                    }
                    else {

                        boolean blWifi = wifi.getBoolean(s, false);
                        boolean blOther = other.getBoolean(s, false);
                        boolean changed = (blWifi != wlWifi || blOther != wlOther);
                        listRules.add(new Rule(info, blWifi, blOther, changed, context));
                    }
                }*/


                /*if (listRules.get(j).info.packageName == s) {

                    boolean blWifi = wifi.getBoolean(s, false);
                    boolean blOther = other.getBoolean(s, false);
                    boolean changed = (blWifi != wlWifi || blOther != wlOther);
                    listRules.set(j, new Rule(listRules.get(j).info, blWifi, blOther, changed, context));
                } else {

                    if ((listRules.get(j).info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

                        boolean blWifi = wifi.getBoolean(s, true);
                        boolean blOther = other.getBoolean(s, true);
                        boolean changed = (blWifi != wlWifi || blOther != wlOther);
                        listRules.set(j, new Rule(listRules.get(j).info, blWifi, blOther, changed, context));
                    }
                    else {

                        boolean blWifi = wifi.getBoolean(s, false);
                        boolean blOther = other.getBoolean(s, false);
                        boolean changed = (blWifi != wlWifi || blOther != wlOther);
                        listRules.set(j, new Rule(listRules.get(j).info, blWifi, blOther, changed, context));
                    }
                }*/
            }
        }

            //else {
       // }
                /*for (String i : context.getResources().getStringArray(R.array.PackageNames)){

                    for (int j=0 ; j <= info.size()-1; j++) {

                        if (info.get(j).packageName.equals(i)){

                            boolean blWifi = wifi.getBoolean(info.get(j).packageName, false);
                            boolean blOther = other.getBoolean(info.get(j).packageName, false);
                            boolean changed = (blWifi != wlWifi || blOther != wlOther);
                            listRules.add(new Rule(info.get(j), blWifi, blOther, changed, context));
                        }
                    }
                    //else {


                    //}
                }*/
            //}


        Collections.sort(listRules);

        return listRules;
    }

    public Drawable getIcon(Context context) {
        return info.applicationInfo.loadIcon(context.getPackageManager());
    }

    @Override
    public int compareTo(Rule other) {
        if (changed == other.changed) {
            int i = name.compareToIgnoreCase(other.name);
            return (i == 0 ? info.packageName.compareTo(other.info.packageName) : i);
        }
        return (changed ? -1 : 1);
    }
}
