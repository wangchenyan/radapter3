package me.wcy.radapter3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * Created by wangchenyan.top on 2022/9/25.
 */
abstract class RViewBinder<VB : ViewBinding, T> {
    private var viewBindingClazz: KClass<*>? = null
    lateinit var adapter: RAdapter<Any>

    /**
     * 如果使用 [RTypeMapper] 映射 [RViewBinder]，请务必复写该方法！
     */
    open fun getViewBindingClazz(): KClass<*> {
        if (viewBindingClazz == null) {
            throw IllegalStateException(
                "viewBindingClazz can not be null!" +
                        "\nIf you use 'RTypeMapper', place override 'getViewBindingClazz()' method from 'RViewBinder'" +
                        ", and provide ViewBinding class"
            )
        }
        return viewBindingClazz!!
    }

    internal fun setViewBindingClazz(clazz: KClass<*>) {
        viewBindingClazz = clazz
    }

    fun onCreateViewHolder(parent: ViewGroup): ViewBindingHolder<VB> {
        val inflateMethod = getViewBindingClazz().java.getMethod(
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
