package me.wcy.radapter3.simple.viewholder

import me.wcy.radapter.simple.databinding.ViewHolderText2Binding
import me.wcy.radapter3.simple.model.Text
import me.wcy.radapter3.RViewHolder

class TextViewHolder2(private val viewBinding: ViewHolderText2Binding) : RViewHolder<ViewHolderText2Binding, Text>(viewBinding) {

    override fun refresh() {
        viewBinding.text2.text = data().text
    }
}