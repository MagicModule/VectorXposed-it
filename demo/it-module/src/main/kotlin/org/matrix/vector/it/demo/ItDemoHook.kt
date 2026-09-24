package org.matrix.vector.it.demo

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Vector-it Custom Demo Hook.
 * Demonstrates module initialization, logging, method interception,
 * and integration with Vector-it extended features.
 */
class ItDemoHook : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "VectorItDemo"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.i(TAG, "Vector-it Demo Module loaded in process: ${lpparam.processName}, package: ${lpparam.packageName}")

        if (lpparam.packageName == "android") {
            // Hook system server ActivityManagerService
            runCatching {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.am.ActivityManagerService",
                    lpparam.classLoader,
                    "systemReady",
                    Runnable::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam<*>) {
                            XposedBridge.log("Vector-it: Android systemReady hook triggered successfully!")
                            Log.i(TAG, "SystemReady callback intercepted by Vector-it!")
                        }
                    }
                )
            }.onFailure {
                Log.w(TAG, "Failed to hook systemReady: ${it.message}")
            }
        } else if (lpparam.packageName == "com.android.systemui") {
            // Hook SystemUI to demonstrate target app hooking
            Log.i(TAG, "Attaching Vector-it hooks to SystemUI...")
        }
    }
}
