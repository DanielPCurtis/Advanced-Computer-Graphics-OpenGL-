package a3;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class ForwardCommand extends AbstractAction {

private Camera cam;
	
	ForwardCommand(Camera c)	
	{
		this.cam = c;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cam.moveForward();
		
	}

}
