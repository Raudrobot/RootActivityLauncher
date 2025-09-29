package tk.zwander.rootactivitylauncher.util

import android.content.Context
import android.content.pm.PackageManager
import com.rosan.dhizuku.api.Dhizuku
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener
import tk.zwander.rootactivitylauncher.R
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DhizukuUtils {
    suspend fun requestDhizukuPermission(): Boolean {
        return suspendCoroutine { continuation ->
            Dhizuku.requestPermission(object : DhizukuRequestPermissionListener() {
                override fun onRequestPermission(grantResult: Int) {
                    continuation.resume(grantResult == PackageManager.PERMISSION_GRANTED)
                }
            })
        }
    }

    suspend fun isDhizukuGranted(context: Context): Boolean {
        return checkDhizukuState(context) == null
    }

    suspend fun checkDhizukuState(context: Context): Throwable? {
        return try {
            if (!Dhizuku.init(context)) {
                return Exception(context.resources.getString(R.string.dhizuku_not_running))
            }

            val permissionResult = Dhizuku.isPermissionGranted() || requestDhizukuPermission()

            if (!permissionResult) {
                return Exception(context.resources.getString(R.string.no_dhizuku_access))
            }

            null
        } catch (e: Throwable) {
            e
        }
    }
}
