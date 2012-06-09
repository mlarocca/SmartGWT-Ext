/**
 * 
 */
package com.smartgwt.extensions.utility.requestrouter.json;

import com.smartgwt.extensions.utility.requestrouter.AJAXResponseHandler;

/**Interfaces to be implemented by classes that will handle data response to AJAXRequest, where the data type
 * is String and the string returned is formatted according to JSON specifications.
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public abstract class StringJSONResponseHandler implements AJAXResponseHandler {
	
	public abstract void onSuccess(String jso );
}
