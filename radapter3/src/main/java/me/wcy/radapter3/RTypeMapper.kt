package me.wcy.radapter3

import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * 数据到 [RViewBinder] 的映射，支持一种数据对应多种 View
 *
 * 注意：映射方法会多次调用，如果要做到性能最优，建议复用 [RViewBinder]，不要每次都重新构造。
 */
interface RTypeMapper<T> {
    fun map(data: T): RViewBinder<out ViewBinding, T>
}