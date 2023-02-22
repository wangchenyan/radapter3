package me.wcy.radapter3

import androidx.viewbinding.ViewBinding

/**
 * 数据到 [RItemBinder] 的映射，支持一种数据对应多种 View
 *
 * 注意：
 *   - 使用 [RTypeMapper] 映射 [RItemBinder]，请务必复写 [RItemBinder.getViewBindingClazz] 方法！
 *   - 映射方法会多次调用，如果要做到性能最优，建议复用 [RItemBinder]，不要每次都重新构造。
 */
interface RTypeMapper<T> {
    fun map(data: T): RItemBinder<out ViewBinding, T>
}