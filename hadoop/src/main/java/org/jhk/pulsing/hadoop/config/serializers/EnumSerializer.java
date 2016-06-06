package org.jhk.pulsing.hadoop.config.serializers;

import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.thrift.TEnum;

import java.io.*;

public final class EnumSerializer implements Serializer<TEnum> {
    private DataOutputStream dOStream;

    public void open(OutputStream out) throws IOException {
        dOStream = new DataOutputStream(out);
    }

    public void serialize(TEnum obj) throws IOException {
        WritableUtils.writeVInt(dOStream, obj.getValue());
    }

    public void close() throws IOException {
        dOStream.close();
    }
}