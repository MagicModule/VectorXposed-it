package org.matrix.vector.impl.hookers

import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Method
import org.matrix.vector.impl.hooks.VectorHookBuilder
import org.matrix.vector.util.Log

/**
 * Vector-it Extended System Hookers.
 * Provides high-level convenience hooking utilities for system-level services
 * (ActivityManager, PackageManager, Clipboard, Telephony, etc.).
 */
object ItSystemHookers {

    private const val TAG = "ItSystemHookers"

    /**
     * Intercepts a specific method on an internal or system class if it exists.
     */
    fun tryHookMethod(
        clazz: Class<*>,
        methodName: String,
        paramTypes: Array<Class<*>>,
        hooker: XposedInterface.Hooker
    ): Boolean {
        return runCatching {
            val method: Method = clazz.getDeclaredMethod(methodName, *paramTypes)
            method.isAccessible = true
            VectorHookBuilder(method).intercept(hooker)
            true
        }.getOrElse {
            Log.v(TAG, "Optional hook ${clazz.name}#$methodName skipped: ${it.message}")
            false
        }
    }

    /**
     * Installs system-wide activity launching interception listener.
     */
    fun monitorActivityStarts(
        classLoader: ClassLoader,
        onStartActivity: (packageName: String?, action: String?) -> Unit
    ) {
        runCatching {
            val amClass = Class.forName("com.android.server.am.ActivityManagerService", false, classLoader)
            amClass.declaredMethods
                .filter { it.name.startsWith("startActivity") }
                .forEach { method ->
                    method.isAccessible = true
                    VectorHookBuilder(method).intercept(object : XposedInterface.Hooker {
                        override fun intercept(chain: XposedInterface.Chain): Any? {
                            // Extract intent or caller info if available
                            val intentArg = chain.args.firstOrNull { it != null && it.javaClass.name == "android.content.Intent" }
                            if (intentArg != null) {
                                val getActionMethod = intentArg.javaClass.getMethod("getAction")
                                val getPackageMethod = intentArg.javaClass.getMethod("getPackage")
                                val action = getActionMethod.invoke(intentArg) as? String
                                val pkg = getPackageMethod.invoke(intentArg) as? String
                                onStartActivity(pkg, action)
                            }
                            return chain.proceed()
                        }
                    })
                }
        }.onFailure {
            Log.w(TAG, "ActivityManagerService monitoring could not be attached: ${it.message}")
        }
    }
}
