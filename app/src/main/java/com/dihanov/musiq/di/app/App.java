package com.dihanov.musiq.di.app;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.dihanov.musiq.R;
import com.dihanov.musiq.config.Config;
import com.dihanov.musiq.di.modules.NetworkModule;
import com.dihanov.musiq.service.scrobble.Scrobbler;
import com.dihanov.musiq.util.Constants;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashSet;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Dimitar Dihanov on 14.9.2017 г..
 */

public class App extends Application implements HasActivityInjector, HasServiceInjector{
    private static SharedPreferences sharedPreferences;
    private static Application app;

    @Inject
    Scrobbler scrobbler;

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Service> serviceDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        FlowManager.init(this);
        DaggerAppComponent
                .builder()
                .application(this)
                .networkModule(new NetworkModule(Config.LAST_FM_API_URL))
                .build()
                .inject(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cabin_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.contains(Constants.FAVORITE_ARTISTS_KEY)){
            sharedPreferences.edit().putStringSet(Constants.FAVORITE_ARTISTS_KEY, new HashSet<>()).apply();
        }
        if(!sharedPreferences.contains(Constants.FAVORITE_ALBUMS_KEY)){
            sharedPreferences.edit().putStringSet(Constants.FAVORITE_ALBUMS_KEY, new HashSet<>()).apply();
        }

        scrobbler.scrobbleFromCache();
        app = this;
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceDispatchingAndroidInjector;
    }

    public static Application getAppContext(){
        return app;
    }

    @Override
    public void onLowMemory() {
        Glide.get(this).clearMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.get(this).clearMemory();
        super.onTrimMemory(level);
    }

    //method for getting SP outside of Android classes:
    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
