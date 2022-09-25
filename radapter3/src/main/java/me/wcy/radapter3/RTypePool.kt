package me.wcy.radapter3

/**
 * 列表 item 实体管理器
 *
 * Created by wangchenyan on 2018/9/21.
 */
internal class RTypePool {
    private val typeList by lazy { mutableListOf<RType<*>>() }
    private val viewBinderList by lazy { mutableListOf<RViewBinder<*, *>>() }

    /**
     * 注册一个实体
     */
    fun <T> register(model: Class<T>, mapper: (data: T) -> RViewBinder<*, T>) {
        val type = RType(model, mapper)
        val index = typeList.indexOf(type)
        if (index >= 0) {
            typeList[index] = type
        } else {
            typeList.add(type)
        }
    }

    /**
     * 根据数据获取 item 实体对应的 ViewBinder 位置，即 Adapter.getItemViewType()
     */
    fun getTypePosition(data: Any?): Int {
        if (data == null) {
            return -1
        }
        val clazz = data.javaClass
        var mapper: ((data: Any) -> RViewBinder<*, *>)? = null
        for (type in typeList) {
            if (clazz == type.model) {
                mapper = type.mapper as (data: Any) -> RViewBinder<*, *>
                break
            }
        }
        if (mapper == null) {
            // 尝试查找子类
            for (type in typeList) {
                if (type.model.isAssignableFrom(clazz)) {
                    mapper = type.mapper as (data: Any) -> RViewBinder<*, *>
                    break
                }
            }
        }
        if (mapper == null) {
            return -1
        }
        // TODO 避免 invoke 多次创建 ViewBinder 实例
        val viewBinder = mapper.invoke(data)
        var exist = viewBinderList.find { it.javaClass == viewBinder.javaClass }
        if (exist == null) {
            viewBinderList.add(viewBinder)
            exist = viewBinder
        }
        return viewBinderList.indexOf(exist)
    }

    /**
     * 获取 ViewBinder
     */
    fun getViewBinder(viewType: Int): RViewBinder<*, *> {
        return viewBinderList[viewType]
    }
}