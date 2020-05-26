package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class DownCommand extends AbstractAction{

	private Camera cam;
	
	DownCommand(Camera c)	
	{
		this.cam = c;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.moveDown();
	}

}
