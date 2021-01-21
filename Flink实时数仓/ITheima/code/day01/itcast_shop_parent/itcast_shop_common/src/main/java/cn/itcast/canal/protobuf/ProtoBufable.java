package cn.itcast.canal.protobuf;

/**
 * 定义protobuf序列化接口
 * 这个接口定义的是返回的byte[]二进制字节码对象
 * 所有的能够使用protobuf序列化的bean都需要集成该接口
 */
public interface ProtoBufable {
    /**
     * 将对象转换成二进制数组
     * @return
     */
    byte[] toBytes();
}
