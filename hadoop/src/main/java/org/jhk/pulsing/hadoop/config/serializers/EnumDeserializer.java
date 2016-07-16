package org.jhk.pulsing.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.thrift.TEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public final class EnumDeserializer implements Deserializer<TEnum> {
    
    private static final Logger _LOG = LoggerFactory.getLogger(EnumDeserializer.class);
    
    private DataInputStream dIStream;
    private final Method findByValue;

    public EnumDeserializer(Class<TEnum> clazz) {
        
        try {
            
            Class[] args = new Class[] {Integer.TYPE};
            findByValue = clazz.getDeclaredMethod("findByValue", new Class[] {Integer.TYPE});
            
        } catch (NoSuchMethodException nsmException) {
            throw new RuntimeException(nsmException);
        }
        
    }
    
    public void open(InputStream iStream) throws IOException {
        dIStream = new DataInputStream(iStream);
    }

    public TEnum deserialize(TEnum obj) throws IOException {
        _LOG.info("EnumDeserializer.deserialize " + obj);
        
        try {
            int val = WritableUtils.readVInt(dIStream);
            return (TEnum) findByValue.invoke(null, new Object[]{ val });
        } catch (Exception dException) {
            throw new IOException(dException);
        }
    }

    public void close() throws IOException {
        if(dIStream != null) {
            dIStream.close();
        }
    }
}
