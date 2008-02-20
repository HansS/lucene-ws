package net.lucenews3.lucene.support;

import java.util.List;

import net.lucenews3.atom.Entry;

public interface EntryListParser<I> extends Parser<I, List<Entry>> {

	@Override
	public List<Entry> parse(I input);
	
}
