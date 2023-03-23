package me.wcy.radapter3

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * Created by wangchenyan.top on 2023/3/23.
 */
class DefaultDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}