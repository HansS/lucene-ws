package net.lucenews.model;

import java.io.*;
import org.apache.log4j.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.wordnet.*;

public class LuceneQueryExpander {
    
    private float boost;
    private Searcher searcher;
    private Analyzer analyzer;
    
    
    
    public LuceneQueryExpander () {
        setBoost( 1.0f );
    }
    
    
    
    public float getBoost () {
        return boost;
    }
    
    public void setBoost (float boost) {
        this.boost = boost;
    }
    
    
    
    public Searcher getSearcher () {
        return searcher;
    }
    
    public void setSearcher (Searcher searcher) {
        this.searcher = searcher;
    }
    
    
    
    public Analyzer getAnalyzer () {
        return analyzer;
    }
    
    public void setAnalyzer (Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    
    
    public Query expand (Query query)
        throws IOException
    {
        return expand( query, getSearcher() );
    }
    
    public Query expand (Query query, Searcher searcher)
        throws IOException
    {
        return expand( query, searcher, getAnalyzer() );
    }
    
    public Query expand (Query query, Searcher searcher, Analyzer analyzer)
        throws IOException
    {
        return expand( query, searcher, analyzer, getBoost() );
    }
    
    
    
    /**
     * Expands a Query
     */
    
    public Query expand (Query query, Searcher searcher, Analyzer analyzer, float boost)
        throws IOException
    {
        if (query instanceof BooleanQuery) {
            return expand( (BooleanQuery) query, searcher, analyzer, boost );
        }
        
        if (query instanceof TermQuery) {
            return expand( (TermQuery) query, searcher, analyzer, boost );
        }
        
        return query;
    }
    
    
    
    /**
     * Expands a BooleanQuery
     */
    
    public Query expand (BooleanQuery query, Searcher searcher, Analyzer analyzer, float boost)
        throws IOException
    {
        Logger.getLogger(this.getClass()).debug("Expanding BooleanQuery: " + query);
        
        BooleanClause[] clauses = query.getClauses();
        
        for (int i = 0; i < clauses.length; i++) {
            BooleanClause clause = clauses[ i ];
            clause.setQuery( expand( clause.getQuery(), searcher, analyzer, boost ) );
        }
        
        return query;
    }
    
    
    
    /**
     * Expands a TermQuery
     */
    
    public Query expand (TermQuery query, Searcher searcher, Analyzer analyzer, float boost)
        throws IOException
    {
        Term term = query.getTerm();
        
        Query expanded = SynExpand.expand( term.text(), searcher, analyzer, term.field(), boost );
        
        if (expanded instanceof BooleanQuery) {
            BooleanQuery booleanQuery = (BooleanQuery) expanded;
            BooleanClause[] clauses = booleanQuery.getClauses();
            
            Logger.getLogger(this.getClass()).debug("Expanded into a BooleanQuery: " + booleanQuery);
            
            TokenTermQuery tokenTermQuery = null;
            
            for (int i = 0; i < clauses.length; i++) {
                BooleanClause clause = clauses[ i ];
                if (clause.getQuery() instanceof TokenTermQuery) {
                    tokenTermQuery = (TokenTermQuery) clause.getQuery();
                    break;
                }
            }
            
            if (tokenTermQuery != null) {
                TokenBooleanQuery tokenBoolean = new TokenBooleanQuery( booleanQuery.isCoordDisabled(), tokenTermQuery.getToken() );
                
                for (int i = 0; i < clauses.length; i++) {
                    BooleanClause clause = clauses[ i ];
                    tokenBoolean.add( clause );
                    tokenBoolean.setMinimumNumberShouldMatch( booleanQuery.getMinimumNumberShouldMatch() );
                }
                
                Logger.getLogger(this.getClass()).debug("Returning TokenBooleanQuery: " + tokenBoolean);
                return tokenBoolean;
            }
        }
        
        return expanded;
    }
    
}
