package me.wcy.radapter3.simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import me.wcy.radapter.simple.R
import me.wcy.radapter3.simple.model.Image
import me.wcy.radapter3.simple.model.Text
import me.wcy.radapter3.simple.viewholder.ImageViewHolder
import me.wcy.radapter3.simple.viewholder.TextViewHolder1
import me.wcy.radapter3.simple.viewholder.TextViewHolder2
import me.wcy.radapter3.RAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val adapter = RAdapter(dataList)
        adapter.register(ImageViewHolder::class.java)
        adapter.register(Text::class.java) { data ->
            when (data.style) {
                1 -> TextViewHolder1::class.java
                2 -> TextViewHolder2::class.java
                else -> TextViewHolder2::class.java
            }
        }

        adapter.putExtra(100, "any extra")

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler.adapter = adapter
    }
}
