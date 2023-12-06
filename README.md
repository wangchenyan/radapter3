# radapter3

[![](https://jitpack.io/v/wangchenyan/radapter3.svg)](https://jitpack.io/#wangchenyan/radapter3)
使用 radapter，你可以方便的构造多种类型的列表视图。

![](https://raw.githubusercontent.com/wangchenyan/radapter3/master/art/recycler-view.jpg)

## 前言

当前 ViewBinding 很受欢迎，我们也要紧跟潮流呀，这不，radapter3 来了，支持 ViewBinding，并且利用函数内联，简化了数据注册，
使用更加简单。
由于 API 和旧版本不兼容，干脆新开一个仓库。如果不想用 ViewBinding，还想用老版本，[传送门](https://github.com/wangchenyan/radapter) 

## 使用

### Gradle

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency

```
dependencies {
    implementation("com.github.wangchenyan:radapter3:3.1.1")
}
```

### 混淆

该库已经默认添加了混淆配置，使用 AAR 依赖是不需要特殊处理的，如果使用 Jar 依赖，需要手动添加以下配置

```
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}

```

## 介绍

原始的使用 Recycler View 的方式我就不赘述了，总之是比较繁琐的，我直接介绍 radapter 如何使用。

对于多种类型的列表，我们一般是根据 `getItemViewType()` 区分类型，来构造、渲染视图，这种繁琐的方式大家应该已经写得要吐了吧，反正我是不想再写了。

radapter 就是来解救你的，它可以让你丢弃 Adapter，丢弃多类型时繁琐的逻辑，只需要保留有用的部分，即
ViewHolder，使 ViewHolder 可以专注于处理自己的业务。

来看一个🌰，一个图文混合的列表

```
// 首先，添加数据，Image 保存了图片资源ID，Text 保存了文本
val dataList = mutableListOf<Any>()
dataList.add(Image(R.mipmap.image1))
dataList.add(Text("渊虹"))
dataList.add(Image(R.mipmap.image2))
dataList.add(Text("鲨齿"))
dataList.add(Image(R.mipmap.image3))
dataList.add(Text("干将莫邪"))
dataList.add(Image(R.mipmap.image4))
dataList.add(Text("墨眉"))

// 使用 radapter，注册 ViewHolder
val adapter = RAdapter<Any>()
adapter.register(ImageItemBinder())
adapter.register(TextItemBinder())

// 设置 adapter
recycler.layoutManager = LinearLayoutManager(this)
recycler.adapter = adapter
```

相比旧版本，你甚至不用注册数据类型和布局了，只需要在 ImageItemBinder 中通过泛型声明即可

```
class ImageItemBinder : RItemBinder<ViewHolderImageBinding, Image>() {
    override fun onBind(viewBinding: ViewHolderImageBinding, item: Image, position: Int) {
        viewBinding.image.setImageResource(item.resId)
    }
}
```

继承 `RItemBinder`，复写 `onBind` 方法刷新视图即可

看下效果

![](https://raw.githubusercontent.com/wangchenyan/radapter3/master/art/image01.jpg)

到现在，我们可以根据数据类型区分不同的 ViewHolder，但有时候同一种类型的数据，可能根据不同的属性，也要展示不同的
ViewHolder，怎么办呢？

我们把🌰稍微改一下，还是图文混合，现在可以设置文本的样式，样式不同要使用不同的 ViewHolder，看一下如何实现

```
// 我们给 Text 加上 style 属性
val dataList = mutableListOf<Any>()
dataList.add(Image(R.mipmap.image1))
dataList.add(Text("渊虹", 2))
dataList.add(Image(R.mipmap.image2))
dataList.add(Text("鲨齿", 1))
dataList.add(Image(R.mipmap.image3))
dataList.add(Text("干将莫邪", 2))
dataList.add(Image(R.mipmap.image4))
dataList.add(Text("墨眉", 1))

val adapter = RAdapter<Any>()
adapter.register(ImageItemBinder())
// 注册 Text 时使用 RTypeMapper，根据 Text 的属性，使用不同的 ViewHolder
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

recycler.layoutManager = LinearLayoutManager(this)
recycler.adapter = adapter
```

**针对一种数据对应多种布局的场景，因为无法使用内联，无法自动获取布局 ViewBinding 类型，因此需要在 RItemBinder 中重写获取布局 ViewBinding 类的方法。**

```kotlin
class TextItemBinder1 : RItemBinder<ViewHolderText1Binding, Text>() {
    override fun onBind(viewBinding: ViewHolderText1Binding, item: Text, position: Int) {
        viewBinding.text1.text = item.text
    }
    // 针对一种数据对应多种布局的场景，需要在 RItemBinder 中重写获取布局 ViewBinding 类的方法
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
    // 针对一种数据对应多种布局的场景，需要在 RItemBinder 中重写获取布局 ViewBinding 类的方法
    override fun getViewBindingClazz(): KClass<*> = ViewHolderText2Binding::class
}
```

仍然很简单，看下效果

![](https://raw.githubusercontent.com/wangchenyan/radapter3/master/art/image02.jpg)
