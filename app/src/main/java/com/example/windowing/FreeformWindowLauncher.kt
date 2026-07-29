package com.example.windowing

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.util.Log

object FreeformWindowLauncher {

    private const val TAG = "FreeformWindowLauncher"

    fun launchInFreeform(
        context: Context,
        packageName: String,
        bounds: Rect = Rect(120, 100, 1000, 750)
    ): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) {
                Log.e(TAG, "Launch intent not found for package: $packageName")
                return false
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            val options = ActivityOptions.makeBasic()

            try {
                // Set windowing mode to 5 (WINDOWING_MODE_FREEFORM) via reflection
                val setLaunchWindowingModeMethod = ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.java)
                setLaunchWindowingModeMethod.invoke(options, 5)
            } catch (e: Exception) {
                Log.w(TAG, "setLaunchWindowingMode via reflection failed", e)
            }

            try {
                // Set launch bounds via reflection for maximum compatibility across Android versions
                val setLaunchBoundsMethod = ActivityOptions::class.java.getMethod("setLaunchBounds", Rect::class.java)
                setLaunchBoundsMethod.invoke(options, bounds)
            } catch (e: Exception) {
                Log.w(TAG, "setLaunchBounds via reflection failed, using standard options", e)
            }

            context.startActivity(intent, options.toBundle())
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app in freeform mode: ${e.message}", e)
            false
        }
    }
}
