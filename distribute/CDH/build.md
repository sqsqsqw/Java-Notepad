# 1. CDH搭建



## 1.1 说明

目前是在并无多少知识基础的情况下安装CDH，如果存在概念上的错误后期会进行调整。

Cloudera公司于2021年2月份开始实施通过 `付费墙` 和 `身份验证` 的方式下载repo包，导致网上大部分的离线CDH安装教程失效，但是在线CDH安装又会因为rpm包过大而影响了安装时间，接下来通过三天的探索最终完成了对CDH的基本快速安装。

由于本人是在测试环境下安装CDH，所以会使用 Parcels 的方式，并使用嵌入式postgreSQL做为数据库。

## 1.2 概述

Cloudera版本（Cloudera’s Distribution Including Apache Hadoop，简称“CDH”），基于Web的用户界面,支持大多数Hadoop组件，包括HDFS、MapReduce、Hive、Pig、 Hbase、Zookeeper、Sqoop,简化了大数据平台的安装、使用难度。

## 1.3 安装部署

| 序号 | IP地址 | 主机名 |系统版本| 类型
| -------- | -------- | -------- | -------- | -------- |
| 1 | 192.168.222.129 | cdh1 |centos7.9|service|
| 2 | 192.168.222.130 | cdh2 |centos7.9|agent|
| 3 | 192.168.222.131 | cdh3 |centos7.9|agent|

    注：Cloudera Manager在安装时可能会遇到主机名不能有下划线的情况，本人建议以小写字母和数字的混合形式设置主机名

### 1.3.1 设置主机名

```shell
    vi /etc/hostname    ## 更改主机名
    vi /etc/sysconfig/network-scripts/ifcfg-ens33  ## 更改网络配置
    vi /etc/hosts ## 更改hosts文件
    ## 本人是在本地开的虚拟机，并且是测试环境，所以reboot是比较理想的更新网络配置的方式
    reboot
```

### 1.3.2 取消防火墙（三台）

```shell
systemctl stop firewalld.service
systemctl disable firewalld.service
systemctl status firewalld.service
```

### 1.3.3 关闭selinux（三台）

```shell
setenforce 0
vim /etc/selinux/config

更改：
SELINUX=disable

退出后：
reboot
```
以上两个步骤是为了service和agent连接不会受到selinux和防火墙的影响。

### 1.3.4 yum设置不删除资源包（三台）

```shell 
vi /etc/yum.conf 
## 设置
keepcache=1
```

这个操作是为了获得rpm包并提供给其他主机使用，如果本身有cloudera相关资源包可以忽略下一个步骤

### 1.3.5 获取需要的资源包

接下来的步骤需要获取以下资源包：

- cloudera-manager-agent
- cloudera-manager-daemons
- cloudera-manager-server
- cloudera-manager-server-db-2
- enterprise-debuginfo
- openjdk

方法其实很简单，只需要让其中一个机器装一遍这个软件，并在下载资源包的位置做个备份就好了，假设我们需要下载agent包：

yum install cloudera-manager-agent -y

安装完毕后资源包将会在 /var/cache/yum/x86_64/7/cloudera-manager/package 中找到，将下载后的包进行备份，并获取全部需要的包即可。

### 1.3.6 将资源安装在其他主机上

获取到资源包后即可把资源包放置在 /var/cache/yum/x86_64/7/cloudera-manager/package 同一位置中，yum在安装前会优先扫描这个文件夹下面的资源包，如果存在则直接跳过下载流程。这样所有的安装流程就完成了。

### 1.3.7 cloudera官网上获取安装脚本并运行

上面几个步骤是为了在安装时减少下载的时间而提前安装资源，接下来去官网上寻找 [CDP私有云](https://www.cloudera.com/downloads/cdp-private-cloud-trial.html.html) 的体验资格，并按照接下来的流程在service上进行安装。

```shell
cd 
wget https://archive.cloudera.com/cm7/7.1.4/cloudera-manager-installer.bin
chmod u+x cloudera-manager-installer.bin
sudo ./cloudera-manager-installer.bin
```

安装后就可以通过 http://192.168.222.129:7180/ 访问网页的控制台，并添加新的集群。

### 1.3.8 按照流程进行安装

1. 进入集群安装界面，进入到欢迎流程，点击继续
2. 设置集群名称并设置集群类型
3. 扫描主机可以在主机名称上写下 `192.168.222.[129-131]` ,然后点击扫描，就可以在下面的扫描结果中看到三个主机

>注：如果当前受管 是 ，那么就可以进入主页后找到主机选项并删除这些受管的主机就可以了。

4. 接下来选择存储库，一般可以选用线上的存储库就可以了，其他的默认就行。（但是因为已经预先安装了，所以不会花费太大时间）
5. 接下来选择JDK，选择操作系统默认就可以。
6. 最终选择SSH凭证，我直接使用root输入密码的方式，生产环境可以使用公钥进行登录。
7. 然后接下来的三个流程都是安装流程，下载和安装可以稍微等一段时间

>注：service主机上的 /var/log/cloudera-scm-server/cloudera-scm-server.log 和 /var/log/cloudera-scm-agent/cloudera-scm-agent.log 这两个log文件下可以看到安装的报错信息，之前看到安装过程中出现了 failed to receive heartbeat from agent 的问题，最终通过 https://www.cnblogs.com/zlslch/p/7121275.html 这个方法解决了问题。如果再遇到其他的问题，可以通过cloudera的官方社区寻找解决办法
