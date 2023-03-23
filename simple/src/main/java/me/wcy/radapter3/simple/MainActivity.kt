package me.wcy.radapter3.simple

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import me.wcy.radapter.simple.R
import me.wcy.radapter.simple.databinding.ViewHolderImageBinding
import me.wcy.radapter.simple.databinding.ViewHolderText1Binding
import me.wcy.radapter.simple.databinding.ViewHolderText2Binding
import me.wcy.radapter3.RAdapter
import me.wcy.radapter3.RItemBinder
import me.wcy.radapter3.RTypeMapper
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recycler)
        val btnAdd: Button = findViewById(R.id.btnAdd)
        val btnRemove: Button = findViewById(R.id.btnRemove)
        val btnAddAll: Button = findViewById(R.id.btnAddAll)
        val btnChange: Button = findViewById(R.id.btnChange)
        val btnRefresh: Button = findViewById(R.id.btnRefresh)

        val adapter = RAdapter<Any>()
        adapter.register(ImageItemBinder())
        adapter.register(Text::class, object : RTypeMapper<Text> {
            val textViewBinder1 = TextItemBinder1()
            val textViewBinder2 = TextItemBinder2()

            override fun map(data: Text): RItemBinder<out ViewBinding, Text> {
                return when (data.style) {
                    1 -> textViewBinder1
                    2 -> textViewBinder2
                    else -> textViewBinder2
                }
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        adapter.refresh(getDataList())

        btnAdd.setOnClickListener {
            adapter.add(Text("插入一项", 1), 1)
        }
        btnRemove.setOnClickListener {
            adapter.remove(2)
        }
        btnAddAll.setOnClickListener {
            adapter.addAll(
                listOf(
                    Text("追加多项", 2),
                    Image(R.mipmap.image7)
                )
            )
        }
        btnChange.setOnClickListener {
            adapter.change(Image(R.mipmap.image8), 1)
        }
        btnRefresh.setOnClickListener {
            adapter.refresh(getDataList())
        }
    }

    private fun getDataList(): List<Any> {
        return mutableListOf<Any>().apply {
            add(Image(R.mipmap.image1))
            add(Text("渊虹", 2))
            add(Image(R.mipmap.image2))
            add(Text("鲨齿", 1))
            add(Image(R.mipmap.image3))
            add(Text("干将莫邪", 2))
            add(Image(R.mipmap.image4))
            add(Text("墨眉", 1))
            add(Image(R.mipmap.image5))
            add(Text("水寒", 2))
            add(Image(R.mipmap.image6))
            add(Text("太阿", 1))
        }
    }

    data class Text(val text: CharSequence, val style: Int)

    data class Image(val resId: Int)

    class ImageItemBinder : RItemBinder<ViewHolderImageBinding, Image>() {
        override fun onBind(viewBinding: ViewHolderImageBinding, item: Image, position: Int) {
            viewBinding.image.setImageResource(item.resId)
        }
    }

    class TextItemBinder1 : RItemBinder<ViewHolderText1Binding, Text>() {
        override fun onBind(viewBinding: ViewHolderText1Binding, item: Text, position: Int) {
            viewBinding.text1.text = item.text
        }

        override fun getViewBindingClazz(): KClass<*> = ViewHolderText1Binding::class
    }

    class TextItemBinder2 : RItemBinder<ViewHolderText2Binding, Text>() {
        override fun onBind(viewBinding: ViewHolderText2Binding, item: Text, position: Int) {
            viewBinding.text2.text = item.text
            viewBinding.text2.setOnClickListener {
                Toast.makeText(viewBinding.root.context, "点击：${item.text}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun getViewBindingClazz(): KClass<*> = ViewHolderText2Binding::class
    }
}
