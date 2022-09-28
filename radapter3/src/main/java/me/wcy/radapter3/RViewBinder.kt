package me.wcy.radapter3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * Created by wangchenyan.top on 2022/9/25.
 */
abstract class RViewBinder<VB : ViewBinding, T> {
    internal lateinit var viewBindingClazz: KClass<*>
    lateinit var adapter: RAdapter<Any>

    fun onCreateViewHolder(parent: ViewGroup): ViewBindingHolder<VB> {
        val inflateMethod = viewBindingClazz.java.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.javaPrimitiveType
        )
        val binding =
            inflateMethod.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        return ViewBindingHolder(binding, this)
    }

    internal fun onBindInternal(viewBinding: ViewBinding, item: Any, position: Int) {
        onBind(viewBinding as VB, item as T, position)
    }

    abstract fun onBind(viewBinding: VB, item: T, position: Int)
}
