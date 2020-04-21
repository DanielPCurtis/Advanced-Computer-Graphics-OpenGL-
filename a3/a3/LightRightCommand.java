package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightRightCommand extends AbstractAction{

private Light light;
	
	LightRightCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveRight();
		
	}

}
