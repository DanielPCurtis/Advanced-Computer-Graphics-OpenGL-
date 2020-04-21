package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightUpCommand extends AbstractAction{

private Light light;
	
	LightUpCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveUp();
		
	}

}