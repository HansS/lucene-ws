package net.lucenews.model.exception;

import javax.servlet.http.*;


public class IllegalActionException extends LuceneException {
    
    public IllegalActionException (String message) {
        super( message );
    }
    
    public int getStatus () {
        return HttpServletResponse.SC_FORBIDDEN;
    }
    
}
