package me.wcy.radapter3

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * 支持多种类型的 RecyclerView Adapter
 */
class RAdapter<T> : RecyclerView.Adapter<ViewBindingHolder<*>>() {
    private val dataList: MutableList<T> = mutableListOf()
    private val typePool by lazy { RTypePool() }

    /**
     * 注册 ViewBinder
     *
     * @param viewBinder 视图绑定器
     */
    inline fun <reified VB : ViewBinding, reified D> register(viewBinder: RViewBinder<VB, D>): RAdapter<T> {
        register(D::class.java, object : RTypeMapper<D> {
            override fun map(data: D): Pair<RViewBinder<out ViewBinding, D>, KClass<out ViewBinding>> {
                return Pair(viewBinder, VB::class)
            }
        })
        return this
    }

    /**
     * 注册 ViewBinder
     *
     * @param model 数据类型
     * @param mapper 数据到 [RViewBinder] 的映射，支持一种数据对应多种 View
     */
    fun <D> register(model: Class<D>, mapper: RTypeMapper<D>): RAdapter<T> {
        typePool.register(model, mapper)
        return this
    }

    /**
     * 获取数据集
     */
    fun getDataList(): List<T> {
        return dataList
    }

    /**
     * 刷新数据集
     */
    fun refresh(list: List<T>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 添加数据并刷新视图
     */
    fun add(item: T, index: Int? = null) {
        if (index != null && index !in 0..dataList.size) {
            Log.w(TAG, "下标越界，size: ${dataList.size}, index: $index")
            return
        }
        val position = index ?: dataList.size
        dataList.add(position, item)
        notifyItemInserted(position)
    }

    /**
     * 添加数据集并刷新视图
     */
    fun addAll(list: List<T>) {
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * 移除数据并刷新视图
     */
    fun remove(position: Int) {
        if (position !in 0 until dataList.size) {
            Log.w(TAG, "下标越界，size: ${dataList.size}, position: $position")
            return
        }
        dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 移除数据并刷新视图
     */
    fun remove(item: T) {
        remove(dataList.indexOf(item))
    }

    /**
     * 更新数据并刷新视图
     */
    fun change(item: T, position: Int) {
        if (position !in 0 until dataList.size) {
            Log.w(TAG, "下标越界，size: ${dataList.size}, position: $position")
            return
        }
        if (item == null) {
            Log.w(TAG, "item is null")
            return
        }
        dataList[position] = item
        notifyItemChanged(position)
    }

    /**
     * 数据是否为空
     */
    fun isEmpty() = dataList.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<*> {
        val viewBinder = typePool.getViewBinder(viewType)
        viewBinder.adapter = this as RAdapter<Any>
        return viewBinder.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<*>, position: Int) {
        try {
            holder.viewBinder.onBindInternal(holder.vb, dataList[position] as Any, position)
        } catch (e: Throwable) {
            Log.e(TAG, "bind view error", e)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val data = dataList[position]
        val type = typePool.getTypePosition(data)
        if (type < 0) {
            throw IllegalStateException("can not find view type for ${data}, have you register the data?")
        }
        return type
    }

    companion object {
        private const val TAG = "RAdapter"
    }
}
