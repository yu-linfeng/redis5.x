# 准备工作

## Redis安装

Redis5.0.7下载地址：[https://redis.io/download](https://redis.io/download)

### mac OS

在下载完redis-5.0.7.tar.gz后，我们通过命令```tar -zxvf redis-5.0.7.tar.gz```，解压后将redis安装包拷贝到```/usr/local```目录下，命令```sudo mv redis-5.0.7 /usr/local```。

进入redis安装包目录后（```cd /usr/local/redis-5.0.7```），执行命令安装```sudo make install```。

出现以下信息表示安装成功：

```
okevindeMacBook-Air:redis-5.0.7 okevin$ sudo make install
cd src && /Library/Developer/CommandLineTools/usr/bin/make install
    CC Makefile.dep
    INSTALL redis-sentinel
    CC redis-cli.o
    LINK redis-cli
    CC redis-benchmark.o
    LINK redis-benchmark
    INSTALL redis-check-rdb

Hint: It's a good idea to run 'make test' ;)

    INSTALL install
    INSTALL install
    INSTALL install
    INSTALL install
    INSTALL install
```

运行命令```redis-server```启动redis。

```
okevindeMacBook-Air:redis-5.0.7 okevin$ redis-server
10768:C 08 Feb 2020 19:52:40.149 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
10768:C 08 Feb 2020 19:52:40.149 # Redis version=5.0.7, bits=64, commit=00000000, modified=0, pid=10768, just started
10768:C 08 Feb 2020 19:52:40.149 # Warning: no config file specified, using the default config. In order to specify a config file use redis-server /path/to/redis.conf
10768:M 08 Feb 2020 19:52:40.151 * Increased maximum number of open files to 10032 (it was originally set to 256).
                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.7 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379
 |    `-._   `._    /     _.-'    |     PID: 10768
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                               

10768:M 08 Feb 2020 19:52:40.152 # Server initialized
10768:M 08 Feb 2020 19:52:40.152 * Ready to accept connections
```

出现以上信息表示启动成功。

新打开一个终端窗口，输入命令```redis-cli```测试连接redis服务。

```
okevindeMacBook-Air:redis-5.0.7 okevin$ redis-cli
127.0.0.1:6379> 
```

出现以上信息表示已经可以通过命令行和redis服务交互了。

