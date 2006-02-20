package net.lucenews.model;

import java.util.*;
import net.lucenews.model.exception.*;


public class LimitedIterator<E> implements Iterator<E>
{
	
	private Iterator<E> iterator;
	private Limiter limiter;
	private int position;
	private int limit;
	private boolean initialized;
	
	
	public LimitedIterator (Iterator<E> iterator, Limiter limiter)
		throws InsufficientDataException
	{
		this.iterator = iterator;
		this.limiter  = limiter;
		limit = limiter.getLimit();
		position = 1;
		//initialize();
	}
	
	protected void initialize ()
	{
		int skip = limiter.getSkipped();
		for( int i = 0; i < skip; i++ )
			iterator.next();
		
		initialized = true;
	}
	
	public boolean hasNext ()
	{
		if( !initialized )
			initialize();
		
		return 1 <= position && position <= limit;
	}
	
	public E next ()
	{
		if( !initialized )
			initialize();
		
		position++;
		return iterator.next();
	}
	
	public void remove ()
	{
	}
	
}
