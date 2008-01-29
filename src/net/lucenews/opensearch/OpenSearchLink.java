package net.lucenews.opensearch;

import org.w3c.dom.*;

public class OpenSearchLink {
    
    private String href;
    private String hreflang;
    private String rel;
    private String type;
    
    
    
    
    public String getHref () {
        return href;
    }
    
    public void setHref (String href) {
        this.href = href;
    }
    
    
    
    public String getHrefLang () {
        return hreflang;
    }
    
    public void setHrefLang (String hreflang) {
        this.hreflang = hreflang;
    }
    
    
    
    public String getRel () {
        return rel;
    }
    
    public void setRel (String rel) {
        this.rel = rel;
    }
    
    
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    
    
    public OpenSearchLink clone () {
        OpenSearchLink link = new OpenSearchLink();
        link.setHref( getHref() );
        link.setHrefLang( getHrefLang() );
        link.setRel( getRel() );
        link.setType( getType() );
        return link;
    }
    
    
    
    public Element asElement (Document document, OpenSearch.Format format) throws OpenSearchException {
        return asElement(document, format, OpenSearch.STRICT);
    }
    
    public Element asElement (Document document, OpenSearch.Format format, OpenSearch.Mode mode) throws OpenSearchException {
        return asElement( document, format, mode, false );
    }
    
    public Element asElement (Document document, OpenSearch.Format format, OpenSearch.Mode mode, boolean entry) throws OpenSearchException {
        Element element = null;
        
        if (format == OpenSearch.ATOM && entry) {
            element = document.createElement("link");
        }
        else {
            if (getRel().equals("search") || format == OpenSearch.RSS) {

        element = document.createElementNS("http://www.w3.org/2005/Atom","link");
            }
            else {
                element = document.createElement("link");
            }
        }
        
        if (getHref() != null) {
            element.setAttribute("href", getHref());
        }
        
        if (getHrefLang() != null) {
            element.setAttribute("hreflang", getHrefLang());
        }
        
        if (getRel() != null) {
            element.setAttribute("rel", getRel());
        }
        
        if (getType() != null) {
            element.setAttribute("type", getType());
        }
        
        return element;
    }
    
}
