# PL/SQL

## 程序结构

PL/SQL不区分大小写
一共分为三个部分：声明部分，可执行部分，异常处理部分

```SQL
    DECLARE
        --声明
        I INTEGER;
    BEGIN
        --执行语句
        --[异常处理]
    END;
```

## 输出

Oracle中默认输出选项是关闭的，需要先开启功能

``` vb
    > sqlplus
    SQL> set serveroutput on
    SQL> DECLARE
       2    --声明
       3    I INTEGER;
       4 BEGIN
       5    DBMS_output.put_line('Hello' || '_' || 'World');
       6 END;
       7 /                  --结束标识
    Hello_World
    PL/SQL 过程已成功完成
    SQL>
```

## 变量

一共两种类型

- 普通数据类型 (char, varchar2, date, number, boolean, long)
- 特殊变量类型（引用型变量，记录型变量）

声明变量的语法如下

``` sql
    变量名 变量类型 (变量长度) 
    例如：v_name varchar2(256);
```

### 普通变量

普通变量一共有两种赋值方式

1. 直接赋值语句     v_name := '张三'
2. 语句赋值         select 数值 into 变量

```SQL
    DECLARE
       v_name VARCHAR2(30) := '张三';
       v_age  NUMBER;
    BEGIN
        SELECT 20 INTO v_age FROM dual;
    END;
```

### 引用型变量

引用型变量通过 变量名称 表名.列名%TYPE

```SQL
    DECLARE
       v_name emp.ename%TYPE;
       v_age  emp.eage%TYPE;
    BEGIN
        SELECT ename, eage INTO v_name, v_age FROM emp WHERE eno = 1;
    END;
```

### 记录型变量

记录型变量可以记录一整行记录，相当于Java中的对象

语法：变量名称 表名%ROWTYPE

```SQL
    DECLARE
       v_emp emp%ROWTYPE;
    BEGIN
        SELECT * INTO v_emp FROM emp WHERE eno = 1;
    END;
```