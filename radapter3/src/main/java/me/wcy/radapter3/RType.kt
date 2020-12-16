package me.wcy.radapter3

/**
 * 一个列表 item 的实体，包含数据类型和 ViewHolder
 *
 * Created by wangchenyan on 2018/9/21.
 */
internal class RType<T>(
        internal val model: Class<T>,
        internal val converter: (data: T) -> Class<out RViewHolder<*, T>>) {

    override fun hashCode(): Int {
        return model.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is RType<*> && model == other.model
    }
}