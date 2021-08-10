## Shadow



### 优势和劣势

优点：

* Shadow所指的插件是插件的代码完全是一个正常可安装的App代码。这样的App代码应用了Shadow之后可以免安装运行在另一个App中。

* Shadow是一个完全无Hack，甚至零反射实现的Android插件框架。

* Shadow是一个全动态实现的插件框架，就是说插件框架的代码跟插件的代码一样都是动态发布的。

* Shadow没有使用任何非公开SDK接口，实现了和原本在用的使用了大量非公开SDK接口的实现一样的功能。

* 除了插件代码之外，插件框架本身的所有逻辑代码也都是动态的。实际上插件框架的代码我们是和插件打包在一起发布的，Shadow接入时只使用了15.1KB，160个方法。



![image-20210629203251939](.\image-20210629203251939.png)



缺点（需要注意的点）：

* Shadow的插件要和宿主包名保持一致

  * > Shadow代码中并没有什么对包名的特殊处理逻辑，只有一处检查包名是否一致的逻辑（`com.tencent.shadow.core.loader.blocs.ParsePluginApkBloc#parse`）。去掉这段逻辑大部分情况下插件也是可以运行的。只有在一些OEM手机的特殊场景会出问题，比如一些国产手机系统的WebView或者输入栏中长按弹出菜单就有可能会Crash。所以去掉了这个限制后，就需要不停地去兼容各种OEM系统。这也不是不可行，因为Shadow有全动态的设计，插件框架的兼容代码也可以动态更新。
    >
    > 但是我们认为更合理的方法还是保持插件和宿主包名一致。只需要有一套完善的自动构建CI/CD，针对不同渠道自动修改ApplicationId，编译出ApplicationId不同的插件包去分发也不是很难的事情。关键是，这件事看起来麻烦，实际上长期来看不需要人工干预。而不停地兼容OEM系统，则需要长期投入人力人工分析解决。

    

* Fragment命名会改变，调试变麻烦

  * >Fragment的具体实现方案和原因在这里先不讲，总是我们由于坚持不使用任何Hack手段实现，包括在编译期也坚持不Hack官方的构建流程，导致我们最后只能选择将插件中原本的Fragment类名改名为在原本名字后面加一个下划线。比如业务插件里有一个`com.xx.GiftFragment`类，实际运行时这个类的名字就变成了`com.xx.GiftFragment_`。这就导致在`com.xx.GiftFragment`的源码上打断点是断不下来的。必须在程序运行起来之后，用IDE的重命名功能把它改名为`com.xx.GiftFragment_`，使得源码和运行时类名字一致才能断点。

    

* 版本控制更复杂

  * > 这实际上是优点带来的负面问题，在Shadow的设计中有3个部分：`host`,`manager`,`plugin`，这3个部分是分别发布版本的。而其他没有全动态设计的插件框架只有`host`和`plugin`两部分。这些部分之间的版本关系都是多对多的关系，所以版本管理变得更复杂了。这部分版本的管理，在我们的实现中是一个依赖于腾讯内部后台框架的后台服务，因此没能在这次开源中带出来。Shadow开源的代码目前是没有包括插件下载和版本检查实现的。manager只实现了下载插件之后的安装逻辑，也包括升级功能。
    >
    > 
    >





### 基本原理

Shadow是零反射设计，每个插件会在一个独立的进程，



#### 关于动态化

Shadow将我们定义的插件框架的所有部分全部实现了动态化加载，使得插件框架自身的问题可以动态修复，也使得插件框架成为了插件包的一部分，避免了插件需要适配不同版本插件框架的问题。



#### 关于插件的调用

**加载宿主类**：插件只能加载白名单的宿主类

> 1. 在白名单中，直接走双亲委派逻辑。直接交给宿主的PathClassLoader加载。
> 2. 不在白名单中，先尝试自己加载。自己加载失败，交给宿主PathClassLoader的父加载器加载。这个过程跳过了宿主PathClassLoader。



**加载其他插件中的类**:把所依赖的插件的classloader组装的该插件的parent ClassLoader

> config.json对插件的描述其实还有一个dependsOn字段，用来表示依赖的其他插件的。shadow在加载插件时，会先判断它所依赖的插件是否已经加载。如果依赖的插件已经全部加载，则把加载这些插件的PathClassLoader组装到一个CombineClassLoader中。这个CombineClassLoader就是当前插件PathClassLoader的父加载器。如果有依赖的插件没有加载，则抛出异常。因此这就要求我们熟悉插件间的调用关系，在加载插件时，先加载其依赖插件。
>
> CombineClassLoader是介于HostClassLoader和PluginClassLoader中间的



#### 关于Activity

采用的代理的方案，但是不用继承PluginActivity,而是采用在transform中更改字节码来动态更改activity的继承。

![image-20210701222324939](D:\Users\80264247\AppData\Roaming\Typora\typora-user-images\image-20210701222324939.png)





### 框架的结构



包结构：

![76705037-0476ec00-6718-11ea-922c-cca0a902dfc9](D:\Shadow\76705037-0476ec00-6718-11ea-922c-cca0a902dfc9.png)



├── projects
│  ├── sample *// 示例代码*
│  │  ├── README.md
│  │  ├── maven
│  │  ├── sample-constant *// 定义一些常量*
│  │  ├── sample-host *// 宿主实现*
│  │  ├── sample-manager *// PluginManager 实现*
│  │  └── sample-plugin *// 插件的实现*
│  ├── sdk *// 框架实现代码*
│  │  ├── coding *// lint*
│  │  ├── core
│  │  │  ├── common
│  │  │  ├── gradle-plugin *// gradle 插件*
│  │  │  ├── load-parameters
│  │  │  ├── loader *// 负责加载插件*
│  │  │  ├── manager *// 装载插件，管理插件*
│  │  │  ├── runtime *// 插件运行时需要，包括占位 Activity，占位 Provider 等等*
│  │  │  ├── transform *// Transform 实现，用于替换插件 Activity 父类等等*
│  │  │  └── transform-kit
│  │  └── dynamic *// 插件自身动态化实现，包括一些接口的抽象*



Manager、LoadParameters、Loader三个部分。Loader工作在插件进程，负责将插件免安装的运行起来，解决插件框架的核心问题

* Manager：工作在宿主进入插件的入口界面所在进程，负责下载插件、安装插件，然后将插件信息封装在LoadParameters中控制Loader启动插件。管理插件，包括插件的下载逻辑、入口逻辑、预加载逻辑等。反正就是一切还没有进入到Loader之前的所有事情。
* Loader：工作在插件进程，负责将插件免安装的运行起来，解决插件框架的核心问题。Loader是框架的核心部分。主要负责加载插、管理四大组件的生命周期、Application的生命周期等功能。很多插件框架只有Loader这部分功能。
* Runtime：Runtime这一部分主要是注册在AndroidManifest.xml中的一些壳子类。Shadow作者对这部分的描述是被迫动态化







> sample里面的包结构
>
> - `sample-host`是宿主应用
> - `sample-manager`是插件管理器的动态实现
> - `sample-plugin/sample-loader`是loader的动态实现，业务主要在这里定义插件组件和壳子代理组件的配对关系等。
> - `sample-constant`是在前3者中共用的相同字符串常量。
> - `sample-plugin/sample-runtime`是runtime的动态实现，业务主要在这里定义壳子代理组件的实际类。
> - `sample-plugin/sample-app-lib`是业务App的主要代码，是一个aar库。
> - `sample-plugin/sample-normal-app`是一个apk模块壳子，将`sample-app-lib`打包在其中，演示业务App是可以正常安装运行的。
> - `sample-plugin/sample-plugin-app`也是一个apk模块壳子，同样将`sample-app-lib`打包在其中，但是额外应用了Shadow插件，生成的apk不能正常安装运行。







### 接入指南

宿主工程

1. 添加相应的依赖:host
2. Manifest添加代理Activity
3. 在宿主中创建PluginManager工具



PluginManager工程

1. 添加依赖：common,manager,host
2. 创建插件管理类



插件（包名需要和宿主的报名一致）

1. 依赖：plugin

2. 创建runtime，主要放在宿主中注册的壳子

3. 创建loader（业务）

   1. > manager在加载"插件"时，首先需要先加载"插件"中的runtime和loader， 再通过loader的Binder（插件应该处于独立进程中避免native库冲突）操作loader进而加载业务App。

   

4. 在业务工程里配置插件















