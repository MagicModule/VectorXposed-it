package org.javsaia.vector.manager

import android.os.IBinder

/**
 * Forwarding entry point for the Vector Manager under the JavSaia package namespace.
 *
 * ParasiticManagerHooker accesses this class reflectively via BuildConfig.ManagerPackageName.
 */
object Constants {
    const val TAG = org.matrix.vector.manager.Constants.TAG

    @JvmStatic
    fun setBinder(binder: IBinder): Boolean {
        return org.matrix.vector.manager.Constants.setBinder(binder)
    }
}
