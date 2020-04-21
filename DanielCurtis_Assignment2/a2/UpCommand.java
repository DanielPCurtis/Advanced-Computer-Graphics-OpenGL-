package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpCommand extends AbstractAction{

private Camera cam;
	
	UpCommand(Camera c)	
	{
		this.cam = c;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.moveUp();
	}

}
