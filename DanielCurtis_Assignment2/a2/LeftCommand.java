package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LeftCommand extends AbstractAction {
	
private Camera cam;
	
	LeftCommand(Camera c)	
	{
		this.cam = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.moveLeft();
		
	}

}
