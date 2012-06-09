package com.smartgwt.extensions.taskbar.client;

import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.extensions.taskbar.client.events.Event;
import com.smartgwt.extensions.taskbar.client.events.EventChain;
import com.smartgwt.extensions.taskbar.client.events.EventContainer;
import com.smartgwt.extensions.taskbar.client.events.EventGroup;

/**
 * A variant to the base class (TaskBar) that, instead of shrinking the tasks as more are added, has a fixed
 * tasks width, adds tasks in a tabular fashion, with a fixed number of columns and a variable number of rows;
 * Taskbar's height varies automatically according to the number of rows.
 * 
 * @author Marcello La Rocca (marcellolarocca@gmail.com)
 * 
 */
public class MultiRowTaskBar extends TaskBar {

	protected final TaskBar thisTaskBar = this;

	
	private static final byte DEFAULT_TASKS_PER_ROW = 5;
	private byte tasksPerRow = DEFAULT_TASKS_PER_ROW;
	
//	private static final byte TASKBAR_TASKS = DEFAULT_TASKS_PER_ROW * TASKBAR_ROWS;
	
	public MultiRowTaskBar(int left, int top, int width, int height){
		super(left, top, width, height);
		//After initializing the base taskbar part of the object, a few more actions are needed
		computeTasksWidth(width);
		updateTaskBarHeight();		
		setMaxTasksWidth(width);		//In case only one task per row is allowed
	}
	
	/**
	 * 
	 */
	@Override
	protected void addTask(final Task task, final TaskBarWindow win){
		//Resizes the bar first

		final Event updateTasksHeightEvent = mainEventsChain.addNewEvent();
		updateTasksHeightEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					updateTaskBarHeight(updateTasksHeightEvent);
				}
			}
		);
		super.addTask(task, win);

//No need		mainEventsChain.execute(false);
	}	

	/**
	 * 
	 */
	@Override
	protected void setTaskCoordinates(final Task task) {
		int pos = getTaskPosition(task);
		int col = pos % tasksPerRow;
		int row = (int) Math.floor( ((double)pos) / tasksPerRow );
		
		task.setLeft( TASK_MARGIN + col * (tasksWidth + TASK_MARGIN)  );
		task.setTop( TASK_MARGIN + row * (tasksHeight + TASK_MARGIN)  );		
	}

	/**
	 * 
	 */
	@Override
	protected void setTaskCoordinates(final Task task, final EventContainer eventContainer) {
		int pos = getTaskPosition(task);

		int col = pos % tasksPerRow;
		int row = (int) Math.floor( ((double)pos) / tasksPerRow );

		int oldTop = task.getTop();
		final int newLeft = TASK_MARGIN + col * (tasksWidth + TASK_MARGIN) ;
		final int newTop = TASK_MARGIN + row * (tasksHeight + TASK_MARGIN);
		if (newTop == oldTop ){

			//Slides on the same row...
			final Event setTaskPositionEvent = eventContainer.addNewEventAfterCurrent();
			setTaskPositionEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						task.animateMove(newLeft, null, setTaskPositionEvent.setExternalOnCompletionNotification() , mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME) );
					}
				}
			);
		}else{
			//Makes the task disappear from its old position and reappear in its new one
			final EventChain repositionChain = new EventChain();
			eventContainer.addNewEventAfterCurrent(repositionChain);
			
			final Event hidingEvent = repositionChain.addNewEvent();
			hidingEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						task.animateHide(
								AnimationEffect.FADE, 
								hidingEvent.setExternalOnCompletionNotification() ,
								mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME)
							);
					}
				}
			);
			final Event reshowingEvent = repositionChain.addNewEvent();
			reshowingEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						task.setLeft(newLeft);
						task.setTop(newTop);
						task.animateShow(AnimationEffect.FADE, reshowingEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
					}
				}
			);
		}

	}
	
	/**
	 * 
	 */
	@Override
	protected void refreshTaskBar(final Event refreshTaskBarEvent) {
		// System.out.println("Refreshing");
		
		final Event resizeTaskBarEvent = mainEventsChain.addNewEventAfterCurrent();
		resizeTaskBarEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					updateTaskBarHeight(resizeTaskBarEvent);
				}
			}
		);
		/*
		final Event checkVisibilityEvent = mainEventsChain.addNewEventAfter(resizeTaskBarEvent);
		checkVisibilityEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					if (!isVisible() && !tasks.isEmpty())
						animateShow(AnimationEffect.FADE,
								checkVisibilityEvent.setExternalOnCompletionNotification(),
								mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
					else if (isVisible() && tasks.isEmpty())
						animateHide(AnimationEffect.FADE,
								checkVisibilityEvent.setExternalOnCompletionNotification(),
								mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));					
				}
			}
		);
		*/
	}
	
	/**
	 * Sets the tasks' height
	 * Being impossible to estimate the global height for the taskbar (since it grows or shrinks over time as tasks
	 * are added or removed) a new method that allows to change the height of each single row (i.e. task) is necessary 
	 * @param newTasksHeight The new value for tasks height
	 */
	public void setTasksHeight(int newTasksHeight){
		if ( newTasksHeight < minTasksHeight || newTasksHeight > maxTasksHeight ){
			throw new IllegalArgumentException();
		}
		//else
		
		if ( newTasksHeight == tasksHeight )
			return ;
		
		if ( newTasksHeight > tasksHeight ){
			//First resizes the taskBar, then resizes the tasks
			
			tasksHeight = newTasksHeight;
			
			final Event computeTaskBarHeightEvent = mainEventsChain.addNewEvent();
			computeTaskBarHeightEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						updateTaskBarHeight(computeTaskBarHeightEvent);
					}
				}
			);
			
			final EventGroup resizeTasksEvent = new EventGroup();
			mainEventsChain.addNewEventAfter(resizeTasksEvent, computeTaskBarHeightEvent);
			for (int i=0; i<tasks.size(); i++ ){
				final int pos = i;
				final Event e = resizeTasksEvent.addNewEventAfterCurrent();
				e.setEventBody(
					new AnimationCallback() {
						
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(null, tasksHeight, e.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					}
				);
			}
			
		}else{
			//First resizes all the tasks, and then the taskbar can be resized
			
			tasksHeight = newTasksHeight;
			
			final EventGroup resizeTasksEvent = new EventGroup();
			mainEventsChain.addNewEvent(resizeTasksEvent);
			for (int i=0; i<tasks.size(); i++ ){
				final int pos = i;
				final Event e = resizeTasksEvent.addNewEventAfterCurrent();
				e.setEventBody(
					new AnimationCallback() {
						
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(null, tasksHeight, e.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					}
				);
			}
		
			final Event computeTaskBarHeightEvent = mainEventsChain.addNewEventAfter(resizeTasksEvent);
			computeTaskBarHeightEvent.setEventBody(
				new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						updateTaskBarHeight(computeTaskBarHeightEvent);
					}
				}
			);

		}
		mainEventsChain.execute(false);		
	}
	
	/**
	 * Computes the new appropriate value for the height of a taskbar with #howManyTasks tasks 
	 * @param howManyTasks The number of tasks present or estimated for the taskbar (can be used to estimate 
	 * 			future need for resizing)
	 * @return The appropriate height for the taskbar height, in pixels
	 */
	private int computeTaskBarHeight(int howManyTasks){
		int row = (int) Math.floor( ((double)howManyTasks-1) / tasksPerRow );
		int height =  (row + 1) * ( tasksHeight + TASK_MARGIN ) + 2 * TASK_MARGIN;
		return height;
	}	
	
	/**
	 * 
	 */
	@Override
	protected void computeTasksWidth(int taskBarWidth) {
		final int oldTasksWidth = tasksWidth;
		tasksWidth = (taskBarWidth - ( tasksPerRow + 1) * TASK_MARGIN)	/ tasksPerRow;

		if (tasks.size() > 0) {
			if ( tasksWidth < minTasksWidth ){
				SC.warn("TaskBar width is too small to hold " + tasksPerRow + " tasks per row<br>The bar is going to be resized" );
				tasksWidth = oldTasksWidth;
				
			}else{
				tasksWidth = tasksWidth <= maxTasksWidth ? tasksWidth : maxTasksWidth; 
				updateTasksWidth(oldTasksWidth);
			}
		}		
	}
	
	/**
	 * Handles all the animations necessary to adjust the taskbar height
	 */
	protected void updateTaskBarHeight(){
		int height = computeTaskBarHeight(tasks.size());
		animateResize(null, height, null, 1);	//Must go around setHeight
	}
	
	/**
	 * Handles all the animations necessary to adjust the taskbar height
	 * @param updateTaskBarHeightEvent The Event inside whom the animations must take place
	 */
	protected void updateTaskBarHeight(Event updateTaskBarHeightEvent){
		int oldHeight = getHeight();
		int height = computeTaskBarHeight(tasks.size());
		if (height != oldHeight)
			animateResize(null, height, updateTaskBarHeightEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
	}
	
	/**
	 * Returns the number of tasks displayed in each row of the TaskBar
	 * @return The number of tasks displayed in each row of the TaskBar
	 */
	public byte getTasksPerRow(){
		return tasksPerRow;
	}
	
	/**
	 * Sets the number of tasks displayed in each row of the TaskBar
	 * @param newTasksPerRow The number of tasks to be displayed in each row of the TaskBar
	 * @throws IllegalArgumentException Argument must be greater than zero
	 */
	public void setTasksPerRow(final byte newTasksPerRow) throws IllegalArgumentException{
		if (newTasksPerRow <= 0){
			throw new IllegalArgumentException();
		}//else
		
		Event updateTasksPerRowEvent = mainEventsChain.addNewEvent();
		updateTasksPerRowEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					tasksPerRow = newTasksPerRow;
					computeTasksWidth(getWidth());
				}
			}
		);
		final Event updateTasksBarHeightEvent = mainEventsChain.addNewEvent();
		updateTasksBarHeightEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					updateTaskBarHeight(updateTasksBarHeightEvent);
				}
			}
		);		
		mainEventsChain.execute(false);
		
	}	
	
	/**
	 * Prevents other classes from setting Height
	 */
	@Deprecated
	@Override
	public void setHeight(int height){
		//Do nothing, because you can't set Height!
	}
	
	/**
	 * Prevents other classes from setting Height
	 */
	@Deprecated
	@Override
	public void setHeight(String height){
		//Do nothing, because you can't set Height!
	}	
	
	
	/**
	 * Prevents other classes from getting Height
	 */
	@Deprecated
	@Override
	public int getTasksHeight(){
		throw new IllegalArgumentException("Height can not be set or got in MultiRow Taskbars");
	}
	
/**
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		setMaxTaskWidth(width);	//In case only one tasks per row is allowed
		//Not desirable if an esplicit call to setMaxTaskWidth has been made...
	}
*/	
}
