package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ToggleAxesCommand extends AbstractAction {

	private Starter starter;
	
	ToggleAxesCommand(Starter st)	
	{
		this.starter = st;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		starter.axes();
		
	}

}