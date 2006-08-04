package net.lucenews.controller;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import net.lucenews.*;
import net.lucenews.model.*;
import net.lucenews.model.exception.*;
import net.lucenews.opensearch.*;
import net.lucenews.view.*;
import org.apache.log4j.*;
import org.w3c.dom.*;


public class OpenSearchController extends Controller {
    
    
    /**
     * Gets the OpenSearch description document for the particular index.
     * 
     * @param c The context
     * @throws IndicesNotFoundException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws IOException
     */
    
    public static void doGet (LuceneContext c)
        throws
            IndicesNotFoundException, ParserConfigurationException, TransformerException,
            IOException, OpenSearchException
    {
        LuceneWebService   service = c.service();
        LuceneIndexManager manager = service.getIndexManager();
        LuceneRequest      req     = c.req();
        LuceneRequest      request = c.req();
        LuceneResponse     res     = c.res();
        LuceneIndex[]      indices = manager.getIndices( req.getIndexNames() );
        
        
        OpenSearchDescription description = new OpenSearchDescription();
        
        
        // If only one index exists, add its defaults first
        if ( indices.length == 1 ) {
            addDefaults( description, indices[ 0 ] );
        }
        
        // ShortName
        if ( description.getShortName() == null ) {
            description.setShortName( ServletUtils.joined( ServletUtils.objectsMapped("getTitle", indices) ) );
        }
        
        // Description
        if ( description.getDescription() == null ) {
            description.setDescription( "OpenSearch description for " + description.getShortName() );
        }
        
        
        // Template
        HttpURI template = new HttpURI( service.getServiceURL( request ) );
        template.addPath( ServletUtils.join( ",", (Object[]) indices ) );
        template.setParameter( "searchTerms",     "{searchTerms}" );
        template.setParameter( "count",           "{count?}" );
        template.setParameter( "startIndex",      "{startIndex?}" );
        template.setParameter( "startPage",       "{startPage?}" );
        template.setParameter( "language",        "{language?}" );
        template.setParameter( "outputEncoding",  "{outputEncoding?}" );
        template.setParameter( "inputEncoding",   "{inputEncoding?}" );
        template.setParameter( "totalResults",    "{totalResults?}" );
        template.setParameter( "analyzer",        "{lucene:analyzer?}" );
        template.setParameter( "defaultField",    "{lucene:defaultField?}" );
        template.setParameter( "defaultOperator", "{lucene:defaultOperator?}" );
        template.setParameter( "filter",          "{lucene:filter?}" );
        template.setParameter( "locale",          "{lucene:locale?}" );
        template.setParameter( "sort",            "{lucene:sort?}" );
        
        
        // Atom
        OpenSearchUrl atomUrl = new OpenSearchUrl();
        atomUrl.setType("application/atom+xml");
        atomUrl.setTemplate( template.with( "format", "atom" ).toString() );
        atomUrl.setNamespace( "lucene", "http://www.lucene-ws.net/spec/1.0/" );
        /**
        atomUrl.addParam( "searchTerms",     "{searchTerms}" );
        atomUrl.addParam( "count",           "{count?}" );
        atomUrl.addParam( "startIndex",      "{startIndex?}" );
        atomUrl.addParam( "startPage",       "{startPage?}" );
        atomUrl.addParam( "language",        "{language?}" );
        atomUrl.addParam( "outputEncoding",  "{outputEncoding?}" );
        atomUrl.addParam( "inputEncoding",   "{inputEncoding?}" );
        atomUrl.addParam( "totalResults",    "{totalResults?}" );
        atomUrl.addParam( "analyzer",        "{lucene:analyzer?}" );
        atomUrl.addParam( "defaultField",    "{lucene:defaultField?}" );
        atomUrl.addParam( "defaultOperator", "{lucene:defaultOperator?}" );
        atomUrl.addParam( "filter",          "{lucene:filter?}" );
        atomUrl.addParam( "locale",          "{lucene:locale?}" );
        atomUrl.addParam( "sort",            "{lucene:sort?}" );
        */
        description.addUrl( atomUrl );
        
        
        // RSS
        OpenSearchUrl rssUrl = new OpenSearchUrl();
        rssUrl.setType("application/rss+xml");
        rssUrl.setTemplate( template.with( "format", "rss" ).toString() );
        rssUrl.setNamespace( "lucene", "http://www.lucene-ws.net/spec/1.0/" );
        /**
        rssUrl.addParam( "searchTerms",     "{searchTerms}" );
        rssUrl.addParam( "count",           "{count?}" );
        rssUrl.addParam( "startIndex",      "{startIndex?}" );
        rssUrl.addParam( "startPage",       "{startPage?}" );
        rssUrl.addParam( "language",        "{language?}" );
        rssUrl.addParam( "outputEncoding",  "{outputEncoding?}" );
        rssUrl.addParam( "inputEncoding",   "{inputEncoding?}" );
        rssUrl.addParam( "totalResults",    "{totalResults?}" );
        rssUrl.addParam( "analyzer",        "{lucene:analyzer?}" );
        rssUrl.addParam( "defaultField",    "{lucene:defaultField?}" );
        rssUrl.addParam( "defaultOperator", "{lucene:defaultOperator?}" );
        rssUrl.addParam( "filter",          "{lucene:filter?}" );
        rssUrl.addParam( "locale",          "{lucene:locale?}" );
        rssUrl.addParam( "sort",            "{lucene:sort?}" );
        */
        description.addUrl( rssUrl );
        
        
        
        // Images
        
        boolean addedImage = false;
        for (int i = 0; i < indices.length; i++) {
            LuceneIndex index = indices[ i ];
            OpenSearchImage indexImage = null;
            
            try {
                indexImage = index.getImage();
            }
            catch (NumberFormatException nfe) {
                Logger.getLogger(OpenSearchController.class).debug( nfe );
            }
            
            if (indexImage != null) {
                description.addImage( indexImage );
                addedImage = true;
            }
        }
        
        if ( !addedImage ) {
            OpenSearchImage serviceImage = service.getImage();
            if ( serviceImage != null ) {
                description.addImage( serviceImage );
            }
        }
        
        
        
        // OutputEncoding / InputEncoding
        Iterator<Map.Entry<String,Charset>> charsetIterator = Charset.availableCharsets().entrySet().iterator();
        
        while (charsetIterator.hasNext()) {
            Map.Entry<String,Charset> entry = charsetIterator.next();
            
            String  name    = entry.getKey();
            Charset charset = entry.getValue();
            
            // OutputEncoding
            if (charset.canEncode()) {
                //description.addOutputEncoding( name );
            }
            
            // InputEncoding
            try {
                charset.newDecoder();
                //description.addInputEncoding( name );
            }
            catch (UnsupportedOperationException inputUoe) {
            }
        }
        
        
        OpenSearchView.process( c, description );
    }    
    
    
    public static void addDefaults (OpenSearchDescription description, LuceneIndex index) throws IOException {
        String ns = "opensearch.description.";
        
        // ShortName
        description.setShortName( index.getProperty( ns + "shortname" ) );
        
        // Description
        description.setDescription( index.getProperty( ns + "description" ) );
        
        // Contact
        description.setContact( index.getProperty( ns + "contact" ) );
        
        // Tags
        description.setTags( index.getProperty( ns + "tags" ) );
        
        // LongName
        description.setLongName( index.getProperty( ns + "longname" ) );
        
        // Developer
        description.setDeveloper( index.getProperty( ns + "developer" ) );
        
        // Attribution
        description.setAttribution( index.getProperty( ns + "attribution" ) );
        
        // SyndicationRight
        description.setSyndicationRight( index.getProperty( ns + "syndicationright" ) );
        
        // AdultContent
        description.setAdultContent( index.getProperty( ns + "adultcontent" ) );
    }
    
}
