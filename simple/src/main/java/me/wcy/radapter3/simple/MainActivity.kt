package me.wcy.radapter3.simple

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.wcy.radapter.simple.R
import me.wcy.radapter.simple.databinding.ViewHolderImageBinding
import me.wcy.radapter.simple.databinding.ViewHolderText1Binding
import me.wcy.radapter.simple.databinding.ViewHolderText2Binding
import me.wcy.radapter3.RAdapter
import me.wcy.radapter3.RViewBinder
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recycler)

        val dataList = mutableListOf<Any>()
        dataList.add(Image(R.mipmap.image1))
        dataList.add(Text("渊虹", 2))
        dataList.add(Image(R.mipmap.image2))
        dataList.add(Text("鲨齿", 1))
        dataList.add(Image(R.mipmap.image3))
        dataList.add(Text("干将莫邪", 2))
        dataList.add(Image(R.mipmap.image4))
        dataList.add(Text("墨眉", 1))
        dataList.add(Image(R.mipmap.image5))
        dataList.add(Text("水寒", 2))
        dataList.add(Image(R.mipmap.image6))
        dataList.add(Text("太阿", 1))

        val adapter = RAdapter<Any>()
        adapter.register(ImageViewBinder())
        val textViewBinder1 = TextViewBinder1()
        val textViewBinder2 = TextViewBinder2()
        adapter.register(Text::class.java) { data ->
            when (data.style) {
                1 -> textViewBinder1
                2 -> textViewBinder2
                else -> textViewBinder2
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        adapter.refresh(dataList)
    }

    data class Text(val text: CharSequence, val style: Int)

    data class Image(val resId: Int)

    class ImageViewBinder : RViewBinder<ViewHolderImageBinding, Image>() {
        override val viewBindingClazz: KClass<ViewHolderImageBinding>
            get() = ViewHolderImageBinding::class

        override fun onBind(viewBinding: ViewHolderImageBinding, item: Image, position: Int) {
            viewBinding.image.setImageResource(item.resId)
        }
    }

    class TextViewBinder1 : RViewBinder<ViewHolderText1Binding, Text>() {
        override val viewBindingClazz: KClass<ViewHolderText1Binding>
            get() = ViewHolderText1Binding::class

        override fun onBind(viewBinding: ViewHolderText1Binding, item: Text, position: Int) {
            viewBinding.text1.text = item.text
        }
    }

    class TextViewBinder2 : RViewBinder<ViewHolderText2Binding, Text>() {
        override val viewBindingClazz: KClass<ViewHolderText2Binding>
            get() = ViewHolderText2Binding::class

        override fun onBind(viewBinding: ViewHolderText2Binding, item: Text, position: Int) {
            viewBinding.text2.text = item.text
            viewBinding.text2.setOnClickListener {
                Toast.makeText(viewBinding.root.context, "点击：${item.text}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
