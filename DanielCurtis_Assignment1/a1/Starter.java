//	   Daniel Curtis	//
//	   Assignment 1 	//
//	   CSC 155			//
//	   02/12/2020		//
//     Test success		//
//     On PONG in       //
//     RVR 5029			//

package a1;


import java.awt.BorderLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;

public class Starter extends JFrame implements GLEventListener, MouseWheelListener
{	private GLCanvas myCanvas;
	private GL4 gl;
	private int renderingProgram;
	private int vao[] = new int[1];
	
	//Coordinate Variables
	private float x = 0.0f;
	private float inc = 0.01f;
	private float incx = 0.0f;
	private float incy = 0.0f;
	private float size = 1.0f;
	private float totalAngle = 0.0f;
	
	//My flags
	private float rotate=0;
	private float upDown=0;
	private float color=0;
	
	//Command Declarations
	private RotateCommand rotateCommand;
	private UpDownCommand upDownCommand;  
	private ColorCommand colorCommand;

	//Button Declarations
	private JButton rotateButton;
	private JButton upDownButton;
	private JButton colorButton;
	
	int offsetRotate;
	int offsetAngley;
	int offsetAnglex;
	double currAngle;
	
	public Starter()
	{	//Add a Panel to the Content Pane
		JPanel topPanel = new JPanel();
		this.add(topPanel, BorderLayout.NORTH);
		
		//Add Rotate Command to button, Add button to Content Pane
		rotateButton = new JButton("Rotate");
		rotateCommand = new RotateCommand(this);
		rotateButton.addActionListener(rotateCommand);
		topPanel.add(rotateButton);
		
		//Add UpDown Command to button, Add button to Content Pane
		upDownCommand = new UpDownCommand(this);
		upDownButton = new JButton("Up/Down");
		upDownButton.addActionListener(upDownCommand);
		topPanel.add(upDownButton);
		
		//Add Color Command to button, Add button to Content Pane
		colorButton = new JButton("Change Color");
		colorCommand = new ColorCommand(this);
		colorButton.addActionListener(colorCommand);
		topPanel.add(colorButton);
		
		//Add a Listener for the Mouse Wheel
		this.addMouseWheelListener(this);
		
		// get the content pane of the JFrame (this)
		JComponent contentPane = (JComponent) this.getContentPane();
		// get the "focus is in the window" input map for the content pane
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		// create a keystroke object to represent the "c" key
		KeyStroke cKey = KeyStroke.getKeyStroke('c');
		// create a keystroke object to represent the "r" key
		KeyStroke rKey = KeyStroke.getKeyStroke('r');
		// put the "cKey" keystroke object into the content pane’s "when focus is
		// in the window" input map under the identifier name "color“
		imap.put(cKey, "color");
		imap.put(rKey, "rotate");
		// get the action map for the content pane
		ActionMap amap = contentPane.getActionMap();
		// put the "myCommand" command object into the content pane's action map
		amap.put("color", colorCommand);
		amap.put("rotate", rotateCommand);
		//have the JFrame request keyboard focus
		this.requestFocus();
		
		setTitle("a1"); 
		setSize(800, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}
	
	public void display(GLAutoDrawable drawable)
	{	gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);
		
		Color();
		Size();
		Rotate(); 
		UpDown(); 
		
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}

	public void init(GLAutoDrawable drawable)
	{	gl = (GL4) GLContext.getCurrentGL();
		System.out.println("Running OpenGL Version: " + gl.glGetString(GL.GL_VERSION));
		System.out.println("Running JOGL Version: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
		System.out.println("Running Java Version: " + System.getProperty("java.version"));
		renderingProgram = Utils.createShaderProgram("a1/vertShader.glsl", "a1/fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}
	
	/*
	 * Toggle for rotate function
	 */
	public void RotatorToggle()	
	{	//change the current value of rotate
		if(rotate == 0)	{ rotate=1; upDown=0; }
		else			{ rotate=0; }
	}
	
	/*
	 * Toggle for updown function
	 */
	public void UpDownToggle()
	{	//change the current value of upDown
		if(upDown == 0)	{ upDown=1; rotate=0; }
		else			{ upDown=0; }
	}
	
	/*
	 * Toggle for color function
	 */
	public void ColorToggle()	
	{	//change the current value of color
		if(color == 0)	{ color=1; }
		else			{ color=0; }
	}
	
	@Override
	public void mouseWheelMoved (MouseWheelEvent e)	
	{
		if(e.getWheelRotation() > 0  && size < 5.0f)		{ size += 0.1f; }
		else if (e.getWheelRotation() < 0 && size > 0.01f)	{ size -= 0.1f; }
	}
	
	/*
	 * Updates vertShader with the current value of the color flag
	 */
	public void Color() 
	{
		int offsetColor = gl.glGetUniformLocation(renderingProgram, "color");
		gl.glProgramUniform1f(renderingProgram, offsetColor, color);
	}
	
	/*
	 * Updates vertShader with the current value of the rotate flag
	 * Calculates with current angle and updates vertShader with the new x and y coordinates
	 */
	public void Rotate() 
	{
		offsetRotate = gl.glGetUniformLocation(renderingProgram, "rotate");
		gl.glProgramUniform1f(renderingProgram, offsetRotate, rotate);
		
		currAngle = Math.toRadians(totalAngle);
		incx = (float) (0.5 * (Math.cos(currAngle)));
		incy = (float) (0.5 * (Math.sin(currAngle)));
		totalAngle += 5.0f;

		offsetAnglex = gl.glGetUniformLocation(renderingProgram, "incx");
		gl.glProgramUniform1f(renderingProgram, offsetAnglex, incx);
		offsetAngley = gl.glGetUniformLocation(renderingProgram, "incy");
		gl.glProgramUniform1f(renderingProgram, offsetAngley, incy);
	}

	/*
	 * Updates vertShader with the current value of upDown flag
	 * Calculates new y-coordinate and updates vertShader
	 */
	public void UpDown() 
	{
		int offsetUpDown = gl.glGetUniformLocation(renderingProgram, "upDown");
		gl.glProgramUniform1f(renderingProgram, offsetUpDown, upDown);
		
		x += inc;
		if (x > 1.0f) inc = -0.01f;
		if (x < -1.0f) inc = 0.01f;
		
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "inc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
	}
	
	/*
	 * Updates vertShader with the current size value
	 */
	public void Size()
	{
		int offsetSize = gl.glGetUniformLocation(renderingProgram, "size");
		gl.glProgramUniform1f(renderingProgram, offsetSize, size);
	}

}
