package a2;

import java.nio.*;
import java.lang.Math;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import org.joml.*;

public class Starter extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private double startTime = 0.0;
	private double elapsedTime;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[10];
	private float cameraX, cameraY, cameraZ;
	
	
	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4fStack mvStack = new Matrix4fStack(8);
	private Matrix4f pMat = new Matrix4f();
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, projLoc;
	private float aspect;
	private double tf;
	
	//Texture Variables
	private int sTexture;			//Sun Texture
	private int p1Texture;			//Planet1 Texture
	private int p2Texture;			//
	private int m1Texture;			//
	private int m2Texture;			//
	private int objTexture;			//
	

	
	//sphere for sun
	private Sphere mySun;
	private int numSphereVerts;
	
	private int numObjVertices;
	private ImportedModel myModel;
	
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
	
	Camera myCam = new Camera(0.0f, 0.0f, 20.0f);

	public Starter()
	{	
		
		JComponent contentPane = (JComponent) this.getContentPane();
		
		//Commands
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
		
		// get the "focus is in the window" input map for the content pane
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
		
		imap.put(wKey, "forward");				// Z-Axis
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
		
		this.requestFocus();
		
		setTitle("Assignment 2");
		setSize(600, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		elapsedTime = System.currentTimeMillis() - startTime;

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		
		mvStack.pushMatrix();
		mvStack.mul(myCam.set());	//Camera 
		
		tf = elapsedTime/1000.0;  // time factor

		// ----------------------  pyramid == sun  
		mvStack.pushMatrix();
		mvStack.translate(0.0f, 0.0f, 0.0f);
		//mvStack.scale(1.5f, 1.5f, 1.5f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, 0.0f, 1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glEnable(GL_DEPTH_TEST);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, sTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, 18);
		mvStack.popMatrix();
		
		//Planet 1 
		//Sphere - Earth texture orbits sun
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf)*3.0f, 0.0f, (float)Math.cos(tf)*3.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, -1.0f, 0.0f, 0.0f);  // spin clockwise
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		
		gl.glBindTexture(GL_TEXTURE_2D, p1Texture);
		gl.glEnable(GL_CULL_FACE);
		
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix();
	
		
		//Moon  1 - Grass texture orbits p1
		mvStack.pushMatrix();
		mvStack.translate(0.0f, (float)Math.sin(tf)*2.0f, (float)Math.cos(tf)*2.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, 0.0f, -1.0f, 0.0f);  // spin counter-clockwise
		mvStack.scale(0.25f, 0.25f, 0.25f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
				
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
				
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
				
		gl.glBindTexture(GL_TEXTURE_2D, m1Texture);
		gl.glEnable(GL_CULL_FACE);
				
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
				
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix(); 
		
		//Shuttle Obj orbits Planet1 
		mvStack.pushMatrix();
		mvStack.translate((float)Math.cos(tf)*1.0f, 0.0f, (float)Math.sin(tf)*1.0f );
		mvStack.rotate((float)tf, 0.0f, -1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, objTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, myModel.getNumVertices());
		mvStack.popMatrix();
		mvStack.popMatrix();
		
		//Planet 2 - castleroof texture orbits sun
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf)*10.0f, 0.0f, (float)Math.cos(tf)*10.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf, 0.0f, -1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
				
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, p2Texture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, 24);
		mvStack.popMatrix();
		
		//Moon 2 - brick texture orbits p2
		mvStack.pushMatrix();
		mvStack.translate(0.0f, (float)Math.sin(tf)*2.0f, (float)Math.cos(tf)*2.0f);
		mvStack.rotate((float)tf, -1.0f, 0.0f, 0.0f);  // spin counter-clockwise
		mvStack.scale(0.25f, 0.25f, 0.25f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
				
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
				
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		gl.glActiveTexture(GL_TEXTURE0);
				
		gl.glBindTexture(GL_TEXTURE_2D, m2Texture);
		gl.glEnable(GL_CULL_FACE);
				
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
				
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix();
		
		//clear the stack
		mvStack.popMatrix();	mvStack.popMatrix();	mvStack.popMatrix();	mvStack.popMatrix();
		
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		startTime = System.currentTimeMillis();
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		//Import spaceship model
		myModel = new ImportedModel("../shuttle.obj");   			
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		
		//load texture files
		m1Texture = Utils.loadTexture("soccer.png");
		sTexture = Utils.loadTexture("grass.jpg");
		p2Texture = Utils.loadTexture("castleroof.jpg");
		p1Texture = Utils.loadTexture("earth.jpg");
		m2Texture = Utils.loadTexture("brick1.jpg");
		objTexture = Utils.loadTexture("spstob_1.jpg");

	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
				
		float[] pyramidPositions =
		{	-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    //front
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,    //right
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  //back
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,  //left
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,  //RR
		};
		
		float[] pyrTextureCoordinates =
			{	0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
			};
		
		float[] myShape = 
		{		0.0f, 1.0f, 0.5f, -1.0f,-0.5f,0.5f, 1.0f,-0.5f,0.5f,
				1.0f,0.5f,0.5f, -1.0f,0.5f,0.5f, 0.0f,-1.0f,0.5f,
				-1.0f,-0.5f,0.5f, -1.0f,-0.5f,-0.5f, -1.0f,1.0f,0.0f,
				-1.0f,0.5f,0.5f,  -1.0f,0.5f,-0.5f,  -1.0f,-1.0f,0.0f,
				0.0f,1.0f,-0.5f, -1.0f,-0.5f,-0.5f, 1.0f,-0.5f,-0.5f,
				1.0f,0.5f,-0.5f, -1.0f,0.5f,-0.5f,   0.0f,-1.0f,-0.5f,
				1.0f,-0.5f,0.5f, 1.0f,-0.5f,-0.5f, 1.0f,1.0f,0.0f,
				1.0f,0.5f,0.5f,  1.0f,0.5f,-0.5f,  1.0f,-1.0f,0.0f,
				
		};
		
		float[] myShapeTexture = 
		{		0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};
				
				//use the sun as a standard for all spheres to get indices
				Sphere mySun = new Sphere(96);
				numSphereVerts = mySun.getIndices().length;
				
				int[] indices = mySun.getIndices();
				Vector3f[] vert = mySun.getVertices();
				Vector2f[] tex  = mySun.getTexCoords();
				Vector3f[] norm = mySun.getNormals();
				
				float[] pvalues = new float[indices.length*3];
				float[] tvalues = new float[indices.length*2];
				float[] nvalues = new float[indices.length*3];
				
				for (int i=0; i<indices.length; i++)
				{	pvalues[i*3] = (float) (vert[indices[i]]).x;
					pvalues[i*3+1] = (float) (vert[indices[i]]).y;
					pvalues[i*3+2] = (float) (vert[indices[i]]).z;
					tvalues[i*2] = (float) (tex[indices[i]]).x;
					tvalues[i*2+1] = (float) (tex[indices[i]]).y;
					nvalues[i*3] = (float) (norm[indices[i]]).x;
					nvalues[i*3+1]= (float)(norm[indices[i]]).y;
					nvalues[i*3+2]=(float) (norm[indices[i]]).z;
				}
				
				
				//Spaceship Model Coordinates taken from textbook
				
				numObjVertices = myModel.getNumVertices();
				Vector3f[] vertices = myModel.getVertices();
				Vector2f[] texCoords = myModel.getTexCoords();
				Vector3f[] normals = myModel.getNormals();
				
				float[] pvaluesShip = new float[numObjVertices*3];
				float[] tvaluesShip = new float[numObjVertices*2];
				float[] nvaluesShip = new float[numObjVertices*3];
				
				for (int i=0; i<numObjVertices; i++)
				{	pvaluesShip[i*3]   = (float) (vertices[i]).x();
					pvaluesShip[i*3+1] = (float) (vertices[i]).y();
					pvaluesShip[i*3+2] = (float) (vertices[i]).z();
					tvaluesShip[i*2]   = (float) (texCoords[i]).x();
					tvaluesShip[i*2+1] = (float) (texCoords[i]).y();
					nvaluesShip[i*3]   = (float) (normals[i]).x();
					nvaluesShip[i*3+1] = (float) (normals[i]).y();
					nvaluesShip[i*3+2] = (float) (normals[i]).z();
				}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		//Pyramid
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramidPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
		
		//Pyramid Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrTexBuf = Buffers.newDirectFloatBuffer(pyrTextureCoordinates);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrTexBuf.limit()*4, pyrTexBuf, GL_STATIC_DRAW);
		
		//My Shape
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer myShapeBuf = Buffers.newDirectFloatBuffer(myShape);
		gl.glBufferData(GL_ARRAY_BUFFER, myShapeBuf.limit()*4, myShapeBuf, GL_STATIC_DRAW);
		
		//My Shape Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pyrBuf2 = Buffers.newDirectFloatBuffer(myShapeTexture);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf2.limit()*4, pyrBuf2, GL_STATIC_DRAW);
		
		//Sun Sphere
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
		
		//SpaceShip Model
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer vertBufShip = Buffers.newDirectFloatBuffer(pvaluesShip);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBufShip.limit()*4, vertBufShip, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer texBufShip = Buffers.newDirectFloatBuffer(tvaluesShip);
		gl.glBufferData(GL_ARRAY_BUFFER, texBufShip.limit()*4, texBufShip, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer norBufShip = Buffers.newDirectFloatBuffer(nvaluesShip);
		gl.glBufferData(GL_ARRAY_BUFFER, norBufShip.limit()*4,norBufShip, GL_STATIC_DRAW);

	}
	
	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	
}