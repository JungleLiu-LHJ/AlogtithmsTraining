# RecyvlerView 与 ListView

## 缓存机制

RecyclerView比ListView多两级缓存，支持多个离ItemView缓存，支持开发者自定义缓存处理逻辑，支持所有RecyclerView共用同一个RecyclerViewPool(缓存池)。

ListView 两级缓存：

![image-20200829104142457](C:\Users\40515\Desktop\面试准备\listView)

RecyclerView 四级缓存：

![image-20200829104219468](C:\Users\40515\Desktop\面试准备\recyclerview)

> 
>
> 1. mActiveViews和mAttachedScrap功能相似，意义在于快速重用屏幕上可见的列表项ItemView，而不需要重新createView和bindView； 
> 2.  mScrapView和mCachedViews + mReyclerViewPool功能相似，意义在于缓存离开屏幕的ItemView，目的是让即将进入屏幕的ItemView重用.
> 3. RecyclerView的优势在于a.mCacheViews的使用，可以做到屏幕外的列表项ItemView进入屏幕内时也无须bindView快速重用；b.mRecyclerPool可以供多个RecyclerView共同使用，在特定场景下，如viewpaper+多个列表页下有优势.客观来说，RecyclerView在特定场景下对ListView的缓存机制做了补强和完善。

### **缓存不同：**

1). RecyclerView缓存RecyclerView.ViewHolder，抽象可理解为： View + ViewHolder(避免每次createView时调用findViewById) + flag(标识状态)

 2). ListView缓存View。

>
>
>RecyclerView中mCacheViews(屏幕外)获取缓存时，是通过匹配pos获取目标位置的缓存，这样做的好处是，当数据源数据不变的情况下，无须重新bindView：



## 局部刷新

由上文可知，RecyclerView的缓存机制确实更加完善，但还不算质的变化，RecyclerView更大的亮点在于提供了局部刷新的接口，通过局部刷新，就能避免调用许多无用的bindView。

当调用fill()中RecyclerView.getViewForPosition(pos)时，RecyclerView通过对pos和flag的预处理，使得bindview只调用一次.

ListView和RecyclerView最大的区别在于数据源改变时的缓存的处理逻辑，ListView是"一锅端"，将所有的mActiveViews都移入了二级缓存mScrapViews，而RecyclerView则是更加灵活地对每个View修改标志位，区分是否重新bindView。