package net.lucenews3.lucene.support;

import org.apache.lucene.search.Sort;

public interface SortParser {

	public Sort parseSort(String string);
	
}
