package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LightToggleCommand extends AbstractAction{

private Starter cd;
	
	LightToggleCommand(Starter starter)	
	{
		this.cd = starter ;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		cd.lock();
		
	}

}