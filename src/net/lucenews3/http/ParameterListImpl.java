package net.lucenews3.http;

import java.util.ArrayList;

import net.lucenews3.KeyValue;
import net.lucenews3.KeyValueMap;
import net.lucenews3.KeyValueMapImpl;

public class ParameterListImpl extends ArrayList<KeyValue<String, String>> implements ParameterList {

	private static final long serialVersionUID = -8936358817363622985L;
	
	private KeyValueMap<String, String> byKey;
	
	public ParameterListImpl() {
		this.byKey = new KeyValueMapImpl<String, String>(this);
	}

	@Override
	public KeyValueMap<String, String> byKey() {
		return byKey;
	}

}
