# Thinking

## 数据结构

Redis5.x共有6种数据结构：

- String

```SET key value```设置key-value键值对数据

```GET key```获取key的value数据

- Hash

```HMSET key field1 value```

```HGET key field```

- List

```lpush key value```

- Set

```sadd key value```

- Sorted Set

```zadd key score value```

- Stream

```xadd key value```



## 发布/订阅

### 订阅(subscribe)

```SUBSCRIBE channal```

### 发布(publish)

```PUBLISH channal "message"```



## 事务

事务可一次执行多条命令。Redis中的事务并不包含**原子性**，中间命令失败并不会导致前面的命令回滚，也不会导致后面的命令不执行。它更像是把几个命令打包执行。

```
MULTI
...#命令
EXEC
```



## 脚本

_lua_脚本学习



## 数据备份恢复

