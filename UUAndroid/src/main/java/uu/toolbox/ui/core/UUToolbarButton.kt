package uu.toolbox.ui.core

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout

abstract class UUToolbarButton : ConstraintLayout
{
    private var icon: AppCompatImageView? = null
    private var label: AppCompatTextView? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        init(attrs)
    }

    constructor(context: Context) : super(context)
    {
        init(null)
    }

    @LayoutRes
    protected abstract fun getRootLayoutId(): Int

    @IdRes
    protected abstract fun getButtonId(): Int

    @IdRes
    protected abstract fun getIconId(): Int

    private fun init(attrs: AttributeSet?)
    {
        val rootLayoutId = getRootLayoutId()
        if (rootLayoutId != -1)
        {
            View.inflate(context, rootLayoutId, this)

            val buttonId = getButtonId()
            if (buttonId != -1)
            {
                label = findViewById(buttonId)
            }

            val iconId = getIconId()
            if (iconId != -1)
            {
                icon = findViewById(iconId)
            }
        }
    }

    fun setLabel(@StringRes labelResourceId: Int)
    {
        label?.setText(labelResourceId)
    }

    fun setIcon(@DrawableRes iconResourceId: Int)
    {
        if (iconResourceId != -1)
        {
            icon?.setImageDrawable(resources.getDrawable(iconResourceId))
            icon?.visibility = View.VISIBLE
        }
        else
        {
            icon?.visibility = View.GONE
        }
    }
}