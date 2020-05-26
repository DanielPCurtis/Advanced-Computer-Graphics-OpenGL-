package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchLeftCommand extends AbstractAction{

	private Camera cam;
	
	PitchLeftCommand(Camera c){
		this.cam = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		cam.pitchUp();
	}

}
