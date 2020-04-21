package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightFrontCommand extends AbstractAction{

private Light light;
	
	LightFrontCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveFront();
		
	}

}