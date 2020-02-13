# 命令

按照**第一章 准备工作**中的步骤，我们通过```redis-cli```命令进入redis命令行交互。

本章节将围绕上一章节的数据类型，针对不同的数据类型操作不同的Redis命令。

## 字符串（string）

### 读/写/删简单命令

写命令通过```set```关键字实现，```set [key] [value]```。

```
127.0.0.1:6379> set hello world
OK
```

读命令通过```get```关键字实现，```get [key]```。

```
127.0.0.1:6379> get hello
"world"
```

删除命令通过```del```关键字实现（删除命令可以适用于所有的数据类型），```del [key]```。

```
127.0.0.1:6379> del hello
(integer) 1
```

字符串数据类型还有一个```mset```表示同时写入一个或多个字符串值，```mset [key1] [value1] [key2] [value2]```。

```
127.0.0.1:6379> mset key1 value1 key2 value2
OK
```

### 自增/自减命令

自增+1命令通过```incr```关键字实现，```incr [key]```。

```
127.0.0.1:6379> set hello 1				#写入key为hello的值为1
OK
127.0.0.1:6379> get hello					#读取key为hello的值
"1"
127.0.0.1:6379> incr hello				#将key为hello的值自增+1
(integer) 2
127.0.0.1:6379> get hello					#读取key为hello的值
"2"
```

自减-1命令通过```decr```关键字实现，```decr [key]```。

```
127.0.0.1:6379> set world 1				#写入key为world的值为1
OK
127.0.0.1:6379> get world					#读取key为world的值
"1"
127.0.0.1:6379> decr world				#将key为world的值自减-1
(integer) 0
127.0.0.1:6379> get world					#读取key为world的值
"0"
```

自增**任意整数**通过```incrby```实现，```incrby [key] [number]```。

```
127.0.0.1:6379> set coderbuff 1		#写入key为coderbuff的值为1
OK
127.0.0.1:6379> get coderbuff			#读取key为coderbuff的值
"1"
127.0.0.1:6379> incrby coderbuff 10	#将key为coderbuff的值自增+10
(integer) 11
127.0.0.1:6379> get coderbuff			#读取key为coderbuff的值
"11"
```

自减**任意整数**通过```decrby```实现，```decrby [key] [number]```。

```
127.0.0.1:6379> set coderbook 1		#写入key为coderbook的值为1
OK
127.0.0.1:6379> get coderbook			#读取key为coderbook的值
"1"
127.0.0.1:6379> decrby coderbook 10		#将key为coderbook的值自增-10
(integer) -9
127.0.0.1:6379> get coderbook			#读取key为coderbook的值
"-9"
```

自增**任意浮点数**通过```incrbyfloat```，```incrbyfloat [key] [float]```。

```
127.0.0.1:6379> set coderchat 1		#写入key为coderchat的值为1
OK
127.0.0.1:6379> get coderchat			#读取key为coderchat的值
"1"
127.0.0.1:6379> incrbyfloat coderchat 1.1		#将key为coderchat的值自增+1.1
"2.1"
127.0.0.1:6379> get coderchat			#读取key为coderchat的值
"2.1"
```

### 字符串操作命令

redis中对字符串类型的数据类型不仅可以使用上述命令，它甚至还能像Java一样进行值得追加、获取子串等。

**追加value值给指定key到末尾**通过```append```，```apppend [key] [append_string]```。

```
127.0.0.1:6379> set say hello		#写入key为say的值为hello
OK
127.0.0.1:6379> get say					#读取key为say的值
"hello"
127.0.0.1:6379> append say world		#在key为say的value值末尾追加world
(integer) 10										#返回key当前value值得长度
127.0.0.1:6379> get say					#读取key为say的值
"helloworld"
```

接着这个示例，通过命令```getrange```**获取子字符串**，```getrange [key] [start] [end]```。

```
127.0.0.1:6379> getrange say 1 10		#获取位于位置1到10的子字符串
"elloworld"
127.0.0.1:6379> get say							#可以看到，尽管我们上面获取了子字符串，但并未修改原字符串
"helloworld"
```

同样是这个示例，我们通过命令```setrange```**替换子字符串为给定值**，我们会给出两个关键参数，第一个参数是[start]表示从哪里开始替换，第二个参数是[value]表示替换的内容，```setrange [key] [start] [value]```。

```
127.0.0.1:6379> setrange say 1 i
(integer) 10
127.0.0.1:6379> get say
"hilloworld"
```

### 二进制位命令

任何数据在操作系统中都是以**二进制位**形式存储的，字符串类型中redis提供了对其进行二进制位操作。通常情况下运用可能不多，但可以通过它实现一些“巧妙”的设计。

**例如，在钉钉消息中，我们发送一条消息会显示“已读”和“未读”的人，我们需要将这两个信息存储在redis中，应该怎么设计？**

我们设计一条消息的key值结构为“[user_id]:[msg_id]”，所以key=“1:100”就表示“用户ID为1发送的消息ID为100”。**注意**，此时如果用户ID=2的人读了这条消息，就通过命令```setbit 1:100 2 1```写入，如果用户ID=100的人读了这条消息，就通过```setbit 1:100 10 1```。这条命令的含义表示对key=1:100的二进制第2位写入1，对key=1:100的二进制第10位写入1，1表示已读，0则表示未读。

```
127.0.0.1:6379> set 1:100 0					#定义key=1:100，表示用户ID为1发出的消息ID为100的消息
OK
127.0.0.1:6379> setbit 1:100 0 0		#初始化1:100的第0位到第10位的二进制为0，表示刚发出来都是未读。
(integer) 0
127.0.0.1:6379> setbit 1:100 1 0
(integer) 0
127.0.0.1:6379> setbit 1:100 2 0
(integer) 1
127.0.0.1:6379> setbit 1:100 3 0
(integer) 1
127.0.0.1:6379> setbit 1:100 4 0
(integer) 0
127.0.0.1:6379> setbit 1:100 5 0
(integer) 0
127.0.0.1:6379> setbit 1:100 6 0
(integer) 0
127.0.0.1:6379> setbit 1:100 7 0
(integer) 0
127.0.0.1:6379> setbit 1:100 8 0
(integer) 0
127.0.0.1:6379> setbit 1:100 9 0
(integer) 0
127.0.0.1:6379> setbit 1:100 10 0
```

上面我们就初始化好了一个bitmap（位图）。接下来，当用户ID=2和用户ID=10读了这条消息。

```
127.0.0.1:6379> setbit 1:100 1 1		#实际上，发出消息的同时，自己也已读了
(integer) 0
127.0.0.1:6379> setbit 1:100 2 1		#用户ID=2读了这条消息
(integer) 0
127.0.0.1:6379> setbit 1:100 10 1		#用户ID=10读了这条消息
(integer) 0
```

我们通过```getbit```命令可以判断出哪些用户是否已读这条消息，例如，我们判断用户ID=3是否已读这条消息。

```
127.0.0.1:6379> getbit 1:100 3		#读取key为1:100的二进制位第3位二进制值
(integer) 0												#返回0，表示未读
```

判断用户ID=10是否已读这条消息。

```
127.0.0.1:6379> getbit 1:100 10		#读取key为1:100的二进制位第10位二进制值
(integer) 1												#返回1，表示已读
```

我们还可以通过```bitcount```统计值为1的数量，也就是有多少人已读这条消息。

```
127.0.0.1:6379> bitcount 1:100		#统计二进制位为1的数量
(integer) 3												#只有3个，和我们上面的假定一致，用户自己（用户ID=1）和用户ID=2、用户ID=10的用户读了这条消息
```

最后还有一个关于二进制位的命令```bittop [operation] [result] [key1] [key2]```，可以对多个key值的二进制位进行二进制运算，包括```并AND```、```或OR```、```异或XOR```、```非NOT```，计算结果保存在[result]中。

## 列表（list）

### 推入/弹出常用命令

通过```rpush```、```lpush```，将一个或多个值向右或向左推入。

```rpush [key] [value1] [value2]```，将value值推入到列表的**右端**。

```lpush [key] [value1] [value2]```，将value值推入到列表的**左端**。

```
127.0.0.1:6379> rpush books java		#向列表key值为books的右侧推入value值java
(integer) 1
127.0.0.1:6379> lpush books c				#向列表key值为books的左侧推入value值c
(integer) 2
127.0.0.1:6379> rpush books python	#向列表key值为books的右侧推入value值python
(integer) 3
127.0.0.1:6379> lrange books 0 -1		#查看列表key值为books的值
1) "c"
2) "java"
3) "python"
```

接着上面的示例，通过```rpop```、```lpop```，移除并返回列表中最后端、最左端的元素。

```rpop [key]```，移除列表最后端的元素。

```lpop [key]```，移除列表最左端的元素。

```
127.0.0.1:6379> rpop books					#移除列表key值为books最右端的元素并返回
"python"
127.0.0.1:6379> lrange books 0 -1		#查看列表key为books的所有元素
1) "c"
2) "java"
127.0.0.1:6379> lpop books					#移除列表key值为books最左端的元素并返回
"c"
127.0.0.1:6379> lrange books 0 -1		#查看列表key为books的所有元素
1) "java"
```

在介绍完推入和弹出命令后，接下来将介绍与**列表范围**查看的命令。

```lrange [key] [start] [end]```命令用于返回列表从[start]到[end]位置范围内的所有元素，注意，位于[start]、[end]的元素也会被返回，上面的例子已经出现过该命令。

```
127.0.0.1:6379> rpush level A B C D			#对列表key为level的右侧连续推入value：A B C D
(integer) 4
127.0.0.1:6379> lrange level 0 1				#返回key为level的位置0~1的元素
1) "A"
2) "B"
127.0.0.1:6379> lrange level 0 -1				#查看列表key为level的所有元素
1) "A"
2) "B"
2) "C"
2) "D"
```

```lindex [key] [index]```命令用于返回指定位置[index]的元素，仍然使用上述示例。

```
127.0.0.1:6379> lindex level 3
"D"
```

```lrange```和```lindex```均不会修改原本的列表值，但```ltrim```则用于“修建”列表值。

```ltrim [key] [start] [end]```表示只保留列表从[start]到[end]范围的所有元素，注意，包含位于[start]、[end]的元素，同样采用上述示例。

```
127.0.0.1:6379> lrange level 0 -1		#查看列表key为level的所有元素
1) "A"
2) "B"
2) "C"
2) "D"
127.0.0.1:6379> ltrim level 1 2			#保留列表位置为1~2的元素
OK
127.0.0.1:6379> lrange level 0 -1		#查看列表key为level的所有元素
1) "B"
2) "C"
```

列表基本的命令就是上面这些，还有一些比较“高级”的命令：将元素从一个队列移动到另外一个队列，或者阻塞式的执行弹出命令知道有新元素新加入到列表中。在这里就不再介绍，后面章节在介绍到redis的一些应用时再讲。

## 哈希（hash）

```hmset```命令可写入hash类型的值，```hmset [key] [field1] [value1] [field2] [value2]```。

```
127.0.0.1:6379> hmset okevin name kevin sex male
OK
```

```hmget```命令可读取hash类型的值，```hmget [key] [field]```。

```
127.0.0.1:6379> hmget okevin name
1) "kevin"
```

```hlen```返回hash包含的键值对数量，```hlen [key]```。

```
127.0.0.1:6379> hlen okevin
(integer) 2
```

```hmgetall```返回hash包含的所有键值对，```hmgetall [key]```。

```
127.0.0.1:6379> hgetall okevin		#返回所有的键值对，奇数列为field，偶数列为value
1) "name"
2) "kevin"
3) "sex"
4) "male"
```

```hexists```命令检查给定的field是否存在hash值中，返回0表示不存在，返回1表示存在，```hexists [key] [field]```。

```
127.0.0.1:6379> hexists okevin name
(integer) 1
127.0.0.1:6379> hexists okevin age
(integer) 0
```

```hkeys```获取hash包含的所有field键，```hkeys [key]```。

```
127.0.0.1:6379> hkeys okevin
1) "name"
2) "sex"
```

```hvals```获取hash包含的所有field对应的value值，```hvals [key]```。

```
127.0.0.1:6379> hvals okevin
1) "kevin"
2) "male"
```

```hincrby```给hash中指定的field键自增任意整数（和字符串类型的```incrby```类似），```hincrby [key] [field] [number]```。

```
127.0.0.1:6379> hmset okevin age 0				#新增“年龄”field值为0
OK
127.0.0.1:6379> hmget okevin age					#获取“年龄”
1) "0"
127.0.0.1:6379> hincrby okevin age 25			#给“年龄”field自增“25”整数
(integer) 25
127.0.0.1:6379> hmget okevin age					#获取“年龄”
1) "25"
```

```hincrbyfloat```给hash中指定的filed键自增任意浮点数（和字符串类型的```incrbyfloat```类似），```hincrbyfloat [key] [field] [number]```。

```
127.0.0.1:6379> hincrbyfloat okevin age 0.5
"25.5"
127.0.0.1:6379> hmget okevin age
1) "25.5"
```

```hdel```删除hash中指定的filed，```hmdel [key] [field]```。

```
127.0.0.1:6379> hgetall okevin
1) "name"
2) "kevin"
3) "sex"
4) "male"
5) "age"
6) "25.5"
127.0.0.1:6379> hdel okevin age
(integer) 1
127.0.0.1:6379> hgetall okevin
1) "name"
2) "kevin"
3) "sex"
4) "male"
```



## 集合（set）

在上一章节讲到，集合（set）是以无序方式存储各不相同元素的数据类型。它和Java中的Set类型类似。它同样具有新增、删除、读取等基本操作，还有两个集合之间运算的操作。

### 读/写等常用命令

```sadd```命令将一个或多个元素添加到集合里，并返回被添加元素中原本并不存在集合中的元素数量，```sadd [key] [member] [member]```。

```
127.0.0.1:6379> sadd students kevin yulinfeng
(integer) 2
```

```sismember```命令检查元素是否存在集合中，返回1表示存在，返回0表示不存在，```sismember [key] [member]```。

```
127.0.0.1:6379> sismember students kevin
(integer) 1
127.0.0.1:6379> sismember students linfeng
(integer) 0
```

```smembers```命令返回集合中包含的所有元素，```smembers [key]```。

```
127.0.0.1:6379> smembers students
1) "yulinfeng"
2) "kevin"
```

```scard```命令返回集合中元素的数量，```scards [key]```。

```
127.0.0.1:6379> scard students
(integer) 2
```

```srandmember```命令从集合中随机返回一个或多个元素，当返回的元素数量设置为正数时，元素不会重复，当返回的元素数量设置为负数时，元素可能会重复，```srandmember [key] [count]```。

```
127.0.0.1:6379> srandmember students 1
1) "yulinfeng"
127.0.0.1:6379> srandmember students 1
1) "kevin"
127.0.0.1:6379> srandmember students 3
1) "kevin"
2) "yulinfeng"
127.0.0.1:6379> srandmember students -3
1) "yulinfeng"
2) "kevin"
3) "kevin"
```

```srem```命令从集合中移除一个或多个指定的元素，并返回被移除的元素数量，```srem [key] [member] [member]```。

### 多个集合命令





