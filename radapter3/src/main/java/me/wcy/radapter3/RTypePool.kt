package me.wcy.radapter3

import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

/**
 * 列表 item 实体管理器
 *
 * Created by wangchenyan on 2018/9/21.
 */
internal class RTypePool {
    private val typeList by lazy { mutableListOf<RType<*>>() }
    private val itemBinderList: MutableList<RItemBinder<*, *>> by lazy {
        mutableListOf()
    }

    /**
     * 注册一个实体
     */
    fun <T : Any> register(
        model: KClass<T>,
        mapper: RTypeMapper<T>
    ) {
        val type = RType(model, mapper)
        val index = typeList.indexOf(type)
        if (index >= 0) {
            typeList[index] = type
        } else {
            typeList.add(type)
        }
    }

    /**
     * 根据数据获取 item 实体对应的 [RItemBinder] 位置，即 [RecyclerView.Adapter.getItemViewType]
     */
    fun getTypePosition(data: Any?): Int {
        if (data == null) {
            return -1
        }
        val dataClazz = data.javaClass
        var mapper: RTypeMapper<Any>? = null
        for (type in typeList) {
            if (dataClazz == type.model.java) {
                mapper = type.mapper as RTypeMapper<Any>
                break
            }
        }
        if (mapper == null) {
            // 尝试查找子类
            for (type in typeList) {
                if (type.model.java.isAssignableFrom(dataClazz)) {
                    mapper = type.mapper as RTypeMapper<Any>
                    break
                }
            }
        }
        if (mapper == null) {
            return -1
        }
        // TODO 避免多次创建 ItemBinder 实例
        val itemBinder = mapper.map(data)
        var exist = itemBinderList.find { it.javaClass == itemBinder.javaClass }
        if (exist == null) {
            itemBinderList.add(itemBinder)
            exist = itemBinder
        }
        return itemBinderList.indexOf(exist)
    }

    /**
     * 获取 [RItemBinder]
     */
    fun getItemBinder(viewType: Int): RItemBinder<*, *> {
        return itemBinderList[viewType]
    }
}