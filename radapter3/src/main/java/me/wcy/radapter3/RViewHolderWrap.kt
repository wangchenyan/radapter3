package me.wcy.radapter3

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by wcy on 2020/12/16.
 */
class RViewHolderWrap(internal val holder: RViewHolder<*, *>) : RecyclerView.ViewHolder(holder.viewBinding.root)