package net.lucenews.http;

import java.util.AbstractCollection;
import java.util.Iterator;

public class DefaultIndexCollection extends AbstractCollection<Index> implements IndexCollection {

	@Override
	public Iterator<Index> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		int result = 0;
		
		Iterator<Index> iterator = iterator();
		while (iterator.hasNext()) {
			result++;
		}
		
		return result;
	}
	
}