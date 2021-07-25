## RecyclerView 源码读书笔记



### view测量流程

<img src=".\huizhiguocheng.png" alt="huizhiguocheng" style="zoom:70%;" />

#### onMeasure

```java
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (mLayout == null) {
        	// 1. 没有设置layoutManager
            defaultOnMeasure(widthSpec, heightSpec); //只测量了自己的尺寸，子view的没有测量，onlayout的时候也不会做任何操作，所以不会显示item
            return;
        }
        if (mLayout.isAutoMeasureEnabled()) {
            // 2. 开启了自动测量。会执行dispatchLayoutStep1->dispatchLayoutStep2
             final int widthMode = MeasureSpec.getMode(widthSpec);
            final int heightMode = MeasureSpec.getMode(heightSpec);
            
            mLayout.onMeasure(mRecycler, mState, widthSpec, heightSpec);//测量自身的尺寸
  		
            final boolean measureSpecModeIsExactly =
                    widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY;
            	//如果测量是绝对值，则跳过measure过程直接走layout
            if (measureSpecModeIsExactly || mAdapter == null) {
                return;
            }

            if (mState.mLayoutStep == State.STEP_START) {
                    /**dispatchLayoutStep1：
     				* 1.处理Adapter的更新
     				* 2.决定那些动画需要执行
    			    * 3.保存当前View的信息
    				* 4.如果必要的话，执行上一个Layout的操作并且保存他的信息
    			    */
                dispatchLayoutStep1(); //主要和ItemAnimator相关
                //执行完dispatchLayoutStep1()后是State.STEP_LAYOUT
            }
    
            mLayout.setMeasureSpecs(widthSpec, heightSpec);
            mState.mIsMeasuring = true;
             //真正执行LayoutManager绘制的地方
            dispatchLayoutStep2();//真正布局children，调用mLayout.onLayoutChildren()对children进行测量和布局，该方法是在子类里面自己实现的
		    //执行完后是State.STEP_ANIMATIONS
            // now we can get the width and height from the children.
            mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);

       		//如果需要再次测量的话，宽高都不确定的时候，会测量两次
            if (mLayout.shouldMeasureTwice()) {
                mLayout.setMeasureSpecs(
                        MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
                mState.mIsMeasuring = true;
                dispatchLayoutStep2();
                // now we can get the width and height from the children.
                mLayout.setMeasuredDimensionFromChildren(widthSpec, heightSpec);
            }
            
        } else {
            // 3. 没有开启自动测量
             if (mHasFixedSize) {
                mLayout.onMeasure(mRecycler, mState, widthSpec, heightSpec);//进行测量
                return;
            }
            // custom onMeasure
        	...
            //处理数据更新事务
                
            mLayout.onMeasure(mRecycler, mState, widthSpec, heightSpec);//进行测量
            stopInterceptRequestLayout(false);
       
        }
    }
```

> 1. `mLayout`即`LayoutManager`的对象。我们知道，当`RecyclerView`的`LayoutManager`为空时,`RecyclerView`不能显示任何的数据，在这里我们找到答案。
>
> 2. `LayoutManager`开启了自动测量时，这是一种情况。在这种情况下，有可能会测量两次。
>
> 3. 第三种情况就是没有开启自动测量的情况，这种情况比较少，因为为了`RecyclerView`支持`warp_content`属性，系统提供的`LayoutManager`都开启自动测量的，不过我们还是要分析的。



##### 当LayoutManager开启自动测量

1. 调用`LayoutManager`的`onMeasure`方法进行测量。对于`onMeasure`方法，传统的`LayoutManager`都没有实现这个方法。调用`defaultOnMeasure`测量自身宽高，
2. 如果`mState.mLayoutStep`为`State.STEP_START`的话，那么就会执行`dispatchLayoutStep1`方法，然后会执行`dispatchLayoutStep2`方法。
3. 如果需要第二次测量的话，会再一次调用`dispatchLayoutStep2` 方法。

| 取值                  | 含义                                                         |
| --------------------- | ------------------------------------------------------------ |
| State.STEP_START      | `mState.mLayoutStep`的默认值，这种情况下，表示RecyclerView还未经历`dispatchLayoutStep1`，因为`dispatchLayoutStep1`调用之后`mState.mLayoutStep`会变为`State.STEP_LAYOUT`。 |
| State.STEP_LAYOUT     | 当`mState.mLayoutStep`为`State.STEP_LAYOUT`时，表示此时处于layout阶段，这个阶段会调用`dispatchLayoutStep2`方法`layout` `RecyclerView`的`children`。调用`dispatchLayoutStep2`方法之后，此时`mState.mLayoutStep`变为了`State.STEP_ANIMATIONS`。 |
| State.STEP_ANIMATIONS | 当`mState.mLayoutStep`为`State.STEP_ANIMATIONS`时，表示`RecyclerView`处于第三个阶段，也就是执行动画的阶段，也就是调用`dispatchLayoutStep3`方法。当`dispatchLayoutStep3`方法执行完毕之后，`mState.mLayoutStep`又变为了`State.STEP_START`。 |

| 方法名              | 作用                                                         |
| ------------------- | ------------------------------------------------------------ |
| dispatchLayoutStep1 | 三大`dispatchLayoutStep`方法第一步。本方法的作用主要有三点：1.处理`Adapter`更新;2.决定是否执行`ItemAnimator`;3.保存`ItemView`的动画信息。本方法也被称为preLayout(预布局)，当`Adapter`更新了，这个方法会保存每个`ItemView`的旧信息(oldViewHolderInfo) |
| dispatchLayoutStep2 | 三大`dispatchLayoutStep`方法第二步。在这个方法里面，真正进行`children`的测量和布局。 |
| dispatchLayoutStep3 | 三大`dispatchLayoutStep`方法第三步。这个方法的作用执行在`dispatchLayoutStep1`方法里面保存的动画信息。 |



```java
private void dispatchLayoutStep2() {
        ....
        //重写的getItemCount方法
        mState.mItemCount = mAdapter.getItemCount();
        ....
        // Step 2: Run layout
        mState.mInPreLayout = false;
        mLayout.onLayoutChildren(mRecycler, mState); //layoutManager来实现的
         ....
    }


```

下面就以LiniearLayoutManager为例子来看



###### LinearLayoutManger

```java
public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // layout algorithm:
        //找寻锚点
        // 1) by checking children and other variables, find an anchor coordinate and an anchor
        // item position.
        //两个方向填充，从锚点往上，从锚点往下
        // 2) fill towards start, stacking from bottom
        // 3) fill towards end, stacking from top
        // 4) scroll to fulfill requirements like stack from bottom.
        // create layout state
        ....
        // resolve layout direction
        //判断绘制方向,给mShouldReverseLayout赋值,默认是正向绘制，则mShouldReverseLayout是false
        resolveShouldLayoutReverse();//确定是否需要倒着绘制，是否倒着绘制是用户定义的，默认为false
        final View focused = getFocusedChild();
        //mValid的默认值是false，一次测量之后设为true，onLayout完成后会回调执行reset方法，又变为false
        if (!mAnchorInfo.mValid || mPendingScrollPosition != NO_POSITION
                || mPendingSavedState != null) {
        ....
            //mStackFromEnd默认是false，除非手动调用setStackFromEnd()方法，两个都会false，异或则为false
            mAnchorInfo.mLayoutFromEnd = mShouldReverseLayout ^ mStackFromEnd;
            // calculate anchor position and coordinate
            //计算锚点的位置和偏移量
            updateAnchorInfoForLayout(recycler, state, mAnchorInfo);
        ....
        } else if (focused != null && (mOrientationHelper.getDecoratedStart(focused)
                >= mOrientationHelper.getEndAfterPadding()
                || mOrientationHelper.getDecoratedEnd(focused)
                <= mOrientationHelper.getStartAfterPadding())) {
         ....
        }
         ....
        //mLayoutFromEnd为false
        if (mAnchorInfo.mLayoutFromEnd) {
            //倒着绘制的话，先往上绘制，再往下绘制
            // fill towards start
            // 从锚点到往上
            updateLayoutStateToFillStart(mAnchorInfo);
            ....
            fill(recycler, mLayoutState, state, false);
            ....
            // 从锚点到往下
            // fill towards end
            updateLayoutStateToFillEnd(mAnchorInfo);
            ....
            //调两遍fill方法
            fill(recycler, mLayoutState, state, false);
            ....
            if (mLayoutState.mAvailable > 0) {
                // end could not consume all. add more items towards start
            ....
                updateLayoutStateToFillStart(firstElement, startOffset);
                mLayoutState.mExtra = extraForStart;
                fill(recycler, mLayoutState, state, false);
             ....
            }
        } else {
            //正常绘制流程的话，先往下绘制，再往上绘制
            // fill towards end
            updateLayoutStateToFillEnd(mAnchorInfo);
            ....
            fill(recycler, mLayoutState, state, false);
             ....
            // fill towards start
            updateLayoutStateToFillStart(mAnchorInfo);
            ....
            fill(recycler, mLayoutState, state, false);
             ....
            if (mLayoutState.mAvailable > 0) {
                ....
                // start could not consume all it should. add more items towards end
                updateLayoutStateToFillEnd(lastElement, endOffset);
                 ....
                fill(recycler, mLayoutState, state, false);
                ....
            }
        }
        ....
        layoutForPredictiveAnimations(recycler, state, startOffset, endOffset);
        //完成后重置参数
        if (!state.isPreLayout()) {
            mOrientationHelper.onLayoutComplete();
        } else {
            mAnchorInfo.reset();
        }
        mLastStackFromEnd = mStackFromEnd;
    }
/**
    先寻找页面当前的锚点
     以这个锚点未基准，向上和向下分别填充
    填充完后，如果还有剩余的可填充大小，再填充一次
**/


int fill(RecyclerView.Recycler recycler, LayoutState layoutState,
             RecyclerView.State state, boolean stopOnFocusable) {
            .....
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
                         .....
        return start - layoutState.mAvailable;
    }

void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state,
                     LayoutState layoutState, LayoutChunkResult result) {
        /**next方法很重要,调用了recycler.getViewForPosition(mCurrentPosition);
           getViewForPosition(mCurrentPosition)里面会从Recycler的scrap，cache，RecyclerViewPool拿ViewHolder,或者直接mAdapter.createViewHolder创建	
        **/
        View view = layoutState.next(recycler);
   
        if (view == null) {
            if (DEBUG && layoutState.mScrapList == null) {
                throw new RuntimeException("received null view when unexpected");
            }
           ...
        }
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (layoutState.mScrapList == null) {
            if (mShouldReverseLayout == (layoutState.mLayoutDirection
                    == LayoutState.LAYOUT_START)) {
                addView(view);
            } else {
                addView(view, 0);
            }
        } else {
            if (mShouldReverseLayout == (layoutState.mLayoutDirection
                    == LayoutState.LAYOUT_START)) {
                addDisappearingView(view);
            } else {
                addDisappearingView(view, 0);
            }
        }
        //测量ChildView
        measureChildWithMargins(view, 0, 0);
        ......
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        //layout Child
        layoutDecoratedWithMargins(view, left, top, right, bottom); //对子View完成了layout。
        ......
    }

    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			//注意这里有不一样的，置分割线
            final Rect insets = mRecyclerView.getItemDecorInsetsForChild(child);
            widthUsed += insets.left + insets.right;
            heightUsed += insets.top + insets.bottom;

            final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),
                    getPaddingLeft() + getPaddingRight()
                            + lp.leftMargin + lp.rightMargin + widthUsed, lp.width,
                    canScrollHorizontally());
            final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),
                    getPaddingTop() + getPaddingBottom()
                            + lp.topMargin + lp.bottomMargin + heightUsed, lp.height,
                    canScrollVertically());
            if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
                child.measure(widthSpec, heightSpec);
            }
        }

//量子View的时候是将我们实现自定义分割线重写的getItemOffsets方法
 Rect getItemDecorInsetsForChild(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (!lp.mInsetsDirty) {
            return lp.mDecorInsets;
        }

        if (mState.isPreLayout() && (lp.isItemChanged() || lp.isViewInvalid())) {
            // changed/invalid items should not be updated until they are rebound.
            return lp.mDecorInsets;
        }
        final Rect insets = lp.mDecorInsets;
        insets.set(0, 0, 0, 0);
        final int decorCount = mItemDecorations.size();
        for (int i = 0; i < decorCount; i++) {
            mTempRect.set(0, 0, 0, 0);
            mItemDecorations.get(i).getItemOffsets(mTempRect, child, this, mState);
            insets.left += mTempRect.left;
            insets.top += mTempRect.top;
            insets.right += mTempRect.right;
            insets.bottom += mTempRect.bottom;
        }
        lp.mInsetsDirty = false;
        return insets;
    }

 //对子View完成了layout。
      public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right,
                int bottom) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            final Rect insets = lp.mDecorInsets;
            child.layout(left + insets.left + lp.leftMargin, top + insets.top + lp.topMargin,
                    right - insets.right - lp.rightMargin,
                    bottom - insets.bottom - lp.bottomMargin);
        }

```





##### 没有开启自动测量

1. 如果`mHasFixedSize`为true(也就是调用了`setHasFixedSize`方法)，将直接调用`LayoutManager`的`onMeasure`方法进行测量。

2. 如果`mHasFixedSize`为false，同时此时如果有数据更新，先处理数据更新的事务，然后调用`LayoutManager`的`onMeasure`方法进行测量



#### Layout

```java
 protected void onLayout(boolean changed, int l, int t, int r, int b) {
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        mFirstLayoutComplete = true;
 }




void dispatchLayout() {
    if (mAdapter == null) {
        Log.e(TAG, "No adapter attached; skipping layout");
        // leave the state in START
        return;
    }
    if (mLayout == null) {
        Log.e(TAG, "No layout manager attached; skipping layout");
        // leave the state in START
        return;
    }
    mState.mIsMeasuring = false;
    //给RecyclerView设置固定的宽高的时候，onMeasure是直接跳过了执行，所以onLayout这里仍然需要根据mState做操作
    if (mState.mLayoutStep == State.STEP_START) {//
        //如果onMeasure没有执行，mState.mLayoutStep == State.STEP_START就成立，所以仍然会执行 dispatchLayoutStep1()， dispatchLayoutStep2();也就对应的会绘制子View。
        dispatchLayoutStep1();
        mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
    } else if (mAdapterHelper.hasUpdates() || mLayout.getWidth() != getWidth()
            || mLayout.getHeight() != getHeight()) {
        //如果改变了宽高也会导致执行dispatchLayoutStep2
        // First 2 steps are done in onMeasure but looks like we have to run again due to
        // changed size.
        mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
    } else {
        // always make sure we sync them (to ensure mode is exact)
        mLayout.setExactMeasureSpecsFrom(this);
    }
    dispatchLayoutStep3(); //主要处理ItemAnimation动画
}
```



### 缓存中获取

![cache](.\cache.webp)

```java
View next(RecyclerView.Recycler recycler) {
            //默认mScrapList=null，但是执行layoutForPredictiveAnimations方法的时候不会为空，只有执行layoutForPredictiveAnimations前不为空，执行完后又变为空
            if (mScrapList != null) {
                return nextViewFromScrapList();
            }
            //重要，从recycler获得View,mScrapList是被LayoutManager持有，recycler是被RecyclerView持有
            final View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mItemDirection;
            return view;
        }


public final class Recycler {
    final ArrayList<ViewHolder> mAttachedScrap = new ArrayList<>();//一级缓存：mAttachedScrap
    ArrayList<ViewHolder> mChangedScrap = null;//存放动画的ViewHolder,动画结束了自然就清空了（貌似以前叫mHiddenVeiw）

    final ArrayList<ViewHolder> mCachedViews = new ArrayList<ViewHolder>();//二级缓存：mCacheViews

    private final List<ViewHolder>
            mUnmodifiableAttachedScrap = Collections.unmodifiableList(mAttachedScrap);

    private int mRequestedCacheMax = DEFAULT_CACHE_SIZE;
    int mViewCacheMax = DEFAULT_CACHE_SIZE;

    RecycledViewPool mRecyclerPool;//四级缓存：mRecyclerPool

    private ViewCacheExtension mViewCacheExtension;//三级缓存：mViewCacheExtension

    static final int DEFAULT_CACHE_SIZE = 2;
    ...
    }
    /**类的结构也比较清楚，这里可以清楚的看到我们后面讲到的四级缓存机制所用到的类都在这里可以看到：
    * 1.一级缓存：mAttachedScrap
    * 2.二级缓存：mCacheViews
    * 3.三级缓存：mViewCacheExtension
    * 4.四级缓存：mRecyclerPool**/



```

下面看`getViewForPosition`方法最后调用的方法`tryGetViewHolderForPositionByDeadline`：

```java
    @Nullable
    ViewHolder tryGetViewHolderForPositionByDeadline(int position,
                                                     boolean dryRun, long deadlineNs) {
        if (position < 0 || position >= mState.getItemCount()) {
            throw new IndexOutOfBoundsException("Invalid item position " + position
                    + "(" + position + "). Item count:" + mState.getItemCount()
                    + exceptionLabel());
        }
        boolean fromScrapOrHiddenOrCache = false;
        ViewHolder holder = null;
        // 0) If there is a changed scrap, try to find from there
        if (mState.isPreLayout()) {
            //preLayout默认是false，只有有动画的时候才为true，有动画才进来
            holder = getChangedScrapViewForPosition(position); //从mChangedScrap获取viewHolder
            fromScrapOrHiddenOrCache = holder != null;
        }
        // 1) Find by position from scrap/hidden list/cache
        if (holder == null) {//第一次尝试
            holder = getScrapOrHiddenOrCachedHolderForPosition(position, dryRun);//从一级缓存mAttachedScrap、HiddenView和二级缓存CacheView中拿viewHolder，下面是校验
            if (holder != null) {
                if (!validateViewHolderForOffsetPosition(holder)) {
                    //如果检查发现这个holder不是当前position的
                    // recycle holder (and unscrap if relevant) since it can't be used
                    if (!dryRun) {
                        // we would like to recycle this but need to make sure it is not used by
                        // animation logic etc.
                        holder.addFlags(ViewHolder.FLAG_INVALID);
                        //从scrap中移除
                        if (holder.isScrap()) {
                            removeDetachedView(holder.itemView, false);
                            holder.unScrap();
                        } else if (holder.wasReturnedFromScrap()) {
                            holder.clearReturnedFromScrapFlag();
                        }
                        //放到ViewCache或者Pool中
                        recycleViewHolderInternal(holder);
                    }
                    //至空继续寻找
                    holder = null;
                } else {
                    fromScrapOrHiddenOrCache = true;
                }
            }
        }
        if (holder == null) {
          。...
            // 2) Find from scrap/cache via stable ids, if exists
            if (mAdapter.hasStableIds()) {
                holder = getScrapOrCachedViewForId(mAdapter.getItemId(offsetPosition),
                        type, dryRun);
                if (holder != null) {
                    // update position
                    holder.mPosition = offsetPosition;
                    fromScrapOrHiddenOrCache = true;
                }
            }
            //自定义缓存
            if (holder == null && mViewCacheExtension != null) {
                // We are NOT sending the offsetPosition because LayoutManager does not
                // know it.
                //第三次查找，从自定义缓存查找，自己写查询规则
                final View view = mViewCacheExtension
                        .getViewForPositionAndType(this, position, type);
                if (view != null) {
                    holder = getChildViewHolder(view);
                    if (holder == null) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned"
                                + " a view which does not have a ViewHolder"
                                + exceptionLabel());
                    } else if (holder.shouldIgnore()) {
                        throw new IllegalArgumentException("getViewForPositionAndType returned"
                                + " a view that is ignored. You must call stopIgnoring before"
                                + " returning this view." + exceptionLabel());
                    }
                }
            }
            //第四次尝试取，pool，从pool里面取
            if (holder == null) { // fallback to pool
                if (DEBUG) {
                    Log.d(TAG, "tryGetViewHolderForPositionByDeadline("
                            + position + ") fetching from shared pool");
                }
                holder = getRecycledViewPool().getRecycledView(type);
                if (holder != null) {
                    holder.resetInternal();
                    if (FORCE_INVALIDATE_DISPLAY_LIST) {
                        invalidateDisplayListInt(holder);
                    }
                }
            }
            //create
            if (holder == null) {
                long start = getNanoTime();
                if (deadlineNs != FOREVER_NS
                        && !mRecyclerPool.willCreateInTime(type, start, deadlineNs)) {
                    // abort - we have a deadline we can't meet
                    return null;
                }
                //最后由adapter创建
                holder = mAdapter.createViewHolder(RecyclerView.this, type);
                if (ALLOW_THREAD_GAP_WORK) {
                    // only bother finding nested RV if prefetching
                    RecyclerView innerView = findNestedRecyclerView(holder.itemView);
                    if (innerView != null) {
                        holder.mNestedRecyclerView = new WeakReference<>(innerView);
                    }
                }

                long end = getNanoTime();
                mRecyclerPool.factorInCreateTime(type, end - start);
            }
        }
        ....
               if (mState.isPreLayout() && holder.isBound()) {
                // do not update unless we absolutely have to.
                holder.mPreLayoutPosition = position;
            } else if (!holder.isBound() || holder.needsUpdate() || holder.isInvalid()) { //判断是否需要重新绑定数据
                if (DEBUG && holder.isRemoved()) {
                    throw new IllegalStateException("Removed holder should be bound and it should"
                            + " come here only in pre-layout. Holder: " + holder
                            + exceptionLabel());
                }
                final int offsetPosition = mAdapterHelper.findPositionOffset(position);
                bound = tryBindViewHolderByDeadline(holder, offsetPosition, position, deadlineNs);
                   //里面调用了  mAdapter.bindViewHolder(holder, offsetPosition) 进行bind数据
            }
        ...
        return holder;
    }
```



1. Find by position from scrap/hidden list/cache
   1. 从mAttachedScrap中获取
   2. 从HiddenView中获取
   3. 从CacheView获取

下面是流程图：

![tryToFindFirst](.\tryToFindFirst.png)





2. Find from scrap/cache via stable ids, if exists

这里的判断其实和上面那一次差不多，需要注意的是**多了对于id的判断和对于type的判断**，也就是当我们将hasStableIds()设为true后需要重写holder.getItemId() 方法，来为每一个item设置一个单独的id。具体流程图如下：

![tryToFindSecond](.\tryToFindSecond.png)



3. 从自定义缓存查找

4. 从Pool拿

   ```java
   public static class RecycledViewPool {
       private static final int DEFAULT_MAX_SCRAP = 5;
       static class ScrapData {
           ArrayList<ViewHolder> mScrapHeap = new ArrayList<>();
           int mMaxScrap = DEFAULT_MAX_SCRAP; //mScrapHeap里面只能存5个
           long mCreateRunningAverageNs = 0;
           long mBindRunningAverageNs = 0;
       }
       SparseArray<ScrapData> mScrap = new SparseArray<>();//存各种类型的viewHodler，key是type，value是viewHolder
   
       private int mAttachCount = 0;
       ...
       public ViewHolder getRecycledView(int viewType) {
           final ScrapData scrapData = mScrap.get(viewType);
           if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
               final ArrayList<ViewHolder> scrapHeap = scrapData.mScrapHeap;
               return scrapHeap.remove(scrapHeap.size() - 1);//每次拿的时候需要remove，通过remove来获取
           }
           return null;
       }
       ......
     }
   ```

5. 如果上述都没拿到，就从adapter进行创建



> 1.RecyclerView内部大体可以分为四级缓存：mAttachedScrap,mCacheViews,ViewCacheExtension,RecycledViewPool.
>  2.mAttachedScrap,mCacheViews在第一次尝试的时候只是对View的复用，并且不区分type，但在第二次尝试的时候是区分了Type，是对于ViewHolder的复用，ViewCacheExtension,RecycledViewPool是对于ViewHolder的复用，而且区分type。
>  3.如果缓存ViewHolder时发现超过了mCachedView的限制，会将最老的ViewHolder(也就是mCachedView缓存队列的第一个ViewHolder)移到RecycledViewPool中。



#### 流程图解

<img src=".\huadong.png" alt="tryToFindFirst" style="zoom:67%;" />



关于一级缓存

> `detachAndScrapAttachedViews(recycler);`的作用就是把当前屏幕上所有的HolderView与屏幕分离，将它们从RecyclerView的布局中拿下来，然后存放在一个列表中，在重新布局时，像搭积木一样，把这些HolderView重新一个个放在新位置上去。将屏幕上的HolderView从RecyclerView的布局中拿下来后，存放的列表叫mAttachedScrap，它依然是一个List，就是用来保存从RecyclerView的布局中拿下来的HolderView列表。所以，大家可以查看所有自定义的LayoutManager，`detachAndScrapAttachedViews(recycler);`只会被用在`onLayoutChildren`函数中。就是因为`onLayoutChildren`函数是用来布局新的Item的，只有在布局时，才会先把HolderView detach掉然后再add进来重新布局。但大家需要注意的是mAttachedScrap中存储的就是新布局前从RecyclerView中剥离下来的当前在显示的Item的HolderView。这些HolderView并不参与回收复用。单纯只是为了先从RecyclerView中拿下来，再重新布局上去。对于新布局中没有用到的HolderView，会从mAttachedScrap移到mCachedViews中，让它参与复用。





