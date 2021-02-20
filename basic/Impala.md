[TOC]

# 3 Impala SQL基础

## 3.1 介绍

节选自 [impala 概述_w3cschool](https://www.w3cschool.cn/impala/impala_overview.html)

### 3.1.1 什么是impala

Impala是用于处理存储在Hadoop集群中的大量数据的MPP（大规模并行处理）SQL查询引擎。 它是一个用C++和Java编写的开源软件。 与其他Hadoop的SQL引擎相比，它提供了高性能和低延迟。

换句话说，Impala是性能最高的SQL引擎（提供类似RDBMS的体验），它提供了访问存储在Hadoop分布式文件系统中的数据的最快方法。

### 3.1.2 impala的优点

- 使用impala，您可以使用传统的SQL知识以极快的速度处理存储在HDFS中的数据。
- 由于在数据驻留（在Hadoop集群上）时执行数据处理，因此在使用Impala时，不需要对存储在Hadoop上的数据进行数据转换和数据移动。
- 使用Impala，您可以访问存储在HDFS，HBase和Amazon s3中的数据，而无需了解Java（MapReduce作业）。您可以使用SQL查询的基本概念访问它们。
- 为了在业务工具中写入查询，数据必须经历复杂的提取 - 变换负载（ETL）周期。但是，使用Impala，此过程缩短了加载和重组的耗时阶段通过新技术克服，如探索性数据分析和数据发现，使过程更快。
- Impala正在率先使用Parquet文件格式，这是一种针对数据仓库场景中典型的大规模查询进行优化的柱状存储布局。

## 3.2 简单语句

### 3.2.1 创建

- 创建数据库

```SQL
-- 创建数据库[如果不存在]
CREATE DATABASE [IF NOT EXISTS] database_name ;  

-- 查看数据库
SHOW DATABASES ;
```

- 创建表

```SQL
CREATE TABLE IF NOT EXISTS database_name.table_name (
   column1 data_type COMMENT "",
   column2 data_type,
   column3 data_type,
   ………
   columnN data_type
)
partitioned by (part_name1 data_type, part_name2 data_type ………)   --  分区
stored as parquet -- 压缩的文件格式选择
;
```

注：
1. 分区可以从物理层面进行分区，从而减少数据读取的时间开销
2. 文件格式的选择有如下

| 文件类型 | impala能否直接创建 | impala能否直接插入 |
|:-------:|:-----------------:|:-----------------:|
| Parquet | 能 | 支持create table, insert |
| TextFile | 能,默认的文件格式 | CREATE TABLE, INSERT, 查询。如果使用 LZO 压缩，则必须在 Hive 中创建表和加载数据 |
| RCFile | 能 | 支持CREATE，查询，在 Hive 中加载数据 |
| SequenceFile | 能 | 支持：CREATE TABLE, INSERT, 查询。需设置 |
| orc | ? | ? |

需要说明一下，TextFile类型是默认的文件格式，这个格式不会对文本做任何处理，但是空间占比也会比较大。

orc是hive 0.11版本才推出的压缩格式，根据网上查阅相关信息，orc的压缩比和响应速度都要在parquet上快上不少。目前相关信息还没有查阅。

### 3.2.2 删除

- 删除数据库

```SQL
-- 删除（数据库|XSD）[如果存在] 数据库名 
DROP (DATABASE|SCHEMA) [IF EXISTS] database_name;
```

- 删除表数据

- 删除表数据+表结构

### 3.2.3 添加

```SQL
-- 将select语句查到的数据按照table_name1的表结构进行存储，注意拥有分区字段的表在select * 的时候会在最后列出分区字段。
INSERT OVERWRITE TABLE database_name.table_name1 
SELECT * FROM database_name.table_name2 ;
```

### 3.2.4 查看表结构

```SQL
DESCRIBE FORMATTED database_name.table_name ;
```

### 3.2.5 导入

目前个人使用过的导入方式是通过hive进行导入，然后通过impala的refresh语句进行更新。

导入的数据是以unl的后缀文件，内部数据按照‘|’分隔符进行分割

1. 创建一个映射表，分隔符为到处数据的分隔符

```SQL
-- 创建映射表
create table IF NOT EXISTS database_name.table_name (
   column1 data_type COMMENT "",
   column2 data_type,
   column3 data_type,
   ………
   columnN data_type
)
row format delimited fields terminated by '|';
-- 按照|分隔符进行分割
-- unl文件内也通过|进行数据分割
```

2. 在linux命令行中使用hive运行sql语句命令进行导入

```bash
## 将unl文件放置在当前目录下并运行下面命令进行导入，-e选项是将后面的sql语句在hive上运行
hive -e "LOAD DATA LOCAL IMPALA '${table_name}.unl' OVERWRITE INTO TABLE ${database_name}.${table_name} ;" 
```

3. 使用impala-shell命令对hive的数据更新到impala中

```bash
## 将unl文件放置在当前目录下并运行下面命令进行导入，-q选项是将后面的sql语句在impala上运行
impala -q "REFRESH  ${database_name}.${table_name}" ;
```

## 3.3 知识点

### 3.3.1 查看某字段是否存在重复

```SQL
SELECT FIELD1, COUNT(*)
  FROM DB1.TABLE1
 GROUP BY FIELD1
HAVING COUNT(*) > 1 ;
```

### 3.3.2 创建一个和某个表同一结构的表

```SQL
CREATE TABLE TABLE_NAME2 AS 
SELECT * FROM TABLE_NAME1 WHERE 1=2 ;

-- like语句可以将全部的索引以及完整表结构复制到新的表中，但是Oracle并不支持这个语法
CREATE TABLE TABLE_NAME2 LIKE TABLE_NAME1 ;
```

### 3.3.3 在hive上更新后刷新到impala

```SQL
-- 对impala中没有的表进行更新时使用这个语句，时间会比较慢
INVALIDATE METADATA database_name.table_name ;

-- 对impala中已经存在的表进行更新
REFRESH  database_name.table_name ;
```

### 3.3.4 若聚合函数的值存在null，则聚合函数会忽略null值

```SQL 
-- 如果计算整行行数，则存在null值的行也会被计算
-- 结果：2
SELECT COUNT(*)   --COUNT(a.*)会导致语法错误
FROM (
   SELECT 5 AS num
   UNION
   SELECT NULL AS num
) a ;

-- 如果计算单元格数，则会省略null值
-- 结果：1
SELECT COUNT(num)
FROM (
   SELECT 5 AS num
   UNION
   SELECT NULL AS num
) a ;

-- 请区分聚合函数和其他语法的区别
-- 结果： NULL
SELECT 5 + NULL;
-- 结果： 5
SELECT SUM(num)
FROM (
   SELECT 5 AS num
   UNION
   SELECT NULL AS num
) a ;

-- 遇到的坑！！！！
-- 结果：5
SELECT GROUP_CONCAT(num, ';')
FROM (
   SELECT '5' AS num
   UNION
   SELECT NULL AS num
) a ;
```

### 3.3.5 中文字符串切割

   这个坑之前困扰了好长时间，也一直没有解决，假设数据表的类型为textfile，并且分隔符为某一字符，那么在截断字符串时就会出现将汉字从中间截断的情况，而且最后一位乱码的数值会比较容易和分隔符相融合，最终呈现出缺少字段的情况。目前的解决办法是将截断操作放到oracle中进行操作，并将需要字段长度的大小除3取整，如：地址字段上报需求为90位长度，那么在Oralce则需要使用SUBSTR(address, 1, 30)这样的操作，以保证字符最大长度为90位字符（默认编码格式UTF-8）

```SQL
-- 在Oracle数据库中是对字符进行处理
-- 结果：一二三四五
SELECT SUBSTR("一二三四五六七八九", 1, 5) FROM dual ;

-- 但是在Impala数据库中，substr却是对字节进行处理
-- 结果：一 (截断后的字符呈现乱码，但是这里结果描述的乱码是模拟出来的)
SELECT SUBSTR("一二三四五六七八九", 1, 5) ;
```

### 3.3.6 排序函数使用总结

```SQL

/* 
rank  num
1     1 
1     1 
2     2 
3     3
*/
dense_rank() over(partition by a.a order by a.b) 

/* 
rank  num
1     1 
1     1 
2     3 
3     4
*/
rank() over(partition by a.a order by a.b)

/* 
rank  num
1     1 
1     2 
2     3 
3     4
*/
row_number() over(partition by a.a order by a.b)

over(
   partition by a.a  -- 分组
       order by a.b      -- 排序
        rows 2 preceding  -- 限制行数（当前行的前两行）
)
```

### 3.3.7 cross join

```SQL

SELECT * 
FROM (
   SELECT 'A' AS name
   UNION ALL
   SELECT 'B' AS name
) t1
CROSS JOIN (      -- 笛卡尔积
   SELECT 1 AS num
   UNION ALL
   SELECT 2 AS num
) t2 ;
-- 等价于
SELECT * 
FROM (
   SELECT 'A' AS name
   UNION ALL
   SELECT 'B' AS name
) t1
JOIN (
   SELECT 1 AS num
   UNION ALL
   SELECT 2 AS num
) t2
ON 1=1 ;
```

### 3.3.8 函数总结

```SQL
count(field)                  -- 计算非空字段个数
count(*)                      -- 计算行数
sum(field)                    -- 计算非空字段的总和
avg(field)                    -- 计算非空字段的平均数
coalesce(ceil1, ceil2...)     -- 可传入任意数量的参数，返回第一个非空字段
```

### 3.3.9 SQL格式

从《SQL进阶教程》- MICK 这本书中看的SQL格式风格确实简洁易懂，故概括自此书中的文章。

#### 注释

两种注释

``` SQL
--第一种注释
/*第二种注释*/
```

需要把揉在一起难以阅读的条件分割成有意义的代码块时，比如必须往 WHERE 子句中写很多条件的时候，这种写法很方便。注释也可以与代码在同一行。

``` SQL
SELECT col_1 
 FROM SomeTable; 
 WHERE col_1 = 'a'
 AND col_2 = 'b'
 -- 下面的条件用于指定 col_3 的值是'c'或者'd'
 AND col_3 IN ( 'c', 'd' );
```

#### 缩进

SQL 中 SELECT、FROM 等语句都有着明确的作用，请务必以这样的单位进行换行。笔者认为，比起所有关键字都顶格左齐的写法，让关键字右齐的写法更好。

``` SQL
SELECT col_1, 
       col_2, 
       col_3, 
       COUNT(*) 
  FROM tbl_A 
 WHERE col_1 = 'a'
   AND col_2 = ( SELECT MAX(col_2) 
                   FROM tbl_B 
                  WHERE col_3 = 100 ) 
 GROUP BY col_1, 
          col_2, 
          col_3
```

#### 空格

不管用什么语言编程都一样，代码中需要适当地留一些空格。如果一点都不留，所有的代码都紧凑到一起，代码的逻辑单元就会不明确，也会给阅读的人带来额外负担。

``` SQL
-- √好的示例
SELECT  col_1
  FROM  tbl_A A, tbl_B B 
 WHERE  ( A.col_1 >= 100 OR A.col_2 IN ( 'a', 'b' ) ) 
   AND  A.col_3 = B.col_3; 
--× 坏的示例
SELECT col_1
  FROM tbl_A A,tbl_B B 
 WHERE (A.col_1>=100 OR A.col_2 IN ('a','b')) 
   AND A.col_3=B.col_3;
```

#### 大小写

在 SQL 里，关于应该如何区分使用大小写字母有着不成文的约定：关键字使用大写字母，列名和表名使用小写字母（也有一些人习惯只将单词的首字母大写 A）。很多图书也都是这样的。笔者经常看到有些人写出的 SQL 语句全部使用大写字母，或者全部使用小写字母，真心感觉不舒服。

``` SQL
-- √大小写有区分，易读
SELECT  col_1, col_2, col_3, 
        COUNT(*) 
  FROM  tbl_A 
 WHERE  col_1 = 'a'
   AND  col_2 = ( SELECT  MAX(col_2) 
                    FROM  tbl_B 
                   WHERE  col_3 = 100 ) 
 GROUP BY  col_1, col_2, col_3; 
```

#### 逗号

这种“前置逗号”的写法有两个好处。第一个是删掉最后一列“col_4”后执行也不会出错。如果按照一般的写法来写，那么删掉最后的 col_4 后，SELECT 子句的结尾会变成“col_3,”，执行会出错。为了防止出错，还必须手动地删除逗号才行。当然，“前置逗号”的写法在需要删除第一列时也会有同样的问题，但是一般来说需要添加或删掉的大多是最后一列。写在第一位的列很多时候都是重要的列，相对而言不会有很大的变动。

第二个好处是，每行中逗号都出现在同一列，因此使用 Emacs 等可以进行矩形区域选择的编辑器就会非常方便操作。如果将逗号写在列后面，那么逗号的列位置就会因列的长度不同而参差不齐。

``` SQL
SELECT    col_1 
        , col_2 
        , col_3 
        , col_4 
  FROM  tbl_A;
```