package com.smartgwt.extensions.taskbar.client.events;

import com.smartgwt.client.widgets.AnimationCallback;

/**
 * Class Event defines a new extension for AnimationCallback interface.
 * An Event is a convenient way to call an externally defined method which is able to notify when it is finished.
 * Particular care must be used when the event body is set: the created AnimationCallback code must contain
 * at top 1 animation instruction that defines the Event length (longer sequences of animations must be split in
 * several events), and in that case, the callback passed to the animation must be the return value of
 * event.getOnCompletionCallback().
 * To properly use events, these steps should be followed:
 * 		0) 		EventContainer eventContainer;			//	Create an event container (EventChain or EventGroup)
 * 		1) 		final Event event = new Event();		//	Create an Event	(Must be declared as final only if you are planning to include animation code inside it)
 * 		2) 		eventContainer.addNewEvent(event); 		//	Add the event to a container
 * 		3)												//	Set the event body (what the event does)
 					event.setEventBody(
 					new AnimationCallback(){
				
						@Override
						public void execute(boolean earlyFinish) {
							...
							x.animateResize(w,h,event.getOnCompletionCallback());	//	Let the event know that its 
																					//	body contains an animation
						}
					}
				);
 * 		4)		...										//You may add more events to the container
 * 		n)		eventContainer.execute					//Eventually, the container must be started directly (or automatically, if it's itself an element of another container)				
 * 
 * The Event Class handles itself all the remaining details.
 * 
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 */
public class Event implements AnimationCallback {

//	private static int counter = 0;	//Used to define a unique ID
//	private int ID;		//Unique ID	
	
	protected AnimationCallback eventBody = null;		//The core code that must be executed when the event is fired
	protected AnimationCallback onEventCompleted;		//Handle the notification of the event completion
	
	private EventContainer container = null;			//Link to the (possible) container
	
	private boolean waitingForCallback = false;			//It is set to true only when getOnCompletionCallback() is called: this way the notification of the event completion is postponed (it must be fired externally by whoever calls getOnCompletionCallback)

	/**  
	 */
	public Event(){
//		ID = ++counter;
		final Event thisEvent = this;	//Needs to keep track of "this" reference for callbacks

		//Sets what to do when the Event is over: basically, notify the container, if any 
		onEventCompleted = new AnimationCallback() {
			
			@Override
			public void execute(boolean earlyFinish) {
//				System.out.println("Event " + ID + " completed");

				if (container!=null)
					container.onEventCompleted(thisEvent);
			}
		};
	}

	/**
	 * Sets the code that will be executed as the event is triggered 
	 * 
	 * @param callback the reference to the callback to be executed as "body"
	 */
	public void setEventBody(AnimationCallback callback){
		eventBody = callback;
	}
	
	/**
	 * Sets the container to whom the Event belongs
	 * @param eventContainer The event container
	 */
	public void setContainer(EventContainer eventContainer){
		container = eventContainer;
	}
	
	/**
	 * 
	 */
	@Override
	public void execute(boolean earlyFinish) {
//		System.out.println("Event ID: " + ID + " inside execute");
		if (eventBody!=null)
			eventBody.execute(earlyFinish);
		if ( !waitingForCallback )
			onEventCompleted.execute(earlyFinish);
	}
	
	/**
	 * Sets the Event so that the onCompletion notification must be triggered outside the event itself;
	 * A reference to the callback that notify the event container is returned so that the caller can (and must)
	 * execute it or the container will never know the event has been completed
	 * (<b>WARNING</b>: not executing the callback after calling this method will probably lead to deadlock)    
	 * @return The AnimationCallback that must be executed once the event is over  
	 */
	public AnimationCallback setExternalOnCompletionNotification(){
		waitingForCallback = true;
		return onEventCompleted;
	}
		
}
