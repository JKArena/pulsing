package org.jhk.pulsing.cascading.hadoop.config;

import java.util.Comparator;

import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;
import org.jhk.pulsing.cascading.hadoop.config.serializers.EnumDeserializer;
import org.jhk.pulsing.cascading.hadoop.config.serializers.EnumSerializer;
import org.jhk.pulsing.cascading.hadoop.config.serializers.ThriftComparator;
import org.jhk.pulsing.cascading.hadoop.config.serializers.ThriftDeserializer;
import org.jhk.pulsing.cascading.hadoop.config.serializers.ThriftSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.tuple.Comparison;

public final class ThriftSerialization implements Serialization, Comparison {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ThriftSerialization.class);
    
    @Override
    public boolean accept(Class clazz) {
        return isTBase(clazz) || isTEnum(clazz);
    }
    
    @Override
    public Deserializer getDeserializer(Class clazz) {
        boolean iTBase = isTBase(clazz);
        
        _LOGGER.debug("ThriftSerialization.getDeserializer : " + clazz.getName() + " : " + iTBase);
        return iTBase ? new ThriftDeserializer(clazz) : new EnumDeserializer(clazz);
    }

    @Override
    public Serializer getSerializer(Class clazz) {
        boolean iTBase = isTBase(clazz);
        
        _LOGGER.debug("ThriftSerialization.getSerializer : " + clazz.getName() + " : " + iTBase);
        return iTBase ? new ThriftSerializer() : new EnumSerializer();
    }

    @Override
    public Comparator getComparator(Class type) {
        _LOGGER.debug("ThriftSerialization.getComparator " + type.getName());
        return new ThriftComparator(type);
    }
    
    public boolean isTBase(Class<?> clazz) {
        return TBase.class.isAssignableFrom(clazz);
    }
    
    public boolean isTEnum(Class<?> clazz) {
        return TEnum.class.isAssignableFrom(clazz);
    }

}
