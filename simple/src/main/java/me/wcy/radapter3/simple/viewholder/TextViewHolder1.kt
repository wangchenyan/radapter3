package me.wcy.radapter3.simple.viewholder

import me.wcy.radapter.simple.databinding.ViewHolderText1Binding
import me.wcy.radapter3.simple.model.Text
import me.wcy.radapter3.RViewHolder

class TextViewHolder1(private val viewBinding: ViewHolderText1Binding) : RViewHolder<ViewHolderText1Binding, Text>(viewBinding) {

    override fun refresh() {
        viewBinding.text1.text = data().text
    }
}