package net.lucenews3.lucene.support;

import java.io.IOException;
import java.util.AbstractList;
import java.util.BitSet;

import net.lucenews.http.ExceptionWrapper;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;

public class IndexReaderDocumentList extends AbstractList<Document> implements DocumentList {

	private IndexReader reader;
	private IndexWriter writer;
	private IndexSearcher searcher;
	private Filter filter;
	private FilterMerger filterMerger;
	private boolean initialized;
	private boolean includeDeleted;
	private BitSet eligibleDocuments;
	private ExceptionWrapper exceptionWrapper;
	
	public IndexReaderDocumentList(IndexReaderDocumentList prototype) {
		this.reader = prototype.reader;
		this.writer = prototype.writer;
		this.searcher = prototype.searcher;
		this.filter = prototype.filter;
		this.exceptionWrapper = prototype.exceptionWrapper;
	}
	
	public IndexReaderDocumentList(IndexReader reader, IndexWriter writer, IndexSearcher searcher) {
		this.reader = reader;
		this.writer = writer;
		this.searcher = searcher;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void initialize() {
		if (!initialized) {
			if (filter == null) {
				eligibleDocuments = null;
			} else {
				try {
					eligibleDocuments = filter.bits(reader);
				} catch (IOException e) {
					throw exceptionWrapper.wrap(e);
				}
			}
			initialized = true;
		}
	}
	
	/**
	 * Determines whether or not a document is eligible to be
	 * included in this list.
	 * @param documentNumber
	 * @return
	 */
	public boolean isIncluded(int documentNumber) {
		boolean result;
		
		initialize();
		if (!includeDeleted && reader.isDeleted(documentNumber)) {
			result = false;
		} else if (eligibleDocuments == null) {
			result = true;
		} else {
			result = eligibleDocuments.get(documentNumber);
		}
		
		return result;
	}
	
	/**
	 * Determines the document number associated with
	 * the <code>index</code><sup>th</sup> <em>non-deleted</em>
	 * document. This number is suitable for use with 
	 * {@link IndexReader#document(int)}.
	 * @param index
	 * @return
	 */
	public int indexToDocumentNumber(int index) {
		int currentIndex = 0;
		int currentNumber = 0;
		
		while (currentIndex < index) {
			if (isIncluded(currentNumber)) {
				currentIndex++;
			}
			currentNumber++;
		}
		
		return -1;
	}
	
	@Override
	public boolean add(Document document) {
		try {
			writer.addDocument(document);
		} catch (CorruptIndexException e) {
			throw exceptionWrapper.wrap(e);
		} catch (IOException e) {
			throw exceptionWrapper.wrap(e);
		}
		return true;
	}
	
	/**
	 * Retrieves the <code>index</code><sup>th</sup> <em>non-deleted</em>
	 * document. The first existing document is at index 0.
	 */
	@Override
	public Document get(int index) {
		Document result;
		
		try {
			result = reader.document(indexToDocumentNumber(index));
		} catch (CorruptIndexException e) {
			throw exceptionWrapper.wrap(e);
		} catch (IOException e) {
			throw exceptionWrapper.wrap(e);
		}
		
		return result;
	}

	@Override
	public Document remove(int index) {
		return null;
	}
	
	@Override
	public int size() {
		return reader.numDocs();
	}

	/**
	 * Returns an instance of <code>DocumentList</code> representing
	 * a subset of the current collection of documents which meet the
	 * given criteria.
	 * @param criteria a Lucene query to dictate the contents of the sub-list
	 * @return a <code>DocumentList</code> which has been filtered by the given criteria
	 */
	public DocumentList filteredBy(Filter filter) {
		final IndexReaderDocumentList result = new IndexReaderDocumentList(this);
		result.filter = filterMerger.mergeFilters(result.filter, filter);
		return result;
	}

}