package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Random;
import static mygame.Board.FRAME_THICKNESS;
import static mygame.GameState.DISK_HEIGHT;
import static mygame.GameState.FRICTION;
import static mygame.GameState.MAX_SPEED;
import static mygame.GameState.NEGDISK_R;
import static mygame.GameState.POSDISK_R;
import static mygame.GameState.POSNEG_MAX_COORD;

/**
 *
 * @author Boberz
 */
public class PosPuck {
    AssetManager assetManager;
    Geometry geoPlusPoint, geoPoint;
    Material matPlusPoint, matPoint;
    Node plusNode, pointNode;
    Random rnd;
    int id;
    int points;
    float speedX, speedY;
    float posX, posY, posZ;
    float WEIGHT = (float) (Math.PI * POSDISK_R * POSDISK_R);
    ArrayList<Spatial> pts = new ArrayList<>();
    
    //Coordinate of the point indicators atop the puck. If you want 
    // to add another place on the puck where point can be, just 
    // add three coordinates like shown below.
    static final float POINT_POS[] = new float[] {
        0f,             0f,             DISK_HEIGHT/2,
        -POSDISK_R/3,   POSDISK_R/3,    DISK_HEIGHT/2,
        POSDISK_R/3,    POSDISK_R/3,    DISK_HEIGHT/2,
        -POSDISK_R/3,   -POSDISK_R/3,   DISK_HEIGHT/2,
        POSDISK_R/3,    -POSDISK_R/3,   DISK_HEIGHT/2
        //X_coordinate, Y_coordinate,   Z_coordinate
    };
    
    //All the valid spawn locations for positive pucks.
    //Easy to add more valid spawn locations
    static final float SPAWN_LOC_POS[] = new float[] {
        -POSNEG_MAX_COORD,-POSNEG_MAX_COORD,DISK_HEIGHT/2,
        -POSNEG_MAX_COORD,0,                DISK_HEIGHT/2,
        -POSNEG_MAX_COORD,POSNEG_MAX_COORD, DISK_HEIGHT/2,
        0,                -POSNEG_MAX_COORD,DISK_HEIGHT/2,
        0,                POSNEG_MAX_COORD, DISK_HEIGHT/2,
        POSNEG_MAX_COORD, -POSNEG_MAX_COORD,DISK_HEIGHT/2,
        POSNEG_MAX_COORD, 0,                DISK_HEIGHT/2,
        POSNEG_MAX_COORD, POSNEG_MAX_COORD, DISK_HEIGHT/2
        //X_coordinate  , Y_coordinate    , Z_coordinate
    };
    
    //Constructor for the assetManager
    public PosPuck(AssetManager assetManager, int id, int points){
        this.assetManager = assetManager;
        this.id = id;
        this.points = points;
        this.pts = new ArrayList<>();
    }
    
    public void addPos(int i) {
        //green puck
        rnd = new Random();
        Cylinder plusPoint = new Cylinder(80, 80, POSDISK_R, DISK_HEIGHT, true);
        geoPlusPoint = new Geometry("Cylinder", plusPoint);
        matPlusPoint = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        plusNode = new Node("plusNode");
        posX = SPAWN_LOC_POS[0+(i*3)];
        posY = SPAWN_LOC_POS[1+(i*3)];
        posZ = SPAWN_LOC_POS[2+(i*3)];
        
        plusNode.setLocalTranslation(posX, posY, posZ);
        matPlusPoint.setColor("Color", ColorRGBA.Green);
        geoPlusPoint.setMaterial(matPlusPoint);
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
        
        //points on the puck
        for (int j = 0; j < points; j++) {
            Sphere point = new Sphere(80, 80, 2);
            geoPoint = new Geometry("Sphere", point);
            matPoint = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            pointNode = new Node("pointNode");
            pointNode.setLocalTranslation(POINT_POS[0+(j*3)], POINT_POS[1+(j*3)], POINT_POS[2+(j*3)]);
            matPoint.setColor("Color", ColorRGBA.Black);
            geoPoint.setMaterial(matPoint);

            pointNode.attachChild(geoPoint);
            this.pts.add(pointNode);
            plusNode.attachChild(geoPlusPoint); //the puck itself
            plusNode.attachChild(pts.get(j)); //points on puck
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

        if (speedX < 0) {
            speedX = speedX + FRICTION;
        }
        else {
            speedX = speedX - FRICTION;
        }
        if (speedY < 0) {
            speedY = speedY + FRICTION;
        }
        else {
            speedY = speedY - FRICTION;
        }
        
        posX = posX + (speedX * tpf);
        posY = posY + (speedY * tpf);
        posZ = plusNode.getLocalTranslation().z;
        plusNode.setLocalTranslation(posX, posY, posZ);
    }
    
    public void wallCollision(ArrayList<Node> walls) {
        float edgeLeft =    ((walls.get(0).getLocalTranslation().x) + FRAME_THICKNESS + POSDISK_R);
        float edgeRight =   ((walls.get(1).getLocalTranslation().x) - FRAME_THICKNESS - POSDISK_R);
        float edgeTop =     ((walls.get(2).getLocalTranslation().y) - FRAME_THICKNESS - POSDISK_R);
        float edgeBottom =  ((walls.get(3).getLocalTranslation().y) + FRAME_THICKNESS + POSDISK_R);
        if (posX <= edgeLeft && speedX < 0) {
            posX = edgeLeft;
            speedX = speedX * -1;
        }
        if (posX >= edgeRight && speedX > 0) {
            posX = edgeRight;
            speedX = speedX * -1;
        }
        if (posY >= edgeTop && speedY > 0) {
            posY = edgeTop;
            speedY = speedY * -1;
        }
        if (posY <= edgeBottom && speedY < 0) {
            posY = edgeBottom;
            speedY = speedY * -1;
        }
    }
    
    public void puckCollision(ArrayList<PosPuck> pos, ArrayList<NegPuck> neg, float tpf) {
        float difX, difY;
        float hypo;
        for (int i = 0; i < neg.size(); i++) {
            difX = posX - neg.get(i).posX;
            difY = posY - neg.get(i).posY;
            hypo = (float) Math.sqrt(difX*difX + difY*difY);

            if (hypo <= NEGDISK_R + NEGDISK_R) {
                collWithNeg(neg.get(i), tpf);
                puckMovement(tpf);
                neg.get(i).puckMovement(tpf);
            }  
        }
        for (int j = this.id+1; j < pos.size(); j++) {
            difX = posX - pos.get(j).posX;
            difY = posY - pos.get(j).posY;
            hypo = (float) Math.sqrt(difX*difX + difY*difY);

            if (hypo <= POSDISK_R + POSDISK_R) {
                collWithPos(pos.get(j), tpf);
                puckMovement(tpf);
                pos.get(j).puckMovement(tpf);
                
            }  
        }
    }
    
    static void removeGeom(PosPuck puck) {
        if (puck.plusNode.getChildren().size() > 1) {
            puck.plusNode.detachChildAt(1);
            puck.points = puck.points - 1;
            puck.pts.remove(puck.points);
        }
    }
    
    private void collWithPos(PosPuck curPuck, float tpf) {
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

        float Va2n = (VAn * (curPuck.WEIGHT - WEIGHT) + (2 * curPuck.WEIGHT * VBn)) / 
                (WEIGHT + curPuck.WEIGHT);
        float Vb2n = (VBn * (WEIGHT - curPuck.WEIGHT) + (2 * WEIGHT * VAn)) /
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

        float Va2n = (VAn * (curPuck.WEIGHT - WEIGHT) + (2 * curPuck.WEIGHT * VBn)) / 
                (WEIGHT + curPuck.WEIGHT);
        float Vb2n = (VBn * (WEIGHT - curPuck.WEIGHT) + (2 * WEIGHT * VAn)) /
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
