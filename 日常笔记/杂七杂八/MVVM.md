# MVVM

android MVVM和DataBinding框架不一样的，MVVM是一种架构模式，DataBinding是一个实现数据和UI绑定的框架、是构建MVVM模式的一个工具。

### MVC：

**View**:XML布局     

**Model**:实体模型（数据存储、获取、数据状态变化）  

**Control**:对应于Activity、处理数据、业务和UI

> 这样Activity中的代码大爆炸。相信大多数Android开发者都遇到过一个Acitivty数以千行的代码情况吧！所以，更贴切的说法是，这个MVC结构最终其实只是一个Model-View（Activity:View&Controller）的结构。

### MVP：

**View: **对应于Activity和XML，负责View的绘制以及与用户的交互。 

**Model: **依然是实体模型。

 **Presenter: **负责完成View与Model间的交互和业务逻辑



### MVVM：

**View: **对应于Activity和XML，负责View的绘制以及与用户交互。 

**Model: **实体模型。

 **ViewModel: **负责完成View与Model间的交互，负责业务逻辑。

> 在常规的开发模式中，数据变化需要更新UI的时候，需要先获取UI控件的引用，然后再更新UI。获取用户的输入和操作也需要通过UI控件的引用。在MVVM中，这些都是通过数据驱动来自动完成的，数据变化后会自动更新UI，UI的改变也能自动反馈到数据层，数据成为主导因素。这样MVVM层在业务逻辑处理中只要关心数据，不需要直接和UI打交道，在业务处理过程中简单方便很多。



### View

View层做的就是和UI相关的工作，我们只在XML、Activity和Fragment写View层的代码，View层不做和业务相关的事，也就是我们在Activity不写业务逻辑和业务数据相关的代码，更新UI通过数据绑定实现，尽量在ViewModel里面做（更新绑定的数据源即可），Activity要做的事就是初始化一些控件（如控件的颜色，添加RecyclerView的分割线），View层可以提供更新UI的接口（但是我们更倾向所有的UI元素都是通过数据来驱动更改UI），View层可以处理事件（但是我们更希望UI事件通过Command来绑定）。**简单地说：View层不做任何业务逻辑、不涉及操作数据、不处理数据，UI和数据严格的分开。**

### ViewModel

ViewModel层做的事情刚好和View层相反，ViewModel只做和业务逻辑和业务数据相关的事，不做任何和UI相关的事情，ViewModel 层不会持有任何控件的引用，更不会在ViewModel中通过UI控件的引用去做更新UI的事情。ViewModel就是专注于业务的逻辑处理，做的事情也都只是对数据的操作（这些数据绑定在相应的控件上会自动去更改UI）。同时DataBinding框架已经支持双向绑定，让我们可以通过双向绑定获取View层反馈给ViewModel层的数据，并对这些数据上进行操作。关于对UI控件事件的处理，我们也希望能把这些事件处理绑定到控件上，并把这些事件的处理统一化，为此我们通过BindingAdapter对一些常用的事件做了封装，把一个个事件封装成一个个Command，对于每个事件我们用一个ReplyCommand去处理就行了，ReplyCommand会把你可能需要的数据带给你，这使得我们在ViewModel层处理事件的时候只需要关心处理数据就行了，具体见**MVVM Light Toolkit 使用指南**的Command部分。再强调一遍：ViewModel 不做和UI相关的事。

### Model

Model层最大的特点是被赋予了数据获取的职责，与我们平常Model层只定义实体对象的行为截然不同。实例中，数据的获取、存储、数据状态变化都是Model层的任务。Model包括实体模型（Bean）、Retrofit的Service ，获取网络数据接口，本地存储（增删改查）接口，数据变化监听等。Model提供数据获取接口供ViewModel调用，经数据转换和操作并最终映射绑定到View层某个UI元素的属性上。



> If the MVP pattern meant that the Presenter was telling the View directly what to display, in MVVM, **ViewModel exposes streams of events** to which the Views can bind to. Like this, the ViewModel does not need to hold a reference to the View anymore, like the Presenter is. This also means that all the interfaces that the MVP pattern requires, are now dropped.
>
> The Views also notify the ViewModel about different actions. Thus, the MVVM pattern supports two-way data binding between the View and ViewModel and there is a many-to-one relationship between View and ViewModel. View has a reference to ViewModel but **ViewModel has no information about the View**. The consumer of the data should know about the producer, but the producer — the ViewModel — doesn’t know, and doesn’t care, who consumes the data.



### LiveData

使用 LiveData 时没必要执行此步骤，因为它具有生命周期感知能力。这意味着，除非 Fragment 处于活跃状态（即，已接收 [`onStart()`](https://developer.android.com/reference/android/app/Fragment#onStart()) 但尚未接收 [`onStop()`](https://developer.android.com/reference/android/app/Fragment#onStop())），否则它不会调用 `onChanged()` 回调。当调用 Fragment 的 [`onDestroy()`](https://developer.android.com/reference/android/app/Fragment#onDestroy()) 方法时，LiveData 还会自动移除观察者。



ViewModel:

```kotlin
class UserProfileViewModel(       savedStateHandle: SavedStateHandle    ) : ViewModel() {       val userId : String = savedStateHandle["uid"] ?:              throw IllegalArgumentException("missing user id")       val user : LiveData<User> = TODO()    }
```



view:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {       super.onViewCreated(view, savedInstanceState)       viewModel.user.observe(viewLifecycleOwner) {           // update UI       }    }
```





依赖注入：

依赖注入体系架构的主要特征是将应用程序逻辑分离为两个不相交的类集

为了使功能和构造类不相交，必须满足以下条件：

- 封装核心应用程序功能的类不能解析依赖关系或从Functional集实例化类
- 解决依赖关系或从Functional set实例化类的类不得封装任何核心应用程序的功能



为了将构造和功能集合在一起。主要有两种集成的方法：

- 纯依赖注入
- 依赖注入框架



其实就是通过方法进行创建新的对象。

