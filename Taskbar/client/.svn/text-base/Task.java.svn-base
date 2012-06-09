package com.smartgwt.extensions.taskbar.client;


import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Tasks are the buttons contained in the TaskBar.
 * Each Task in connected to a TaskBarWindow (and viceversa) and allow to minimize/restore it.
 * 
 * @author Marcello La Rocca (marcellolarocca@gmail.com) 
 *
 */
public class Task extends ToolStripButton implements ClickHandler {
	
	/**
	 *	The TaskBarWindow connected to this Task 
	 */
	private TaskBarWindow window;
	
	
	private static final int STATUS_NORMAL = 0;
	private static final int STATUS_HAS_FOCUS = 1;
	private static final int STATUS_MINIMIZED = 2;
	private int status = STATUS_NORMAL;
	
  
    private static final int HOVER_WIDTH = 250;
    private static final int HOVER_OPACITY = 80;
    private int taskbarPosition = NO_POSITION;

    private static final String HOVER_TITLE_COLOR = "#f50000;";
    private static final String HOVER_TITLE_BGCOLOR = "#ffd937;";
    private static final String HOVER_BODY_BGCOLOR = "#fff69b;";
    private static final String HOVER_BORDER = "2px solid #ffaf03;";
    private static final String HOVER_FONT ="arial, verdana, serif;";

    private static final String HOVER_TITLE_PREAMBLE = "<div style='width:100%; background-color: "+HOVER_TITLE_BGCOLOR+" border: " + HOVER_BORDER + " border-bottom-width: 0px;'><center><b style='color:"+HOVER_TITLE_COLOR+" font-family: "+HOVER_FONT+"; font-size: 14px;'>";
    private static final String HOVER_TITLE_CLOSURE = "</b></center></div>";

    private static final String HOVER_BODY_PREAMBLE = "<div style='width:100%; background-color: "+HOVER_BODY_BGCOLOR+ " border: "+ HOVER_BORDER + "'><br><center><div style='padding:2px; font-family: "+HOVER_FONT+" font-size: 11px;'>";
    private static final String HOVER_BODY_CLOSURE = "</div></center></div>";
    
    private static final String HOVER_BODY_TEXT = "<b>Click</b> to Minimize/Restore";
    
    /**
     * Constant value for an invalid position in arrays
     */
	public static final int NO_POSITION = -1;
	
   /* 
    	@Override
    	protected native JavaScriptObject create()/*-{
	       var config = this.@com.smartgwt.client.widgets.BaseWidget::getConfig()();
	       var widget = $wnd.isc.Task.create(config);
	       this.@com.smartgwt.client.widgets.BaseWidget::doInit()();
	       return widget;
	   	}-* /; */
    
	/**
	 * @param win The TaskBarWindow that must be connected to this Task
	 * @param width Task width
	 * @param width Task height
	 */
	public Task(TaskBarWindow win, int width, int height) {
		
		window = win;
		this.setAutoFit(false);
		this.setTitle(win.getTitle());
		this.setWidth(width);
		this.setHeight(height);
        
		setActionType(SelectionType.CHECKBOX);
        //setRadioGroup(TaskBar.getButtonGroup());
		addClickHandler(this);
	
		this.setBaseStyle("buttonTitle");	//Change if you want a different appearance
		
		this.setTaskPrompt(win.getTitle(), HOVER_BODY_TEXT);	//Sets a Tooltip prompt to explain options
		
	}
	
	/**
	 * Restores the Task button on the TaskBar (as the corresponding window is restored)
	 */
	public void enterNormalStatus(){
		this.setTitle(window.getTitle());
//		this.setBorder("none");
		setOpacity(100);
		setSelected(true);
		status = STATUS_NORMAL;
		window.setOnBottom();
	}
	
	/**
	 * Minimizes the Task button on the TaskBar (as the corresponding window is minimized)
	 */
	public void enterMinimizedStatus(){
		this.setTitle(window.getTitle());
//		this.setBorder("none");
		setOpacity(75);
		setSelected(false);
		status = STATUS_MINIMIZED;

		if ( !window.getMinimized() ){
			window.minimizeWindow();		
		}
	}
	
	/**
	 * Minimizes the Task button on the TaskBar (as the corresponding window is minimized)
	 */
	public void enterFocusStatus(){
		this.setTitle("<b style='color:red'>"+window.getTitle()+"</b>");
//		this.setBorder("1px solid red");
		setOpacity(100);
		setSelected(true);
		status = STATUS_HAS_FOCUS;
		
		if ( window.getMinimized() ){
			window.restoreWindow();
		}
		
		window.setOnTop();
		
	}
	
	/**
	 * Groups all the actions, including the animation, to be taken when a task is removed from a TaskBar 
	 * @param onCompletion The callback to be called after the animation is completed
	 * @param animationTime The duration of the animation
	 */
	public void removeTask(AnimationCallback onCompletion, int animationTime){
		this.animateHide(AnimationEffect.FADE, onCompletion, animationTime);
	}
	
	/*
	 * Utility to set a styled titled version of the tooltip prompt 
	 * @param title A title for the prompt box
	 * @param body The main message of the tooltip
	 */
    private void setTaskPrompt(String title, String body){
        this.setHoverWidth(HOVER_WIDTH);
        this.setHoverMoveWithMouse(Boolean.TRUE);
        this.setHoverStyle("padding:0px;");
        super.setPrompt(HOVER_TITLE_PREAMBLE + title+ HOVER_TITLE_CLOSURE + HOVER_BODY_PREAMBLE + body + HOVER_BODY_CLOSURE);
        this.setHoverOpacity(HOVER_OPACITY);
    }
    
    /** Prevents the user to call the base class method: the prompt must not be outside this class
     */
   @Deprecated
   @Override
   public void setPrompt(String text){}
   
   
    /**
     * 
     * @return The TaskBarWindow connected to the Task
     */
    public TaskBarWindow getWindow(){
    	return window;
    }
    
    /**
     * @return 	The last known position of the Task in its TaskBar. It is not guaranteed that the value
     * 			returned is accurate, but it is a way to tune the search for the actual position
     */
    public int getLastKnownTaskPosition(){
    	return taskbarPosition;
    }
    
    /**
     * Update the last known position in its TaskBar for this Task.
     * This method should be called inside the taskbar that owns the Task each time a new value is assigned
     * to the position of a specific task
     * @param pos The position held by the Task
     */
    public void updatePositionInsideTaskbar(int pos){
    	taskbarPosition = pos;
    }
	
    /**
     * 
     */
	@Override
	public void onClick(ClickEvent event) {
		switch (status){
		case STATUS_NORMAL:
			//=> selected
			enterFocusStatus();
			break;
		case STATUS_HAS_FOCUS:
			enterMinimizedStatus();
			//=> minimized
			break;		
		case STATUS_MINIMIZED:
			//=> selected
			enterFocusStatus();
			break;			
		}

		
	}
	
	
	
}
