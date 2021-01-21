# ProtoBuf 3语法

## 定义ProtoBuf消息类型

要定义一个“搜索请求”的消息格式，每一个请求含有一个查询字符串、感兴趣的查询结果所在的页数，以及每一页多少条查询结果。可以采用如下的方式来定义消息类型的.proto文件

```protobuf
syntax = "proto3";
message SearchRequest {
	string query = 1;
	int32 page_number = 2;
	int32 result_per_page = 3;
}　
```

- 文件的第一行指定了正在使用proto3语法。如果没有指定这个，编译器会使用proto2。这个指定语法行必须是文件的非空非注释的第一个行。
- SearchRequest消息格式有3个字段，在消息中承载的数据分别对应于每一个字段。其中每个字段都有一个名字和一种类型。

### 指定字段类型

在上面的例子中，所有字段都是标量类型：

* 两个整型（page_number和result_per_page）
* 一个string类型（query）

当然，也可以为字段指定其他的合成类型，包括枚举（enumerations）或其他消息类型

### 分配标识号

在消息定义中，每个字段都有唯一的一个数字标识符。这些标识符是用来在消息的二进制格式中识别各个字段的，一旦开始使用就不能够再改变。

> * [1,15]之内的标识号在编码的时候会占用一个字节
> * [16,2047]之内的标识号则占用2个字节
> * 所以应该为那些频繁出现的消息元素保留 [1,15]之内的标识号
> * 切记：要为将来有可能添加的、频繁出现的标识号预留一些标识号

* 最小的标识号可以从1开始，最大到2^29 - 1, or 536,870,911
* 不可以使用其中的[19000－19999]的标识号， Protobuf协议实现中对这些进行了预留

### 指定字段规则

所指定的消息字段修饰符必须是如下之一：

- singular：一个格式良好的消息应该有0个或者1个这种字段（但是不能超过1个）。

- repeated：在一个格式良好的消息中，这种字段可以重复任意多次（包括0次）。重复的值的顺序会被保留。

	在proto3中，repeated的标量域默认情况下使用packed。

	可以了解更多的pakced属性在[Protocol Buffer 编码](https://developers.google.com/protocol-buffers/docs/encoding?hl=zh-cn#packed)

### 添加更多消息类型

在一个.proto文件中可以定义多个消息类型。在定义多个相关的消息的时候，这一点特别有用——例如，如果想定义与SearchResponse消息类型对应的回复消息格式的话，你可以将它添加到相同的.proto文件中，如：

```protobuf
message SearchRequest {
	string query = 1;
	int32 page_number = 2;
	int32 result_per_page = 3;
}
message SearchResponse {
	...
}
```

### 添加注释

向.proto文件添加注释，可以使用C/C++/java风格的双斜杠（//） 语法格式，如：

```protobuf
message SearchRequest {
	string query = 1;
	// Which page number do we want?
	int32 page_number = 2; 
	// Number of results to return per page.
	int32 result_per_page = 3; 
}
```

### 保留标识符（Reserved）

如果通过删除或者注释所有域，以后的用户可以重用标识号。当重新更新类型的时候，如果使用旧版本加载相同的.proto文件这会导致严重的问题，包括数据损坏、隐私错误等等。现在有一种确保不会发生这种情况的方法就是指定保留标识符，protocol buffer的编译器会警告未来尝试使用这些域标识符的用户。

```protobuf
message Foo {
	reserved 2, 15, 9 to 11;
	reserved "foo", "bar";
}
```



> 不要在同一行reserved声明中同时声明域名字和标识号

## 从.proto文件生成了什么？

当用protocol buffer编译器来运行.proto文件时，编译器将生成所选择语言的代码，这些代码可以操作在.proto文件中定义的消息类型，包括获取、设置字段值，将消息序列化到一个输出流中，以及从一个输入流中解析消息。对Java来说，编译器为每一个消息类型生成了一个.java文件，以及一个特殊的Builder类（该类是用来创建消息类接口的）。

## 标量数值类型

一个标量消息字段可以含有一个如下的类型——该表格展示了定义于.proto文件中的类型，以及与之对应的、在自动生成的访问类中定义的类型：

| .proto Type | Notes                                                        | Java Type  |
| ----------- | ------------------------------------------------------------ | ---------- |
| double      | 双精度浮点型                                                 | double     |
| float       | 单精度浮点型                                                 | float      |
| int32       | 使用变长编码，对于负值的效率很低，如果域有可能有负值，请使用sint64替代 | int        |
| uint32      | 使用变长编码                                                 | int        |
| uint64      | 使用变长编码                                                 | long       |
| sint32      | 使用变长编码，这些编码在负值时比int32高效的多                | int        |
| sint64      | 使用变长编码，有符号的整型值。编码时比通常的int64高效。      | long       |
| fixed32     | 总是4个字节，如果数值总是比总是比228大的话，这个类型会比uint32高效。 | int        |
| fixed64     | 总是8个字节，如果数值总是比总是比256大的话，这个类型会比uint64高效。 | long       |
| sfixed32    | 总是4个字节                                                  | int        |
| sfixed64    | 总是8个字节                                                  | long       |
| bool        | 布尔类型                                                     | boolean    |
| string      | 一个字符串必须是UTF-8编码或者7-bit ASCII编码的文本。         | String     |
| bytes       | 可能包含任意顺序的字节数据。                                 | ByteString |

1. 在java中，无符号32位和64位整型被表示成他们的整型对应形似，最高位被储存在标志位中。
2. 对于所有的情况，设定值会执行类型检查以确保此值是有效。
3. 64位或者无符号32位整型在解码时被表示成为ilong，但是在设置时可以使用int型值设定，在所有的情况下，值必须符合其设置其类型的要求。
5. Integer在64位的机器上使用，string在32位机器上使用

## 默认值

当一个消息被解析的时候，如果被编码的信息不包含一个特定的singular元素，被解析的对象锁对应的域被设置位一个默认值，对于不同类型指定如下：

- 对于strings，默认是一个空string

- 对于bytes，默认是一个空的bytes

- 对于bools，默认是false

- 对于数值类型，默认是0

- 对于枚举，默认是第一个定义的枚举值，必须为0;

- 对于消息类型（message），域没有被设置，确切的消息是根据语言确定的

- 对于可重复域的默认值是空（通常情况下是对应语言中空列表）

	

> 对于标量消息域，一旦消息被解析，就无法判断域释放被设置为默认值（例如，例如boolean值是否被设置为false）还是根本没有被设置。在定义消息类型时非常注意。例如，比如不应该定义boolean的默认值false作为任何行为的触发方式。也应该注意如果一个标量消息域被设置为标志位，这个值不应该被序列化传输

## 枚举

当需要定义一个消息类型的时候，可能想为一个字段指定某“预定义值序列”中的一个值。例如，假设要为每一个SearchRequest消息添加一个 corpus字段，而corpus的值可能是UNIVERSAL，WEB，IMAGES，LOCAL，NEWS，PRODUCTS或VIDEO中的一个。 其实可以很容易地实现这一点：通过向消息定义中添加一个枚举（enum）并且为每个可能的值定义一个常量就可以了。

在下面的例子中，在消息格式中添加了一个叫做Corpus的枚举类型——它含有所有可能的值 ——以及一个类型为Corpus的字段：

```protobuf
message SearchRequest {
	string query = 1;
	int32 page_number = 2;
	int32 result_per_page = 3;
	enum Corpus {
		UNIVERSAL = 0;
		WEB = 1;
		IMAGES = 2;
		LOCAL = 3;
		NEWS = 4;
		PRODUCTS = 5;
		VIDEO = 6;
     }
     Corpus corpus = 4;
}
```

如你所见，Corpus枚举的第一个常量映射为0：每个枚举类型必须将其第一个类型映射为0，这是因为：

- 必须有有一个0值，我们可以用这个0值作为默认值。

- 这个零值必须为第一个元素，为了兼容proto2语义，枚举类的第一个值总是默认值。

- 可以通过将不同的枚举常量指定位相同的值。如果这样做需要将allow_alias设定位true，否则编译器会在别名的地方产生一个错误信息。
- 枚举常量必须在32位整型值的范围内。因为enum值是使用可变编码方式的，对负数不够高效，因此不推荐在enum中使用负数。如上例所示，可以在 一个消息定义的内部或外部定义枚举——这些枚举可以在.proto文件中的任何消息定义里重用。当然也可以在一个消息中声明一个枚举类型，而在另一个不同 的消息中使用它——采用MessageType.EnumType的语法格式
- 当对一个使用了枚举的.proto文件运行protocol buffer编译器的时候，生成的代码中将有一个对应的enum（对Java或C++来说），或者一个特殊的EnumDescriptor类（对 Python来说），它被用来在运行时生成的类中创建一系列的整型值符号常量（symbolic constants）。
- 在反序列化的过程中，无法识别的枚举值会被保存在消息中，虽然这种表示方式需要依据所使用语言而定。在那些支持开放枚举类型超出指定范围之外的语言中（例如C++和Go），为识别的值会被表示成所支持的整型。在使用封闭枚举类型的语言中（Java），使用枚举中的一个类型来表示未识别的值，并且可以使用所支持整型来访问。在其他情况下，如果解析的消息被序列号，未识别的值将保持原样。


## 使用其他消息类型

可以将其他消息类型用作字段类型。例如，假设在每一个SearchResponse消息中包含Result消息，此时可以在相同的.proto文件中定义一个Result消息类型，然后在SearchResponse消息中指定一个Result类型的字段，如：

### 嵌套类型

可以在其他消息类型中定义、使用消息类型，在下面的例子中，Result消息就定义在SearchResponse消息内，如：

如果想在它的父消息类型的外部重用这个消息类型，你需要以Parent.Type的形式使用它，如：

```protobuf
message SomeOtherMessage {
	SearchResponse.Result result = 1;
}
```

### 更新一个消息类型

如果一个已有的消息格式已无法满足新的需求——如，要在消息中添加一个额外的字段——但是同时旧版本写的代码仍然可用。不用担心！更新消息而不破坏已有代码是非常简单的。在更新时只要记住以下的规则即可。

- 不要更改任何已有的字段的数值标识
- 如果增加新的字段，使用旧格式的字段仍然可以被新产生的代码所解析。应该记住这些元素的默认值这样新代码就可以以适当的方式和旧代码产生的数据交互。相似的，通过新代码产生的消息也可以被旧代码解析：只不过新的字段会被忽视掉。注意，未被识别的字段会在反序列化的过程中丢弃掉，所以如果消息再被传递给新的代码，新的字段依然是不可用的（这和proto2中的行为是不同的，在proto2中未定义的域依然会随着消息被序列化）
- 非required的字段可以移除——只要它们的标识号在新的消息类型中不再使用（更好的做法可能是重命名那个字段，例如在字段前添加“OBSOLETE_”前缀，那样的话，使用的.proto文件的用户将来就不会无意中重新使用了那些不该使用的标识号）。
- int32, uint32, int64, uint64,和bool是全部兼容的，这意味着可以将这些类型中的一个转换为另外一个，而不会破坏向前、 向后的兼容性。如果解析出来的数字与对应的类型不相符，那么结果就像在C++中对它进行了强制类型转换一样（例如，如果把一个64位数字当作int32来 读取，那么它就会被截断为32位的数字）。
- sint32和sint64是互相兼容的，但是它们与其他整数类型不兼容。
- string和bytes是兼容的——只要bytes是有效的UTF-8编码。
- 嵌套消息与bytes是兼容的——只要bytes包含该消息的一个编码过的版本。
- fixed32与sfixed32是兼容的，fixed64与sfixed64是兼容的。
- 枚举类型与int32，uint32，int64和uint64相兼容（注意如果值不相兼容则会被截断），然而在客户端反序列化之后他们可能会有不同的处理方式，例如，未识别的proto3枚举类型会被保留在消息中，但是他的表示方式会依照语言而定。int类型的字段总会保留他们的

## Oneof

如果消息中有很多可选字段， 并且同时至多一个字段会被设置， 可以加强这个行为，使用oneof特性节省内存.

Oneof字段就像可选字段， 除了它们会共享内存， 至多一个字段会被设置。 设置其中一个字段会清除其它字段。 你可以使用`case()`或者`WhichOneof()` 方法检查哪个oneof字段被设置.

### 使用Oneof

为了在.proto定义Oneof字段， 需要在名字前面加上oneof关键字, 比如下面例子的test_oneof:

```
message SampleMessage {
	oneof test_oneof {
		string name = 4;
		SubMessage sub_message = 9;
	}
}
```

然后可以增加oneof字段到 oneof 定义中. 可以增加任意类型的字段, 但是不能使用repeated 关键字.

在产生的代码中, oneof字段拥有同样的 getters 和setters， 就像正常的可选字段一样. 也有一个特殊的方法来检查到底那个字段被设置. 

### Oneof 特性

- 设置oneof会自动清楚其它oneof字段的值. 所以设置多次后，只有最后一次设置的字段有值.

```java
SampleMessage message;
message.set_name("name");
CHECK(message.has_name());
message.mutable_sub_message();  // Will clear name field.``CHECK(!message.has_name());`
```

- 如果解析器遇到同一个oneof中有多个成员，只有最会一个会被解析成消息。
- oneof不支持`repeated`.
- 反射API对oneof 字段有效.

### 向后兼容性问题

当增加或者删除oneof字段时一定要小心. 如果检查oneof的值返回`None/NOT_SET`, 它意味着oneof字段没有被赋值或者在一个不同的版本中赋值了。 不会知道是哪种情况，因为没有办法判断如果未识别的字段是一个oneof字段。

Tage 重用问题：

- 将字段移入或移除oneof：在消息被序列号或者解析后，你也许会失去一些信息（有些字段也许会被清除）
- 删除一个字段或者加入一个字段：在消息被序列号或者解析后，这也许会清除你现在设置的oneof字段
- 分离或者融合oneof：行为与移动常规字段相似。

## Map（映射）

如果希望创建一个关联映射，protocol buffer提供了一种快捷的语法：

```
map<key_type, value_type> map_field = N;
```

其中`key_type`可以是任意Integer或者string类型（所以，除了floating和bytes的任意标量类型都是可以的）`value_type`可以是任意类型。

例如，如果希望创建一个project的映射，每个`Projecct`使用一个string作为key，你可以像下面这样定义：

```
map<string, Project> projects = 3;
```

- Map的字段可以是repeated。
- 序列化后的顺序和map迭代器的顺序是不确定的，所以你不要期望以固定顺序处理Map
- 当为.proto文件产生生成文本格式的时候，map会按照key 的顺序排序，数值化的key会按照数值排序。
- 从序列化中解析或者融合时，如果有重复的key则后一个key不会被使用，当从文本格式中解析map时，如果存在重复的key。

生成map的API现在对于所有proto3支持的语言都可用了，你可以从[API指南](https://developers.google.com/protocol-buffers/docs/reference/overview?hl=zh-cn)找到更多信息。

### 向后兼容性问题

map语法序列化后等同于如下内容，因此即使是不支持map语法的protocol buffer实现也是可以处理你的数据的：

```protobuf
message MapFieldEntry {
  key_type key = 1;
  value_type value = 2;
}

repeated MapFieldEntry map_field = N;
```

## 包

当然可以为.proto文件新增一个可选的package声明符，用来防止不同的消息类型有命名冲突。如：

```protobuf
packag foo.bar;
message Open { ... }
```

在其他的消息格式定义中可以使用包名+消息名的方式来定义域的类型，如：

```protobuf
message Foo {
	...
	required foo.bar.Open open = 1;
	...}
```

包的声明符会根据使用语言的不同影响生成的代码。

### 包及名称的解析

Protocol buffer语言中类型名称的解析与C++是一致的：首先从最内部开始查找，依次向外进行，每个包会被看作是其父类包的内部类。当然对于 （`foo.bar.Baz`）这样以“.”分隔的意味着是从最外围开始的。

ProtocolBuffer编译器会解析.proto文件中定义的所有类型名。 对于不同语言的代码生成器会知道如何来指向每个具体的类型，即使它们使用了不同的规则。

## 定义服务(Service)

如果想要将消息类型用在RPC(远程方法调用)系统中，可以在.proto文件中定义一个RPC服务接口，protocol buffer编译器将会根据所选择的不同语言生成服务接口代码及存根。如，想要定义一个RPC服务并具有一个方法，该方法能够接收 SearchRequest并返回一个SearchResponse，此时可以在.proto文件中进行如下定义：

```protobuf
service SearchService {
	rpc Search (SearchRequest) returns (SearchResponse);
}
```

最直观的使用protocol buffer的RPC系统是[gRPC](https://github.com/grpc/grpc-experiments)一个由谷歌开发的语言和平台中的开源的PRC系统，gRPC在使用protocl buffer时非常有效，如果使用特殊的protocol buffer插件可以直接为您从.proto文件中产生相关的RPC代码。

如果你不想使用gRPC，也可以使用protocol buffer用于自己的RPC实现，你可以从[proto2语言指南中找到更多信息](https://developers.google.com/protocol-buffers/docs/proto?hl=zh-cn#services)

还有一些第三方开发的PRC实现使用Protocol Buffer。参考[第三方插件wiki](https://github.com/google/protobuf/blob/master/docs/third_party.md)查看这些实现的列表。

## JSON 映射

Proto3 支持JSON的编码规范，使他更容易在不同系统之间共享数据，在下表中逐个描述类型。

如果JSON编码的数据丢失或者其本身就是`null`，这个数据会在解析成protocol buffer的时候被表示成默认值。如果一个字段在protocol buffer中表示为默认值，体会在转化成JSON的时候编码的时候忽略掉以节省空间。具体实现可以提供在JSON编码中可选的默认值。

| proto3                 | JSON          | JSON示例                                | 注意                                                         |
| ---------------------- | ------------- | --------------------------------------- | ------------------------------------------------------------ |
| message                | object        | {“fBar”: v, “g”: null, …}               | 产生JSON对象，消息字段名可以被映射成lowerCamelCase形式，并且成为JSON对象键，null被接受并成为对应字段的默认值 |
| enum                   | string        | “FOO_BAR”                               | 枚举值的名字在proto文件中被指定                              |
| map                    | object        | {“k”: v, …}                             | 所有的键都被转换成string                                     |
| repeated V             | array         | [v, …]                                  | null被视为空列表                                             |
| bool                   | true, false   | true, false                             |                                                              |
| string                 | string        | “Hello World!”                          |                                                              |
| bytes                  | base64 string | “YWJjMTIzIT8kKiYoKSctPUB+”              |                                                              |
| int32, fixed32, uint32 | number        | 1, -10, 0                               | JSON值会是一个十进制数，数值型或者string类型都会接受         |
| int64, fixed64, uint64 | string        | “1”, “-10”                              | JSON值会是一个十进制数，数值型或者string类型都会接受         |
| float, double          | number        | 1.1, -10.0, 0, “NaN”, “Infinity”        | JSON值会是一个数字或者一个指定的字符串如”NaN”,”infinity”或者”-Infinity”，数值型或者字符串都是可接受的，指数符号也可以接受 |
| Any                    | object        | {“@type”: “url”, “f”: v, … }            | 如果一个Any保留一个特上述的JSON映射，则它会转换成一个如下形式：`{"@type": xxx, "value": yyy}`否则，该值会被转换成一个JSON对象，`@type`字段会被插入所指定的确定的值 |
| Timestamp              | string        | “1972-01-01T10:00:20.021Z”              | 使用RFC 339，其中生成的输出将始终是Z-归一化啊的，并且使用0，3，6或者9位小数 |
| Duration               | string        | “1.000340012s”, “1s”                    | 生成的输出总是0，3，6或者9位小数，具体依赖于所需要的精度，接受所有可以转换为纳秒级的精度 |
| Struct                 | object        | { … }                                   | 任意的JSON对象，见struct.proto                               |
| Wrapper types          | various types | 2, “2”, “foo”, true, “true”, null, 0, … | 包装器在JSON中的表示方式类似于基本类型，但是允许nulll，并且在转换的过程中保留null |
| FieldMask              | string        | “f.fooBar,h”                            | 见fieldmask.proto                                            |
| ListValue              | array         | [foo, bar, …]                           |                                                              |
| Value                  | value         |                                         | 任意JSON值                                                   |
| NullValue              | null          |                                         | JSON null                                                    |

## 选项

在定义.proto文件时能够标注一系列的options。Options并不改变整个文件声明的含义，但却能够影响特定环境下处理方式。完整的可用选项可以在google/protobuf/descriptor.proto找到。

一些选项是文件级别的，意味着它可以作用于最外范围，不包含在任何消息内部、enum或服务定义中。一些选项是消息级别的，意味着它可以用在消息定义的内部。当然有些选项可以作用在域、enum类型、enum值、服务类型及服务方法中。到目前为止，并没有一种有效的选项能作用于所有的类型。

如下就是一些常用的选择：

1、`java_package` (文件选项) :这个选项表明生成java类所在的包。如果在.proto文件中没有明确的声明java_package，就采用默认的包名。当然了，默认方式产生的 java包名并不是最好的方式，按照应用名称倒序方式进行排序的。如果不需要产生java代码，则该选项将不起任何作用。如：

```java
option java_package = "com.example.foo";
```

2、`java_outer_classname` (文件选项): 该选项表明想要生成Java类的名称。如果在.proto文件中没有明确的java_outer_classname定义，生成的class名称将会根据.proto文件的名称采用驼峰式的命名方式进行生成。如（foo_bar.proto生成的java类名为FooBar.java）,如果不生成java代码，则该选项不起任何作用。如：

```protobuf
option java_outer_classname = "Ponycopter";
```

3、`optimize_for`(文件选项): 可以被设置为 SPEED, CODE_SIZE,或者LITE_RUNTIME。这些值将通过如下的方式影响C++及java代码的生成： 

* `SPEED (default)`: protocol buffer编译器将通过在消息类型上执行序列化、语法分析及其他通用的操作。这种代码是最优的。
* `CODE_SIZE`: protocol buffer编译器将会产生最少量的类，通过共享或基于反射的代码来实现序列化、语法分析及各种其它操作。采用该方式产生的代码将比SPEED要少得多， 但是操作要相对慢些。当然实现的类及其对外的API与SPEED模式都是一样的。这种方式经常用在一些包含大量的.proto文件而且并不盲目追求速度的 应用中。
* `LITE_RUNTIME`: protocol buffer编译器依赖于运行时核心类库来生成代码（即采用libprotobuf-lite 替代libprotobuf）。这种核心类库由于忽略了一 些描述符及反射，要比全类库小得多。这种模式经常在移动手机平台应用多一些。编译器采用该模式产生的方法实现与SPEED模式不相上下，产生的类通过实现 MessageLite接口，但它仅仅是Messager接口的一个子集。

```protobuf
option optimize_for = CODE_SIZE;
```

- `deprecated`(字段选项):如果设置为`true`则表示该字段已经被废弃，并且不应该在新的代码中使用。在大多数语言中没有实际的意义。在java中，这回变成`@Deprecated`注释，在未来，其他语言的代码生成器也许会在字标识符中产生废弃注释，废弃注释会在编译器尝试使用该字段时发出警告。如果字段没有被使用你也不希望有新用户使用它，尝试使用保留语句替换字段声明。

```
int32 old_field = 6 [deprecated=true];
```

### 自定义选项

ProtocolBuffers允许自定义并使用选项。该功能应该属于一个高级特性，对于大部分人是用不到的。如果你的确希望创建自己的选项，请参看[ Proto2 Language Guide](https://developers.google.com/protocol-buffers/docs/proto?hl=zh-cn#customoptions)。注意创建自定义选项使用了拓展，拓展只在proto3中可用。