package me.wcy.radapter3

import kotlin.reflect.KClass

/**
 * 一个列表 item 的实体，包含数据类型 [T] 和视图绑定器 [RItemBinder]
 *
 * Created by wangchenyan on 2018/9/21.
 */
internal class RType<T : Any>(
    internal val model: KClass<T>,
    internal val mapper: RTypeMapper<T>
) {

    override fun hashCode(): Int {
        return model.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is RType<*> && model == other.model
    }
}