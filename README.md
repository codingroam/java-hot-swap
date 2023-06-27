# java-hot-swap

java字节码热替换项目，无需重启java进程实现代码更新，提高开发效率，节约时间去陪女朋友！

## 功能模块

- hot-swap-core: 核心处理逻辑，加载 class
- hot-swap-agent: javaagent 入口
- hot-swap-monitor: 监听本地class 文件变化，实现本地class文件热替换到目标java进程
- hot-swap-server: 远程模式下，需要在目标java进程服务器启动server服务来达到Attach JVM(连接远程目标进程的目的)



### 本地模式
本地模式下只需启动monitor服务
### 远程模式
远程模式下需要启动monitor服务和server服务,server服务需要在目标java进程所在的主机启动

### conf配置及说明

```properties
// conf.properties
#mode=local本地模式启动，目标java进程与要热更新的文件在同一台机器上。mode=server远程模式启动，目标java进程与热更新文件在不同机器，此时需要用远程模式
mode=server

#监听位置，要监听的文件位置
monitorPath = D:/JavaProject/boot-web-demo/target/classes/com/example/bootwebdemo

#目标java进程pid
pidnum=1000

#远程模式下需要配置，hot-swap-server服务的ip:端口，ip即为目标java程序所在主机的ip(hot-swap-server服务与目标Java进程需要部署在同一台机器)，port默认8091，可以启动server服务时修改。
serverUrl = http://170.107.42.61:8091

#目标java进程需要开启一个port来接收改动的文件从而进行热替换
targetPort = 19527

#热加载方式，auto表示文件改动后自动热加载，manual表示要输入文件号指定要热加载的文件
hotSwapStyle = auto
```









###启动命令
<b>项目文件夹boot-jar下有启动jar包和脚本及配置文件</b>
```
linux和mac下使用boot.sh启动,方式如下
启动 monitor和server

# 启动monitor
bash boot.sh monitor 

#启动server
# -Dserver.port=6000 指定端口号，默认 8091
bash boot.sh server -Dserver.port=6000

windows下直接双击start.bat启动，根据提示启动monitor和server服务
```

monitor 指令

| 指令       | 参数      | 说明              |
|----------|---------|-----------------|
| h       |       | 查看帮助            |
查看帮助有详细的指令说明




## 编译打包

环境依赖

- jdk 1.8
- maven





## 基本原理

1. monitor服务监听本地文件变更（.class）并缓存变更文件路径
2. hot-swap-agent通过 javaagent 技术 attach 到 jvm 进程，拿到`Instrumentation`对象，并开始加载hot-swap-core。jar
3. 使用自定义类加载器（与业务代码隔离）加载 `hot-swap-core`,并执行start方法在目标进程开启socket端口 
4. monitor连接上目标进程socket端口，向目标java进程发送需要热替换的、变动的class文件
5. 通过`instrumentation.redefineClasses()`方法重新定义并加载 class


## 参考项目

- [arthas](https://github.com/alibaba/arthas)
- [lets-hotfix](https://github.com/liuzhengyang/lets-hotfix)
- [HotswapAgent](https://github.com/HotswapProjects/HotswapAgent)
- [java-hot-reload-agnet]

