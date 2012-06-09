package com.smartgwt.extensions.utility.requestrouter.json;

import com.google.gwt.json.client.JSONArray;
import com.smartgwt.extensions.utility.requestrouter.AJAXResponseHandler;

/**Interfaces to be implemented by classes that will handle data response to AJAXRequest, where the retrieved
 * data is of JSONArray type
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public interface JSONArrayResponseHandler extends AJAXResponseHandler {
	public abstract void onSuccess(JSONArray jso );
}
