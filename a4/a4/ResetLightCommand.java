package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ResetLightCommand extends AbstractAction{

private Light light;
	
	ResetLightCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.resetLight();
		
	}

}
