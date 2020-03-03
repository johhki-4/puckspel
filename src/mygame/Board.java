package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author johhki-4
 */
public class Board {
    AssetManager assetManager;
    ArrayList<Node> walls = new ArrayList<>(); 
    
    // thickness of the sides of the frame
    static final float FRAME_THICKNESS = 24f; 
    // width (and height) of the free area inside the frame, where disks move
    static final float FREE_AREA_WIDTH = 292f; 
    // total outer width (and height) of the frame
    static final float FRAME_SIZE = FREE_AREA_WIDTH + 2f * FRAME_THICKNESS; 
    
    static final float FRAME_VER[] = new float[] {
        -(FREE_AREA_WIDTH-FRAME_THICKNESS), 0, 0,
          FREE_AREA_WIDTH-FRAME_THICKNESS , 0, 0
    };
    static final float FRAME_HOR[] = new float[] {
        0,   FREE_AREA_WIDTH-FRAME_THICKNESS , 0,
        0, -(FREE_AREA_WIDTH-FRAME_THICKNESS), 0
    };
    
    //All parts of the game board
    Geometry geoBoard, geoFrame;
    Material matBoard, matFrame;
    Node boardNode, wallNode, frameNode;
    
    //Constructor for the assetManager
    public Board(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    public Node createBoard() {
        Box board = new Box(FREE_AREA_WIDTH, FREE_AREA_WIDTH, 1f);
        geoBoard = new Geometry("Box", board);
        matBoard = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        boardNode = new Node("boardNode");
        frameNode = new Node("frameNode");
        frameNode.setLocalTranslation(0f, 0f, FRAME_THICKNESS);
        
        matBoard.setColor("Color", ColorRGBA.White);
        geoBoard.setMaterial(matBoard);
        boardNode.attachChild(geoBoard);
        boardNode.setLocalTranslation(0f, 0f, 0f);
        
        for (int i = 0; i < FRAME_VER.length/3; i++) {
            Box ver = new Box(FRAME_THICKNESS, FREE_AREA_WIDTH, FRAME_THICKNESS);
            geoFrame = new Geometry("Box", ver);
            matFrame = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            matFrame.setTexture("ColorMap",assetManager.loadTexture("Textures/WoodVertical.jpg"));
            wallNode = new Node("wallNode");
            wallNode.setLocalTranslation(FRAME_VER[0+(i*3)], FRAME_VER[1+(i*3)], FRAME_VER[2+(i*3)]);
            geoFrame.setMaterial(matFrame);
            
            wallNode.attachChild(geoFrame);
            walls.add(wallNode);
            frameNode.attachChild(wallNode);
        }
        for (int i = 0; i < FRAME_HOR.length/3; i++) {
            Box hor = new Box(FREE_AREA_WIDTH, FRAME_THICKNESS, FRAME_THICKNESS);
            geoFrame = new Geometry("Box", hor);
            matFrame = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            matFrame.setTexture("ColorMap",assetManager.loadTexture("Textures/WoodHorizontal.jpg"));
            wallNode = new Node("wallNode");
            wallNode.setLocalTranslation(FRAME_HOR[0+(i*3)], FRAME_HOR[1+(i*3)], FRAME_HOR[2+(i*3)]);
            geoFrame.setMaterial(matFrame);
            
            wallNode.attachChild(geoFrame);
            walls.add(wallNode);
            frameNode.attachChild(wallNode);
        }
        
        boardNode.attachChild(frameNode);
        return boardNode;
    }
    public void removePucks() {
        boardNode.detachAllChildren();
    }
}
