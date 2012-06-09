package com.smartgwt.extensions.taskbar.client;

import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.extensions.taskbar.client.events.Event;
import com.smartgwt.extensions.taskbar.client.events.EventChain;
import com.smartgwt.extensions.taskbar.client.events.EventContainer;
import com.smartgwt.extensions.taskbar.client.events.EventGroup;


/**
 * A variant to the base class (TaskBar) that, instead of shrinking the tasks as more are added, given a fixed
 * tasks width, maintains the minimum possible bar width by self enlarging as new tasks are added, or self shrinking
 * as they are removed.
 * @author Marcello La Rocca (marcellolarocca@gmail.com)
 * 
 */
public class AutoFittingTaskBar extends TaskBar {

	public AutoFittingTaskBar(int left, int top, int width, int height) {
		super(left, top, width, height);
		//updateTaskBarWidth(mainEventsChain);
		computeTasksWidth(width);			//Tasks width must be explicitly computed
		setMaxTasksWidth(2 * getTasksWidth());
		mainEventsChain.execute(false);		//Starts the event created by computeTasksWidth
	}
	
	/**
	 * 
	 */
	@Override
	protected void addTask(final Task task, final TaskBarWindow win){
		//Resizes the bar first
		final int newWidth = computeTaskBarWidth(tasks.size()+1);
		final Event updateTaskBarWidthEvent = mainEventsChain.addNewEvent();
		updateTaskBarWidthEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					animateResize(newWidth, null, updateTaskBarWidthEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
				}
			}
		);
		//Then executes the generic method
		super.addTask(task, win);

	}
	
	/**
	 * Computes the appropriate width for a taskbar containing #howManyTasks tasks
	 * @param howManyTasks The actual or estimated number of tasks in the taskbar
	 * @return The appropriate width for the taskbar, in pixels
	 */
	private int computeTaskBarWidth(int howManyTasks){
		return 2 * TASK_MARGIN + howManyTasks * ( tasksWidth + TASK_MARGIN	);
	}	
	
	/**
	 * 
	 */
	@Override
	protected void computeTasksWidth(int taskBarWidth) {
//		System.out.println("Computing task width");
		EventChain updateTaskBarWidthEvent = new EventChain();
		mainEventsChain.addNewEventAfterCurrent(updateTaskBarWidthEvent);
		updateTaskBarWidth(updateTaskBarWidthEvent);
	}
	
	/**
	 * @return The actual width of a single task
	 */
	public int getTasksWidth(){
		return tasksWidth;
	}
	
	/**
	 * Sets the tasks' width
	 * Being impossible to estimate the global width for the taskbar (since it grows or shrinks over time as tasks
	 * are added or removed) a new method that allows to change the width of each single task is necessary 
	 * @param newTasksWidth The new value for tasks width
	 */
	public void setTasksWidth(final int newTasksWidth){
		if ( newTasksWidth < minTasksWidth || newTasksWidth > maxTasksWidth){
//			System.out.println(minTasksWidth + " < " + newTasksWidth + " < " + maxTasksWidth);
			throw new IllegalArgumentException();
		}
		//else
		
		if ( newTasksWidth == tasksWidth )
			return ;

		final Event tasksWidthUpdateEvent = mainEventsChain.addNewEvent();
		final EventChain tasksWidthUpdateEventChain = new EventChain();
		mainEventsChain.addNewEvent(tasksWidthUpdateEventChain);
		tasksWidthUpdateEvent.setEventBody(  
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					tasksWidthUpdate(newTasksWidth, tasksWidthUpdateEventChain);
					
				}
			}
		);
	
			
		mainEventsChain.execute(false);	
	}


	protected void tasksWidthUpdate(final int newTasksWidth, EventContainer eventContainer) {

	
		if ( newTasksWidth > tasksWidth ){
			
			//First resizes the taskBar, then resizes the tasks
			tasksWidth = newTasksWidth;
			
			updateTaskBarWidth(eventContainer); //Creates the Events itself			
			
//			updateTasksWidth(oldTasksWidth);	//Creates the Events itself
			
			
			final EventGroup resizeTasksEvent = new EventGroup();
			eventContainer.addNewEvent(resizeTasksEvent);
			for (int i=0; i<tasks.size(); i++ ){
				final int pos = i;
				final Event e = resizeTasksEvent.addNewEventAfterCurrent();
				e.setEventBody(
					new AnimationCallback() {
						
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(tasksWidth, null, e.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					}
				);
			}
			
			
		}else{
			//First resizes all the tasks, and then the taskbar can be resized
			tasksWidth = newTasksWidth;
			
			final EventGroup resizeTasksEvent = new EventGroup();
			eventContainer.addNewEvent(resizeTasksEvent);
			for (int i=0; i<tasks.size(); i++ ){
				final int pos = i;
				final Event e = resizeTasksEvent.addNewEventAfterCurrent();
				e.setEventBody(
					new AnimationCallback() {
						
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(tasksWidth, null, e.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					}
				);
			}
			
			
//			updateTasksWidth(oldTasksWidth);	//Creates the Events itself
			
			updateTaskBarWidth(eventContainer); //Creates the Events itself			
		
		}
			
	}
	
	/**
	 * Updates the taskbar width
	 * @param eventContainer
	 */
	protected void updateTaskBarWidth(final EventContainer eventContainer){
		int oldWidth = getWidth();
		final int width = computeTaskBarWidth(tasks.size());
		
		//Different procedures are needed if the bar is going to be enlarged or shrunk
		
		if (width > oldWidth)
		{	
			//First resizes the bar, then moves the elements
			final Event resizeTaskBarEvent = eventContainer.addNewEvent();
			resizeTaskBarEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						animateResize(width, null, resizeTaskBarEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
					}
				}
			);

			final EventGroup updateTasksPositionEvent = new EventGroup();
			eventContainer.addNewEventAfter(updateTasksPositionEvent, resizeTaskBarEvent);
			for (int i=0; i<tasks.size(); i++){
				final int pos = i;
				setTaskCoordinates(tasks.get(pos), updateTasksPositionEvent);	
			}
			

		}else{
			
			//First moves the tasks, then resizes the bar
			final EventGroup updateTasksPositionEvent = new EventGroup();
			eventContainer.addNewEvent(updateTasksPositionEvent);
			for (int i=0; i<tasks.size(); i++){
				final int pos = i;
				setTaskCoordinates(tasks.get(pos), updateTasksPositionEvent);	
			}

			final Event resizeTaskBarEvent = eventContainer.addNewEvent();
			resizeTaskBarEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						animateResize(width, null, resizeTaskBarEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
					}
				}
			);
		}
	}
	
	/**
	 * Handles all the animations necessary to adjust the taskbar width
	 * @param updateTaskBarWidthEvent The Event inside whom the animations must take place
	 */
	protected void updateTaskBarWidth(Event updateTaskBarWidthEvent){
		int oldWidth = getWidth();
		int width = computeTaskBarWidth(tasks.size());
		if (width != oldWidth)
			animateResize(width, null, updateTaskBarWidthEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
	}
	
	/**
	 * Prevents other classes from setting Width
	 */
	@Deprecated
	@Override
	public void setWidth(int width){
		//Do nothing, because you can't set Width!
	}
	
	/**
	 * Prevents other classes from setting Width
	 */
	@Deprecated
	@Override
	public void setWidth(String width){
		//Do nothing, because you can't set Width!
	}	
	
}
