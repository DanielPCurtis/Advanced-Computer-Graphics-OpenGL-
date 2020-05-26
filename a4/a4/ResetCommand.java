package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ResetCommand extends AbstractAction{

	private Camera cam;
	
	ResetCommand(Camera c)	{
		this.cam = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.reset();
	}

}
