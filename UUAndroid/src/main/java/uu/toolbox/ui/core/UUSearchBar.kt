package uu.toolbox.ui.core

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import uu.toolbox.logging.UULog

abstract class UUSearchBar : ConstraintLayout
{
    interface Listener
    {
        fun onSearchTextChanged(text: String)
        fun onSearchTapped(text: String)
    }

    var listener: Listener? = null

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
    abstract fun getRootLayoutId() : Int

    @IdRes
    abstract fun getSearchBoxId() : Int

    private fun init(attrs: AttributeSet?)
    {
        val rootLayoutId = getRootLayoutId()
        val searchBoxId = getSearchBoxId()
        if (rootLayoutId != -1  && searchBoxId != -1)
        {
            inflate(getContext(), rootLayoutId, this)

            val searchBox = findViewById<UUEditText>(searchBoxId)

            searchBox.setOnEditorActionListener(TextView.OnEditorActionListener()
            { _, actionId, _ ->

                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchBox.clearFocus()
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(searchBox.windowToken, 0)

                    notifySearchTapped(searchBox.text?.toString() ?: "")
                    return@OnEditorActionListener true
                }

                return@OnEditorActionListener false
            })

            searchBox.addTextChangedListener(object : TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {

                }

                override fun afterTextChanged(s: Editable)
                {
                    notifyTextChanged(s.toString())
                }
            })
        }
    }

    private fun notifyTextChanged(@NonNull text: String)
    {
        if (listener != null)
        {
            try
            {
                listener!!.onSearchTextChanged(text)
            }
            catch (ex: Exception)
            {
                UULog.debug(javaClass, "notifyTextChanged", ex)
            }

        }
    }

    private fun notifySearchTapped(@NonNull text: String)
    {
        if (listener != null)
        {
            try
            {
                listener!!.onSearchTapped(text)
            }
            catch (ex: Exception)
            {
                UULog.debug(javaClass, "notifySearchTapped", ex)
            }
        }
    }
}