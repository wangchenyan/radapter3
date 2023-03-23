package me.wcy.radapter3

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * 支持多种类型的 RecyclerView Adapter
 */
class RAdapter<T>(diffCallback: DiffUtil.ItemCallback<T> = DefaultDiffCallback()) :
    ListAdapter<T, ViewBindingHolder<*>>(diffCallback) {
    private val typePool by lazy { RTypePool() }

    /**
     * 注册 [RItemBinder]
     *
     * @param itemBinder 视图绑定器
     */
    inline fun <reified VB : ViewBinding, reified D : Any> register(itemBinder: RItemBinder<VB, D>) =
        apply {
            register(itemBinder, VB::class, D::class)
        }

    /**
     * 注册 [RItemBinder]
     *
     * @param itemBinder 视图绑定器
     */
    fun <VB : ViewBinding, D : Any> register(
        itemBinder: RItemBinder<VB, D>,
        bindingClazz: KClass<VB>,
        dataClazz: KClass<D>
    ) = apply {
        register(dataClazz, object : RTypeMapper<D> {
            override fun map(data: D): RItemBinder<out ViewBinding, D> {
                return itemBinder.apply {
                    setViewBindingClazz(bindingClazz)
                }
            }
        })
    }

    /**
     * 注册 [RItemBinder]
     *
     * @param model 数据类型
     * @param mapper 数据 [D] 到 [RItemBinder] 的映射，支持一种数据对应多种 View
     *
     * 注意：
     *   - 使用 [RTypeMapper] 映射 [RItemBinder]，请务必复写 [RItemBinder.getViewBindingClazz] 方法！
     */
    fun <D : Any> register(model: KClass<D>, mapper: RTypeMapper<D>) = apply {
        typePool.register(model, mapper)
    }

    /**
     * 获取数据集
     */
    fun getDataList(): List<T> {
        return currentList
    }

    /**
     * 刷新数据集
     */
    @MainThread
    fun refresh(list: List<T>) {
        val newList = ArrayList(list)
        submitList(newList)
    }

    /**
     * 添加数据并刷新视图
     */
    @MainThread
    fun add(item: T, index: Int? = null) {
        val currentList = currentList
        if (index != null && index !in 0..currentList.size) {
            Log.w(TAG, "下标越界，size: ${currentList.size}, index: $index")
            return
        }
        val newList = ArrayList(currentList)
        val position = index ?: newList.size
        newList.add(position, item)
        submitList(newList)
    }

    /**
     * 添加数据集并刷新视图
     */
    @MainThread
    fun addAll(list: List<T>) {
        val newList = ArrayList(currentList).apply {
            addAll(list)
        }
        submitList(newList)
    }

    /**
     * 移除数据并刷新视图
     */
    @MainThread
    fun remove(position: Int) {
        val currentList = currentList
        if (position !in 0 until currentList.size) {
            Log.w(TAG, "下标越界，size: ${currentList.size}, position: $position")
            return
        }
        val newList = ArrayList(currentList)
        newList.removeAt(position)
        submitList(newList)
    }

    /**
     * 移除数据并刷新视图
     */
    @MainThread
    fun remove(item: T) {
        remove(currentList.indexOf(item))
    }

    /**
     * 更新数据并刷新视图
     */
    @MainThread
    fun change(item: T, position: Int) {
        if (item == null) {
            Log.w(TAG, "item is null")
            return
        }
        val currentList = currentList
        if (position !in 0 until currentList.size) {
            Log.w(TAG, "下标越界，size: ${currentList.size}, position: $position")
            return
        }
        val newList = ArrayList(currentList)
        newList[position] = item
        submitList(newList)
    }

    /**
     * 数据是否为空
     */
    fun isEmpty() = currentList.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<*> {
        val itemBinder = typePool.getItemBinder(viewType)
        itemBinder.adapter = this as RAdapter<Any>
        return itemBinder.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<*>, position: Int) {
        try {
            holder.itemBinder.onBindInternal(holder.vb, currentList[position] as Any, position)
        } catch (e: Throwable) {
            Log.e(TAG, "bind view error", e)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = currentList[position]
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
