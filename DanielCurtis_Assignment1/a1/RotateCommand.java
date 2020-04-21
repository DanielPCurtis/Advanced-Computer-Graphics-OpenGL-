package a1;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class RotateCommand extends AbstractAction
{
	private Starter st;
	
	RotateCommand(Starter start)
	{	
		this.st = start;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt)	
	{
		st.RotatorToggle();
	}
}
