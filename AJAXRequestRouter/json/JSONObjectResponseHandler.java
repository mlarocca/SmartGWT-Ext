package com.smartgwt.extensions.utility.requestrouter.json;

import com.google.gwt.json.client.JSONObject;
import com.smartgwt.extensions.utility.requestrouter.AJAXResponseHandler;

/**Interfaces to be implemented by classes that will handle data response to AJAXRequest, where the retrieved
 * data is of JSONObject type
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public interface JSONObjectResponseHandler extends AJAXResponseHandler {
	public abstract void onSuccess(JSONObject jso );
}
