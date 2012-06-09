package com.smartgwt.extensions.taskbar.client.events;

import java.util.Vector;

/**
 * Abstract Event Container: gives the basics methods
 * A container is also an Event, so that different containers may be combined together to compose more complex structures  
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 *
 */
public abstract class EventContainer extends Event{
	
	/**
	 * The queue containing all the events registered to this container
	 */
	protected final Vector<Event> events = new Vector<Event>();

	/**
	 * Abstract method that must be implemented by all containers: it's up to the particular container to decide
	 * what to do when one of its Events is completed
	 * @param ev The event which has been completed
	 */
	public abstract void onEventCompleted(Event ev);
	
	/**
	 * A convenient way to group all the code needed to create a new Event 
	 * @return The newly created Event
	 */
	private Event createEvent() {
		Event newEvent = new Event();
		return newEvent;
	}	

	
	/**
	 * Creates a new Event and adds it to the end of the queue
	 * @return The newly created Event
	 */
	public Event addNewEvent() {
		Event newEvent = createEvent();
		
		addNewEvent(newEvent);
		return newEvent;
	}
	
	/**
	 * Adds the Event passed as parameter to the end of the queue
	 * @param newEvent The Event to be added to the container
	 */
	public void addNewEvent(Event newEvent) {
		setAsEventContainer(newEvent);
		events.add(newEvent);
//		System.out.println("Adding event " + newEvent.ID + " at position " + (events.size()-1) );
	}

	/**
	 * Creates a new Event and adds after the specified event, if it belongs to the container
	 * @param afterThisEvent The Event after which the newly created one must be added
	 * @return The newly created Event
	 * @throws ArrayIndexOutOfBoundsException	If no such Event as "<i>afterThisEvent</i>" belongs to the container,
	 *  there is no way to decide where the new Event should be added
	 */
	public Event addNewEventAfter(Event afterThisEvent) throws ArrayIndexOutOfBoundsException{
		Event newEvent = createEvent();

		addNewEventAfter(newEvent, afterThisEvent);

		return newEvent;
	}
	
	/**
	 * Adds the Event <i>newEvent</i> after the specified one, if it belongs to the container
	 * @param newEvent The new Event to be added to the container
	 * @param afterThisEvent The Event after which the newly created one must be added
	 * @throws ArrayIndexOutOfBoundsException	If no such Event as "afterThisEvent" belongs to the container,
	 *  there is no way to decide where the new Event should be added
	 */
	public void addNewEventAfter(Event newEvent, Event afterThisEvent) throws ArrayIndexOutOfBoundsException{
		setAsEventContainer(newEvent);
		int i = events.indexOf(afterThisEvent);
		if (i >=0 )	//check if i is a valid index
			events.add(++i,newEvent);
		else		//if not, throws an exception
			throw new ArrayIndexOutOfBoundsException("Element not in the array");

//		System.out.println("Adding event " + newEvent.ID + " at position " + i + " After event " + afterThisEvent.ID);
	}

	/**
	 * Creates a new Event and adds it to the container after the current one.
	 * If there is no current event (i.e. the queue is empty) then the newly created Event is added as the head of the queue
	 * <b>NOTE</b>: If you are planning to inherit from this abstract class,
	 * the current Event in a container should always be the first one!
	 * @return The newly created Event
	 */
	public Event addNewEventAfterCurrent() {
		Event newEvent = createEvent();

		addNewEventAfterCurrent(newEvent);
		return newEvent;
	}
	
	/**
	 * Adds <i>newEvent</i> to the container after the current one.
	 * If there is no current event (i.e. the queue is empty) then Event is added as the head of the queue
	 * <b>NOTE</b>: If you are planning to inherit from this abstract class,
	 * the current Event in a container should always be the first one!
	 * @param newEvent The Event to be added to this container
	 */
	public void addNewEventAfterCurrent(Event newEvent) {
		setAsEventContainer(newEvent);
		if (events.isEmpty()){
			addNewEvent(newEvent);
		}else{
			//INVARIANT: The current event is always the first one, unless the queue is empty
			events.add(1,newEvent);
		}

//		System.out.println("Adding event " + newEvent.ID + " after current event"  );
	}
	
	/**
	 * Sets this particular Event Container as the owner of an Event
	 * @param ev The event which needs to be assigned to this container
	 */
	public void setAsEventContainer(Event ev) {
		ev.setContainer(this);
	}
}
