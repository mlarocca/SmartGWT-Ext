package com.smartgwt.extensions.utility.requestrouter;

/**Basic interface to be implemented by classes which are going to handle the response to an AJAXRequest
 * This interface should NOT be directly implemented by such classes! This is a commom super-interface
 * for the inherited interfaces which are going to be implemented by actual handlers.
 * Inherited interfaces should declare a "onSuccess()" method too.
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public interface AJAXResponseHandler 
{
	public  void onFailure( @SuppressWarnings("rawtypes") AJAXRequest request );
	public  void onQueueFull( @SuppressWarnings("rawtypes") AJAXRequest request );	
}