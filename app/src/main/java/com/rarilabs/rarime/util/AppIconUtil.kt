package com.rarilabs.rarime.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.rarilabs.rarime.data.enums.AppIcon
import kotlin.system.exitProcess

object AppIconUtil {
    fun getIcon(context: Context): AppIcon {
        AppIcon.entries.forEach {
            val componentState = context.packageManager
                .getComponentEnabledSetting(ComponentName(context, it.activity))
            if (componentState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return it
            }
        }

        return AppIcon.WHITE
    }


    fun setIcon(context: Context, icon: AppIcon) {
        val packageManager = context.packageManager

        AppIcon.entries.filter { it.activity != icon.activity }.forEach {
            packageManager.setComponentEnabledSetting(
                ComponentName(context, it.activity),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        packageManager.setComponentEnabledSetting(
            ComponentName(context, icon.activity),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
        Log.d("SetIcon", "Enabled ${icon.activity}")

        relaunchApp(context, icon.activity)
    }

    private fun relaunchApp(context: Context, activityAlias: String) {
        val intent = Intent().apply {
            component = ComponentName(context, activityAlias)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(intent)

        exitProcess(0)
    }
}