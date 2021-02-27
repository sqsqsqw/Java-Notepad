# 1. CDH搭建



## 1.1 说明

作者：kaliarch
链接：https://www.jianshu.com/p/106739236db4
来源：简书

## 1.2 概述

Cloudera版本（Cloudera’s Distribution Including Apache Hadoop，简称“CDH”），基于Web的用户界面,支持大多数Hadoop组件，包括HDFS、MapReduce、Hive、Pig、 Hbase、Zookeeper、Sqoop,简化了大数据平台的安装、使用难度。

## 1.3 安装部署

| 序号 | IP地址 | 主机名 |系统版本|
| -------- | -------- | -------- | -------- |
| 1 | 192.168.222.129 | CDH_1 |centos7.9|
| 2 | 192.168.222.130 | CDH_2 |centos7.9|
| 3 | 192.168.222.131 | CDH_3 |centos7.9|

### 1.3.1 安装依赖包（三台）

```shell
yum install -y cyrus-sasl-gssapi portmap fuse-libs bind-utils libxslt fuse
yum install -y /lib/lsb/init-functions createrepo deltarpm python-dektarpm
yum install -y mod_ssl openssl-devel python-psycopg2 MySQL-python
```

### 1.3.2 安装httpd（三台）

```shell
yum install httpd
yum install createrepo
```

### 1.3.3 配置hosts（三台）

```
vi /etc/hosts
192.168.222.129 CDH_1
192.168.222.130 CDH_2
192.168.222.131 CDH_3
192.168.222.132 CDH_4
```

### 1.3.4 取消防火墙（三台）

```shell
systemctl stop firewalld.service
systemctl disable firewalld.service
systemctl status firewalld.service
```

### 1.3.5 关闭selinux（三台）

```shell
setenforce 0
vim /etc/selinux/config

更改：
SELINUX=disable

退出后：
reboot
```

### 1.3.6 配置httpd服务

```shell
systemctl start httpd.service
cd /var/www/html

```

# 因为目前下载cm包需要付费，并且没有找到资源，所以放弃安装，，，，
wget https://archive.cloudera.com/cm6/6.2.1/redhat7/yum/cloudera-manager.repo -P /etc/yum.repos.d/