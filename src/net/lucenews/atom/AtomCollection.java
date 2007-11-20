package net.lucenews.atom;

import org.w3c.dom.*;
import java.util.*;

public class AtomCollection {
    
    private String title, href, member_type, list_template;
    
    
    public AtomCollection (String title, String href, String member_type) {
        this( title, href, member_type, null );
    }
    
    public AtomCollection (String title, String href, String member_type, String list_template) {
        setTitle( title );
        setHref( href );
        setMemberType( member_type );
        setListTemplate( list_template );
    }
    
    
    public String getTitle () {
        return title;
    }
    
    public void setTitle (String title) {
        this.title = title;
    }
    
    
    
    public String getHref () {
        return getHREF();
    }
    
    public String getHREF () {
        return href;
    }
    
    public void setHref (String href) {
        setHREF( href );
    }
    
    public void setHREF (String href) {
        this.href = href;
    }
    
    
    
    public String getMemberType () {
        return member_type;
    }
    
    public void setMemberType (String member_type) {
        this.member_type = member_type;
    }
    
    
    
    public String getListTemplate () {
        return list_template;
    }
    
    public void setListTemplate (String list_template) {
        this.list_template = list_template;
    }
    
    
    
 // This was updated to add the 'accept' elements and change to 'atom:title' tags.
   
    public Element asElement (Document document) {
        Element collection = document.createElement("collection");
        //collection.setAttribute("title", String.valueOf(getTitle()));
	Element title = document.createElement("atom:title");
	title.appendChild(document.createTextNode(String.valueOf(getTitle())));
	collection.setAttribute("href", String.valueOf(getHref()));
        //Changed to the Element type below from the 'member-type'below that
        Element accept = document.createElement("accept");
	accept.appendChild( document.createTextNode( "application/atom+xml;type=entry" ));
	collection.appendChild(accept);
	collection.appendChild(title);
        if (getListTemplate() != null) {
            Element list_template = document.createElement("list-template");
            list_template.appendChild( document.createTextNode( String.valueOf( getListTemplate() ) ) );
            collection.appendChild(list_template);
        }
        
        return collection;
    }
    
    
}
