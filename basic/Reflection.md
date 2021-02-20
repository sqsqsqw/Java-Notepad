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