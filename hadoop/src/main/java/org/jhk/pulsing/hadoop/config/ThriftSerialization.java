package org.jhk.pulsing.hadoop.config;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;
import org.jhk.pulsing.hadoop.config.serializers.EnumDeserializer;
import org.jhk.pulsing.hadoop.config.serializers.EnumSerializer;
import org.jhk.pulsing.hadoop.config.serializers.ThriftComparator;
import org.jhk.pulsing.hadoop.config.serializers.ThriftDeserializer;
import org.jhk.pulsing.hadoop.config.serializers.ThriftSerializer;

import cascading.tuple.Comparison;
import jcascalog.Api;

public final class ThriftSerialization implements Serialization, Comparison {
    
    public static void setApplicationConfig() {
        Map<String, String> config = new HashMap<>();
        
        config.put("io.serializations", ThriftSerialization.class.getName() + "," + WritableSerialization.class.getName());
        Api.setApplicationConf(config);
    }
    
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
