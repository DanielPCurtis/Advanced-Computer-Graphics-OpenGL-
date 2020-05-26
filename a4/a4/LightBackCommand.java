package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightBackCommand extends AbstractAction{

private Light light;
	
	LightBackCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveBack();
		
	}

}