package me.wcy.radapter3

import android.content.Context
import androidx.viewbinding.ViewBinding

/**
 * ViewHolder 基类
 */
abstract class RViewHolder<VB : ViewBinding, T>(internal val viewBinding: VB) {
    protected val context: Context = viewBinding.root.context
    private var adapter: RAdapter? = null
    private var data: T? = null
    private var position = -1
    private var isPosExpired = false

    /**
     * 获取 adapter
     */
    protected fun adapter(): RAdapter {
        if (adapter == null) {
            throw IllegalStateException("ViewHolder has not bind adapter!")
        }
        return adapter!!
    }

    internal fun setAdapter(adapter: RAdapter) {
        this.adapter = adapter
    }

    /**
     * 获取 item 在列表中的位置
     */
    protected fun position(): Int {
        if (isPosExpired) {
            position = adapter().getDataList().indexOf(data())
            isPosExpired = false
        }
        return position
    }

    internal fun setPosition(position: Int) {
        this.position = position
    }

    @Suppress("UNCHECKED_CAST")
    internal fun setData(data: Any?) {
        this.data = data as T
    }

    /**
     * 获取数据
     */
    protected fun data(): T {
        if (data == null) {
            throw IllegalStateException("ViewHolder has not bind data!")
        }
        return data!!
    }

    internal fun setPosExpired() {
        isPosExpired = true
    }

    /**
     * 刷新界面
     */
    abstract fun onBindViewHolder()

    /**
     * 获取 adapter.putExtra 设置的额外参数
     */
    protected fun getExtra(key: Int): Any? {
        return adapter?.getExtra(key)
    }

    /**
     * ViewHolder 进入屏幕
     */
    open fun onViewAttachedToWindow() {
    }

    /**
     * ViewHolder 离开屏幕
     */
    open fun onViewDetachedFromWindow() {
    }
}
