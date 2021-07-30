package uu.toolbox.ui.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import uu.toolbox.core.UUNonNullObjectDelegate
import uu.toolbox.network.UURemoteData

open class UURecyclerViewAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    interface UUViewHolder
    {
        fun bind(data: Any, position: Int)
        fun recycle()
    }

    abstract class ViewHolderFactory<T>(
            val layoutId: Int,
            val modelClass: Class<T>)
    {
        open fun createViewHolder(parent: View): RecyclerView.ViewHolder
        {
            return object: RecyclerView.ViewHolder(parent), UUViewHolder
            {
                override fun bind(data: Any, position: Int)
                {
                    bind(data as T, itemView, position)
                }

                override fun recycle()
                {
                    recycleHolder()
                }
            }
        }

        abstract fun bind(data: T, view: View, position: Int)

        protected open fun recycleHolder()
        {

        }

        open fun initView(parent: ViewGroup, itemView: View, viewType: Int)
        {

        }
    }


    val data: ArrayList<Any> = ArrayList()
    private val registeredViewHolderFactories: HashMap<Int, ViewHolderFactory<*>> = HashMap()
    private val registeredViewTypes: HashMap<Class<*>, Int> = HashMap()
    private val clickListener: View.OnClickListener

    var cellClickHandler: UUNonNullObjectDelegate<Any>? = null

    init
    {
        clickListener = View.OnClickListener()
        { v ->
            cellClickHandler?.onCompleted(v.tag)
        }

        val filter = IntentFilter()
        filter.addAction(UURemoteData.Notifications.DataDownloaded)

        val lbm = LocalBroadcastManager.getInstance(context)
        val br = object: BroadcastReceiver()
        {
            override fun onReceive(context: Context?, intent: Intent?)
            {
                if (context != null && intent != null)
                {
                    handlePhotoDownload(context, intent)
                }
            }
        }

        lbm.registerReceiver(br, filter)
    }

    private fun handlePhotoDownload(context: Context, intent: Intent)
    {
        if (UURemoteData.Notifications.DataDownloaded == intent.action)
        {
            val key = intent.getStringExtra(UURemoteData.NotificationKeys.RemotePath)

            for (i in data.indices)
            {
                val obj = data[i]
                if (obj is UURemoteData.UURemoteDataReceiver)
                {
                    if (key?.let { obj.hasRemoteData(it) } == true)
                    {
                        notifyItemChanged(i)
                    }
                }
            }
        }
    }

    fun setData(list: ArrayList<Any>)
    {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun registerViewHolderFactory(factory: ViewHolderFactory<*>)
    {
        val viewType = registeredViewHolderFactories.size
        registeredViewHolderFactories[viewType] = factory
        registeredViewTypes[factory.modelClass] = viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val factory = registeredViewHolderFactories[viewType]
        if (factory == null)
        {
            throw RuntimeException("No factory registered for $viewType!")
        }

        val view = LayoutInflater.from(parent.context).inflate(factory.layoutId, parent, false)
        factory.initView(parent, view, viewType)
        return factory.createViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val item = data[position]
        holder.itemView.tag = item
        holder.itemView.setOnClickListener(clickListener)

        if (holder is UUViewHolder)
        {
            holder.bind(item, position)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        val clazz = data[position].javaClass
        val viewType = registeredViewTypes[clazz]

        if (viewType == null)
        {
            throw RuntimeException("No view type registered for $clazz")
        }

        return viewType
    }

    override fun getItemCount(): Int
    {
        return data.size
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder)
    {
        super.onViewRecycled(holder)

        if (holder is UUViewHolder)
        {
            holder.recycle()
        }
    }
}
