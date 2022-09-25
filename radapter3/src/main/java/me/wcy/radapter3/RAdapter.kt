package me.wcy.radapter3

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * 支持多种类型的 RecyclerView Adapter
 */
class RAdapter<T> : RecyclerView.Adapter<ViewBindingHolder<*>>() {
    private val dataList: MutableList<T> = mutableListOf()
    private val typePool by lazy { RTypePool() }

    companion object {
        private const val TAG = "RAdapter"
    }

    /**
     * 注册 ViewBinder
     */
    inline fun <VB : ViewBinding, reified D> register(viewBinder: RViewBinder<VB, D>): RAdapter<T> {
        register(D::class.java) { viewBinder }
        return this
    }

    /**
     * 注册 ViewBinder
     */
    fun <D> register(
        model: Class<D>, mapper: (data: D) -> RViewBinder<*, D>
    ): RAdapter<T> {
        typePool.register(model, mapper)
        return this
    }

    /**
     * 获取数据列表
     */
    fun getDataList(): List<T> {
        return dataList
    }

    fun refresh(list: List<T>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun add(item: T, index: Int? = null) {
        val position = if (index != null && index in 0..dataList.size) {
            index
        } else {
            dataList.size
        }
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    fun addAll(list: List<T>) {
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        if (position in 0 until dataList.size) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun remove(item: T) {
        remove(dataList.indexOf(item))
    }

    fun change(item: T, position: Int) {
        if (position in 0 until dataList.size && item != null) {
            dataList[position] = item
            notifyItemChanged(position)
        }
    }

    fun isEmpty() = dataList.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<*> {
        val viewBinder = typePool.getViewBinder(viewType)
        viewBinder.adapter = this
        return viewBinder.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<*>, position: Int) {
        try {
            holder.viewBinder.onBindInternal(holder.vb, dataList[position] as Any, position)
        } catch (e: Throwable) {
            Log.e(TAG, "bind view holder error", e)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val data = dataList[position]
        val type = typePool.getTypePosition(data)
        if (type < 0) {
            throw IllegalStateException("can not find view type of ${data}, have you register the data?")
        }
        return type
    }
}
