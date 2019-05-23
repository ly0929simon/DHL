package com.dhl.xmlpi.labelservice.exception;

public class LabelServiceException extends Exception
{

    /** serialVersionUID */
    private static final long serialVersionUID = -4401044829676110919L;

    private String            xmlpiMessage;
    
    private Exception originalCause;

    public LabelServiceException(String xmlpiMessage, Exception e)
    {
        super(e.getMessage(), e.getCause());
        this.originalCause = e;
        this.xmlpiMessage = xmlpiMessage;
    }

    public LabelServiceException(String xmlpiMessage)
    {
        this.xmlpiMessage = xmlpiMessage;
    }

    public String getXmlpiMessage()
    {
        return xmlpiMessage;
    }
    
    public Exception getOriginalCause() {
    	return originalCause;
    }

}
