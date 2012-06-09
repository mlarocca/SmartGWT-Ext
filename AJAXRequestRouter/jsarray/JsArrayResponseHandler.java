/**
 * 
 */
package com.smartgwt.extensions.utility.requestrouter.jsarray;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.smartgwt.extensions.utility.requestrouter.AJAXResponseHandler;

/**Interfaces to be implemented by classes that will handle data response to AJAXRequest, where the data
 * is an array of objects derived from JavaScriptObject supertype.
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public interface JsArrayResponseHandler<T extends JavaScriptObject> extends AJAXResponseHandler{
	
	public abstract void onSuccess(JsArray<T> jso );
}
