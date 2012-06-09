package com.smartgwt.extensions.taskbar.client.events;

import com.smartgwt.client.widgets.AnimationCallback;

/**
 * A particular Event Container: it is also a firable event, that fires itself all the events it contains simultaneously
 * 
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 */
public class EventGroup extends EventContainer{	
	
	
	public EventGroup() {
		super();
//		System.out.println("			(Event Group)"	);
		eventBody = new AnimationCallback() {
			
			@Override
			public void execute(boolean earlyFinish) {
				startEvents();
				
			}
		};
	}

	
	private void startEvents(){
		if (events.isEmpty()){	//To avoid deadlock if the group is initially empty, notify the completion
			onEventCompleted.execute(true);
		}
		
		for (int i=0; i< events.size(); i++){
//			System.out.println("	Starting Event " );
			Event e = events.get(i);
			e.execute(false);
		}
	}
		
	@Override
	public void execute(boolean earlyFinish) {
//		System.out.println("Event Group ID: " + ID + " execute");
		if (eventBody!=null)
			eventBody.execute(earlyFinish);
	}	
	
	@Override
	public void onEventCompleted(Event ev){		
		events.remove(ev);
//		System.out.println("Event Group's Event over - Queue size: " + events.size());
		if (events.isEmpty()){
//			System.out.println("Event Group completed " );
			onEventCompleted.execute(false);	//Group Event completed;			
		}
	}
	
	/**
	 * Deprecated method: EventGroup's body can not be set from outside this class
	 */
	@Override
	@Deprecated
	public void setEventBody(AnimationCallback callback) {
		//Do nothing
	}
}
