/*
 *
 *   FOR INTERNAL USE ONLY. NOT A CONTRIBUTION.
 *
 *   This software source code contains valuable, confidential, trade secret information owned by Coolfire Solutions, Inc. (“Coolfire”) and is protected by trade secrets, copyright laws, and international copyright treaties, as well as other intellectual property laws and treaties.
 *
 *  ACCESS TO AND USE OF THIS SOURCE CODE IS RESTRICTED TO AUTHORIZED PERSONS WHO HAVE ENTERED INTO A WRITTEN LICENSE AGREEMENT AND A CONFIDENTIALITY AGREEMENT WITH COOLFIRE.
 *
 *  This source code may not be licensed, disclosed or used except as authorized such written License Agreement or in a separate writing by a duly authorized officer of Coolfire.
 *
 */

package edu.siue.mech.seniordesign;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ApplicationLifecycle implements Application.ActivityLifecycleCallbacks {

    public interface ApplicationLifecycleListener{
        void onAppForegrounded(Activity activity);
        void onAppBackgrounded(Activity activity);
    }

    private int numStarted = 0;
    public ApplicationLifecycleListener listener;

    public ApplicationLifecycle(ApplicationLifecycleListener listener){
        this.listener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(numStarted == 0){
            //App went to foreground
            this.listener.onAppForegrounded(activity);
        }
        numStarted++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
        if(numStarted == 0){
            //App went to background
            this.listener.onAppBackgrounded(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
