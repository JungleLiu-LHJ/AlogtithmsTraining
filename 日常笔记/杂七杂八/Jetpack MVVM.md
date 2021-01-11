# JetPack MVVM



## Lifecycle

### 介绍

Lifecycle主要为了解决生命周期管理的一致性问题

[生命周期感知型组件](https://developer.android.com/topic/libraries/architecture/lifecycle)可执行操作来响应另一个组件（如 Activity 和 Fragment）的生命周期状态的变化。这些组件有助于您编写更有条理且往往更精简的代码，此类代码更易于维护。

### 添加

```java
dependencies {
        def lifecycle_version = "2.2.0"

        // ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
        // LiveData
        implementation "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
        // Lifecycles only (without ViewModel or LiveData)
        implementation "androidx.lifecycle:lifecycle-runtime:$lifecycle_version"

        // Saved state module for ViewModel
        implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

        // Annotation processor
        annotationProcessor "androidx.lifecycle:lifecycle-compiler:$lifecycle_version"
        // alternately - if using Java8, use the following instead of lifecycle-compiler
        implementation "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"

        // optional - ReactiveStreams support for LiveData
        implementation "androidx.lifecycle:lifecycle-reactivestreams:$lifecycle_version"

        // optional - Test helpers for LiveData
        testImplementation "androidx.arch.core:core-testing:$lifecycle_version"
    }
```



解决：在视图控制器（如fragment）`getLifecycle().addObserver(GpsManager.getInstance)` ，优雅地完成 第三方组件在自己内部 对 LifecycleOwner 生命周期的感知。



## LiveData



### 介绍与优势

[`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) 是一种可观察的数据存储器类。与常规的可观察类不同，LiveData 具有生命周期感知能力，意指它遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期

如果观察者（由 [`Observer`](https://developer.android.com/reference/androidx/lifecycle/Observer) 类表示）的生命周期处于 [`STARTED`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.State#STARTED) 或 [`RESUMED`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle.State#RESUMED) 状态，则 LiveData 会认为该观察者处于活跃状态。LiveData 只会将更新通知给活跃的观察者。为观察 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) 对象而注册的非活跃观察者不会收到更改通知。

优势：

* **确保界面符合数据状态**

LiveData 遵循观察者模式。当生命周期状态发生变化时，LiveData 会通知 [`Observer`](https://developer.android.com/reference/androidx/lifecycle/Observer) 对象。您可以整合代码以在这些 `Observer` 对象中更新界面。观察者可以在每次发生更改时更新界面，而不是在每次应用数据发生更改时更新界面。

* **不会发生内存泄露**

观察者会绑定到 [`Lifecycle`](https://developer.android.com/reference/androidx/lifecycle/Lifecycle) 对象，并在其关联的生命周期遭到销毁后进行自我清理。

* **不会因 Activity 停止而导致崩溃**

如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），则它不会接收任何 LiveData 事件。

* **不再需要手动处理生命周期**

界面组件只是观察相关数据，不会停止或恢复观察。LiveData 将自动管理所有这些操作，因为它在观察时可以感知相关的生命周期状态变化。

* **数据始终保持最新状态**

如果生命周期变为非活跃状态，它会在再次变为活跃状态时接收最新的数据。例如，曾经在后台的 Activity 会在返回前台后立即接收最新的数据。

* **适当的配置更改**

如果由于配置更改（如设备旋转）而重新创建了 Activity 或 Fragment，它会立即接收最新的可用数据。

* **共享资源**

您可以使用单一实例模式扩展 [`LiveData`](https://developer.android.com/reference/androidx/lifecycle/LiveData) 对象以封装系统服务，以便在应用中共享它们。`LiveData` 对象连接到系统服务一次，然后需要相应资源的任何观察者只需观察 `LiveData` 对象。



### 使用步骤：

1. 通常在viewmodel里面创建LiveData

```java
    public class NameViewModel extends ViewModel {    
        // Create a LiveData with a String    
        private MutableLiveData<String> currentName;        
        public MutableLiveData<String> getCurrentName() {            
            if (currentName == null) {                
                currentName = new MutableLiveData<String>();            
            }            return currentName;        
        }    
        // Rest of the ViewModel...    
    }    
```

2. 通常在界面控制器（Activity或Fragment）创建Observer对象，该方法可以控制当LiveData对象存储的数据改变的时候所发生什么

```java
    public class NameActivity extends AppCompatActivity {

        private NameViewModel model;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Other code to setup the activity...

            // Get the ViewModel.
            model = ViewModelProviders.of(this).get(NameViewModel.class);

            // Create the observer which updates the UI.
            final Observer<String> nameObserver = new Observer<String>() {
                @Override
                public void onChanged(@Nullable final String newName) {
                    // Update the UI, in this case, a TextView.
                    nameTextView.setText(newName);
                }
            };

            // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            model.getCurrentName().observe(this, nameObserver);
        }
    }
```

3. 使用 [`observe()`](https://developer.android.com/reference/androidx/lifecycle/LiveData#observe(android.arch.lifecycle.LifecycleOwner,
   android.arch.lifecycle.Observer)) 方法将 `Observer` 对象附加到 `LiveData` 对象。`observe()` 方法会采用 [`LifecycleOwner`](https://developer.android.com/reference/androidx/lifecycle/LifecycleOwner) 对象。这样会使 `Observer` 对象订阅 `LiveData` 对象，以使其收到有关更改的通知。通常情况下，您可以在界面控制器（如 Activity 或 Fragment）中附加 `Observer` 对象。

   这是按钮更新的一个例子（不是MVVM的）

```java
    button.setOnClickListener(new OnClickListener() {        
        @Override        
        public void onClick(View v) {            
            String anotherName = "John Doe";                                                       model.getCurrentName().setValue(anotherName);        
        }    
    });
```



### MutableLiveData 与 LiveData

`MutableLiveData`的`postValue`和`setValue`变成了public的。

所以你需要改变你的数据则用MutableLiveData，不希望你的数据被改变则用LiveData





## ViewModel

### 介绍

在 Jetpack ViewModel 面市之前，MVP 的 Presenter 和 MVVM - Clean 的 ViewModel 都不具备状态管理分治的能力。

Presenter 和 Clean ViewModel 的生命周期都与视图控制器同生共死，因而它们顶多是为 DataBinding 提供状态的托管，而无法实现状态的分治。

到了 Jetpack 这一版，ViewModel 以精妙的设计，达成了状态管理，以及可共享的作用域。

架构组件为界面控制器提供了 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 辅助程序类，该类负责为界面准备数据。 在配置更改期间会自动保留 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 对象，以便它们存储的数据立即可供下一个 Activity 或 Fragment 实例使用。例如，如果您需要在应用中显示用户列表，请确保将获取和保留该用户列表的责任分配给 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel)，而不是 Activity 或 Fragment、



### 生命周期

您通常在系统首次调用 Activity 对象的 `onCreate()` 方法时请求 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel)。系统可能会在 Activity 的整个生命周期内多次调用 `onCreate()`，如在旋转设备屏幕时。[`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 存在的时间范围是从您首次请求 [`ViewModel`](https://developer.android.com/reference/androidx/lifecycle/ViewModel) 直到 Activity 完成并销毁。

![说明 ViewModel 随着 Activity 状态的改变而经历的生命周期。](.\pic\viewmodel-lifecycle.png)







## DataBinding









## 总结

Lifecycle 的存在，主要是为了解决 **生命周期管理 的一致性问题**。

LiveData 的存在，主要是为了帮助 新手老手 都能不假思索地 **遵循 通过唯一可信源分发状态 的标准化开发理念**，从而在快速开发过程中 规避一系列 **难以追溯、难以排查、不可预期** 的问题。

ViewModel 的存在，主要是为了解决 **状态管理 和 页面通信 的问题**。

DataBinding 的存在，主要是为了解决 **视图调用 的一致性问题**。

它们的存在 大都是为了 在软件工程的背景下 解决一致性的问题、将容易出错的操作在后台封装好，**方便使用者快速、稳定、不产生预期外错误地编码**。

