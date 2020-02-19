# 事务

我们在学习MySQL的存储殷勤时知道，MySQL中innodb支持事务而myisam不支持事务。而事务具有四个特性：

- 一致性
- 原子性
- 隔离性
- 持久性

在redis尽管提供了事务相关的命令，但实际上它是一个“假事务”，因为它并不支持回滚，也就是说在redis中一个事务有多个命令执行，并不能保证原子性。所以要使用redis的事务，一定要**慎重**。

## Redis中的“假事务”（不保证原子性）

在redis中事务相关的命令一共有以下几个：

```watch [key1] [key2]```：监视一个或多个key，在事务开始之前如果被监视的key有改动，则事务被打断。

```multi```：标记一个事务的开始。

```exec```：执行事务。

```discard```：取消事务的执行。

```unwatch```：取消监视的key。

- 正常执行事务

```
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name kevin
QUEUED
127.0.0.1:6379> set age 25
QUEUED
127.0.0.1:6379> get name
QUEUED
127.0.0.1:6379> set sex male
QUEUED
127.0.0.1:6379> exec
1) OK
2) OK
3) "kevin"
4) OK
```

- 取消事务执行

取消事务执行，命令将不会被执行。

```
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name yulinfeng
QUEUED
127.0.0.1:6379> set age 26
QUEUED
127.0.0.1:6379> discard
OK
127.0.0.1:6379> get name
"kevin"
```

- 事务中的命令出现**命令性错误**，类似Java的编译错误，执行事务时，所有的命令都不会被执行。

```
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name yulinfeng
QUEUED
127.0.0.1:6379> setget age 26
(error) ERR unknown command `setget`, with args beginning with: `age`, `26`, 
127.0.0.1:6379> exec
(error) EXECABORT Transaction discarded because of previous errors.
127.0.0.1:6379> get name
"kevin"
```

- 事务中出现**执行时错误**，类似Java的运行时异常，执行事务时，部分命令会被执行成功，也即是**不保证原子性**。

```
127.0.0.1:6379> multi
OK
127.0.0.1:6379> incr name
QUEUED
127.0.0.1:6379> set age 26
QUEUED
127.0.0.1:6379> exec
1) (error) ERR value is not an integer or out of range
2) OK
127.0.0.1:6379> get age
"26"
```

- 使用```watch```监视key在事务之前被改动，正常未被改动时的情况，所有命令正常执行。

```
127.0.0.1:6379> watch name
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name yulinfeng
QUEUED
127.0.0.1:6379> set age 18
QUEUED
127.0.0.1:6379> exec
1) OK
2) OK
127.0.0.1:6379> get age
"18"
```

- 使用```watch```监视key，此时在事务执行前key被改动，事务将取消不会执行所有命令。

我们现在一个redis客户端中执行watch命令。

```
127.0.0.1:6379> watch name
OK
```

此时我们打开另一个redis客户端，修改key=name的值。

```
127.0.0.1:6379> set name kevin
OK
```

我们再次回到第一个客户端，开始输入事务的命令块。

```
127.0.0.1:6379> multi
OK
127.0.0.1:6379> set name abc
QUEUED
127.0.0.1:6379> set age 1
QUEUED
127.0.0.1:6379> exec
(nil)
```

可看到通过```exec```执行事务时，事务并没有执行成功，而是返回“nil”。

Java中Jedis使用redis事务，则通过调用以下方法实现，具体命令可参照文档：

```java
@Test
public void testTransaction() {
    Jedis jedis = RedisClient.getJedis();
    jedis.watch("a", "c");
    Transaction transaction = jedis.multi();
    transaction.set("a", "b");
    transaction.set("c", "d");
    transaction.exec();
}
```

## 通过Lua脚本保证Redis的真事务

redis中自带的事务命令，最致命的前面已经多次提到，那就是**不保证原子性**，所以在使用redis的事务时，一定要谨慎。

但如果我们一定要在redis中实现真正的事务应该怎么办呢？redis为我们提供了另外一种更为“灵活”的方式——**Lua脚本**。

在这里当然并不会详细讲解Lua的语法规则，我们一步步来看在redis中如何执行Lua脚本，以及Lua是如何运用在redis保证事务的。

我们先用Lua脚本在redis中实现调用字符串的```set```命令，我们先看示例：

```
127.0.0.1:6379> eval "return redis.call('set', KEYS[1], ARGV[1])" 1 company bat
OK
127.0.0.1:6379> get company
"bat"
```

```eval```是执行Lua脚本的命令，第二个参数是Lua脚本，第三个参数是一个数字表示一共有多少个key，第四个参数表示key值，第五个参数表示value值，```eval [lua scripts] [numskey] [key1] [key2] [value1] [value2] ……```。

接下来，我们来一个Lua脚本，脚本中包含写入name的值和age的值。

```
127.0.0.1:6379> eval "redis.call('set', KEYS[1], ARGV[1]) redis.call('set', KEYS[2], ARGV[2])" 2 name age kevin 25
(nil)
127.0.0.1:6379> get name
"kevin"
127.0.0.1:6379> get age
"25"
```

对于简单的Lua脚本通过命令行的方式直接编辑问题不大，但如果是比较复杂得Lua脚本，通常我们会单独写一个Lua脚本文件，然后载入它，例如以下示例：

```lua
local exist = redis.call('exists', KEYS[1])

if exist then
    return redis.call('incr', KEYS[1])
else
    return nil
end
```

我们将它保存为Lua脚本文件，执行以下命令：

```
okevindeMacBook-Air:redis-5.0.7 okevin$ redis-cli --eval ~/Desktop/lua_test.lua view
(nil)
```

可以看到key=view并不存在，所以返回nil，如果此时我们在redis中定义了一个key=view的值，此时将返回以下信息：

```
okevindeMacBook-Air:redis-5.0.7 okevin$ redis-cli --eval ~/Desktop/lua_test.lua view
(integer) 2
```



## Jedis中如何载入Lua脚本

> 有关本节的源码：[https://github.com/yu-linfeng/redis5.x_tutorial/tree/master/code/jedis](https://github.com/yu-linfeng/redis5.x_tutorial/tree/master/code/jedis)

在Jedis可以直接调用```Jedis```类的```eval```方法，第一个参数是Lua脚本，第二个参数是key值，第三个参数是value值。

```java
public void testLua() {
    Jedis jedis = RedisClient.getJedis();
    List<String> keys = new ArrayList<>();
    keys.add("name");
    keys.add("age");
    List<String> values = new ArrayList<>();
    values.add("kevin");
    values.add("25");
    jedis.eval("redis.call('set', KEYS[1], ARGV[1]) redis.call('set', KEYS[2], ARGV[2])", keys, values);
    jedis.close();
}
```









