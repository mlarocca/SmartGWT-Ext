package com.smartgwt.extensions.taskbar.client;

import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.DragRepositionStopEvent;
import com.smartgwt.client.widgets.events.DragRepositionStopHandler;
import com.smartgwt.client.widgets.events.MinimizeClickEvent;
import com.smartgwt.client.widgets.events.MinimizeClickHandler;

/**
 * Generic SmartGWT Window that can be added to a TaskBar 
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 *
 */
public abstract class TaskBarWindow extends Window {
	
	private int oldLeft = 0;
	private int oldTop = 0; 
	private int oldWidth = 0;
	private int oldHeight = 0;
	
	private boolean isOnTop;

	/**
	 * Reference to the Task connected to this Window 
	 */
	private Task task = null;

	/**
	 * Reference to the TaskBar who owns the Window
	 */
	private TaskBar taskBar = null;
	
    private static final int ANIMATION_TIME = 500;
    
//    private static final String WARNING_TEXT_REGISTRATION = "Impossibile aggiungere la finestra";
    
//    private static final String WARNING_TEXT_TASKBAR = "Errore: impossibile ripristinare la finestra";
	
    /**
     * @param title The Window title
     */
	public TaskBarWindow(String title ) {
		final TaskBarWindow thisWindow = this;
		this.setTitle(title);

        this.addMinimizeClickHandler(new MinimizeClickHandler() {
            public void onMinimizeClick(MinimizeClickEvent event) {
            	//Can't just minimize the window: control goes to the connected task
            	event.cancel();
            	task.enterMinimizedStatus();
            	minimizeWindow();              
            }
        } );
        
        this.addCloseClickHandler( new CloseClickHandler() {
			
			@Override
			public void onCloseClick(CloseClientEvent event) {
				//Must remove the window from the TaskBar to whom it's registered, if any
				if (taskBar!=null){
					taskBar.removeWindow(thisWindow);			
				}
				thisWindow.destroy();			
			}
		});
		
        this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkOnTop();
			}
		});
        
        this.addDragRepositionStopHandler(
    		new DragRepositionStopHandler() {
				
				@Override
				public void onDragRepositionStop(DragRepositionStopEvent event) {
					checkOnTop();						
				}
			}
        );

	}
	
	/**
	 * Steps in when a TaskBarWindow needs to be minimized	
	 */
	public void minimizeWindow(){
        oldLeft = getLeft();
        oldTop = getTop();
        oldWidth = getWidth();
        oldHeight = getHeight();
        Task task = getTask();
        
		taskBar.unmarkWindowAsSelected(this);
        
		animateMove(task.getAbsoluteLeft(), task.getAbsoluteTop(), 

        		new AnimationCallback() {
					
					@Override
					public void execute(boolean earlyFinish) {
						//This is crucial because the test in Task.onDoubleClick is on window.getMinimized();
						minimize();
					}
				}
			
        		, ANIMATION_TIME );
        
        animateResize(task.getWidth(), task.getHeight());
        animateHide(AnimationEffect.FADE, null, ANIMATION_TIME);
	}

	/**
	 * Steps in when a TaskBarWindow needs to be restored
	 */
	public void restoreWindow(){
		//Need to restore because it's been minimized before
		restore();
		this.animateMove( oldLeft, oldTop, null, ANIMATION_TIME );
		this.animateResize(oldWidth, oldHeight );
		this.animateShow(AnimationEffect.FADE, null, ANIMATION_TIME);
	
		this.restore();
	}
	

	/**
	/**
	 * Sets the connections between this Window, the TaskBar which owns it and the Task that minimize/restore the Window 
	 * 
	 * @param ownerTaskBar 	The TaskBar to whom this Window is added
	 * @param connectedTask	The corresponding Task inside the TaskBar
	 */
	public void addToTaskBar(TaskBar ownerTaskBar, Task connectedTask){
		taskBar = ownerTaskBar;
		task = connectedTask;		
	}

	/**
	 * 
	 * @return the Task connected to this Window
	 */
	public Task getTask(){
		return task;
	}

	/**
	 * Acknowledge that the window has been brought on top and notify the related task
	 * 
	 */
	protected void checkOnTop(){
		if ( !isOnTop ){
			isOnTop = true;
			getTask().enterFocusStatus();
		}		
	}
	
	/**
	 * 
	 * @return Whether or not this window is on top (among all other windows in the same taskbar)
	 */
	public boolean isOnTop() {
		return isOnTop;
	}

	/**
	 * Brings the current window to front; also notifies the taskbar containing this
	 * window that this one is selected;
	 * 
	 */
	public void setOnTop() {
		bringToFront();
		isOnTop = true;
		taskBar.markWindowAsSelected(this);
	}
	
	/**
	 * Acknowledge that this window is not on Top anymore
	 * 
	 */
	public void setOnBottom() {
		isOnTop = false;
	}
}
