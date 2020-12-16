package me.wcy.radapter3

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 支持多种类型的 RecyclerView Adapter
 */
class RAdapter(private val dataList: MutableList<*>) : RecyclerView.Adapter<RViewHolderWrap>() {
    private val typePool by lazy { RTypeManager() }
    private val extras by lazy { SparseArray<Any>() }
    private val dataObserver by lazy { DataObserver() }
    private val attachedHolderList by lazy { mutableListOf<RViewHolderWrap>() }

    companion object {
        private const val TAG = "RAdapter"
    }

    /**
     * 注册 ViewHolder
     *
     * @param viewHolder ViewHolder
     */
    inline fun <VB : ViewBinding, reified T> register(viewHolder: Class<out RViewHolder<VB, T>>): RAdapter {
        val dataClass = T::class.java
        register(dataClass) { viewHolder }
        return this
    }

    /**
     * 注册 ViewHolder
     *
     * @param model 数据类
     * @param converter 数据到 ViewHolder 的转换器
     */
    fun <T> register(model: Class<T>, converter: (data: T) -> Class<out RViewHolder<*, T>>): RAdapter {
        typePool.register(model, converter)
        return this
    }

    /**
     * 获取数据列表
     */
    fun getDataList(): MutableList<*> {
        return dataList
    }

    /**
     * 获取额外参数
     */
    fun getExtra(key: Int): Any? {
        return extras.get(key)
    }

    /**
     * 设置额外参数，可在 ViewHolder 中读取 ViewHolder.getExtra(key)
     */
    fun putExtra(key: Int, value: Any): RAdapter {
        extras.put(key, value)
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolderWrap {
        val clazz = typePool.getHolderClass(viewType)
        val viewHolder = createViewHolder(clazz as Class<RViewHolder<ViewBinding, Any>>, parent)
        viewHolder.setAdapter(this)
        return RViewHolderWrap(viewHolder)
    }

    private inline fun <reified VB : ViewBinding, T> createViewHolder(holderClass: Class<RViewHolder<VB, T>>, parent: ViewGroup): RViewHolder<VB, T> {
        val bindingClass = (holderClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
        val viewBinding = inflateMethod.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        val constructor = holderClass.getConstructor(bindingClass)
        return constructor.newInstance(viewBinding)
    }

    override fun onBindViewHolder(holderWrap: RViewHolderWrap, position: Int) {
        try {
            val holder = holderWrap.holder
            holder.setPosition(position)
            holder.setData(dataList[position])
            holder.onBindViewHolder()
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
            throw IllegalStateException("can not find view type of ${data?.javaClass}, have you register the data?")
        }
        return type
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        registerAdapterDataObserver(dataObserver)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        unregisterAdapterDataObserver(dataObserver)
    }

    override fun onViewAttachedToWindow(holderWrap: RViewHolderWrap) {
        super.onViewAttachedToWindow(holderWrap)
        holderWrap.holder.onViewAttachedToWindow()
        attachedHolderList.add(holderWrap)
    }

    override fun onViewDetachedFromWindow(holderWrap: RViewHolderWrap) {
        super.onViewDetachedFromWindow(holderWrap)
        holderWrap.holder.onViewDetachedFromWindow()
        attachedHolderList.remove(holderWrap)
    }

    /**
     * 数据改变时通知 ViewHolder position 过期
     */
    private inner class DataObserver : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            notifyHolderPosExpired()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            notifyHolderPosExpired()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            notifyHolderPosExpired()
        }
    }

    /**
     * 数据顺序改变，通知 ViewHolder position 过期
     */
    private fun notifyHolderPosExpired() {
        for (holder in attachedHolderList) {
            holder.holder.setPosExpired()
        }
    }
}
