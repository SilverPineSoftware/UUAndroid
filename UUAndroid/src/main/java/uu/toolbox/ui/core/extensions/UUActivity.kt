package uu.toolbox.ui.core.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import uu.toolbox.R
import uu.toolbox.core.UUThread

val EXTRA_EXIT_ANIMATION_ID: String = "ExitAnimationId"

fun Activity.uuLaunchActivityPush(
        activityClass: Class<*>,
        requestCode: Int,
        flags: Int,
        extras: Bundle?)
{
    uuLaunchActivity(activityClass, requestCode, flags, extras, R.anim.slide_in_right, R.anim.slide_out_right)
}

fun Activity.uuLaunchActivityModal(
        activityClass: Class<*>,
        requestCode: Int,
        flags: Int,
        extras: Bundle?)
{
    uuLaunchActivity(activityClass, requestCode, flags, extras, R.anim.slide_in_up, R.anim.slide_out_down)
}

fun Activity.uuLaunchActivity(
        activityClass: Class<*>,
        requestCode: Int,
        flags: Int,
        extras: Bundle?,
        showAnimationId: Int?,
        hideAnimationId: Int?)
{
    UUThread.runOnMainThread()
    {
        val intent = Intent(this, activityClass)
        intent.flags = flags

        if (hideAnimationId != null)
        {
            intent.putExtra(EXTRA_EXIT_ANIMATION_ID, hideAnimationId)
        }

        if (extras != null)
        {
            intent.putExtras(extras)
        }

        if (requestCode != -1)
        {
            startActivityForResult(intent, requestCode)
        }
        else
        {
            startActivity(intent)
        }

        if (showAnimationId != null)
        {
            overridePendingTransition(showAnimationId, R.anim.stay_still)
        }
    }
}


fun Activity.uuFinish(resultCode: Int? = null, resultData: Intent? = null)
{
    var hideAnimationId: Int?

    intent.extras.let()
    {
        hideAnimationId = it.getInt(EXTRA_EXIT_ANIMATION_ID, -1)
    }

    if (resultCode != null && resultData != null)
    {
        setResult(resultCode, resultData)
    }
    else if (resultCode != null)
    {
        setResult(resultCode)
    }

    finish()

    hideAnimationId?.let()
    {
        overridePendingTransition(R.anim.stay_still, it)
    }
}