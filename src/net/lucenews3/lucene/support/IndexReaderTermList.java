package net.lucenews3.lucene.support;

import java.io.IOException;

import net.lucenews.http.ExceptionWrapper;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

/**
 * A list of terms obtained from an index reader.
 *
 */
public class IndexReaderTermList extends AbstractIteratorList<Term> implements TermList {

	private ExceptionWrapper exceptionWrapper;
	private IndexReader indexReader;
	private Term fromTerm;
	private Term toTerm;

	public IndexReaderTermList(IndexReaderTermList prototype) {
		this.exceptionWrapper = prototype.exceptionWrapper;
		this.indexReader = prototype.indexReader;
		this.fromTerm = prototype.fromTerm;
		this.toTerm = prototype.toTerm;
	}
	
	public IndexReaderTermList(IndexReader indexReader) {
		this(indexReader, null);
	}
	
	public IndexReaderTermList(IndexReader indexReader, Term fromTerm) {
		this.indexReader = indexReader;
		this.fromTerm = fromTerm;
	}
	
	public IndexReaderTermList(IndexReader indexReader, Term fromTerm, Term toTerm) {
		this.indexReader = indexReader;
		this.fromTerm = fromTerm;
		this.toTerm = toTerm;
	}
	
	@Override
	public TermIterator iterator() {
		TermIterator result;
		try {
			if (fromTerm == null) {
				result = new TermEnumIterator(indexReader.terms());
			} else {
				result = new TermEnumIterator(indexReader.terms(fromTerm));
			}
		} catch (IOException e) {
			throw exceptionWrapper.wrap(e);
		}
		return result;
	}

	// TODO: Fix this
	public IndexReaderTermList subList(Term fromTerm) {
		IndexReaderTermList result = new IndexReaderTermList(this);
		if (this.fromTerm != null && fromTerm.compareTo(this.fromTerm) < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.toTerm != null && fromTerm.compareTo(this.toTerm) > 0) {
			throw new IllegalArgumentException();
		}
		result.fromTerm = fromTerm;
		return result;
	}

	@Override
	public IndexReaderTermList subList(int fromIndex, int toIndex) {
		// TODO
		return null;
	}
	
	@Override
	public IndexReaderTermList subList(Term fromTerm, Term toTerm) {
		IndexReaderTermList result = new IndexReaderTermList(this);
		if (this.fromTerm != null && fromTerm.compareTo(this.fromTerm) < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.toTerm != null && toTerm.compareTo(this.toTerm) > 0) {
			throw new IndexOutOfBoundsException();
		}
		if (fromTerm.compareTo(toTerm) > 0) {
			throw new IllegalArgumentException();
		}
		result.fromTerm = fromTerm;
		result.toTerm = toTerm;
		return result;
	}

	@Override
	public TermListIterator listIterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TermListIterator listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TermListIterator listIterator(Term term) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
