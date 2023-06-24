package me.wcy.radapter3

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by wangchenyan.top on 2023/6/24.
 */
class DisableDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }
}