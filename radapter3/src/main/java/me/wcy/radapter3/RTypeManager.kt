package me.wcy.radapter3

/**
 * 列表 item 实体管理器
 *
 * Created by wangchenyan on 2018/9/21.
 */
internal class RTypeManager {
    private val typeList by lazy { mutableListOf<RType<*>>() }
    private val holderClassList by lazy { mutableListOf<Class<out RViewHolder<*, *>>>() }

    /**
     * 注册一个实体
     */
    fun <T> register(model: Class<T>, converter: (data: T) -> Class<out RViewHolder<*, T>>) {
        val type = RType(model, converter)
        val index = typeList.indexOf(type)
        if (index >= 0) {
            typeList[index] = type
        } else {
            typeList.add(type)
        }
    }

    /**
     * 根据数据获取 item 实体对应的 ViewHolder 位置，即 Adapter.getItemViewType()
     */
    fun getTypePosition(data: Any?): Int {
        if (data == null) {
            return -1
        }
        val clazz = data.javaClass
        var converter: ((data: Any) -> Class<out RViewHolder<*, Any>>)? = null
        for (type in typeList) {
            if (clazz == type.model) {
                converter = type.converter as (data: Any) -> Class<out RViewHolder<*, Any>>
                break
            }
        }
        if (converter == null) {
            // 尝试查找子类
            for (type in typeList) {
                if (type.model.isAssignableFrom(clazz)) {
                    converter = type.converter as (data: Any) -> Class<out RViewHolder<*, Any>>
                    break
                }
            }
        }
        if (converter == null) {
            return -1
        }
        val holderClass = converter.invoke(data)
        if (!holderClassList.contains(holderClass)) {
            holderClassList.add(holderClass)
        }
        return holderClassList.indexOf(holderClass)
    }

    /**
     * 获取 ViewHolder class
     */
    fun getHolderClass(viewType: Int): Class<out RViewHolder<*, *>> {
        return holderClassList[viewType]
    }
}