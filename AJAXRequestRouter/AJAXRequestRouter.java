package com.smartgwt.extensions.utility.requestrouter;

import java.util.PriorityQueue;
import java.util.Vector;

import com.smartgwt.extensions.utility.requestrouter.jsarray.JsArrayResponseHandler;
import com.smartgwt.extensions.utility.requestrouter.json.JSONArrayResponseHandler;
import com.smartgwt.extensions.utility.requestrouter.json.JSONObjectResponseHandler;
import com.smartgwt.extensions.utility.requestrouter.json.StringJSONResponseHandler;


/**
 * A Router for cross-site AJAX requests, which allows a certain number of requests to be sent at the same time,
 * while, if there is a larger number of requests, the exceeding ones will be put in a waiting_queue and processed
 * as soon as the previous ones are completed.
 * 
 * Pending requests are enqueued according on their priority, except for the Urgent Requests which may skip the
 * waiting_queue and be sent right away. However, there is a maximum number (which can be set through setMaxContemporaryRequests method)
 * of Urgent Requests that may be sent when the waiting queue is full.
 * 
 * The Router uses overloading and polymorphism to automatically ask for a specific type of data from the
 * remote server; at the moment, the response can be of one of the following three types:
 * 1) JavaScriptObject						<-> A simple JavaScriptObject which may be later converted to the
 * 												desired type
 * 2) JsArray<T extends JavaScriptObject>	<-> An array of Data Object; it is particularly useful when data
 * 												type T is defined in order to automatically embed the values
 * 												passed from the server
 * 3) [JSON] String							<-> A common String, that MUST be carefully formatted according to
 * 												the JSON specifications; it may be later parsed by JSONParser
 * 4) JSONObject							<-> An object belonging to the JSONObject class
 * 5) JSONArray								<-> An array of JSONObjects
 * 
 * 
 * The choice of the return type is made by the caller simply by choosing the type of the class which is going
 * to handle the AJAX response; this handlers must implement AJAXResponseHandler interface (for JavaScriptObject 
 * response) or one of its derived interfaces.
 * 
 * @author marcellolarocca@gmail.com
 *
 */
public class AJAXRequestRouter {
	
	private static final byte DEFAULT_QUEUE_SIZE = 5;
	
	private static short maxContemporaryRequests = DEFAULT_QUEUE_SIZE;
	
	private static final byte DEFAULT_URGENT_QUEUE_SIZE = 1;
	
	/**
	 * How many urgent requests may be issued at the same time
	 */
	private static short maxContemporaryUrgentRequests = DEFAULT_URGENT_QUEUE_SIZE;
	
	@SuppressWarnings("rawtypes")
	private static PriorityQueue<AJAXRequest> waiting_queue;
	@SuppressWarnings("rawtypes")
	private static Vector<AJAXRequest> queue_started;
	@SuppressWarnings("rawtypes")
	private static Vector<AJAXRequest> queue_urgent;
	
	protected static AJAXRequestRouter router = null;
	
	
	

	/**
	 * Singleton class => private constructor
	 */
	@SuppressWarnings("rawtypes")
	protected AJAXRequestRouter() {
		waiting_queue = new PriorityQueue<AJAXRequest>();
		queue_started = new Vector<AJAXRequest>();
		queue_urgent  = new Vector<AJAXRequest>();
		router = this;
	}
	
	/**
	 * 
	 * @return A reference to the singleton AJAXRequestRouter object 
	 */
	public static AJAXRequestRouter get(){
		if (router == null){
			router = new AJAXRequestRouter();
		}
		return router;
	}

	
	/**
	 * 
	 * @return The maximum number of concurrent AJAX request that, at the moment, may be processed together at the same time
	 */
	public short getMaxContemporaryRequests() {
		return maxContemporaryRequests;
	}

	/**
	 * Sets the maximum number of concurrent AJAX request that may be processed together at the same time
	 * @param maxContemporaryRequests The value to be set (must be >= 1)
	 */
	public void setMaxContemporaryRequests(short maxContemporaryRequests) throws IllegalArgumentException{
		if (maxContemporaryRequests < 1 ){
			throw new IllegalArgumentException();
		}
		AJAXRequestRouter.maxContemporaryRequests = maxContemporaryRequests;
		sendNextRequestInQueue();	//If the waiting_queue becomes larger, maybe new requests can be started
	}
	
	/**
	 * 
	 * @return The maximum number of concurrent <b>URGENT</b> AJAX request.
	 * Urgent requests are sent immediately even if the request waiting_queue is full, but there is only a limited
	 * number of Urgent requests that can be sent at the same time
	 */
	public short getMaxContemporaryUrgentRequests() {
		return maxContemporaryRequests;
	}

	/**
	 * Sets the maximum number of concurrent <b>URGENT</b> AJAX request 
	 * Urgent requests are sent immediately even if the request waiting_queue is full, but there is only a limited
	 * number of Urgent requests that can be sent at the same time
	 * @param maxContemporaryUrgentRequests The value to be set (must be >= 0 - when it is equal to zero it means
	 * that urgent requests must wait as any other)
	 */
	public void setMaxContemporaryUrgentRequests(short maxContemporaryUrgentRequests) throws IllegalArgumentException{
		if (maxContemporaryUrgentRequests < 0 ){
			throw new IllegalArgumentException();
		}
		AJAXRequestRouter.maxContemporaryUrgentRequests  = maxContemporaryUrgentRequests;
		//If the waiting_queue becomes larger, maybe new requests can be started, but for thread safety it's better 
		//not messing with the waiting_queue (there may be a poll action between the priority check of the head of the waiting_queue
		//and its subsequent extraction)
		//URGENT requests will be issued first anyway
	}	

	
	/**
	 * Add a new AJAX request to the waiting_queue. No timeout parameter is passed, so the default value set in AJAXRequest class is used instead;
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface AJAXResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 */
	 public void addNewRequest(String url, AJAXResponseHandler handler){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		sendNewRequest(request);
	}
		
	/**
	 * Add a new AJAX request to the waiting_queue
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface AJAXResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 * @param priority The request's priority
	 * @param timeout The maximum duration (in seconds) of the AJAX request (a default value is set in AJAXRequest class)
	 */
	public void addNewRequest(String url, AJAXResponseHandler handler, RequestPriority priority, short timeout){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		request.setPriority(priority);
		request.setTimeout(timeout);
		sendNewRequest(request);
	}	 
	
	/**
	 * Add a new AJAX request to the waiting_queue. No timeout parameter is passed, so the default value set in AJAXRequest class is used instead;
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JsArrayResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 */
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public void addNewRequest(String url, JsArrayResponseHandler handler){
		 AJAXRequest request = new AJAXRequest(url, router, handler);
		 sendNewRequest(request);

	}	 
		
	/**
	 * Add a new AJAX request to the waiting_queue
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JsArrayResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 * @param priority The request's priority
	 * @param timeout The maximum duration (in seconds) of the AJAX request (a default value is set in AJAXRequest class)
	 */
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	public void addNewRequest(String url, JsArrayResponseHandler handler, RequestPriority priority, short timeout){
		AJAXRequest request = new AJAXRequest(url, router, handler);
		request.setPriority(priority);
		request.setTimeout(timeout);		
		sendNewRequest(request);
	}
	
	/**
	 * Add a new AJAX request to the waiting_queue. No timeout parameter is passed, so the default value set in AJAXRequest class is used instead;
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface StringJSONResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 */
	 public void addNewRequest(String url, StringJSONResponseHandler handler){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		sendNewRequest(request);

	}	 
		
	/**
	 * Add a new AJAX request to the waiting_queue
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface StringJSONResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 * @param priority The request's priority
	 * @param timeout The maximum duration (in seconds) of the AJAX request (a default value is set in AJAXRequest class)
	 */
	public void addNewRequest(String url, StringJSONResponseHandler handler, RequestPriority priority, short timeout){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		request.setPriority(priority);
		request.setTimeout(timeout);
		sendNewRequest(request);
	}
	
	/**
	 * Add a new AJAX request to the waiting_queue. No timeout parameter is passed, so the default value set in AJAXRequest class is used instead;
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JSONObjectResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 */
	 public void addNewRequest(String url, JSONObjectResponseHandler handler){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		sendNewRequest(request);

	}	 
		
	/**
	 * Add a new AJAX request to the waiting_queue
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JSONObjectResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 * @param priority The request's priority
	 * @param timeout The maximum duration (in seconds) of the AJAX request (a default value is set in AJAXRequest class)
	 */
	public void addNewRequest(String url, JSONObjectResponseHandler handler, RequestPriority priority, short timeout){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		request.setPriority(priority);
		request.setTimeout(timeout);
		sendNewRequest(request);
	}
	
	/**
	 * Add a new AJAX request to the waiting_queue. No timeout parameter is passed, so the default value set in AJAXRequest class is used instead;
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JSONArrayResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 */
	 public void addNewRequest(String url, JSONArrayResponseHandler handler){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		sendNewRequest(request);

	}	 
		
	/**
	 * Add a new AJAX request to the waiting_queue
	 * @param url The url where the request has to be sent
	 * @param handler A class implementing the interface JSONArrayResponseHandler. This class will have to handle the data retrieved and the situations where the waiting_queue is full and the request has to wait or the request fails 
	 * @param priority The request's priority
	 * @param timeout The maximum duration (in seconds) of the AJAX request (a default value is set in AJAXRequest class)
	 */
	public void addNewRequest(String url, JSONArrayResponseHandler handler, RequestPriority priority, short timeout){
		@SuppressWarnings("rawtypes")
		AJAXRequest request = new AJAXRequest(url, router, handler);
		request.setPriority(priority);
		request.setTimeout(timeout);
		sendNewRequest(request);
	}	
		
	/**
	 *  
	 * @param request
	 * @return 	RequestStatus.DELETED		<=> The request was actually and still in the waiting waiting_queue;
	 * 		   	RequestStatus.STARTED		<=> The request has already been sent
	 * 			RequestStatus.INVALID		<=>	The request hasn't been enqueued or it has been already completed   
	 */
	public RequestStatus removeRequest(@SuppressWarnings("rawtypes") AJAXRequest request){
		if ( waiting_queue.remove(request) ){
			return RequestStatus.DELETED;
		}else{

			if ( queue_started.contains(request) || queue_urgent.contains(request)){
				return RequestStatus.STARTED;
			}
			else{
				return RequestStatus.LOST;
			}
		}
	}
	
	 /**
	  * Tries to start the request, but if too many have already been started the request is just enqueued,
	  * until some other requests will be completed
	  * @param request The request to be started
	  */
	protected void sendNewRequest(@SuppressWarnings("rawtypes") AJAXRequest request){
		
		if ( queue_started.size() >= maxContemporaryRequests ){
			//Too many requests started: Check if the request is urgent, and if so verifies if there is room in the urgent requests' waiting_queue
			if ( request.isUrgent() && queue_urgent.size() < maxContemporaryUrgentRequests ){
				//Request can be started as urgent
				queue_urgent.add( request ) ;
				try{
					request.getResponse();
				}catch(Exception e){
//TODO:	request.notifyFailure? 
					System.out.println(e.getMessage() + " | " + e.getCause());
				}			
			}else{
				//Request must be hold until some other request is completed
				waiting_queue.add(request);
				request.notifyQueueFull();
				//waiting_queue automatically handles the priority
			}
		}else{
			//Request can be started right now
			queue_started.add( request ) ;
			try{
				request.getResponse();
			}catch(Exception e){
				System.out.println(e.getMessage() + " | " + e.getCause());
//TODO:	request.notifyFailure? 
			}			
		}		
	}
	
	/**
	 * Send the first request in the waiting_queue, if any; it tries to send as many requests 
	 */
	private void sendNextRequestInQueue(){
		while ( queue_started.size() < maxContemporaryRequests && !waiting_queue.isEmpty() ){
			@SuppressWarnings("rawtypes")
			AJAXRequest request = waiting_queue.poll();
			queue_started.add( request ) ;
			try{
				request.getResponse();
			}catch(Exception e){
			}
		}
		return;
	}

	/**
	 * This method gets called by each AJAXRequest once it's completed (or if fails)
	 * @param request The calling request
	 */
	public void notifyCompletion(@SuppressWarnings("rawtypes") AJAXRequest request){
		//Checks if the request is urgent and only if so (thanks laziness!) tries to remove it from the urgent waiting_queue
		if ( !request.isUrgent() || !queue_urgent.remove(request) ){
			//If the request is not urgent or it has been added to the urgent waiting_queue, then it must be removed
			//from the standard queue_started
			queue_started.remove(request);
		}
		sendNextRequestInQueue();
	}
	

}
