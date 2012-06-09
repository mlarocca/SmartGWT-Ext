package com.smartgwt.extensions.taskbar.client;

import java.util.Vector;

import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.extensions.taskbar.client.events.Event;
import com.smartgwt.extensions.taskbar.client.events.EventChain;
import com.smartgwt.extensions.taskbar.client.events.EventContainer;
import com.smartgwt.extensions.taskbar.client.events.EventGroup;

/**
 * The class models a simple fixed-width taskbar, which can handle Windows registration and automatically
 * shrinks or enlarge the tasks to cope with task removal or insertion
 * @author Marcello La Rocca (marcellolarocca@gmail.com)
 * 
 */
public class TaskBar extends Canvas {

	/**
	 * Tasks array
	 */
	protected Vector<Task> tasks = null;

	protected final TaskBar thisTaskBar = this;
	// private boolean hiding = false;

	/**
	 * Each taskbar owns a chain of events which handles animations in the right order
	 */
	protected EventChain mainEventsChain;

	protected TaskBarWindow selectedWindow = null;
	
	private static final int DEFAULT_TASK_WIDTH = 200;

	protected static final int DEFAULT_MIN_TASK_WIDTH = 5;
	protected int minTasksWidth = DEFAULT_MIN_TASK_WIDTH;

	protected int maxTasksWidth = DEFAULT_TASK_WIDTH;
	protected int tasksWidth = maxTasksWidth;
	private static final int DEFAULT_TASK_HEIGHT = 20;

	protected static final int DEFAULT_MIN_TASK_HEIGHT = 5;
	protected static final int DEFAULT_MAX_TASK_HEIGHT = 200;
	protected int minTasksHeight = DEFAULT_MIN_TASK_HEIGHT;
	protected int maxTasksHeight = DEFAULT_MAX_TASK_HEIGHT;
	protected int tasksHeight = DEFAULT_TASK_HEIGHT;
	
	
	protected static final int ANIMATE_RESIZE_TIME = 500;
	protected static final int ANIMATE_FADE_TIME = 1000;

	protected static final byte TASK_MARGIN = 2;

	/**
	 * 
	 * @param left Left position of the taskbar
	 * @param top Top position of the taskbar
	 * @param width Taskbar width
	 * @param height Taskbar height
	 */
	public TaskBar(int left, int top, int width, int height) {
		super();

		tasks = new Vector<Task>();

		mainEventsChain = new EventChain();

		initTaskBar(left, top, width, height);
//		hide();
	}

	/**
	 * Graphical element initialization
	 * @param left Left position of the taskbar
	 * @param top Top position of the taskbar
	 * @param width Taskbar width
	 * @param height Taskbar height
	 */
	protected void initTaskBar(int left, int top, int width, int height) {
		setLeft(left);
		setTop(top);
		super.setWidth(width);
		setHeight(height);
		computeTasksHeight(height);

		setStyleName("toolStrip");

		setBorder("1px solid gray");
	}

	
	public void registerWindow(final TaskBarWindow w) {
		Task task = new Task(w, tasksWidth, tasksHeight);

		w.addToTaskBar(this, task);
		try {
			addTask(task, w);
			task.enterFocusStatus();
		} catch (Exception e) {
			SC.warn(e.getMessage());
			// UtilityServlet.alert(e.getMessage());
			w.destroy();
			return;
		}
	}
	
	
	/**
	 * Adds a new Task to this taskbar.
	 * A general flow chart is constructed, block by block, by using Events and EventContainers
	 * This sequence of actions is general purpose and can be reused by inherited classes just overriding
	 * the functions called inside the events
	 * @param task The task to be added
	 * @param win The window that will be controlled by the task which is being added
	 */
	protected void addTask(final Task task, final TaskBarWindow win){
		
		task.hide();								//First hides the graphical element corresponding to the task
		tasks.add(task);							//...adds it to the internal array
		setTaskCoordinates(task);					//...and sets its initial coordinates

		//Now starts building the sequence of events that are going to be executed to correctly compute and
		//display the animations needed 

		//The exact sequence is:
		//1. 	Compute the new tasks width
		//2.a 	Add the task's graphical element to the taskbar, 
		//2.b	Update its taskbar sequence number
		//2.c	Animate move the task to its new position
		//3.	Show the task (graphical representation)
		//4.	Refresh the Taskbar (if any refresh action is needed - used by inherited classes)
		
		
		final Event computeNewTaskWidthEvent = mainEventsChain.addNewEvent();
		computeNewTaskWidthEvent.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				computeTasksWidth(getWidth());
			}
		});

		final Event addNewTaskEvent = mainEventsChain.addNewEvent();
		addNewTaskEvent.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				addChild(task);
				task.updatePositionInsideTaskbar(tasks.size() - 1);
				thisTaskBar.setTaskCoordinates(task);
			}
		});

		showTask(task, win, mainEventsChain);	//Adds an Event to the chain

		final Event refreshTaskBarEvent = mainEventsChain.addNewEvent();
		refreshTaskBarEvent.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				refreshTaskBar(refreshTaskBarEvent);
			}
		});

		//Once the sequence of event is set, their execution (in the right order) can start
		mainEventsChain.execute(false);

	}
	
	/**
	 * Removes a registered Window (ant its related task) from the TaskBar
	 * @param w The window that has to be removed
	 */
	public void removeWindow(TaskBarWindow w) {
		final Task task = w.getTask();
		removeTask(task);
	}

	/**
	 * Handles all the operations connected to the removal of a Task element from the TaskBar 
	 * A general flow chart is constructed, block by block, by using Events and EventContainers
	 * This sequence of actions is general purpose and can be reused by inherited classes just overriding
	 * the functions called inside the events
	 * 
	 * @param task
	 */
	private void removeTask(final Task task) {
		
		int i = getTaskPosition(task);		//Gets the position of the task inside the taskbar array

		if (i >= tasks.size() || i < 0) {	//Checks whether the task position is valid (i.e. if the task belongs to this taskbar)
			// UtilityServlet.alert("not found");
			return;
		}

		final int index = i;

		final Event removeTaskEvent = mainEventsChain.addNewEvent();
		removeTaskEvent.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				tasks.get(index).removeTask(
						removeTaskEvent.setExternalOnCompletionNotification(),
						mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
			}
		});

		final Event onTaskRemoved = mainEventsChain.addNewEvent();
		onTaskRemoved.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				// final int pos = getTaskPosition(task);
				removeChild(task);
				tasks.remove(task);
				computeTasksWidth(getWidth());
			}
		});

		final Event refreshTaskBarEvent = mainEventsChain.addNewEvent();
		refreshTaskBarEvent.setEventBody(new AnimationCallback() {

			@Override
			public void execute(boolean earlyFinish) {
				refreshTaskBar(refreshTaskBarEvent);
			}
		});

		mainEventsChain.execute(false);
	}


	/**
	 * Refreshes the taskBar appearance.
	 * This method does nothing in the base class, but may be redefined in any inherited class, in case it is 
	 * needed
	 * @param refreshTaskBarEvent The Event inside which this method must be executed 
	 */
	protected void refreshTaskBar(Event refreshTaskBarEvent) {
		// System.out.println("Refreshing");
	}

	/**
	 * Returns the position of the task in the taskbar's tasks array.
	 * This method makes use of the last known task position inside the taskbar to quicly retrieve its actual position
	 * 
	 * @param task The task whose position is needed
	 * @return
	 */
	protected int getTaskPosition(Task task) {
		int pos = Math.min(task.getLastKnownTaskPosition(), tasks.size() - 1);

		if (pos == Task.NO_POSITION) {
			pos = tasks.indexOf(task);
		} else {
			pos = tasks.lastIndexOf(task, pos);
			if (pos == Task.NO_POSITION) {
				pos = tasks.indexOf(task, pos);
			}
		}
		task.updatePositionInsideTaskbar(pos);
		return pos;
	}

	
	/**
	 * Sets the coordinates of the task on the screen
	 * @param task The task to be repositioned
	 */
	protected void setTaskCoordinates(final Task task) {
		int pos = getTaskPosition(task);
		task.setLeft(TASK_MARGIN + pos * (tasksWidth + TASK_MARGIN));
		task.setTop(TASK_MARGIN);
	}

	
	/**
	 * Sets the coordinates of the task on the screen
	 * @param task The task to be repositioned
	 * @param eventContainer The EventContainer to whom the newly generated Event has to be added
	 */
	protected void setTaskCoordinates(final Task task, EventContainer eventContainer) {
		int pos = getTaskPosition(task);

		final int newLeft = TASK_MARGIN + pos * (tasksWidth + TASK_MARGIN);
		if (newLeft == task.getLeft() ){
			//Nothing to do
			return;
		}
		final Event setTaskCoordinatesEvent = eventContainer.addNewEvent();
		setTaskCoordinatesEvent.setEventBody(
			new AnimationCallback() {
				@Override
				public void execute(boolean earlyFinish) {
					task.animateMove(newLeft, null, setTaskCoordinatesEvent.setExternalOnCompletionNotification(), mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
				}
			}
		); 
	}
	
	/**
	 * Handles the animations connected to the creation of a Task item and its relative Window
	 * @param t The Task item
	 * @param w The Window controlled by the Task
	 * @param eventContainer The EventContainer to whom the newly generated Event has to be added
	 */
	private void showTask(final Task t, final TaskBarWindow w, EventContainer eventContainer) {
		final Event showTaskEvent = eventContainer.addNewEvent();
		showTaskEvent.setEventBody(
			new AnimationCallback() {
				
				@Override
				public void execute(boolean earlyFinish) {
					w.centerInPage();
					w.setOpacity(95);
					w.animateShow(AnimationEffect.FADE,
							showTaskEvent.setExternalOnCompletionNotification(),
							mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
					t.animateShow(AnimationEffect.FADE,
							showTaskEvent.setExternalOnCompletionNotification(),
							mainEventsChain.scaleAnimationTime(ANIMATE_FADE_TIME));
					
				}
			}
		);
	}

	/**
	 * The int parameter version only can be used
	 */
	@Deprecated
	@Override
	public void setWidth(String width) {
	}

	/**
	 * Handles all the changes related to the taskbar resizing
	 */
	@Override
	public void setWidth(final int width) {
		int oldWidth;

		try {
			oldWidth = this.getHeight();

			if (width == oldWidth)
				return;

			if (width > oldWidth) { // => Has to enlarge the taskBar first

				final Event barResizeEvent = mainEventsChain.addNewEvent();
				barResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						animateResize(
								width,
								null,
								barResizeEvent
										.setExternalOnCompletionNotification(),
								mainEventsChain
										.scaleAnimationTime(ANIMATE_RESIZE_TIME));
					}
				});

				final Event tasksResizeEvent = mainEventsChain
						.addNewEventAfter(barResizeEvent);
				tasksResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						computeTasksWidth(width);
					}
				});

			} else {

				final Event barResizeEvent = mainEventsChain.addNewEvent();
				barResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						animateResize(
								width,
								null,
								barResizeEvent
										.setExternalOnCompletionNotification(),
								mainEventsChain
										.scaleAnimationTime(ANIMATE_RESIZE_TIME));

					}
				});

				final Event tasksResizeEvent = mainEventsChain.addNewEvent();
				tasksResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						computeTasksWidth(width);
					}
				});

			}

		} catch (Exception e) {
			// System.out.print("Exception ");
			super.setWidth(width);
			final Event tasksResizeEvent = mainEventsChain.addNewEvent();
			tasksResizeEvent.setEventBody(new AnimationCallback() {

				@Override
				public void execute(boolean earlyFinish) {
					computeTasksWidth(width);
				}
			});
		} finally {
			mainEventsChain.execute(false);
		}

	}
	
	
	/**
	 * The int parameter version only can be used
	 */
	@Deprecated
	@Override
	public void setHeight(String height) {
	}


	/**
	 * Handles all the changes related to the taskbar resizing
	 */
	@Override
	public void setHeight(final int height) {
		int oldHeight;

		try {
			oldHeight = this.getHeight();

			// System.out.println(oldHeight + " | " + height);

			if (height == oldHeight)
				return;

			if (height > oldHeight) { // => Has to enlarge the taskBar first

				final Event barResizeEvent = mainEventsChain.addNewEvent();
				barResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						// System.out.println("1 Resizing to " + height);
						animateResize(
								null,
								height,
								barResizeEvent
										.setExternalOnCompletionNotification(),
								mainEventsChain
										.scaleAnimationTime(ANIMATE_RESIZE_TIME));
					}
				});

				final Event tasksResizeEvent = mainEventsChain
						.addNewEventAfter(barResizeEvent);
				tasksResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						computeTasksHeight(height);
					}
				});

			} else {

				final Event tasksResizeEvent = mainEventsChain.addNewEvent();
				tasksResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						computeTasksHeight(height);
					}
				});

				final Event barResizeEvent = mainEventsChain.addNewEventAfter(tasksResizeEvent);
				barResizeEvent.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						animateResize(
								null,
								height,
								barResizeEvent
										.setExternalOnCompletionNotification(),
								mainEventsChain
										.scaleAnimationTime(ANIMATE_RESIZE_TIME));
					}
				});
			}

		} catch (Exception e) {
			super.setHeight(height);
			final Event tasksResizeEvent = mainEventsChain.addNewEvent();
			tasksResizeEvent.setEventBody(new AnimationCallback() {

				@Override
				public void execute(boolean earlyFinish) {
					computeTasksHeight(height);
				}
			});
		} finally {
			mainEventsChain.execute(false);
		}

	}
	
	/**
	 * Computes tasks ideal width to make them properly fit the Taskbar
	 * <b>WARNING:</b> This method should be called inside an Event object callback only
	 * <b>WARNING:</b> This method is likely to need to be overridden in inherited classes 
	 * @param taskBarWidth The taskbar width
	 */
	protected void computeTasksWidth(int taskBarWidth) {
		if (tasks.size() > 0) {
			final int oldTasksWidth = tasksWidth;
			tasksWidth = (taskBarWidth - (tasks.size() + 1) * TASK_MARGIN)	/ tasks.size();
			if ( tasksWidth < minTasksWidth ){
				SC.warn("Too many windows have been added to the Task Bar for its lenght:<br>The bar is going to be resized" );
				tasksWidth = oldTasksWidth;
				
			}else{
				tasksWidth = tasksWidth <= maxTasksWidth ? tasksWidth : maxTasksWidth; 
				updateTasksWidth(oldTasksWidth);
			}
		}
	}
	
	/**
	 * Handles all the operations connected to the change of the tasks width value
	 * <b>WARNING:</b> This method is likely to be overridden in inherited classes
	 * @param oldTasksWidth
	 */
	protected void updateTasksWidth(int oldTasksWidth) {

		if (tasksWidth != oldTasksWidth && tasks.size() > 0) {

			//Two cases must be distinguished:
			//
			//1.	The tasks must be enlarged: Then they must be first repositioned, and only after that resized
			//2. 	The tasks must be shrunk: Then they can be resized first
			
			if (tasksWidth > oldTasksWidth) { // =>Has to reposition the tasks first
				EventGroup tasksRepositioningEvent = new EventGroup();
				mainEventsChain.addNewEventAfterCurrent(tasksRepositioningEvent);

				for (int i = 0; i < tasks.size(); i++) {
					final int pos = i;
					setTaskCoordinates(tasks.get(pos), tasksRepositioningEvent);
				}

				final EventGroup onPositionSetEvent = new EventGroup();

				mainEventsChain.addNewEventAfter(onPositionSetEvent, tasksRepositioningEvent);
				for (int i = 0; i < tasks.size(); i++) {
					final int pos = i;
					final Event e = onPositionSetEvent.addNewEvent();
					e.setEventBody(new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(
											tasksWidth,
											null,
											e.setExternalOnCompletionNotification(),
											mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					});
				}

			} else { // moveBefore==false

				final EventGroup tasksUpdatingEvent = new EventGroup();
				mainEventsChain.addNewEventAfterCurrent(tasksUpdatingEvent);

				for (int i = 0; i < tasks.size(); i++) {
					final int pos = i;
					final Event taskResizeEvent = tasksUpdatingEvent.addNewEvent();

					taskResizeEvent.setEventBody(new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							tasks.get(pos).animateResize(
											tasksWidth,
											null,
											taskResizeEvent.setExternalOnCompletionNotification(),
											mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME));
						}
					});

					setTaskCoordinates(tasks.get(pos), tasksUpdatingEvent);

				}

			}
		} else {

			//If tasksWidth == oldTasksWidth, checks the tasks positions anyway
			
			EventGroup tasksResizingEvent = new EventGroup();
			mainEventsChain.addNewEventAfterCurrent(tasksResizingEvent);

			for (int i = 0; i < tasks.size(); i++) {
				final int pos = i;
				setTaskCoordinates(tasks.get(pos), tasksResizingEvent);	
			}
		}
	}
	
	/**
	 * Computes tasks ideal height to make them properly fit the Taskbar
	 * <b>WARNING:</b> This method is likely to need to be overridden in inherited classes 
	 * @param taskBarHeight The height of the Taskbar
	 */
	protected void computeTasksHeight(int taskBarHeight) {
		int oldTasksHeight = tasksHeight;
		tasksHeight = taskBarHeight - 3 * TASK_MARGIN - 1;

		if (tasksHeight == oldTasksHeight)
			return; // Nothing to do;

		
		if ( tasksHeight < minTasksHeight){
			SC.warn("Insufficient Taskbar height:<br>The Taskbar is going to be resized" );
			tasksHeight = oldTasksHeight;
		}else{
			if ( tasksHeight > maxTasksHeight){
				tasksHeight = maxTasksHeight;
			}
			updateTasksHeight();
		}
	}


	/**
	 * Handles all the operations connected to the change of the tasks height value
	 * <b>WARNING:</b> This method is likely to be overridden in inherited classes
	 */	
	private void updateTasksHeight() {

		if (tasks.size() > 0) {

			EventGroup onBarResizedEvent = new EventGroup();
			mainEventsChain.addNewEventAfterCurrent(onBarResizedEvent);
			for (int i = 0; i < tasks.size(); i++) {
				final int pos = i;
				final Event e = onBarResizedEvent.addNewEventAfterCurrent();
				e.setEventBody(new AnimationCallback() {

					@Override
					public void execute(boolean earlyFinish) {
						tasks.get(pos).animateResize(
										null,
										tasksHeight,
										e.setExternalOnCompletionNotification(),
										mainEventsChain.scaleAnimationTime(ANIMATE_RESIZE_TIME)
										);
					}
				});
			}

		}
	}	
	

	/**
	 * Sets the maximum value, in pixel, for a single Task item's width.
	 * The value stored will be the greater one between the parameter passed and the minimum tasks width value
	 * After assigning the new value checks whether the current value for tasks width is greater than the new
	 * maximum value itself, and if it is so compute a new value for tasks' width 
	 * @param newMaxtasksWidth The value to be set
	 */
	public void setMaxTasksWidth(int newMaxtasksWidth) {
		maxTasksWidth = Math.max(minTasksWidth, newMaxtasksWidth);
		if ( tasksWidth > maxTasksWidth ){
			computeTasksWidth(getWidth());
		}
	}
	
	/**
	 * Sets the maximum value, in pixel, for a single Task item's height.
	 * The value stored will be the greater one between the parameter passed and the minimum tasks height value
	 * After assigning the new value checks whether the current value for tasks height is greater than the new
	 * maximum value itself, and if it is so compute a new value for tasks' height
	 * @param newMaxTasksHeight The value to be set
	 */
	public void setMaxTasksHeight(int newMaxTasksHeight) {
		maxTasksHeight = Math.max(minTasksHeight, newMaxTasksHeight);
		if ( newMaxTasksHeight > maxTasksHeight ){
			computeTasksHeight(getHeight());
		}		
	}

	/**
	 * Sets the minimum value, in pixel, for a single Task item's width.
	 * The value stored will be the smaller one between the parameter passed and the maximum tasks width value
	 * After assigning the new value checks whether the current value for tasks width is smaller than the new
	 * minimum value itself, and if it is so compute a new value for tasks' width 
	 * @param newMinTasksWidth The value to be set
	 */
	public void setMinTasksWidth(int newMinTasksWidth) {
		minTasksWidth = Math.min(maxTasksWidth, newMinTasksWidth);
		if ( newMinTasksWidth < minTasksWidth ){
			computeTasksWidth(getWidth());
		}		
	}	

	
	/**
	 * Sets the minimum value, in pixel, for a single Task item's height.
	 * The value stored will be the smaller one between the parameter passed and the maximum tasks height value
	 * After assigning the new value checks whether the current value for tasks height is smaller than the new
	 * minimum value itself, and if it is so compute a new value for tasks' width 
	 * @param newMinTasksHeight The value to be set
	 */
	public void setMinTasksHeight(int newMinTasksHeight) {
		minTasksHeight = Math.min(maxTasksHeight, newMinTasksHeight);
		if ( newMinTasksHeight < minTasksHeight){
			computeTasksHeight(getHeight());
		}		
	}	
	
	
	
	
	
	/**
	 * 
	 * @return The maximum value that can be assigned to the tasks width
	 */
	public int getMaxTasksWidth() {
		return maxTasksWidth;

	}
	
	/**
	 * 
	 * @return The maximum value that can be assigned to the tasks height
	 */
	public int getMaxTasksHeight() {
		return maxTasksHeight;		
	}


	/**
	 * 
	 * @return The minimum value that can be assigned to the tasks width
	 */
	public int getMinTasksWidth() {
		return minTasksWidth;	
	}
	

	/**
	 * 
	 * @return The minimum value that can be assigned to the tasks height
	 */
	public int getMinTasksHeight() {
		return minTasksHeight;
	}	
	
	/**
	 * A single task's height
	 */
	public int getTasksHeight(){
		return tasksHeight;
	}
	
	
	/**
	 * When called marks the parameter window as "selected" and bring the previously selected window-s task
	 * to its normal appearance
	 * @param win The window to be selected
	 */
	public void markWindowAsSelected(TaskBarWindow win){
		if (selectedWindow != null && !selectedWindow.equals(win) ){
			selectedWindow.getTask().enterNormalStatus();
		}
		selectedWindow = win;
		
	}
	
	/**
	 * Invoked to tell the TaskBar that the parameter window doen't have to be considered "selected" any more.
	 * The deselection operation is successful iff the currently selected window matches the parameter
	 * @param win The window to be deselected
	 */
	public void unmarkWindowAsSelected(TaskBarWindow win){
		if (selectedWindow != null && selectedWindow.equals(win) ){
			selectedWindow = null;
		}		
	}
}
