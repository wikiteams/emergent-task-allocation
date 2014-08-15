package utils;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class IndexedLinkedHashMap<K,V> extends LinkedHashMap{

	/**
	 * The serialization runtime associates with each serializable 
	 * class a version number, called a serialVersionUID, which is 
	 * used during deserialization to verify that the sender and receiver 
	 * of a serialized object have loaded classes for that object that are 
	 * compatible with respect to serialization.
	 */
	private static final long serialVersionUID = -7899892354146310204L;
	HashMap<Integer,Object> index;
	Integer curr = 0;

    @Override
    public Object put(Object key,Object value){
    	index.put(curr++, key);
        return super.put(key,value);
    }

    public Object getindexed(int i){
        return super.get(index.get(i));
    }

}