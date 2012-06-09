package com.smartgwt.extensions.taskbar.client.events;

import com.smartgwt.client.widgets.AnimationCallback;

/**
 * EventChain is an Event of a particular kind, because it is an Event container too:
 * it handles a set of events to be executed in a FIFO queue, one after another, the latter only once the previous
 * one has been completed.
 * Being an Event itself, an Event chain can be an element of another container: Event chains and Event groups can be 
 * composed together to form more complex sequences of actions
 *   
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 */
public class EventChain extends EventContainer {
	
	private boolean started = false;		//Keep tracks of the status of the chain: in fact, a chain may be started more than once in different area of the chain owner

	/**
	 * 
	 */
	public EventChain(){
		super();
		//Must set the eventBody 
		eventBody = new AnimationCallback() {
			
			@Override
			public void execute(boolean earlyFinish) {
				startChain();				
			}
		};
	}
	

	/**
	 * Starts the chain of Events, triggering them one after another
	 */
	private void startChain(){
		if (started){
			return ;
		}//else

		started = true;
		fireNextEvent();	//Avoid deadlock when events is empty
	}
	
	/**
	 * Triggers the next event in the chain, if the queue is not empty, or notify the possible container of this
	 * event chain that all the events in the chain have been completed
	 */
	private void fireNextEvent(){
		if ( !events.isEmpty() ){
			Event ev = events.get(0);	//Gets the first event
//			System.out.println("Firing event ID " + ev.ID);
			ev.execute(false);
		}else{
			started = false;
			onEventCompleted.execute(false);	//Chain Completed Event;			
		}
	}
	
	@Override
	public void onEventCompleted(Event ev){	
//		System.out.println("Chain onEventCompleted");
		events.remove(ev);
//		System.out.println("Queue size: " + events.size());

		fireNextEvent();
	}

	@Override
	public void execute(boolean earlyFinish) {
//		System.out.println("Chain execute ID " + ID);
		if (eventBody!=null)
			eventBody.execute(earlyFinish);		
	}	

	
	/**
	 * Deprecated method: EventChain's body can not be set from outside this class
	 */
	@Override
	@Deprecated
	public void setEventBody(AnimationCallback callback) {
		//Do nothing
	}


	/**
	 * Scales the animation time of the caller according to the number of events in the chain.
	 * For long sequences for which response time matters, the originally programmed response time
	 * might be too long to wait: hence it is scaled according to the number of Events left in the chain - 
	 * the more Events waiting, the faster animation are played.
	 * <b>WARNING</b>: In order to use this feature this method <u>MUST</u> be called by every animation
	 * instruction inside every Event's body in this container.
	 *
	 * @param originalAnimationTime The interval originally programmed for the animation 
	 * @return The properly scaled interval
	 */
	public int scaleAnimationTime(int originalAnimationTime){
		double newAnimationRatio = events.size() <= 1 ? 1 : Math.log10(events.size() + 10) / (events.size()+1 );
//		System.out.println("Size: " + events.size() + " | " + /*animationTimeRatio + " | " +*/ newAnimationRatio );
		
		return (int) Math.round(originalAnimationTime * newAnimationRatio);
	}
	

}
