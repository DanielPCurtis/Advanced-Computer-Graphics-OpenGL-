package a4;


import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private float uVector;
	private float vVector;
	private float nVector;
	private float totalPitch;
	private float totalPan;
	private float cosPitch;
	private float sinPitch;
	private float cosPan;
	private float sinPan;
	private Matrix4f camMat;
	private Matrix4f rotX;
	private Matrix4f rotY;
	
	public Camera(float u, float v, float n)	{
		reset();
		this.uVector = u;  // xLocation
		this.vVector = v;  // yLocation
		this.nVector = n;  // zLocation  		Pw - world space location
		camMat = new Matrix4f();
		Display();
	}
	
	public void Display()	
	{
		Vector3f newView = new Vector3f(-uVector, -vVector, -nVector);
		cosPan = (float)Math.cos(Math.toRadians(-totalPan));
		sinPan = (float)Math.sin(Math.toRadians(-totalPan));
		cosPitch = (float)Math.cos(Math.toRadians(-totalPitch));
		sinPitch = (float)Math.sin(Math.toRadians(-totalPitch));
		//rotateX();
		//rotateY();
		Vector3f xAxis = new Vector3f(cosPan, 0, sinPan);
		Vector3f yAxis = new Vector3f(sinPitch * sinPan, cosPitch, -sinPitch * cosPan);
		Vector3f zAxis = new Vector3f(-cosPitch * sinPan, sinPitch, cosPan * cosPitch);
		
		camMat = new Matrix4f(	xAxis.x(), yAxis.x(), zAxis.x(), 0,
								xAxis.y(), yAxis.y(), zAxis.y(), 0,
								xAxis.z(), yAxis.z(), zAxis.z(), 0,
								xAxis.dot(newView), yAxis.dot(newView), zAxis.dot(newView), 1
							);
	}
	
	void moveForward()	
	{
		this.nVector -= 1.0f;
		Display();
	}
	
	void moveBackward()	
	{
		this.nVector += 1.0f;
		Display();
	}

	void moveLeft()	
	{
		this.uVector -= 1.0f;
		Display();
	}
	
	void moveRight()	
	{
		this.uVector += 1.0f;
		Display();
	}
	
	void moveUp()	
	{
		this.vVector -= 1.0f;
		Display();
	}
	
	void moveDown()	
	{
		this.vVector += 1.0f;
		Display();
	}
	
	void reset() {
		this.uVector = 0.0f;
		this.vVector = 3.0f;
		this.nVector = 25.0f;
		this.totalPan = 0.0f;
		this.totalPitch = 0.0f;
		Display();
	}

	void pitchUp()	{
		this.totalPan += 1.0f;
		Display();
	}
	
	void pitchDown()	{
		this.totalPan -= 1.0f;;
		Display();
	}
	
	void panLeft()	{
		this.totalPitch += 1.0f;
		Display();
	}
	
	void panRight()	{
		this.totalPitch -= 1.0f;
		Display();
	}
	
	Matrix4f set()	{
		return camMat;
	}
	
	float returnX()	{
		return uVector;
	}
	float returnY()	{
		return vVector;
	}
	float returnZ()	{
		return nVector;
	}
}
