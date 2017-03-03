package com.example.thenry.ridesafe;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.RealmLog;

/**
 Created by thenry on 27/01/2017.
 */

public class RideSafeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .name("RideSafe.realm")
                .deleteRealmIfMigrationNeeded() //nettoie si on change le modèle du realm
                .build();
        Realm.setDefaultConfiguration(myConfig);

        // Enable full log output when debugging
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(Log.VERBOSE);
        }

        //Pour inspecter le realm avec stetho, à voir si il faut l'enlever
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);  // pour parer au grand nombre de méthodes à cause des librairies

    }
}