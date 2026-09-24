package org.matrix.vector.nativebridge

object NativeAPI {
    @JvmStatic external fun recordNativeEntrypoint(library_name: String)
    @JvmStatic external fun isNativeLibRegistered(library_name: String): Boolean
}
