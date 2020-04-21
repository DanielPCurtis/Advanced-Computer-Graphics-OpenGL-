package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightDownCommand extends AbstractAction{

private Light light;
	
	LightDownCommand(Light l)	
	{
		this.light = l;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.moveDown();
		
	}

}