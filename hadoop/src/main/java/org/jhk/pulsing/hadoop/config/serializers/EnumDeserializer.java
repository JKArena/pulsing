package org.jhk.pulsing.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.thrift.TEnum;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public final class EnumDeserializer implements Deserializer<TEnum> {
    
    private DataInputStream dIStream;
    private final Method findByValue;

    public EnumDeserializer(Class<TEnum> c) {
        
        try {
            
            Class[] args = new Class[] {Integer.TYPE};
            findByValue = c.getDeclaredMethod("findByValue", args);
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
    }
    public void open(InputStream in) throws IOException {
        dIStream = new DataInputStream(in);
    }

    public TEnum deserialize(TEnum obj) throws IOException {
        try {
            int val = WritableUtils.readVInt(dIStream);
            Object[] argVec = new Object[]{val};
            return (TEnum) findByValue.invoke(null, argVec);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    public void close() throws IOException {
        if(dIStream != null) {
            dIStream.close();
        }
    }
}
