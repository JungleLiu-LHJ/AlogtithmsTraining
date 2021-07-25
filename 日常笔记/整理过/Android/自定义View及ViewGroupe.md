## 自定义View/ViewGroup



### 自定义View的测量

* 重写 onMeasure() 
* 计算出最终要的尺⼨ 
* ⽤ resolveSize() 或者 resolveSizeAndState() 修正结果
* ⽤ setMeasuredDimension(width, height) 把结果保存



```kotlin
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val size = //自己定义大小
    val width = resolveSize(size, widthMeasureSpec)
    val height = resolveSize(size, heightMeasureSpec)
    setMeasuredDimension(width, height)
  }


  public static final int MEASURED_STATE_MASK = 0xff000000;
//其中resolveSize：
  fun resolveSizeAndState(size: Int, measureSpec: Int, childMeasuredState: Int): Int {
    val specMode = MeasureSpec.getMode(measureSpec)
    val specSize = MeasureSpec.getSize(measureSpec)
    val result: Int = when (specMode) {
      MeasureSpec.AT_MOST -> if (specSize < size) {  //比较给的尺寸和自己定的尺寸，如果超过给定的就用给定的，不超过就用自己的
        specSize or MEASURED_STATE_TOO_SMALL   //如果需要的大于父view给的尺寸，就加一个标志，让父view知道测量的不够了，但是MEASURED_STATE_TOO_SMALL用的不多，android官方也很少用这个
      } else {
        size
      }
      MeasureSpec.EXACTLY -> specSize //父view固定了尺寸，使用父view给的尺寸
      MeasureSpec.UNSPECIFIED -> size //不规定就用自己的size
      else -> size
    }
    return result or (childMeasuredState and MEASURED_STATE_MASK) 
  }



```





### 自定义ViewGroup的测量

* 重写 onMeasure() 

  * 遍历每个⼦ View，测量⼦ View 	
    * 测量完成后，得出⼦ View 的实际位置和尺⼨，并暂时保存 	
    * 有些⼦ View 可能需要重新测量 

  * 测量出所有⼦ View 的位置和尺⼨后，计算出⾃⼰的尺⼨，并⽤ setMeasuredDimension(width, height) 保存 

* 重写 onLayout() 

  * 遍历每个⼦ View，调⽤它们的 layout() ⽅法来将位置和尺⼨传给它们 

#### 流式布局

```kotlin

private val childrenBounds = mutableListOf<Rect>()//用来记录每个view的尺寸

override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
  var widthUsed = 0  //该布局应该用的宽度
  var heightUsed = 0  //用了多少高度（上一行的高度）
  var lineWidthUsed = 0  //这一行用了多少宽度
  var lineMaxHeight = 0  //每一行的最大高度
  val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
  val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
  for ((index, child) in children.withIndex()) {
    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed) //首先测量子view的尺寸

    if (specWidthMode != MeasureSpec.UNSPECIFIED &&   //如果不限制就没必要做换行了
      lineWidthUsed + child.measuredWidth > specWidthSize) {  //如果加上这个子view超过这一行的宽度，下面是换行的逻辑
      lineWidthUsed = 0 
      heightUsed += lineMaxHeight
      lineMaxHeight = 0
      measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed) //重新测量该view的尺寸
    }

    if (index >= childrenBounds.size) {
      childrenBounds.add(Rect())
    }
    val childBounds = childrenBounds[index]
    childBounds.set(lineWidthUsed, heightUsed, lineWidthUsed + child.measuredWidth, heightUsed + child.measuredHeight) //设置该view的尺寸

    lineWidthUsed += child.measuredWidth 
    widthUsed = max(widthUsed, lineWidthUsed)
    lineMaxHeight = max(lineMaxHeight, child.measuredHeight) //记录这一行的最大高度
  }
  val selfWidth = widthUsed
  val selfHeight = heightUsed + lineMaxHeight
  setMeasuredDimension(selfWidth, selfHeight) //设置这个layout的尺寸
}

override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
  for ((index, child) in children.withIndex()) {
    val childBounds = childrenBounds[index]
    child.layout(childBounds.left, childBounds.top, childBounds.right, childBounds.bottom)
  }
}


//其中measureChildWithMargins：对宽和高进行了两次测量（getChildMeasureSpec）
   protected void measureChildWithMargins(View child,
            int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

//类似getChildMeasureSpec 
fun measureChild(index: Int, child: View, mode: Int, value: Int) {
    val layoutParams = child.layoutParams
    var childwidthMode = 0
    var childwidthSize = 0
    val valueUsed = 0
    when (layoutParams.width) {
        LayoutParams.MATCH_PARENT -> {
            when (mode) {
                MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
                    childwidthMode = MeasureSpec.EXACTLY
                    childwidthSize = value - valueUsed
                }

                MeasureSpec.UNSPECIFIED -> {
                    childwidthMode = MeasureSpec.UNSPECIFIED
                    childwidthSize = 0
                }
            }
        }
        LayoutParams.WRAP_CONTENT -> {
            when (mode) {
                MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
                    childwidthMode = MeasureSpec.AT_MOST
                    childwidthSize = value - valueUsed
                }
                MeasureSpec.UNSPECIFIED -> {
                    childwidthMode = MeasureSpec.UNSPECIFIED
                    childwidthSize = 0
                }
            }
        }
        else -> {
            childwidthMode = MeasureSpec.EXACTLY
            childwidthSize = layoutParams.width
        }
    }
    child.measure(childwidthSize, childwidthSize)
}
```







#### LinearLayout

第一次测量：测出大部分子view（高度为0并且有weight不会被测量），算出layout的高度，计算总的权重，带weight的子view先不进行测量

第二次测量：补充测量前面没有测过的子view, 如果确定了LinearLayout的高度后，前面测量的子view并未有填充满linearlayout的高(**因为带权重的view第一次测量的高度不是最终的**)，这里会通过他们的权重比去计算出最终的各个带wrap, 或者带有权重的view的高度。



