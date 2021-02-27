# 2. 反射

## 2.1 注解

### 2.1.1 简介

Annotation 从JDK5.0开始引入的新技术

注解与注释的用处相符，都可以对程序做出解释，但是注解是可以被其他程序（如编译器）读取到的。同样注解也有 __检查和约束__ 的作用

注解的用法是在对应的方法，属性，类的上方使用，注解一般以 __@__ 开头，比如最常见的 `@Override `

### 2.1.2 内置注解

- `@Override` : 定义在 java.lang.Override 中，修辞方法的，表示一个方法重写了父类方法
- `@Deprecated` : 定义在 java.lang.Deprecated 中，修辞方法、属性、类，表示不鼓励程序员使用这样的元素，通常是因为它很危险或存在更好的选择。通俗来说就是遗弃。
- `@SuppressWarnings` : 定义在 java.lang.SuppressWarnings 中，用来抑制编译时的警告信息。

其中 `@SuppressWarnings` 注解是需要添加一个参数才可以使用，参数包括

> ---
>  一个参数的语法 @SuppressWarnings("all")，
>  多个参数的语法 @SuppressWarnings(value={"all","path"})
>
> - deprecation:使用了过时的类或方法的警告
> - unchecked：执行了未检查时的转换时的警告，集合就是未指定泛型
> - fall through:当在switch语句使用时发生case穿透
> - path:在类路径、源文件路径等中有不存在路径的警告
> - serial:可序列化类上缺少serialVerisonUID定义时的警告
> - finally:任何finally橘子不能完成时的警告
> - all：以上所有情况的警告。
> ---

### 2.1.3 元注解

元注解的作用就是负责 __注解其他的注解__ ，Java一共定义了四个标准的元注解类型

- `@Target` : 用于描述注解的适用范围
- `@Retention` : 表示需要在什么级别保存该注释信息，用于描述注解的生命周期 （SOURCE < CLASS < RUNTIME）
- `@Document` : 说明该注解将被包含在 javadoc 中
- `@Inherited` : 说明子类可以继承父类中的该注解

```java
//自定义注解
public class TestAnno {
    @MyAnnotation
    public void test() {}
}

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {
    
}

```

### 2.1.4 自定义注解

使用@interface定义注解时会自动继承 `java.lang.annotation.Annotation`

注解体内每一个方法代表了一个 __参数__ ，方法名就是参数名，返回值类型就是参数类型（只能是基本类型，Class，Stirng，enum的其中一种）

同时可以通过default来生命默认值，如果只有一个参数，则参数名普遍为 __value__（因为在使用中value参数可以省略参数名）

```java
//自定义注解
public class TestAnno2 {
    @MyAnnotation2(name = "Hashqi", age = 3000, school={"a uni", "b uni"})
    public void test() {}

    @MyAnnotation3("aa")
    public void test2() {}
}

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation2 {
    //注解参数
    String name() default "";
    int age() default 0;
    int id() default -1;

    String[] schools();
}
@interface MyAnnotation3 {
    //注解参数
    int value();
}

```

## 2.2 反射机制

### 2.2.1 概述

反射机制主要是用来解决Java本身是静态语言，进而无法在运行时更改其结构的问题。

```javascript
//javascript
function f() {
    var x = "var a=3; var b=5; alert(a+b);";
    eval(x);
    alert(x);
}
```

像 javascript 语言就是动态语言，在运行时代码可以根据某些条件改变自身结构。

但是引入反射机制可以将Java变成准静态语言，但是会影响一些性能（虽然性能要比正常情况慢几十倍，但是由于如今的处理器性能发展迅速，反射所影响的性能变得微乎其微）

Reflection（反射）是Java被视作 __动态语言__ 的关键，反射机制允许程序在执行期间借助于 __Reflection API__ 取得任何类的内部信息,并能直接操作任意对象的内部属性以及方法。

```java
    Class c = Class.forName("java.lang.String");
```

### 2.2.2 主要API

加载完类之后，在堆内存的方法区中就产生了一个Class类型的对象（`Object.getClass()` 方法可以获取）

 `getClass()` 方法的返回类型就是 `java.lang.Class`。同样，类内部的方法，属性和构造器分别为：

- `java.lang.reflect.Method` 
- `java.lang.reflect.Field` 
- `java.lang.reflect.Constructor` 