package org.jhk.pulsing.hadoop.config;

import java.util.Comparator;

import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;
import org.jhk.pulsing.hadoop.config.serializers.EnumDeserializer;
import org.jhk.pulsing.hadoop.config.serializers.EnumSerializer;
import org.jhk.pulsing.hadoop.config.serializers.ThriftComparator;
import org.jhk.pulsing.hadoop.config.serializers.ThriftDeserializer;
import org.jhk.pulsing.hadoop.config.serializers.ThriftSerializer;

import cascading.tuple.Comparison;

public final class ThriftSerialization implements Serialization, Comparison {
    
    @Override
    public boolean accept(Class clazz) {
        return isTBase(clazz) || isTEnum(clazz);
    }

    @Override
    public Deserializer getDeserializer(Class clazz) {
        return isTBase(clazz) ? new ThriftDeserializer(clazz) : new EnumDeserializer(clazz);
    }

    @Override
    public Serializer getSerializer(Class clazz) {
        return isTBase(clazz) ? new ThriftSerializer() : new EnumSerializer();
    }

    @Override
    public Comparator getComparator(Class type) {
        return new ThriftComparator(type);
    }
    
    public boolean isTBase(Class<?> clazz) {
        return TBase.class.isAssignableFrom(clazz);
    }
    
    public boolean isTEnum(Class<?> clazz) {
        return TEnum.class.isAssignableFrom(clazz);
    }

}
