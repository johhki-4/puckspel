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
import static mygame.GameState.INITSPEED;
import static mygame.GameState.MAX_SPEED;
import static mygame.GameState.NEGDISK_R;
import static mygame.GameState.PLAYER_COORD;
import static mygame.GameState.PLAYER_R;
import static mygame.GameState.POSDISK_R;

/**
 *
 * @author Boberz
 */
public class Player {
    private final AssetManager assetManager;
    Random rnd;
    int id;
    int playerPoints;
    float speedX, speedY;
    float posX, posY, posZ;
    float timeSincePointN;
    float timeSincePointP;
    float WEIGHT = (float) (Math.PI * PLAYER_R * PLAYER_R);
    boolean gotPointP = false;
    boolean gotPointN = false;
    Geometry geoPlayer;
    Material matPlayer;
    Node playerNode;
    
    static final float SPAWN_LOC_PLAYER[] = new float[] {
        0,              0,              DISK_HEIGHT/2, 
        -PLAYER_COORD, -PLAYER_COORD,   DISK_HEIGHT/2,
        -PLAYER_COORD,  0,              DISK_HEIGHT/2,
        -PLAYER_COORD,  PLAYER_COORD,   DISK_HEIGHT/2,
        0,             -PLAYER_COORD,   DISK_HEIGHT/2,
        0,              PLAYER_COORD,   DISK_HEIGHT/2,
         PLAYER_COORD, -PLAYER_COORD,   DISK_HEIGHT/2,
         PLAYER_COORD,  0,              DISK_HEIGHT/2,
         PLAYER_COORD,  PLAYER_COORD,   DISK_HEIGHT/2
        //X_coordinate, Y_coordinate,   Z_coordinate
    };
    
    public Player(AssetManager assetManager, int id, int playerPoints){
        this.assetManager = assetManager;
        this.id = id;
        this.playerPoints = playerPoints;
    }
    
    public void addPlayer(int i) {
        rnd = new Random();
        int spawn = rnd.nextInt(SPAWN_LOC_PLAYER.length/3);
        Cylinder playerDisk = new Cylinder(80, 80, PLAYER_R, DISK_HEIGHT, true);
        geoPlayer = new Geometry("Cylinder", playerDisk);
        matPlayer = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerNode = new Node("playerNode");
        posX = SPAWN_LOC_PLAYER[0 + (3*spawn)]; // Left/right
        posY = SPAWN_LOC_PLAYER[1 + (3*spawn)]; // Up/down
        posZ = SPAWN_LOC_PLAYER[2 + (3*spawn)]; // In/outwards
        
        playerNode.setLocalTranslation(posX, posY , posZ);
        matPlayer.setColor("Color", ColorRGBA.Blue);
        geoPlayer.setMaterial(matPlayer);
        playerNode.attachChild(geoPlayer);
    }
    
    public void wallCollision(ArrayList<Node> walls) {
        float edgeLeft =    ((walls.get(0).getLocalTranslation().x) + FRAME_THICKNESS + PLAYER_R);
        float edgeRight =   ((walls.get(1).getLocalTranslation().x) - FRAME_THICKNESS - PLAYER_R);
        float edgeTop =     ((walls.get(2).getLocalTranslation().y) - FRAME_THICKNESS - PLAYER_R);
        float edgeBottom =  ((walls.get(3).getLocalTranslation().y) + FRAME_THICKNESS + PLAYER_R);
        if (posX <= edgeLeft) {
            posX = edgeLeft;
            speedX = speedX * -1;
        }
        else if (posX >= edgeRight) {
            posX = edgeRight;
            speedX = speedX * -1;
        }
        if (posY >= edgeTop) {
            posY = edgeTop;
            speedY = speedY * -1;
        }
        else if (posY <= edgeBottom) {
            posY = edgeBottom;
            speedY = speedY * -1;
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
        else if (speedX > 0.0f) {
            speedX = speedX - FRICTION;
        }
        if (speedY < 0.0f) {
            speedY = speedY + FRICTION;
        }
        else if (speedY > 0.0f) {
            speedY = speedY - FRICTION;
        }
        
        posX = posX + (speedX * tpf);
        posY = posY + (speedY * tpf);
        posZ = playerNode.getLocalTranslation().z;
        playerNode.setLocalTranslation(posX, posY, posZ);
    }

    public void movePlayer(String name, float tpf) {
        switch (name) {
            case "Left":
                speedX = speedX - (INITSPEED/100);
                break;

            case "Right":
                speedX = speedX + (INITSPEED/100);
                break;

            case "Down":
                speedY = speedY - (INITSPEED/100);
                break;

            case "Up":
                speedY = speedY + (INITSPEED/100);
                break;
        }
    }
    
    public PosPuck puckCollision(ArrayList<PosPuck> pos, ArrayList<NegPuck> neg, float tpf) {
        timeSincePointP = timeSincePointP + tpf;
        timeSincePointN = timeSincePointN + tpf;
        float difX;
        float difY;
        float hypo;
        
        if (timeSincePointN > 0.1f) {
            gotPointN = false;
        }
        if (timeSincePointP > 0.1f) {
            gotPointP = false;
        }
        //check collisions with negative disks
        for (int i = 0; i < neg.size(); i++) {
            difX = posX - neg.get(i).posX;
            difY = posY - neg.get(i).posY;
            hypo = (float) Math.sqrt(difX*difX + difY*difY);

            if (hypo <= PLAYER_R + NEGDISK_R) {
                collWithNeg(neg.get(i), tpf);
                puckMovement(tpf);
                neg.get(i).puckMovement(tpf);
                if (gotPointN == false) {
                    losePoints(neg.get(i).points);
                    timeSincePointN = 0;
                    gotPointN = true;
                }
            }
        }
        
        //check collisions with positive disks
        for (int j = 0; j < pos.size(); j++) {
            difX = posX - pos.get(j).posX;
            difY = posY - pos.get(j).posY;
            hypo = (float) Math.sqrt(difX*difX + difY*difY);

            if (hypo <= PLAYER_R + POSDISK_R) {
                collWithPos(pos.get(j), tpf);
                puckMovement(tpf);
                pos.get(j).puckMovement(tpf);
                if (gotPointP == false) {
                    gainPoints(pos.get(j));
                    timeSincePointP = 0;
                    gotPointP = true;
                }
            }  
        }
        return null;
    }

    private void losePoints(int pts) {
        this.playerPoints = this.playerPoints - pts;
    }
    
    private void gainPoints(PosPuck puck) {
        this.playerPoints = this.playerPoints + puck.points;
        if (puck.points <= 0) {
            puck.points = 0;
        }
        else {
            PosPuck.removeGeom(puck);
        }
    }
    
    private void collWithPos(PosPuck curPuck, float tpf) {
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
