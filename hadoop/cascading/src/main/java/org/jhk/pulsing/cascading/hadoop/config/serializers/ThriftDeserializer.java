package org.jhk.pulsing.cascading.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ThriftDeserializer implements Deserializer<TBase<?, ?>> {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ThriftDeserializer.class);

    private TBase<?, ?> prototype;
    private TIOStreamTransport sTransport;
    private TProtocol protocol;

    private DataInputStream dIStream;

    public ThriftDeserializer(Class<TBase<?, ?>> clazz) {
        
        try {
            prototype = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create " + clazz, e);
        }
    }

    public void open(InputStream iStream) throws IOException {
        dIStream = new DataInputStream(iStream);
        sTransport = new TIOStreamTransport(iStream);
        protocol = new TCompactProtocol(sTransport);
    }

    public TBase<?, ?> deserialize(TBase<?, ?> tObject) throws IOException {
        _LOGGER.debug("ThriftDeserializer.deserialize " + tObject);
        
        WritableUtils.readVInt(dIStream);
        TBase<?, ?> object = prototype.deepCopy();
        
        try {
            object.read(protocol);
        } catch (TException e) {
            throw new IOException(e.toString());
        }
        
        return object;
    }

    public void close() throws IOException {
        if (sTransport != null) {
            sTransport.close();
        }
    }

}
