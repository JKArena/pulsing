package org.jhk.pulsing.cascading.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.thrift.TEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class EnumSerializer implements Serializer<TEnum> {
    
    private static final Logger _LOG = LoggerFactory.getLogger(EnumSerializer.class);
    
    private DataOutputStream dOStream;

    public void open(OutputStream out) throws IOException {
        dOStream = new DataOutputStream(out);
    }

    public void serialize(TEnum obj) throws IOException {
        _LOG.info("EnumSerializer.serialize " + obj);
        
        WritableUtils.writeVInt(dOStream, obj.getValue());
    }

    public void close() throws IOException {
        if(dOStream != null) {
            dOStream.close();
        }
    }
    
}
