package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightResetCommand extends AbstractAction{

private Light light;
	
	LightResetCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.resetLight();
		
	}

}