package net.lucenews.model;

/**
*
* Basically a clone of the Document class found within the 
* Lucene packages. We couldn't just extend it because Lucene's
* author made its class final. This means we can't take advantage
* of any inheritance.
*
*/

import java.io.*;
import net.lucenews.*;
import net.lucenews.model.event.*;
import net.lucenews.model.exception.*;

import org.apache.lucene.index.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import java.util.*;
import org.w3c.dom.*;


public class LuceneDocument {
    
    private Analyzer    analyzer;
    private Document    document;
    private LuceneIndex index;
    
    
    
    
    public LuceneDocument () {
        setAnalyzer( null );
        setDocument( new Document() );
    }
    
    public LuceneDocument (Document document) {
        setAnalyzer( null );
        setDocument( document );
    }
    
    public LuceneDocument (Analyzer analyzer) {
        setAnalyzer( analyzer );
        setDocument( new Document() );
    }
    
    public LuceneDocument (Document document, Analyzer analyzer) {
        setAnalyzer( analyzer );
        setDocument( document );
    }
    
    
    
    
    public Analyzer getAnalyzer () {
        return analyzer;
    }
    
    public void setAnalyzer (Analyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    
    
    public Document getDocument () {
        return document;
    }
    
    public void setDocument (Document document) {
        this.document = document;
    }
    
    
    
    
    public LuceneIndex getIndex () {
        return index;
    }
    
    public void setIndex (LuceneIndex index) {
        this.index = index;
    }
    
    public void update ()
        throws
        IllegalActionException, InvalidIdentifierException,
        DocumentNotFoundException, InsufficientDataException, IOException
    {
        getIndex().updateDocument(this);
    }
    
    public String getTitle () throws IOException {
        return getIndex().getTitle( this );
    }
    
    public Calendar getUpdated () throws InsufficientDataException, IOException {
        return getIndex().getUpdated( this );
    }
    
    public String getAuthor () throws IOException {
        return getIndex().getAuthor( this );
    }
    
    
    
    
    
    public void setBoost (float boost) {
        document.setBoost( boost );
    }
    
    public float getBoost () {
        return document.getBoost();
    }
    
    
    
    
    public void add (Field... fields) {
        for (int i = 0; i < fields.length; i++) {
            document.add( fields[ i ] );
        }
    }
    
    
    
    
    public void removeField (String name) {
        document.removeField( name );
    }
    
    
    
    
    public void removeFields (String name) {
        document.removeFields( name );
    }
    
    
    
    
    public Field getField (String name) {
        return document.getField( name );
    }
    
    
    
    
    public String get (String name) {
        return document.get( name );
    }
    
    
    
    public Enumeration<Field> fields () {
        Enumeration<?> documentFields = document.fields();
        Vector<Field> _fields = new Vector<Field>();
        
        while (documentFields.hasMoreElements()) {
            Object documentField = documentFields.nextElement();
            if (documentField instanceof Field) {
                _fields.add( (Field) documentField );
            }
        }
        
        return _fields.elements();
    }
    
    
    
    public Field[] getFields (String name) {
        return document.getFields( name );
    }
    
    
    
    
    public String[] getValues (String name) {
        return document.getValues( name );
    }
    
    
    
    public String toString () {
        return document.toString();
    }
    
    
    
}
