package net.lucenews3.lucene.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

public class SortMergerImpl implements SortMerger {

	public Sort mergeSorts(Sort base, Sort delta) {
		Sort result;
		if (delta == null) {
			result = null;
		} else if (base == null) {
			result = delta;
		} else {
			final List<SortField> mergedSortFields = new ArrayList<SortField>();
			
			// Establish which sort fields are mentioned in the delta.
			final Set<Object> deltaSortFieldKeys = new HashSet<Object>();
			for (SortField deltaSortField : delta.getSort()) {
				deltaSortFieldKeys.add(getSortFieldKey(deltaSortField));
			}
			
			// Add the base fields which are not mentioned in the delta
			for (SortField baseSortField : base.getSort()) {
				final Object baseSortFieldKey = getSortFieldKey(baseSortField);
				if (!deltaSortFieldKeys.contains(baseSortFieldKey)) {
					mergedSortFields.add(baseSortField);
				}
			}
			
			// Add all the fields mentioned in the delta
			mergedSortFields.addAll(Arrays.asList(delta.getSort()));
			
			result = new Sort(mergedSortFields.toArray(new SortField[]{}));
		}
		return result;
	}
	
	protected Object getSortFieldKey(SortField field) {
		Object result;
		switch (field.getType()) {
		case SortField.SCORE:
		case SortField.DOC:
			result = field.getType();
			break;
		default:
			result = field.getField();
			break;
		}
		return result;
	}

}