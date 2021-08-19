## View的绘制流程



 ![image-20210507223811946](.\image-20210507223811946.png)





### activity启动

ActivityThread:

```java
//ActivityThread

@Override
public Activity handleLaunchActivity(ActivityClientRecord r,
        PendingTransactionActions pendingActions, Intent customIntent) {
    ....
           final Activity a = performLaunchActivity(r, customIntent);
    ....
}



  private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
   ...
        java.lang.ClassLoader cl = appContext.getClassLoader();
            activity = mInstrumentation.newActivity(
                    cl, component.getClassName(), r.intent); //反射 new activity
      ...
          
                activity.attach(appContext, this, getInstrumentation(), r.token,
                        r.ident, app, r.intent, r.activityInfo, title, r.parent,
                        r.embeddedID, r.lastNonConfigurationInstances, config,
                        r.referrer, r.voiceInteractor, window, r.configCallback,
                        r.assistToken);   //attach里面mWindow = new PhoneWindow ,      mWindowManager = mWindow.getWindowManager();
		...
        
                 mInstrumentation.callActivityOnCreate(activity, r.state); //调用了performCreate，performCreate里面调用了onCreate
               
      
  }


//Instrumentation

 public Activity newActivity(ClassLoader cl, String className,
            Intent intent)
         ...
        return getFactory(pkg).instantiateActivity(cl, className, intent);
    }


//AppComponentFactory
   public @NonNull Activity instantiateActivity(@NonNull ClassLoader cl, @NonNull String className,
            @Nullable Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (Activity) cl.loadClass(className).newInstance();
    }
```





#### setContentView

activity里面的setContentView其实最后调用了PhoneWindows的setContentView()：

```java
//class PhoneWindows

public void setContentView(int layoutResID) {
    if (mContentParent == null) {
        installDecor(); //  mDecor = generateDecor(-1); 创建了DecorView
   ...
}


 private void installDecor() {
      if (mDecor == null) {
            mDecor = generateDecor(-1);//创建了DecorView
      }
     ...
        if (mContentParent == null) {
            mContentParent = generateLayout(mDecor); //根据theme做window的配置
           
            ...
        }
     
 }

   protected ViewGroup generateLayout(DecorView decor) {
    	//根据theme做window的配置等等。。
       ...
        ...   
        } else if ((features & (1 << FEATURE_ACTION_MODE_OVERLAY)) != 0) {
            layoutResource = R.layout.screen_simple_overlay_action_mode;
        } else {
            // Embedded, so no decoration is needed.
            layoutResource = R.layout.screen_simple; 
            
        }

           //把LinearLayout布局添加进去
         mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);
     ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);//把根布局中的content设置为contentParent，ID_ANDROID_CONTENT=android.R.id.content
       ...
   }


//class DecorView:
    void onResourcesLoaded(LayoutInflater inflater, int layoutResource) {
   	...
        final View root = inflater.inflate(layoutResource, null);
  	...

            // Put it below the color views.
            addView(root, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));//把布局（标题栏+content）添加到里面
     ...
        mContentRoot = (ViewGroup) root;
        initializeElevation();
    }


```





### activity的OnResume

```java
public void handleResumeActivity(IBinder token, boolean finalStateRequest, boolean isForward,
        String reason) {
        ...
          final ActivityClientRecord r = performResumeActivity(token, finalStateRequest, reason);
        ...
            
            r.window = r.activity.getWindow(); //获取到PhoneWindow
            View decor = r.window.getDecorView();
            decor.setVisibility(View.INVISIBLE);
            ViewManager wm = a.getWindowManager();
            WindowManager.LayoutParams l = r.window.getAttributes();
            a.mDecor = decor;
    		...
                
			wm.addView(decor, l); // activity.mWindowManager.addView(decor,l)  mWindowManager在atach的时候被赋值为WindowManagerImpl，  WindowManagerImpl addView --->  WindowManagerGlobal.getInstance().addView()
        }



//class WindowManagerGlobal

    public void addView(View view, ViewGroup.LayoutParams params,
            Display display, Window parentWindow) {
     		...	// 检查参数是否合法

            root = new ViewRootImpl(view.getContext(), display); //创建ViewRootImpl，其中 mThread = Thread.currentThread();

            view.setLayoutParams(wparams);

            mViews.add(view);  //将view添加到列表中
            mRoots.add(root);  //将ViewRootImpl添加到列表中
            mParams.add(wparams);

            // do this last because it fires off messages to start doing things
            try {
                root.setView(view, wparams, panelParentView); //通过ViewRootImpl来更新，关键
            } catch (RuntimeException e) {
                // BadTokenException or InvalidDisplayException, clean up.
                if (index >= 0) {
                    removeViewLocked(index, true);
                }
                throw e;
            }
        }
    }

//class ViewRootImpl
    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
     	...
           requestLayout();
        ...
             view.assignParent(this); //把view的parent设置为ViewRootImpl，这里的view就是decorView
        ...
              res = mWindowSession.addToDisplay(mWindow, mSeq, mWindowAttributes,
                            getHostVisibility(), mDisplay.getDisplayId(), mTmpFrame,
                            mAttachInfo.mContentInsets, mAttachInfo.mStableInsets,
                            mAttachInfo.mOutsets, mAttachInfo.mDisplayCutout, mInputChannel,
                            mTempInsets);
			//通过mWindowSession最终完成Windows的添加过程，mWindowSession类型是IWindowSession,是一个Binder对象，真正的实现类是Session,也就是说Windows的添加过程是一次IPC调用。在Session的内部会通过WindowsManagerService来实现Windows的添加
            
    }

   public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();  //检查线程
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }

    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }

//mTraversalRunnable中的run执行了doTraversal，doTraversal中执行了performTraversals（）

  private void performTraversals() {
   	...
            final View host = mView;
        //获取屏幕大小等。。
          host.dispatchAttachedToWindow(mAttachInfo, 0); //
          mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true); //view的回调
      	dispatchApplyInsets(host); //状态栏和导航栏的添加和做调整
      ...
          getRunQueue().executeActions(mAttachInfo.mHandler); //view.post 的时候handler没有被创建传进来的action，里面通过handler.post放到队列里
      	//后面执行了measure,layout
      
       windowSizeMayChange |= measureHierarchy(host, lp, res,
                    desiredWindowWidth, desiredWindowHeight); //最终调用了measure（decorView的），第一次测量，其实是为了确定window的大小
      ...
        relayoutResult = relayoutWindow(params, viewVisibility, insetsPending);//window的大小
      ...
       performMeasure(childWidthMeasureSpec, childHeightMeasureSpec); //最终确定空间大小
      ...
       performLayout(lp, mWidth, mHeight);//调用了decorView的layout
      ...
	   mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();//回调（平时推荐用来获取控件的大小，比postLayout更快，在用一个message里就可以获取）
      ...
       performDraw();
  }

```







### 关于view.post

ViewRootImpl创建的时候会创建这个handler，onCreate中使用了post的时候handler还没有被创建

```java
//class view
public boolean post(Runnable action) {
    final AttachInfo attachInfo = mAttachInfo;
    if (attachInfo != null) {
        return attachInfo.mHandler.post(action);
    }

    // Postpone the runnable until we know on which thread it needs to run.
    // Assume that the runnable will be successfully placed after attach.
    getRunQueue().post(action); //handler还没有被创建
    return true;
}
```



![image-20210507223833554](.\image-20210507223833554.png)