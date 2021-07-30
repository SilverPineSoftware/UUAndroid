package uu.toolbox.ui.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText

class UUEditText : AppCompatEditText
{
    internal val DRAWABLE_START = 0
    internal val DRAWABLE_END = 2

    private var startDrawable: Drawable? = null
    private var endDrawable: Drawable? = null
    private var ignoreOneLongClick: Boolean = false
    private var rightToLeftLayout: Boolean = false
    private var disallowImeEnter = false

    private fun endDrawableWidth(): Int
    {
        return getEndDrawable()?.bounds?.width() ?: 0
    }

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

    private fun init(attrs: AttributeSet?)
    {
        startDrawable = getStartDrawable()
        endDrawable = getEndDrawable()
        ignoreOneLongClick = false
        rightToLeftLayout = (resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL)


        setOnTouchListener()
        { _, event ->

            if (event.action == MotionEvent.ACTION_UP)
            {
                val x = event.x
                val w = endDrawableWidth()
                val left = this.left
                val right = this.right

                if (rightToLeftLayout)
                {
                    if (x - right <= left + w)
                    {
                        ignoreOneLongClick = true
                        setText("")
                        return@setOnTouchListener true
                    }
                }
                else
                {
                    if (x + left >= right - w)
                    {
                        ignoreOneLongClick = true
                        setText("")
                        return@setOnTouchListener true
                    }
                }

            }

            return@setOnTouchListener false
        }


        /*setOnFocusChangeListener((v, hasFocus) ->
           {
               if (hasFocus)
               {
                   UUView.toggleSoftwareKeyboard(this, true);
               }
           });*/

        setOnLongClickListener()
        {
            if (ignoreOneLongClick)
            {
                ignoreOneLongClick = false
                return@setOnLongClickListener true
            }

            return@setOnLongClickListener false
        }

        updateClearButton()

        addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                updateClearButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun setDynamicStartDrawable(drawable: Drawable?)
    {
        startDrawable = drawable
        updateClearButton()
    }

    fun setDisallowImeEnter(disallowImeEnter: Boolean)
    {
        this.disallowImeEnter = disallowImeEnter
    }

    private fun getCompoundDrawableAtIndex(index: Int): Drawable?
    {
        var d: Drawable? = null
        val list = compoundDrawablesRelative
        if (list.size > index)
        {
            d = list[index]
        }

        return d
    }

    private fun getStartDrawable(): Drawable?
    {
        return getCompoundDrawableAtIndex(DRAWABLE_START)
    }

    private fun getEndDrawable(): Drawable?
    {
        return getCompoundDrawableAtIndex(DRAWABLE_END)
    }

    private fun updateClearButton()
    {
        if ((text?.length ?: 0) > 0)
        {
            setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, endDrawable, null)
        }
        else
        {
            setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null)
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean
    {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)
        {
            clearFocus()
        }

        return super.onKeyPreIme(keyCode, event)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection?
    {
        val conn = super.onCreateInputConnection(outAttrs)

        if (disallowImeEnter)
        {
            outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        }

        return conn
    }
}