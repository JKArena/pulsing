package org.jhk.pulsing.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ThriftSerializer implements Serializer<TBase<?, ?>> {
    
    private static final Logger _LOG = LoggerFactory.getLogger(ThriftSerializer.class);
    
    private TIOStreamTransport sTransport;
    private TProtocol protocol;
    private ByteArrayOutputStream buffer;

    private OutputStream realOutStream;
    private DataOutputStream dOStream;

    public void open(OutputStream oStream) throws IOException {
        realOutStream = oStream;
        dOStream = new DataOutputStream(realOutStream);

        buffer = new ByteArrayOutputStream();
        sTransport = new TIOStreamTransport(buffer);
        protocol = new TCompactProtocol(sTransport);
    }

    public void serialize(TBase<?, ?> tObject) throws IOException {
        _LOG.info("ThriftSerializer.serialize " + tObject);
        
        try {
            buffer.reset();
            tObject.write(protocol);

            WritableUtils.writeVInt(dOStream, buffer.size());
            buffer.writeTo(realOutStream);
        } catch (TException e) {
            throw new IOException(e);
        }
    }

    public void close() throws IOException {
        if(sTransport!=null) {
            sTransport.close();
        }
    }
}