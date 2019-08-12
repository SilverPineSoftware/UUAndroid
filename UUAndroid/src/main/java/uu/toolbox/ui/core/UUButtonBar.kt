package uu.toolbox.ui.core

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import uu.toolbox.logging.UULog
import java.util.*

abstract class UUButtonBar<T : UUToolbarButton> : ConstraintLayout
{
    companion object
    {
        private val ID_START = 1000
    }

    val toolbarItems = ArrayList<T>()
    private var listener: Listener? = null

    protected fun getItemMargin(): Int
    {
        return 0
    }

    interface Listener
    {
        fun onBarButtonSelected(buttonBar: UUButtonBar<*>, index: Int)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun setBarHeight(@DimenRes barHeightDimensionId: Int)
    {
        if (barHeightDimensionId != -1)
        {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(barHeightDimensionId))
        }
    }

    fun setListener(listener: Listener)
    {
        this.listener = listener
    }

    fun setSelectedIndex(index: Int)
    {
        if (index >= 0 && index < toolbarItems.size)
        {
            updateSelection(index)
        }
    }

    fun buttonCount() : Int
    {
        return toolbarItems.size
    }

    private fun updateSelection(index: Int)
    {
        for (i in toolbarItems.indices)
        {
            val btn = toolbarItems[i]
            btn.isSelected = (i == index)
        }
    }

    fun addButton(@StringRes buttonText: Int, @DrawableRes icon: Int): T
    {
        val buttonId = ID_START + toolbarItems.size

        val button = instantiateButton(context)
        button.setLabel(buttonText)
        button.setIcon(icon)
        button.id = buttonId
        button.tag = toolbarItems.size
        button.elevation = 0f

        button.setOnClickListener()
        {

            val selectedIndex = toolbarItems.indexOf(button)
            updateSelection(selectedIndex)

            if (selectedIndex != -1)
            {
                notifyListener(selectedIndex)
            }
        }

        toolbarItems.add(button)
        addView(button)

        updateConstraints()

        return button
    }

    private fun notifyListener(index: Int)
    {
        try
        {
            if (listener != null)
            {
                listener!!.onBarButtonSelected(this, index)
            }

        }
        catch (ex: Exception)
        {
            UULog.error(javaClass, "notifyListener", ex)
        }

    }

    protected abstract fun instantiateButton(context: Context): T

    protected fun updateConstraints()
    {
        try
        {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            val parentId = LayoutParams.PARENT_ID

            val margin = getItemMargin()

            for (i in toolbarItems.indices)
            {
                val item = toolbarItems[i]
                val id = item.id

                var lastId = -1
                if (i > 0)
                {
                    lastId = toolbarItems[i - 1].id
                }

                var nextId = -1
                if (i + 1 < toolbarItems.size)
                {
                    nextId = toolbarItems[i + 1].id
                }

                constraintSet.connect(id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
                constraintSet.connect(id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)

                if (lastId != -1)
                {
                    constraintSet.connect(id, ConstraintSet.START, lastId, ConstraintSet.END)
                }
                else
                {
                    constraintSet.connect(id, ConstraintSet.START, parentId, ConstraintSet.START)
                }

                if (nextId != -1)
                {
                    constraintSet.connect(id, ConstraintSet.END, nextId, ConstraintSet.START)
                }
                else
                {
                    constraintSet.connect(id, ConstraintSet.END, parentId, ConstraintSet.END)
                }

                constraintSet.setMargin(id, ConstraintSet.TOP, margin)
                constraintSet.setMargin(id, ConstraintSet.BOTTOM, margin)
                constraintSet.setMargin(id, ConstraintSet.END, margin)

                if (i == 0)
                {
                    constraintSet.setMargin(id, ConstraintSet.START, margin)
                    constraintSet.setHorizontalChainStyle(id, ConstraintSet.CHAIN_SPREAD)
                }
                else
                {
                    constraintSet.setMargin(id, ConstraintSet.START, 0)
                }

                constraintSet.setApplyElevation(id, true)
                constraintSet.setElevation(id, 0f)

                constraintSet.constrainHeight(id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.constrainWidth(id, ConstraintSet.MATCH_CONSTRAINT)
            }

            constraintSet.applyTo(this)
        }

        catch (ex: Exception)
        {
            UULog.debug(javaClass, "updateConstraints", ex)
        }
    }
}
