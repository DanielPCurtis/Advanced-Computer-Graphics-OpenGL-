package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class BackwardCommand extends AbstractAction{

private Camera cam;
	
	BackwardCommand(Camera c)	
	{
		this.cam = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		cam.moveBackward();
		
	}

}
