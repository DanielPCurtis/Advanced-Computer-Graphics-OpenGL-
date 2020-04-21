package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchRightCommand extends AbstractAction{

	private Camera cam;
	
	PitchRightCommand(Camera c){
		this.cam = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.pitchDown();
	}
	
}
