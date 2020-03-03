package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.Random;
import static mygame.Board.FRAME_THICKNESS;
import static mygame.GameState.DISK_HEIGHT;
import static mygame.GameState.FRICTION;
import static mygame.GameState.MAX_SPEED;
import static mygame.GameState.NEGDISK_R;
import static mygame.GameState.POSNEG_MAX_COORD;

/**
 *
 * @author Boberz
 */
public class NegPuck {
    AssetManager assetManager;
    
    Geometry geoNegPoint;
    Material matNegPoint;
    Node negNode;
    Random rnd;
    private boolean first = true;
    int id;
    int points;
    float speedX, speedY;
    float posX, posY, posZ;
    float WEIGHT = (float) (Math.PI * NEGDISK_R * NEGDISK_R);
    
    static final float SPAWN_LOC_NEG[] = new float[] {
        -POSNEG_MAX_COORD/2,-POSNEG_MAX_COORD,  DISK_HEIGHT/2,
        -POSNEG_MAX_COORD,  -POSNEG_MAX_COORD/2,DISK_HEIGHT/2,
        -POSNEG_MAX_COORD,   POSNEG_MAX_COORD/2,DISK_HEIGHT/2,
        -POSNEG_MAX_COORD/2, POSNEG_MAX_COORD,  DISK_HEIGHT/2,
         POSNEG_MAX_COORD/2, POSNEG_MAX_COORD,  DISK_HEIGHT/2,
         POSNEG_MAX_COORD,   POSNEG_MAX_COORD/2,DISK_HEIGHT/2,
         POSNEG_MAX_COORD,  -POSNEG_MAX_COORD/2,DISK_HEIGHT/2,
         POSNEG_MAX_COORD/2,-POSNEG_MAX_COORD,  DISK_HEIGHT/2
        //X_coordinate,     Y_coordinate,       Z_coordinate
    };
            
    //Constructor for the assetManager
    public NegPuck(AssetManager assetManager, int id, int points){
        this.assetManager = assetManager;
        this.id = id;
        this.points = points;
    }
    
    public void addNeg(int i) {
        rnd = new Random();
        Cylinder negPoint = new Cylinder(80, 80, NEGDISK_R, DISK_HEIGHT, true);
        geoNegPoint = new Geometry("Cylinder", negPoint);
        matNegPoint = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        negNode = new Node("negNode");
        posX = SPAWN_LOC_NEG[0 + (i*3)];
        posY = SPAWN_LOC_NEG[1 + (i*3)];
        posZ = SPAWN_LOC_NEG[2 + (i*3)];
        negNode.setLocalTranslation(posX, posY , posZ);
        
        matNegPoint.setColor("Color", ColorRGBA.Red);
        geoNegPoint.setMaterial(matNegPoint);
        negNode.attachChild(geoNegPoint);
        if (rnd.nextBoolean()) {
            speedX = rnd.nextFloat() * 10;
        }
        else {
            speedX = rnd.nextFloat() * -10;
        }
        if (rnd.nextBoolean()) {
            speedY = rnd.nextFloat() * 10;
        }
        else {
            speedY = rnd.nextFloat() * -10;
        }
    }
    
    public void puckMovement(float tpf) {
        if (speedX < -MAX_SPEED) {
            speedX = -MAX_SPEED;
        }
        else if (speedX > MAX_SPEED) {
            speedX = MAX_SPEED;
        }
        if (speedY < -MAX_SPEED) {
            speedY = -MAX_SPEED;
        }
        else if (speedY > MAX_SPEED) {
            speedY = MAX_SPEED;
        }
        if (speedX < 0.0f) {
            speedX = speedX + FRICTION;
        }
        else {
            speedX = speedX - FRICTION;
        }
        if (speedY < 0.0f) {
            speedY = speedY + FRICTION;
        }
        else {
            speedY = speedY - FRICTION;
        }
        
        posX = posX + speedX * tpf;
        posY = posY + speedY * tpf;
        posZ = negNode.getLocalTranslation().z;
        negNode.setLocalTranslation(posX, posY, posZ);
    }
    
    public void wallCollision(ArrayList<Node> walls) {
        float edgeLeft =    ((walls.get(0).getLocalTranslation().x) + FRAME_THICKNESS + NEGDISK_R);
        float edgeRight =   ((walls.get(1).getLocalTranslation().x) - FRAME_THICKNESS - NEGDISK_R);
        float edgeTop =     ((walls.get(2).getLocalTranslation().y) - FRAME_THICKNESS - NEGDISK_R);
        float edgeBottom =  ((walls.get(3).getLocalTranslation().y) + FRAME_THICKNESS + NEGDISK_R);
        if (posX <= edgeLeft) {
            posX = edgeLeft;
            speedX = speedX * -1;
        }
        if (posX >= edgeRight) {
            posX = edgeRight;
            speedX = speedX * -1;
        }
        if (posY >= edgeTop) {
            posY = edgeTop;
            speedY = speedY * -1;
        }
        if (posY <= edgeBottom) {
            posY = edgeBottom;
            speedY = speedY * -1;
        }
    }
    
    public void puckCollision(ArrayList<NegPuck> neg, float tpf) {
        float difX;
        float difY;
        float hypo;
        
        for (int i = this.id + 1; i < neg.size(); i++) {
            difX = posX - neg.get(i).posX;
            difY = posY - neg.get(i).posY;
            hypo = (float) Math.sqrt(difX*difX + difY*difY);

            if (hypo <= NEGDISK_R + NEGDISK_R) {
                collWithNeg(neg.get(i), tpf);
                puckMovement(tpf);
                neg.get(i).puckMovement(tpf);
                
                }
            }
        }
   
    private void collWithNeg(NegPuck curPuck, float tpf) {
        //Old positions
        float posAX = posX;
        float posAY = posY;
        float posBX = curPuck.posX;
        float posBY = curPuck.posY;

        Vector3f distA = new Vector3f(posAX, posAY, 0);
        Vector3f distB = new Vector3f(posBX, posBY, 0);
        Vector3f dist = new Vector3f((distA.subtract(distB)).divide(2)).mult(tpf);

        float speedAX = speedX - dist.x;
        float speedAY = speedY - dist.y;
        float speedBX = curPuck.speedX - dist.x;
        float speedBY = curPuck.speedY - dist.y;

        posAX = posAX - speedAX;
        posAY = posAY - speedAY;
        posBX = posBX - speedBX;
        posBY = posBY - speedBY;

        Vector3f normal = new Vector3f(posBX - posAX, posBY - posAY, 0);
        float magn =(float) Math.sqrt(Math.pow(normal.x, 2)+ Math.pow(normal.y, 2));
        Vector3f unit = new Vector3f(normal.x, normal.y,0).divide(magn);
        Vector3f tangent = new Vector3f(-unit.y, unit.x,0);

        Vector3f Va = new Vector3f(speedAX, speedAY, 0);
        Vector3f Vb = new Vector3f(speedBX, speedBY, 0);

        float VAn = unit.dot(Va);
        float VBn = unit.dot(Vb);
        float VAt = tangent.dot(Va);
        float VBt = tangent.dot(Vb);

        float VA2t = VAt;
        float VB2t = VBt;

        float Va2n = (VAn * (WEIGHT - curPuck.WEIGHT) + (2 * curPuck.WEIGHT * VBn)) / 
                (WEIGHT + curPuck.WEIGHT);
        float Vb2n = (VBn * (curPuck.WEIGHT - WEIGHT) + (2 * WEIGHT * VAn)) /
                (WEIGHT + curPuck.WEIGHT);

        Vector3f Va2nVEC = new Vector3f(unit.mult(Va2n));
        Vector3f Va2tVEC = new Vector3f(tangent.mult(VA2t));
        Vector3f Vb2nVec = new Vector3f(unit.mult(Vb2n));
        Vector3f Vb2tVec = new Vector3f(tangent.mult(VB2t));

        Vector3f newSpeed1 = new Vector3f(Va2nVEC.add(Va2tVEC));
        Vector3f newSpeed2 = new Vector3f(Vb2nVec.add(Vb2tVec));

        speedX = newSpeed1.x;
        speedY = newSpeed1.y;
        curPuck.speedX = newSpeed2.x;
        curPuck.speedY = newSpeed2.y;
        puckMovement(tpf);
        curPuck.puckMovement(tpf);
    }
}
