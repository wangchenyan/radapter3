package me.wcy.radapter3

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by wangchenyan.top on 2022/9/25.
 */
class ViewBindingHolder<VB : ViewBinding>(val vb: VB, val itemBinder: RItemBinder<VB, *>) :
    RecyclerView.ViewHolder(vb.root)