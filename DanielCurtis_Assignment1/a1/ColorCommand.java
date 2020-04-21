//	   Daniel Curtis	//
//	   Assignment 1 	//
//	   CSC 155			//
//	   02/12/2020		//

package a1;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

public class ColorCommand extends AbstractAction
{
	private Starter st;
	
	ColorCommand(Starter start)	
	{
		this.st = start;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt)	
	{
		st.ColorToggle();
	}
}
