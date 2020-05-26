package a4;

import java.nio.*;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.Texture;

import com.jogamp.common.nio.Buffers;
import org.joml.*;

public class Starter extends JFrame implements GLEventListener, MouseMotionListener, MouseWheelListener
{	private GLCanvas myCanvas;
	private int renderingProgram1, renderingProgram2, renderingProgram3, renderingProgram4, renderingProgram5,
				renderingProgram6, renderingProgram7, renderingProgram8, renderingProgram9,
				renderingProgram10, renderingProgramCubeMap;
	private int vao[] = new int[1];
	private int vbo[] = new int[24];

	// model stuff
	private Torus myTorus;
	//Vertices variables
	private int numDolphinVertices, numBreadVertices, numSphereVertices, numSphereIndices;	

	// location of torus, pyramid, dolphin, and bread
	private Vector3f torusLoc = new Vector3f(-2.5f, 1.7f, 0.8f); // 3dtexture dolphin location
	private Vector3f pyrLoc   = new Vector3f(-6.6f, 1.1f, 0.1f);  //couch cushion sphere location
	private Vector3f dolLoc	  = new Vector3f(-1.0f, 1.0f, 0.9f);   //
	private Vector3f brdLoc	  = new Vector3f(4.0f, 1.0f, 0.0f);		//bread location
	private Vector3f pyr2Loc  = new Vector3f(14.6f, 4.1f, 0.1f);		//fuzzy dolphin location
	private Vector3f sphLoc   = new Vector3f(-3.6f, 4.0f, 0.0f);	
	private Vector3f dol2Loc  = new Vector3f(2.8f, 6.0f, -0.4f); 	//reflective dolphin
	private Vector3f floorLoc = new Vector3f(0.0f, 0.0f, 0.0f);   	//floor pattern location
	//private Vector3f terLoc = new Vector3f (0.0f, -9.0f, 0.4f);
	// My material stuff
	//Jade implemented from the table in the book
	private float[] jadeAmbient()  { return (new float [] {0.135f,  0.2225f, 0.1575f, 0.95f} ); }
	private float[] jadeDiffuse()  { return (new float [] {0.54f,  0.089f, 0.63f, 0.95f} ); }
	private float[] jadeSpecular() { return (new float [] {0.3162f,  0.3162f, 0.3162f, 0.95f} ); }
	private float jadeShininess()  { return 12.8f; }
	//Pearl implemented from the table in the book
	private float[] pearlAmbient()  { return (new float [] {0.25f,  0.20725f, 0.20725f, 0.922f} ); }
	private float[] pearlDiffuse()  { return (new float [] {1.00f,  0.829f, 0.829f, 0.922f} ); }
	private float[] pearlSpecular() { return (new float [] {0.2966f,  0.2966f, 0.2966f, 0.922f} ); }
	private float pearlShininess()  { return 11.264f; }
	
	// white light properties
	private float[] globalAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] lightAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	//mySpotLight - puts a red Tint on objects
	private float[] globalSpot = new float[] { 0.7f, 0.7f, 0.7f, 1.0f };
	private float[] spotLightAmbient = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] spotLightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] spotLightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	
	// bronze material
	private float[] BmatAmb = Utils.bronzeAmbient();
	private float[] BmatDif = Utils.bronzeDiffuse();
	private float[] BmatSpe = Utils.bronzeSpecular();
	private float BmatShi = Utils.bronzeShininess();
	
	// silver material
		private float[] SmatAmb = Utils.bronzeAmbient();
		private float[] SmatDif = Utils.bronzeDiffuse();
		private float[] SmatSpe = Utils.bronzeSpecular();
		private float SmatShi = Utils.bronzeShininess();

	// jade material
	private float[] JmatAmb = jadeAmbient();
	private float[] JmatDif = jadeDiffuse();
	private float[] JmatSpe = jadeSpecular();
	private float JmatShi = jadeShininess();
	// pearl material
	private float[] PmatAmb = pearlAmbient();
	private float[] PmatDif = pearlDiffuse();
	private float[] PmatSpe = pearlSpecular();
	private float PmatShi = pearlShininess();
	
	// variables to hold material and light float values
	private float[] thisAmb, thisDif, thisSpe, matAmb, matDif, matSpe;
	private float thisShi, matShi;
	
	// shadow stuff
	private int scSizeX, scSizeY;
	private int [] shadowTex = new int[1];
	private int [] shadowBuffer = new int[1];
	private Matrix4f lightVmat = new Matrix4f();
	private Matrix4f lightPmat = new Matrix4f();
	private Matrix4f shadowMVP1 = new Matrix4f();
	private Matrix4f shadowMVP2 = new Matrix4f();
	private Matrix4f b = new Matrix4f();

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private Matrix4f mvpMat = new Matrix4f(); // model-view matrix
	private int mvLoc, projLoc, nLoc, sLoc, vLoc, mvpLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private int globalSpotLoc;
	private int spot2Loc, diff2Loc, spec2Loc, pos2Loc;  
	private float aspect;
	private Vector3f currentLightPos = new Vector3f();
	private Vector3f currentSpotLightPos = new Vector3f();
	private float[] lightPos = new float[3];
	private float[] spotLightPos = new float[3];
	private Vector3f origin = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	
	//boolean variables for my lightSwitch and my axes toggle
	boolean posLock = true;
	boolean axes = true;
	
	//The 20 Commandments
	//Do not covet thy neighbors camera
	ForwardCommand forwardCommand;
	BackwardCommand backwardCommand;
	UpCommand upCommand;
	DownCommand downCommand;
	LeftCommand leftCommand;
	RightCommand rightCommand;
	ResetCommand resetCommand;
	PitchLeftCommand pitchLeftCommand;
	PitchRightCommand pitchRightCommand;
	PanUpCommand panUpCommand;
	PanDownCommand panDownCommand;
	
	//Light controls 
	LightLeftCommand lightLeftCommand;
	LightRightCommand lightRightCommand;
	LightUpCommand lightUpCommand;
	LightDownCommand lightDownCommand;
	LightBackCommand lightBackCommand;
	LightFrontCommand lightFrontCommand;
	LightResetCommand lightResetCommand;
	LightToggleCommand	lightToggleCommand;
	
	//Axes control
	ToggleAxesCommand axesCommand;
	
	//Camera coordinates
	Camera myCam = new Camera(0.0f, 3.0f, 30.0f);
	
	//Light coordinates
	Light myLight = new Light(-3.9f, 12.0f, 3.1f);
	
	//Global Light coordinates
	private Vector3f initialLightLoc = new Vector3f(-3.8f, 18.2f, 0.9f);
	
	private int skyboxTexture;
	private int dolTexture;
	private int sandTexture;
	
	private int moonNormalMap;
	private int moonTexture;
	private float rotY = 0.5f;
	private float myRotate = 0.5f;
	
	private int squareMoonTexture;
	private int squareMoonHeight;
	private int squareMoonNormalMap;
	
	private float floorLocX = 0.0f, floorLocY = -6.0f, floorLocZ = 0.0f;
	//private float rotY = -2.5f;
	
	//3dTexture stuff
	private int stripeTexture;
	private int texHeight= 200;
	private int texWidth = 200;
	private int texDepth = 200;
	private double[][][] tex3Dpattern = new double[texHeight][texWidth][texDepth];

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public Starter()
	{	
		JComponent contentPane = (JComponent) getContentPane();
		//where initialization occurs:
		forwardCommand = new ForwardCommand(myCam);
		backwardCommand = new BackwardCommand(myCam);
		upCommand = new UpCommand(myCam);
		downCommand = new DownCommand(myCam);
		leftCommand = new LeftCommand(myCam);
		rightCommand = new RightCommand(myCam);
		resetCommand = new ResetCommand(myCam);
		pitchRightCommand = new PitchRightCommand(myCam);
		pitchLeftCommand = new PitchLeftCommand(myCam);
		panUpCommand = new PanUpCommand(myCam);
		panDownCommand = new PanDownCommand(myCam);
			
		lightLeftCommand = new LightLeftCommand(myLight);
		lightRightCommand  = new LightRightCommand(myLight);
		lightUpCommand  = new LightUpCommand(myLight);
		lightDownCommand  = new LightDownCommand(myLight);
		lightBackCommand  = new LightBackCommand(myLight);
		lightFrontCommand  = new LightFrontCommand(myLight);
		lightResetCommand  = new LightResetCommand(myLight);
		lightToggleCommand = new LightToggleCommand(this);
		
		axesCommand = new ToggleAxesCommand(this);
		
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		// create a keystroke object to represent the "w" key
		KeyStroke wKey = KeyStroke.getKeyStroke('w');
		// create a keystroke object to represent the "s" key
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
		// create a keystroke object to represent the "a" key
		KeyStroke aKey = KeyStroke.getKeyStroke('a');
		// create a keystroke object to represent the "d" key
		KeyStroke dKey = KeyStroke.getKeyStroke('d');
		// create a keystroke object to represent the "e" key
		KeyStroke eKey = KeyStroke.getKeyStroke('e');
		// create a keystroke object to represent the "q" key
		KeyStroke qKey = KeyStroke.getKeyStroke('q');
		// create a keystroke object to represent the "Pan up" key
		KeyStroke panUpKey = KeyStroke.getKeyStroke("UP");
		// create a keystroke object to represent the "Pan down" key
		KeyStroke panDownKey = KeyStroke.getKeyStroke("DOWN");
		// create a keystroke object to represent the "Pitch left" key
		KeyStroke pitchLeftKey = KeyStroke.getKeyStroke("LEFT");
		// create a keystroke object to represent the "Pitch right" key
		KeyStroke pitchRightKey = KeyStroke.getKeyStroke("RIGHT");
		// create a keystroke object to represent the "down" key
		KeyStroke resetKey = KeyStroke.getKeyStroke('x');
		
		KeyStroke jKey = KeyStroke.getKeyStroke('j');
		KeyStroke lKey = KeyStroke.getKeyStroke('l');
		KeyStroke iKey = KeyStroke.getKeyStroke('i');
		KeyStroke kKey = KeyStroke.getKeyStroke('k');
		KeyStroke uKey = KeyStroke.getKeyStroke('u');
		KeyStroke oKey = KeyStroke.getKeyStroke('o');
		KeyStroke resetLightKey = KeyStroke.getKeyStroke('m');
		KeyStroke nKey = KeyStroke.getKeyStroke('n');
		KeyStroke yKey = KeyStroke.getKeyStroke('y');
				
		imap.put(wKey, "forward");				// Z-Ax is
		imap.put(sKey, "backward");
		imap.put(aKey, "left");					// X-Axis
		imap.put(dKey, "right");
		imap.put(eKey, "up");					// Y-Axis
		imap.put(qKey, "down");  
		imap.put(panUpKey, "panUp");			// pan  up/down
		imap.put(panDownKey, "panDown");		
		imap.put(pitchLeftKey,  "pitchLeft");	// pitch left/right
		imap.put(pitchRightKey,  "pitchRight");
		imap.put(resetKey, "reset");
		
		imap.put(jKey, "lightLeft");
		imap.put(lKey, "lightRight");
		imap.put(iKey, "lightUp");
		imap.put(kKey, "lightDown");
		imap.put(oKey, "lightBack");
		imap.put(uKey, "lightFront");
		imap.put(resetLightKey, "lightReset");
		imap.put(nKey, "toggleLight");
		imap.put(yKey, "axes");

				
		ActionMap amap = contentPane.getActionMap();
				
		amap.put("forward", forwardCommand);
		amap.put("backward", backwardCommand);
		amap.put("left", leftCommand);
		amap.put("right", rightCommand);
		amap.put("up", upCommand);
		amap.put("down", downCommand);
		amap.put("panUp", panUpCommand);
		amap.put("panDown", panDownCommand);
		amap.put("pitchLeft", pitchLeftCommand);
		amap.put("pitchRight", pitchRightCommand);
		amap.put("reset", resetCommand);
		
		amap.put("lightLeft", lightLeftCommand);
		amap.put("lightRight", lightRightCommand);
		amap.put("lightUp", lightUpCommand);
		amap.put("lightDown", lightDownCommand);
		amap.put("lightBack", lightBackCommand);
		amap.put("lightFront", lightFrontCommand);
		amap.put("lightReset", lightResetCommand);
		amap.put("toggleLight", lightToggleCommand);
		
		amap.put("axes", axesCommand);

		requestFocus();  
		setTitle("Daniel Curtis - a4");
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		myCanvas.addMouseMotionListener(this);
		myCanvas.addMouseWheelListener(this);
		add(myCanvas);
		setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
		
	}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
public void display(GLAutoDrawable drawable)
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	gl.glClear(GL_COLOR_BUFFER_BIT);
	gl.glClear(GL_DEPTH_BUFFER_BIT);
	
	
	gl.glUseProgram(renderingProgramCubeMap);
	//load the cube map stuff
	
	vMat.identity();
	//vMat.setTranslation(myCam.returnX(), myCam.returnY(), myCam.returnZ());
	vMat.mul(myCam.set());
	
	// draw cube map
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());
		
		if(posLock)	
		{
			lightVmat.identity().setLookAt(currentSpotLightPos, origin, up); //vector from light to origin
		}
		
		else	
		{
			lightVmat.identity().setLookAt(currentLightPos, origin, up); //vector from light to origin
		}
			
		lightPmat.identity().setPerspective((float) Math.toRadians(80.0f), aspect, 0.1f, 1000.0f);
		

		vLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "v_matrix");
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));

		projLoc = gl.glGetUniformLocation(renderingProgramCubeMap, "proj_matrix");
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
				
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);	     // cube is CW, but we are viewing the inside
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		gl.glEnable(GL_DEPTH_TEST);
		
		
		
	gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
	gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);
	
	gl.glDrawBuffer(GL_NONE);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glEnable(GL_POLYGON_OFFSET_FILL);	//  for reducing
	gl.glPolygonOffset(4.0f, 6.0f);		//  shadow artifacts

	passOne();
		
	gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued
	
	gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
	gl.glActiveTexture(GL_TEXTURE0);
	gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
	
	gl.glDrawBuffer(GL_FRONT);

	passTwo();
	
	//Axis 
	if(axes) 		drawAxes();
		
	//Yellow Dot for the Sun
	drawSun();
	letsTesselate();
		
}
	

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
private void drawSun() 
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
	gl.glUseProgram(renderingProgram3);
	
	float x = myLight.returnX();
	float y = myLight.returnY();
	float z = myLight.returnZ();
	
	mvLoc = gl.glGetUniformLocation(renderingProgram3, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram3, "proj_matrix");
	
	mMat.identity();
	mMat.translate(0.0f, 0.0f, 0.0f);
		
	//vMat.identity();
	//vMat.setTranslation(myCam.returnX(), myCam.returnY(), myCam.returnZ());
	//vMat.mul(myCam.set());
		
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));

	int xHold = gl.glGetUniformLocation(renderingProgram3, "lightX");
	gl.glProgramUniform1f(renderingProgram3, xHold, x);
	int yHold = gl.glGetUniformLocation(renderingProgram3, "lightY");
	gl.glProgramUniform1f(renderingProgram3, yHold, y);
	int zHold = gl.glGetUniformLocation(renderingProgram3, "lightZ");
	gl.glProgramUniform1f(renderingProgram3, zHold, z);
	
	//gl.glClear(GL_DEPTH_BUFFER_BIT);
	gl.glDrawArrays(GL_POINTS,0,1);
	gl.glPointSize(10.0f);
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
private void drawAxes() 
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	gl.glUseProgram(renderingProgram4);
	
	mvLoc = gl.glGetUniformLocation(renderingProgram4, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram4, "proj_matrix");
	
	mMat.identity();
	mMat.translate(0.0f, 0.0f, 0.0f);
	//vMat.identity();
	//vMat.setTranslation(myCam.returnX(), myCam.returnY(), myCam.returnZ());
	//vMat.mul(myCam.set());
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	
	gl.glDrawArrays(GL_LINES,0,6);
}

private void letsTesselate()	{
	GL4 gl = (GL4) GLContext.getCurrentGL();
	gl.glUseProgram(renderingProgram10);
	
	mvpLoc = gl.glGetUniformLocation(renderingProgram10, "mvp");
	mvLoc = gl.glGetUniformLocation(renderingProgram10, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram10, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram10, "norm_matrix");
	
	thisAmb = SmatAmb; // the tesselated thing is Bronze
	thisDif = SmatDif;
	thisSpe = SmatSpe;
	thisShi = SmatShi;
	
	
	mMat.identity();
	mMat.translate(0.0f, 30.0f, 0.0f);////FUTURE COORDINATE
	mMat.rotateX((float) Math.toRadians(7.0f));
	mMat.scale(150.0f, 150.0f, 150.0f);
	
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);

	mvpMat.identity();
	mvpMat.mul(pMat);
	mvpMat.mul(vMat);
	mvpMat.mul(mMat);
	
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);
	
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());
	installLights(renderingProgram10, vMat);
	
	gl.glUniformMatrix4fv(mvpLoc, 1, false, mvpMat.get(vals));
	gl.glUniformMatrix4fv(mvLoc, 1, false, vMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	
	gl.glActiveTexture(GL_TEXTURE0);
	gl.glBindTexture(GL_TEXTURE_2D, squareMoonTexture);
	gl.glActiveTexture(GL_TEXTURE1);
	gl.glBindTexture(GL_TEXTURE_2D, squareMoonHeight);
	gl.glActiveTexture(GL_TEXTURE2);
	gl.glBindTexture(GL_TEXTURE_2D, squareMoonNormalMap);

	//gl.glClear(GL_DEPTH_BUFFER_BIT);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glFrontFace(GL_CCW);
	
	gl.glPatchParameteri(GL_PATCH_VERTICES, 4);
	gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	gl.glDrawArraysInstanced(GL_PATCHES, 0, 4, 64*64);

}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public void passOne()
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
	gl.glUseProgram(renderingProgram1);
	sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
	// draw the torus/dolphin 3dTexture in the shadow realm
		
	mMat.identity();
	mMat.translate(torusLoc.x(), torusLoc.y(), torusLoc.z());
	mMat.scale(2.0f, 2.0f, 2.0f);
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));
		
	shadowMVP1.identity();
	shadowMVP1.mul(lightPmat);
	shadowMVP1.mul(lightVmat);
	shadowMVP1.mul(mMat);
	
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);	
	
	gl.glClear(GL_DEPTH_BUFFER_BIT);
	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
	
	gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);


	// draw the dolphin in the shadow realm
		
	mMat.identity();
	mMat.translate(pyrLoc.x(), pyrLoc.y(), pyrLoc.z());
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));

	shadowMVP1.identity();
	shadowMVP1.mul(lightPmat);
	shadowMVP1.mul(lightVmat);
	shadowMVP1.mul(mMat);

	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
	
	gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
	
	//draw the dolphin in the shadow realm
	mMat.identity();
	mMat.translate(dolLoc.x(), dolLoc.y(), dolLoc.z());
	mMat.scale(2.0f, 2.0f, 2.0f);
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));

	shadowMVP1.identity();
	shadowMVP1.mul(lightPmat);
	shadowMVP1.mul(lightVmat);
	shadowMVP1.mul(mMat);

	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
	
	gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
		
	//draw the bread from the shadow realm
		
	mMat.identity();
	mMat.translate(brdLoc.x(), brdLoc.y(), brdLoc.z());
	mMat.scale(0.1f, 0.1f, 0.1f);
	shadowMVP1.identity();
	shadowMVP1.mul(lightPmat);
	shadowMVP1.mul(lightVmat);
	shadowMVP1.mul(mMat);

	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
				
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
			
	gl.glDrawArrays(GL_TRIANGLES, 0, numBreadVertices);	 
	
	//draw the sphere from the shadow realm
	
	mMat.identity();
	mMat.translate(pyrLoc.x(), pyrLoc.y(), pyrLoc.z());
	mMat.scale(2.0f, 2.0f, 2.0f);
	shadowMVP1.identity();
	shadowMVP1.mul(lightPmat);
	shadowMVP1.mul(lightVmat);
	shadowMVP1.mul(mMat);

	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
					
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
				
	gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);	
	
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public void passTwo()
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	gl.glUseProgram(renderingProgram8);
		
	mvLoc = gl.glGetUniformLocation(renderingProgram8, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram8, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram8, "norm_matrix");
	sLoc = gl.glGetUniformLocation(renderingProgram8, "shadowMVP");
		
	// draw the striped 3-D Texture Dolphin
			
	thisAmb = JmatAmb; // the dolphin 3-D texture is jade
	thisDif = JmatDif;
	thisSpe = JmatSpe;
	thisShi = JmatShi;
		
		
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());
	installLights(renderingProgram8, vMat);

	mMat.identity();
	mMat.translate(torusLoc.x(), torusLoc.y(), torusLoc.z());
	mMat.scale(2.0f, 2.0f, 2.0f);
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));
		
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);
		
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
		
	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
	//Dolphin Vertices
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);
	
	//Dolph Normals
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
	gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(1);	
	
	gl.glActiveTexture(GL_TEXTURE0);
	gl.glBindTexture(GL_TEXTURE_3D, stripeTexture);
	

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
	
	gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
	
	// draw the pyramid
		
	
	//draw the bread
	
	gl.glUseProgram(renderingProgram2);
	
	mvLoc = gl.glGetUniformLocation(renderingProgram2, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram2, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram2, "norm_matrix");
	sLoc = gl.glGetUniformLocation(renderingProgram2, "shadowMVP");
		
	thisAmb = BmatAmb; // the bread is bronze
	thisDif = BmatDif;
	thisSpe = BmatSpe;
	thisShi = BmatShi;
						
	mMat.identity();
	mMat.translate(brdLoc.x(), brdLoc.y(), brdLoc.z());
	mMat.scale(0.1f, 0.1f, 0.1f);
		
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());

	installLights(renderingProgram2, vMat);

	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
						
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
				
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
	//Bread Vertices				
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);
	//Bread Normals
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
	gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(1);
		
	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);

	gl.glDrawArrays(GL_TRIANGLES, 0, numBreadVertices);

	
	//draw the cushiony couchy squishy sphere
	
	gl.glUseProgram(renderingProgram5);

	mvLoc = gl.glGetUniformLocation(renderingProgram5, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram5, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram5, "norm_matrix");
	sLoc = gl.glGetUniformLocation(renderingProgram5, "shadowMVP");
	
	
	thisAmb = SmatAmb; // the cushiony couchy squishy sphere is silver
	thisDif = SmatDif;
	thisSpe = SmatSpe;
	thisShi = SmatShi;
	
	mMat.identity();
	mMat.translate(pyrLoc.x(), pyrLoc.y(), pyrLoc.z());
	
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());
	installLights(renderingProgram5, vMat);

	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));

	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);
	
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(1);

	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
	gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(2);
	
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
	gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(3);
	
	gl.glActiveTexture(gl.GL_TEXTURE1);
	gl.glBindTexture(gl.GL_TEXTURE_2D, moonNormalMap);	//brick sphere now
	
	gl.glActiveTexture(gl.GL_TEXTURE2);
	gl.glBindTexture(gl.GL_TEXTURE_2D, moonTexture);

	
	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);

	gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVertices);	
	
	// draw the dolphin with the 2-D texture
	
	gl.glUseProgram(renderingProgram6);

	mvLoc = gl.glGetUniformLocation(renderingProgram6, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram6, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram6, "norm_matrix");
	sLoc = gl.glGetUniformLocation(renderingProgram6, "shadowMVP");
	
	mMat.identity();
	mMat.translate(dolLoc.x(), dolLoc.y(), dolLoc.z());
	mMat.scale(2.0f, 2.0f, 2.0f);
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));
			
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());

	installLights(renderingProgram6, vMat);

	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
					
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
			
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
	//Dolphin Vertices	
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);
	//Dolphin Normals
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
	gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(1);

	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
	//Dolphin 2-D Texture Coordinates
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
	gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(2);
		
	gl.glActiveTexture(GL_TEXTURE1);
	gl.glBindTexture(GL_TEXTURE_2D, dolTexture);
		
	gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
		
	//Draw the plane  
	mMat.translation(floorLocX, floorLocY, floorLocZ);
	mMat.rotateX((float)Math.toRadians(5.0f));
	
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
	mvMat.invert(invTrMat);
		
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
			
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);

	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());

	installLights(renderingProgram6, vMat);

	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
	gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(0);

	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[22]);
	gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(2);

	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[23]);
	gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
	gl.glEnableVertexAttribArray(1);
		
	//gl.glClear(GL_DEPTH_BUFFER_BIT);
	gl.glEnable(GL_CULL_FACE);
	gl.glFrontFace(GL_CCW);
	gl.glEnable(GL_DEPTH_TEST);
	gl.glDepthFunc(GL_LEQUAL);
		
	gl.glActiveTexture(GL_TEXTURE1);
	gl.glBindTexture(GL_TEXTURE_2D, sandTexture);
		
	gl.glDrawArrays(GL_TRIANGLES, 0, 6);
		
	//Furry Dolphin Geometry Shader thing
	gl.glUseProgram(renderingProgram7);

	mvLoc = gl.glGetUniformLocation(renderingProgram7, "mv_matrix");
	projLoc = gl.glGetUniformLocation(renderingProgram7, "proj_matrix");
	nLoc = gl.glGetUniformLocation(renderingProgram7, "norm_matrix");
	sLoc = gl.glGetUniformLocation(renderingProgram7,  "shadowMVP");
		
	mMat.identity();
	mMat.translate(pyr2Loc.x(), pyr2Loc.y(), pyr2Loc.z());
	mMat.scale(20.0f, 20.0f, 20.0f);
	mMat.rotateX((float)Math.toRadians(30.0f));
	mMat.rotateY((float)Math.toRadians(40.0f));

	thisAmb = PmatAmb; // the Sphere2 is pearl
	thisDif = PmatDif;
	thisSpe = PmatSpe;
	thisShi = PmatShi;
			
	currentLightPos.set(initialLightLoc);
	currentSpotLightPos.set(myLight.setLight());
	installLights(renderingProgram7, vMat);
		
	mvMat.identity();
	mvMat.mul(vMat);
	mvMat.mul(mMat);
	mvMat.invert(invTrMat);
	invTrMat.transpose(invTrMat);
	
	shadowMVP2.identity();
	shadowMVP2.mul(b);
	shadowMVP2.mul(lightPmat);
	shadowMVP2.mul(lightVmat);
	shadowMVP2.mul(mMat);
	gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
	gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
	gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
	gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
		//Dolphin Furry Vertices	
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);	//15
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//Dolphin Furry Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);	//17
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		//gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
		
		//Draw the reflective dolphin (ENVIRONMENT MAPPING)
		
		gl.glUseProgram(renderingProgram9);
		
		mvLoc = gl.glGetUniformLocation(renderingProgram9, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram9, "proj_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram9, "norm_matrix");
		
		mMat.identity();
		mMat.translate(dol2Loc.x(), dol2Loc.y(), dol2Loc.z());
		mMat.scale(2.0f, 2.0f, 2.0f);
		mMat.rotateX(myRotate);
		myRotate += 0.01;
		
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);
		

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		//Dolphin Vertex
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		//Dolphin Normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		//gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, numDolphinVertices);
	
}


//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public void init(GLAutoDrawable drawable)
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	renderingProgram1 = Utils.createShaderProgram("a4/vert1shader.glsl", "a4/frag1shader.glsl");
	renderingProgram2 = Utils.createShaderProgram("a4/vert2shader.glsl", "a4/frag2shader.glsl");
	renderingProgram3 = Utils.createShaderProgram("a4/vert3shader.glsl", "a4/frag3shader.glsl");
	renderingProgram4 = Utils.createShaderProgram("a4/lightVert.glsl", "a4/lightFrag.glsl");
	renderingProgram5 = Utils.createShaderProgram("a4/vertNShader.glsl", "a4/fragNShader.glsl");
	renderingProgram6 = Utils.createShaderProgram("a4/vert4shader.glsl", "a4/frag4shader.glsl");
	renderingProgram7 = Utils.createShaderProgram("a4/vert5shader.glsl", "a4/geomShader.glsl", "a4/frag5shader.glsl");
	renderingProgram8 = Utils.createShaderProgram("a4/vert6shader.glsl", "a4/frag6shader.glsl");
	renderingProgram9 = Utils.createShaderProgram("a4/vert7shader.glsl", "a4/frag7shader.glsl");
	renderingProgram10 = Utils.createShaderProgram("a4/vert8shader.glsl", "a4/tessCshader.glsl", "a4/tessEshader.glsl", "a4/frag8shader.glsl");	
	renderingProgramCubeMap = Utils.createShaderProgram("a4/vertCShader.glsl", "a4/fragCShader.glsl");

	aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
	pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

	setupVertices();
	setupShadowBuffers();
				
	b.set(
		0.5f, 0.0f, 0.0f, 0.0f,
		0.0f, 0.5f, 0.0f, 0.0f,
		0.0f, 0.0f, 0.5f, 0.0f,
		0.5f, 0.5f, 0.5f, 1.0f);
		
		skyboxTexture = Utils.loadCubeMap("cubeMap");
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		moonTexture = Utils.loadTexture("Fabric.jpg");
		moonNormalMap = Utils.loadTexture("FabricN.jpg");

		dolTexture = Utils.loadTexture("Dolphin_HighPolyUV.png");
		sandTexture = Utils.loadTexture("water.jpg");
		
		generate3Dpattern();	
		stripeTexture = load3DTexture();
		
		squareMoonTexture = Utils.loadTexture("cloud.jpg");
		squareMoonHeight = Utils.loadTexture("cloudH.jpg");
		squareMoonNormalMap = Utils.loadTexture("cloudN.jpg");
		
}
	
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

private void setupShadowBuffers()
{	GL4 gl = (GL4) GLContext.getCurrentGL();
	scSizeX = myCanvas.getWidth();
	scSizeY = myCanvas.getHeight();
	
	gl.glGenFramebuffers(1, shadowBuffer, 0);
	
	gl.glGenTextures(1, shadowTex, 0);
	gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
	gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
					scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		
	// may reduce shadow border artifacts
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

private void setupVertices()
{	GL4 gl = (GL4) GLContext.getCurrentGL();
		
	//Dolphin definition
		
	ImportedModel dolphin = new ImportedModel("../dolphinHighPoly.obj");
	numDolphinVertices = dolphin.getNumVertices();
	Vector3f[] dVertices = dolphin.getVertices();
	Vector2f[] dTextures = dolphin.getTexCoords();
	Vector3f[] dNormals = dolphin.getNormals();
	
		
	float[] dolphinPvalues = new float[numDolphinVertices*3];
	float[] dolphinTvalues = new float[numDolphinVertices*2];
	float[] dolphinNvalues = new float[numDolphinVertices*3];
		
	for (int i=0; i<numDolphinVertices; i++)
	{	dolphinPvalues[i*3]   = (float) (dVertices[i]).x();
		dolphinPvalues[i*3+1] = (float) (dVertices[i]).y();
		dolphinPvalues[i*3+2] = (float) (dVertices[i]).z();
		dolphinTvalues[i*2]   = (float) (dTextures[i].x());
		dolphinTvalues[i*2+1] = (float) (dTextures[i].y());
		dolphinNvalues[i*3]   = (float) (dNormals[i]).x();
		dolphinNvalues[i*3+1] = (float) (dNormals[i]).y();
		dolphinNvalues[i*3+2] = (float) (dNormals[i]).z();
	}
		
	//Dr. Gordon's Bread definition
		
	ImportedModel bread = new ImportedModel("../Bread.obj");
	numBreadVertices = bread.getNumVertices();
	Vector3f[] bVertices = bread.getVertices();
	Vector3f[] bNormals = bread.getNormals();
		
	float[] breadPvalues = new float[numBreadVertices*3];
	float[] breadNvalues = new float[numBreadVertices*3];
		
	for (int i=0; i<numBreadVertices; i++)
	{	breadPvalues[i*3]   = (float) (bVertices[i]).x();
		breadPvalues[i*3+1] = (float) (bVertices[i]).y();
		breadPvalues[i*3+2] = (float) (bVertices[i]).z();
		breadNvalues[i*3]   = (float) (bNormals[i]).x();
		breadNvalues[i*3+1] = (float) (bNormals[i]).y();
		breadNvalues[i*3+2] = (float) (bNormals[i]).z();
	}

	//CubeMap stuff
	float[] cubeVertexPositions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};
	 
	//sphere vertices 
	Sphere mySphere = new Sphere(128);
	
	numSphereVertices = mySphere.getIndices().length;
	int[] sPindices = mySphere.getIndices();
	Vector3f[] sPvertices = mySphere.getVertices();
	Vector2f[] sPtexCoords = mySphere.getTexCoords();
	Vector3f[] sPnormals = mySphere.getNormals();
	Vector3f[] sPtangents = mySphere.getTangents();
	
	float[] pvalues = new float[sPindices.length*3];
	float[] tvalues = new float[sPindices.length*2];
	float[] nvalues = new float[sPindices.length*3];
	float[] tanvalues = new float[sPindices.length*3];

	for (int i=0; i<sPindices.length; i++)
	{	pvalues[i*3]   = (float) (sPvertices[sPindices[i]]).x();
		pvalues[i*3+1] = (float) (sPvertices[sPindices[i]]).y();
		pvalues[i*3+2] = (float) (sPvertices[sPindices[i]]).z();
		tvalues[i*2]   = (float) (sPtexCoords[sPindices[i]]).x();
		tvalues[i*2+1] = (float) (sPtexCoords[sPindices[i]]).y();
		nvalues[i*3]   = (float) (sPnormals[sPindices[i]]).x();
		nvalues[i*3+1] = (float) (sPnormals[sPindices[i]]).y();
		nvalues[i*3+2] = (float) (sPnormals[sPindices[i]]).z();
		tanvalues[i*3] = (float) (sPtangents[sPindices[i]]).x();
		tanvalues[i*3+1] = (float) (sPtangents[sPindices[i]]).y();
		tanvalues[i*3+2] = (float) (sPtangents[sPindices[i]]).z();
	}
	
	
	float[] PLANE_POSITIONS = {
			-128.0f, 0.0f, -128.0f,  -128.0f, 0.0f, 128.0f,  128.0f, 0.0f, -128.0f,
			128.0f, 0.0f, -128.0f,  -128.0f, 0.0f, 128.0f,  128.0f, 0.0f, 128.0f
		};
	float[] PLANE_TEXCOORDS = {
			0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 0.0f,
			1.0f, 0.0f,  0.0f, 1.0f,  1.0f, 1.0f
		};
	float[] PLANE_NORMALS = {
			0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f
		};
	// buffers definition
	gl.glGenVertexArrays(vao.length, vao, 0);
	gl.glBindVertexArray(vao[0]);

	gl.glGenBuffers(24, vbo, 0);

	//  put the Torus vertices into the first buffer,
	//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
	//FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(torusPvalues);
	//gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		
	//  load the pyramid vertices into the second buffer
	//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
	//FloatBuffer pyrVertBuf = Buffers.newDirectFloatBuffer(pyramidPvalues);
	//gl.glBufferData(GL_ARRAY_BUFFER, pyrVertBuf.limit()*4, pyrVertBuf, GL_STATIC_DRAW);
		
	// load the dolphin vertices into the third buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	FloatBuffer dolVertBuf = Buffers.newDirectFloatBuffer(dolphinPvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, dolVertBuf.limit()*4, dolVertBuf, GL_STATIC_DRAW);
		
	// load the torus normal coordinates into the fourth buffer
	//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
	//FloatBuffer torusNorBuf = Buffers.newDirectFloatBuffer(torusNvalues);
	//gl.glBufferData(GL_ARRAY_BUFFER, torusNorBuf.limit()*4, torusNorBuf, GL_STATIC_DRAW);
		
	// load the pyramid normal coordinates into the fifth buffer
	//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
	//FloatBuffer pyrNorBuf = Buffers.newDirectFloatBuffer(pyramidNvalues);
	//gl.glBufferData(GL_ARRAY_BUFFER, pyrNorBuf.limit()*4, pyrNorBuf, GL_STATIC_DRAW);
		
	// load the dolphin normal coordinates into the sixth buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
	FloatBuffer dolNorBuf = Buffers.newDirectFloatBuffer(dolphinNvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, dolNorBuf.limit()*4, dolNorBuf, GL_STATIC_DRAW);
		
	//gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[6]);
	//IntBuffer idxBuf = Buffers.newDirectIntBuffer(indices);
	//gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuf.limit()*4, idxBuf, GL_STATIC_DRAW);
		
	//load the bread vertices coordinates into the eighth buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
	FloatBuffer brdVertBuf = Buffers.newDirectFloatBuffer(breadPvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, brdVertBuf.limit()*4, brdVertBuf, GL_STATIC_DRAW);
		
	//load the bread normal coordinates into the ninth buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
	FloatBuffer brdNorBuf = Buffers.newDirectFloatBuffer(breadNvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, brdNorBuf.limit()*4, brdNorBuf, GL_STATIC_DRAW);
	
	//load the sky cube vertex positions
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
	FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(cubeVertexPositions);
	gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit()*4, cvertBuf, GL_STATIC_DRAW);
	
	//load the Sphere vertices
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
	FloatBuffer sPvertBuf = Buffers.newDirectFloatBuffer(pvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, sPvertBuf.limit()*4, sPvertBuf, GL_STATIC_DRAW);
	//load the Sphere textures
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
	FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
	//load the sphere normals
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
	FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4, norBuf, GL_STATIC_DRAW);
	//load the sphere tangents
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
	FloatBuffer tanBuf = Buffers.newDirectFloatBuffer(tanvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, tanBuf.limit()*4, tanBuf, GL_STATIC_DRAW);
	//load the dolphin 2-D texture coordinates
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
	FloatBuffer dolTexBuf = Buffers.newDirectFloatBuffer(dolphinTvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, dolTexBuf.limit()*4, dolTexBuf, GL_STATIC_DRAW);
	
	//Sphere2
	/*load the Sphere vertices
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
	FloatBuffer s2PvertBuf = Buffers.newDirectFloatBuffer(p2values);
	gl.glBufferData(GL_ARRAY_BUFFER, s2PvertBuf.limit()*4, s2PvertBuf, GL_STATIC_DRAW);
	//load the Sphere2 indices(no longer texture)
	gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[16]);
	IntBuffer idx2Buf = Buffers.newDirectIntBuffer(s2Pindices);
	gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, idx2Buf.limit()*4, idx2Buf, GL_STATIC_DRAW);
	//load the sphere2 normals
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
	FloatBuffer s2NorBuf = Buffers.newDirectFloatBuffer(n2values);
	gl.glBufferData(GL_ARRAY_BUFFER, s2NorBuf.limit()*4, s2NorBuf, GL_STATIC_DRAW);
	//load the sphere2 tangents
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[18]);
	FloatBuffer s2TanBuf = Buffers.newDirectFloatBuffer(tan2values);
	gl.glBufferData(GL_ARRAY_BUFFER, s2TanBuf.limit()*4, s2TanBuf, GL_STATIC_DRAW);
	*/
	
	// load the reflective dolphin vertices into the third buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[19]);
	FloatBuffer dol2VertBuf = Buffers.newDirectFloatBuffer(dolphinPvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, dol2VertBuf.limit()*4, dol2VertBuf, GL_STATIC_DRAW);
	// load the reflective dolphin normal coordinates into the sixth buffer
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
	FloatBuffer dol2NorBuf = Buffers.newDirectFloatBuffer(dolphinNvalues);
	gl.glBufferData(GL_ARRAY_BUFFER, dol2NorBuf.limit()*4, dol2NorBuf, GL_STATIC_DRAW);
	
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
	FloatBuffer planeBuf = Buffers.newDirectFloatBuffer(PLANE_POSITIONS);
	gl.glBufferData(GL_ARRAY_BUFFER, planeBuf.limit()*4, planeBuf, GL_STATIC_DRAW);

	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[22]);
	FloatBuffer planeTexBuf = Buffers.newDirectFloatBuffer(PLANE_TEXCOORDS);
	gl.glBufferData(GL_ARRAY_BUFFER, planeTexBuf.limit()*4, planeTexBuf, GL_STATIC_DRAW);
	
	gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[23]);
	FloatBuffer planeNorBuf = Buffers.newDirectFloatBuffer(PLANE_NORMALS);
	gl.glBufferData(GL_ARRAY_BUFFER,  planeNorBuf.limit()*4, planeNorBuf, GL_STATIC_DRAW);
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
private void installLights(int renderingProgram, Matrix4f vMatrix)
{	GL4 gl = (GL4) GLContext.getCurrentGL(); 
		
	currentLightPos.mulPosition(vMatrix);
	lightPos[0]=currentLightPos.x(); 
	lightPos[1]=currentLightPos.y(); 
	lightPos[2]=currentLightPos.z();
	currentSpotLightPos.mulPosition(vMatrix);
	spotLightPos[0]=currentSpotLightPos.x();
	spotLightPos[1]=currentSpotLightPos.y();
	spotLightPos[2]=currentSpotLightPos.z();
		
	if(!posLock) 
	{
		globalAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		spotLightAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		spotLightDiffuse = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		spotLightSpecular = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
			
	}
	else {
		globalSpot = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
		spotLightAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		spotLightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		spotLightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	}

	// set current material values
		matAmb = thisAmb;
		matDif = thisDif;
		matSpe = thisSpe;
		matShi = thisShi;
	
		
	// get the locations of the light and material fields in the shader
	globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
	globalSpotLoc = gl.glGetUniformLocation(renderingProgram, "globalSpot");
	ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
	diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
	specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
	posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
	spot2Loc = gl.glGetUniformLocation(renderingProgram, "light2.ambient");
	diff2Loc = gl.glGetUniformLocation(renderingProgram, "light2.diffuse");
	spec2Loc = gl.glGetUniformLocation(renderingProgram, "light2.specular");
	pos2Loc = gl.glGetUniformLocation(renderingProgram, "light2.position");
	mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
	mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
	mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
	mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
	
	//  set the uniform light and material values in the shader
	gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
	gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
	gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
	gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
	gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);	
	gl.glProgramUniform4fv(renderingProgram, globalSpotLoc, 1, globalSpot, 0);
	gl.glProgramUniform4fv(renderingProgram, spot2Loc, 1, spotLightAmbient, 0);
	gl.glProgramUniform4fv(renderingProgram, diff2Loc, 1, spotLightDiffuse, 0);
	gl.glProgramUniform4fv(renderingProgram, spec2Loc, 1, spotLightSpecular, 0);
	gl.glProgramUniform3fv(renderingProgram, pos2Loc, 1, spotLightPos, 0);	
	gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
	gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
	gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
	gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
	
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


// 3D Texture section

private void fillDataArray(byte data[])
{ for (int i=0; i<texWidth; i++)
  { for (int j=0; j<texHeight; j++)
    { for (int k=0; k<texDepth; k++)
      {
	if (tex3Dpattern[i][j][k] == 0.0)
	{	// GREEN color
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+0] = (byte) 0; //red
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+1] = (byte) 133; //green
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+2] = (byte) 66;   //blue
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+3] = (byte) 0;   //alpha
	}
	else
	{	// RED color
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+0] = (byte) 231;   //red
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+1] = (byte) 0;   //green
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+2] = (byte) 1; //blue
		data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+3] = (byte) 0;   //alpha
	}
} } } }

private int load3DTexture()
{	GL4 gl = (GL4) GLContext.getCurrentGL();

	byte[] data = new byte[texWidth*texHeight*texDepth*4];
	
	fillDataArray(data);

	ByteBuffer bb = Buffers.newDirectByteBuffer(data);

	int[] textureIDs = new int[1];
	gl.glGenTextures(1, textureIDs, 0);
	int textureID = textureIDs[0];

	gl.glBindTexture(GL_TEXTURE_3D, textureID);

	gl.glTexStorage3D(GL_TEXTURE_3D, 1, GL_RGBA8, texWidth, texHeight, texDepth);
	gl.glTexSubImage3D(GL_TEXTURE_3D, 0, 0, 0, 0,
			texWidth, texHeight, texDepth, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, bb);
	
	gl.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	return textureID;
}

void generate3Dpattern()
{	for (int x=0; x<texWidth; x++)
	{	for (int y=0; y<texHeight; y++)
		{	for (int z=0; z<texDepth; z++)
			{	if (Math.tan(Math.toRadians(Math.PI * x)) < 0)
					tex3Dpattern[x][y][z] = 0.0;
				else
					tex3Dpattern[x][y][z] = 1.0;
			}	
		}	
	}	
}

//  replace above function with the one below
//	to change the stripes to a checkerboard.
/*
void generate3Dpattern()
{	int xStep, yStep, zStep, sumSteps;
	for (int x=0; x<texWidth; x++)
	{	for (int y=0; y<texHeight; y++)
		{	for (int z=0; z<texDepth; z++)
			{	xStep = (x / 10) % 2;
				yStep = (y / 10) % 2;
				zStep = (z / 10) % 2;
				sumSteps = xStep + yStep + zStep;
				if ((sumSteps % 2) == 0)
					tex3Dpattern[x][y][z] = 0.0;
				else
					tex3Dpattern[x][y][z] = 1.0;
}	}	}	}
*/

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

public static void main(String[] args) { new Starter(); }
public void dispose(GLAutoDrawable drawable) {}

public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
{	GL4 gl = (GL4) GLContext.getCurrentGL();

	aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
	pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

	setupShadowBuffers();
}
	
public void mouseMoved(MouseEvent e) 
{
	//TODO Auto-generated method stub
}

@Override
public void mouseDragged(MouseEvent e) 
{	// TODO Auto-generated method stub
	float x = e.getX();
	float y = e.getY();
		
	if (x > myCanvas.getWidth()/2 && x < myCanvas.getWidth()) 
	{
		currentSpotLightPos.x = (myCanvas.getWidth() / 2 + (x - myCanvas.getWidth()))/15;	
    }
    else if (x > myCanvas.getWidth())
    {
    	currentSpotLightPos.x = myCanvas.getWidth()/15;	
    }
    	
    else if(x < myCanvas.getWidth() /2 ) 
    {
    	currentSpotLightPos.x = ((x - myCanvas.getWidth() / 2))/15;	
    	
    }
    else {
    	currentSpotLightPos.x = 0;	
    }
		 
	
	if (y > myCanvas.getHeight() / 2 && y < myCanvas.getHeight()) 
	{
		currentSpotLightPos.y = (-(myCanvas.getHeight() / 2 + (y - myCanvas.getHeight())))/15;
	}
	else if (y > myCanvas.getHeight()) 
	{ 
	    currentSpotLightPos.y = myCanvas.getHeight()/15;
	}
	else if (y < myCanvas.getHeight() / 2) 
	{
		currentSpotLightPos.y = (-(y - myCanvas.getHeight()/2))/15;
	}
	else
	{
		currentSpotLightPos.y = 0;
	}
	
	myLight.setX(currentSpotLightPos.x);
	myLight.setY(currentSpotLightPos.y);
	
	myCanvas.display();
			
}
	
@Override
public void mouseWheelMoved(MouseWheelEvent e) 
{	float z = e.getWheelRotation();
	
	if(z>0) 
	{
		z = 0.5f;
	}
	else if(z<0)	
	{
		z = (-0.5f);
	}
    	 
	myLight.setX(myLight.returnX());
	myLight.setY(myLight.returnY());
    myLight.setZ(myLight.returnZ() + z);
    
    myCanvas.display();
  
}

void lock()	
{
	if(posLock)				posLock = !posLock;
	else if(!posLock)		posLock = !posLock;
}
	
void axes()	
{
	if(axes)		axes = !axes;
	else if(!axes)	axes = !axes;
}

}