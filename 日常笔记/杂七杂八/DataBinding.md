# DataBinding



## 单向绑定

使用单向绑定刷新UI方式：

1. BaseObservable
2. ObservableField
3. ObservableCollection

### BaseObservable

1. notifyPropertyChanged(); 只会刷新属于它的UI，就如代码，他只会更新name。
2. notifyChange(); 会刷新所有UI。

```java
public class DataBean extends BaseObservable {
    @Bindable
    public String name;
    private int age;

    public DataBean(String name, int age) {
        this.name = name;
        this.age = age;
    }
    @Bindable
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        Log.d("name",name);
        notifyPropertyChanged(BR.dataBean);
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyChange();
    }
    
}

```



### ObservabledField

将变量使用 public final 进行修饰，重写get方法即可（final修饰后的变量），下方代码以 ObservableField<String> 和 ObservableField<Integer> 举例，你也可以用其他的，比如：
ObservableShort、ObservableBoolean、ObservableByte …等一系列

```java
public class DataBean extends BaseObservable {
    public final ObservableField<String> name;
    public final ObservableField<Integer> age;

    public DataBean(ObservableField<String> name, ObservableField<Integer> age) {
        this.name = name;
        this.age = age;
    }
    public ObservableField<String> getName() {
        return name;
    }
    public ObservableField<Integer> getAge() {
        return age;
    }
}
```



### ObservableCollection

不需要`DataBean`，直接用该容器就行。下面注意：

```xml
<data>
    ...
        <import type="android.databinding.ObservableList" />
        <!--注意这个地方，一定要用 "&lt;"和 "&gt;"，这里不支持尖括号-->
        <variable
            name="data"
            type="ObservableList&lt;String&gt;" />
    ...
</data>
...
				android:text='@{data["name"]}'
...
```



```java
public class MainActivity extends AppCompatActivity {
private ActivityMainBinding activityMainBinding;
private ObservableList<String> people;

@SuppressLint({"SetTextI18n", "InlinedApi"})
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    //这句不用管，是关于安卓状态栏的
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    people = new ObservableArrayList<>();
    people.add("NorthernBrain1");
    people.add("NorthernBrain2");
    people.add("NorthernBrain3");
    people.add("NorthernBrain4");

    activityMainBinding.setPeople(people);
    
    //注意加上这一句，否则你的事件没反应
    activityMainBinding.setChangeui(new ChangeUI());
}

public class ChangeUI {
    public void changeName() {
        for (int i = 0, size = people.size(); i < size; i++) {
            people.set(i, "改变UI：" + i);
        }
    }
}
```



## 双向绑定

### **ObservableField** 方式

和单向绑定的`ObservabaledField`相同。不同点是：+一个等号就行：

```xml
android:text="@={databean.data}"
```



## 事件绑定

```xml
android:afterTextChanged="@{listener.afterPasswordChanged}"
android:onClick="@{() -> listener.onClick(data)}"
android:onClick="@{listener::onClick}"
```

