# java-hot-swap

java bytecode hot replacement project, no need to restart the java process to achieve code update, improve development efficiency, save time to accompany girlfriend!

## Function module

- hot-swap-core: indicates the core processing logic and loads the class
- hot-swap-agent: specifies the javaagent entry
- hot-swap-monitor: monitors changes in the local class file and implements hot replacement of the local class file to the target java process
- hot-swap-server: In remote mode, you need to start the server service on the target java process server to Attach JVM(connect to remote target processes).



### Local mode
In local mode, you only need to start the monitor service
### Remote mode
In remote mode, you need to start the monitor and server services. The server services must be started on the host where the target java process resides

### conf configuration and description

```properties
// conf.properties
#mode=local When started in local mode, the target java process is on the same machine as the file to be hot updated. mode=server Start in remote mode. The target java process and hot update file are on different machines. In this case, remote mode is required
mode=server

# Listen location, the location of the file to listen to
monitorPath = D:/JavaProject/boot-web-demo/target/classes/com/example/bootwebdemo

# Target java process pid
pidnum=1000

# In remote mode, the ip address of the hot-swap-server service: port. ip is the ip address of the host where the target java program resides (the hot-swap-server service and the target Java process need to be deployed on the same machine). The default port is 8091, which can be changed when the server service is started.
ServerUrl = http://170.107.42.61:8091

# The target java process needs to open a port to receive the changed file for hot replacement
targetPort = 19527

# Hot loading mode, auto indicates that the file is automatically hot loaded after changes, manual indicates that the file number should be entered to specify the file to be hot loaded
hotSwapStyle = auto
` ` `









### Start command
` ` `
### Start monitor and server (script start, currently only shell script written, support linux and mac)
The project folder boot-jar contains the startup jar package, scripts, and configuration files

```bash
# Start monitor
bash boot.sh monitor

# Start server
# -Dserver.port=6000 Specifies the port number. The default is 8091
bash boot.sh server -Dserver.port=6000
` ` `

monitor instruction

The | directive | parameter | describes |
|----------|---------|-----------------|
| h | | See the help |
View help with detailed instructions




## Compile and package

Environmental dependence

- jdk 1.8+
- maven

```bash
# mac or linux
make package
` ` `

```bash



## The fundamentals

1. The monitor service listens for local file changes (.class) and caches the changed file path
2. hot-swap-agent attaches to the jvm process using the javaagent technology, obtains the Instrumentation object, and starts loading hot-swap-core. jar
3. Use a custom class loader (isolated from the business code) to load 'hot-swap-core' and execute the start method to open the socket port in the target process
4. The monitor connects to the socket port of the target process and sends the class file that needs to be hot replaced to the target java process
5. Through ` instrumentation. RedefineClasses ` () method to redefine and loading class


## Reference items

- [arthas](https://github.com/alibaba/arthas)
- [lets-hotfix](https://github.com/liuzhengyang/lets-hotfix)
- [HotswapAgent](https://github.com/HotswapProjects/HotswapAgent)
- [java-hot-reload-agnet]