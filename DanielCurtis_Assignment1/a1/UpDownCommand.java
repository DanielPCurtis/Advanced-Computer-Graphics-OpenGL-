package a1;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;


public class UpDownCommand extends AbstractAction
{
	private Starter st;
	
	UpDownCommand(Starter start)	
	{
		this.st = start;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) 
	{
		st.UpDownToggle();
	}

}
