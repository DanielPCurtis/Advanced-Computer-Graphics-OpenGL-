package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightLeftCommand extends AbstractAction{

private Light light;
	
	LightLeftCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveLeft();
		
	}

}
