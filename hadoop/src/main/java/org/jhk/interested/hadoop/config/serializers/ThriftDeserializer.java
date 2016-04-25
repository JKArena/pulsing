package org.jhk.interested.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ThriftDeserializer implements Deserializer<TBase> {

    private TBase prototype;
    private TIOStreamTransport sTransport;
    private TProtocol protocol;

    private DataInputStream dIStream;

    public ThriftDeserializer(Class<TBase> c) {
        try {
            prototype = c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create " + c, e);
        }

    }

    public void open(InputStream in) throws IOException {
        dIStream = new DataInputStream(in);
        sTransport = new TIOStreamTransport(in);
        protocol = new TCompactProtocol(sTransport);
    }

    public TBase deserialize(TBase t) throws IOException {
        WritableUtils.readVInt(dIStream);
        TBase object = prototype.deepCopy();
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
