package com.smartgwt.extensions.utility.requestrouter.javascriptobject;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.extensions.utility.requestrouter.AJAXResponseHandler;

/**Interfaces to be implemented by classes that will handle data response to AJAXRequest, where the data
 * is of JavaScriptObject type.
 * NOTE: This is the most generic type that can be handled: it will always be possible to treat the data
 * obtained by AJAXRequest's calls as JavaScriptObjects
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public interface JsObjectResponseHandler extends AJAXResponseHandler {

	public abstract void onSuccess(JavaScriptObject jso );

}