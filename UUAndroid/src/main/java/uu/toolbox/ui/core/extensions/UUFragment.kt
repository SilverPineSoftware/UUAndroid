package uu.toolbox.ui.core.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment

fun Fragment.uuLaunchActivityModal(
        activityClass: Class<*>,
        requestCode: Int,
        flags: Int,
        extras: Bundle?)
{
    activity?.let()
    {
        it.uuLaunchActivityModal(activityClass, requestCode, flags, extras)
    }
}

fun Fragment.uuLaunchActivity(
        activityClass: Class<*>,
        requestCode: Int,
        flags: Int,
        extras: Bundle?,
        showAnimationId: Int?,
        hideAnimationId: Int?)
{
    activity?.let()
    {
        it.uuLaunchActivity(activityClass, requestCode, flags, extras, showAnimationId, hideAnimationId)
    }
}