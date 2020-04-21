package a3;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {

	private float uVector;
	private float vVector;
	private float nVector;
	Vector3f lightMat;
	
	public Light(float x, float y, float z)	{
		resetLight();
		this.uVector = x;
		this.vVector = y;
		this.nVector = z;
		lightMat = new Vector3f();
		Display();
	}

	void Display() {
		// TODO Auto-generated method stub
		Vector3f newLightView = new Vector3f(uVector, vVector, nVector);
		lightMat = newLightView;
	}

	void resetLight() {
		// TODO Auto-generated method stub
		uVector = -3.9f;
		vVector = 2.4f;
		nVector = 3.1f;
		Display();
	}
	
	void moveLeft()	{
		this.uVector -= 0.01f;
		Display();
	}
	
	void moveRight()	{
		this.uVector += 0.01f;
		Display();
	}
	
	void moveUp()	{
		this.vVector += 0.01f;
		Display();
	}
	
	void moveDown()	{
		this.vVector -= 0.01f;
		Display();
	}
	
	void moveBack()	{
		this.nVector -= 0.01f;
		Display();
	}
	
	void moveFront()	{
		this.nVector += 0.01f;
		Display();
	}
	
	Vector3f setLight(){
		//System.out.printf("%f %f %f", uVector, vVector, nVector);
		//System.out.println("");
		return lightMat;
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
	
	void setX(float x)	{
		uVector = x;
		Display();
	}
	
	void setY(float y)	{
		vVector = y;
		Display();
	}
	void setZ(float z)	{
		nVector = z;
		Display();
	}
	
}
