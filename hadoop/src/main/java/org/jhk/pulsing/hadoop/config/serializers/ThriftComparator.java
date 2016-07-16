package org.jhk.pulsing.hadoop.config.serializers;

import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;

import java.io.Serializable;
import java.util.Comparator;

public final class ThriftComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = -8819391304268418425L;

    public ThriftComparator(Class<?> type) {
        super();
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof TEnum && o2 instanceof TEnum) {
            int v1 = ((TEnum) o1).getValue();
            int v2 = ((TEnum) o2).getValue();
            
            return v1 - v2;
        }
        TBase t1 = (TBase) o1;
        TBase t2 = (TBase) o2;
        return t1.compareTo(t2);
    }

}
