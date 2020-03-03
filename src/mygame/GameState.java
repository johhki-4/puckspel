package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import static mygame.Board.FREE_AREA_WIDTH;
import static mygame.NegPuck.SPAWN_LOC_NEG;
import static mygame.PosPuck.SPAWN_LOC_POS;

/**
 *
 * @author johhki-4
 */
public class GameState extends BaseAppState {
    private Main simpleApp;
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<PosPuck> posPucks = new ArrayList<>();  
    ArrayList<NegPuck> negPucks = new ArrayList<>();
    BitmapFont guiFont;
    BitmapText timeLeft;
    BitmapText playerScore;

    Board board;
    Player player;
    PosPuck posPuck;
    NegPuck negPuck;
    
    final int POS_POINT = 5;
    final int NEG_POINT = 3;
    
    static final float FRICTION = 0.001f;
    static final float INITSPEED = 20f;
    static final float MAX_SPEED = 150f;
    static final int STARTING_POINTS = 0;
    float roundTime = 5f;
    private boolean needClean = false;
    static final float PLAYER_R = 20f; // radius of a player's disk
    static final float POSDISK_R = 16f; // radius of a positive disk
    static final float NEGDISK_R = 16f; // radius of a negative disk
    static final float DISK_HEIGHT = 40f; // height of every disk
    static final int HUMAN_PLAYERS = 1; //only one can be playable, but up to 8 can be spawned
    
    static final float PLAYER_COORD = FREE_AREA_WIDTH / 6;
    static final float POSNEG_MAX_COORD = FREE_AREA_WIDTH / 3;
    static final float POSNEG_BETWEEN_COORD = PLAYER_COORD;
    
    @Override
    protected void initialize(Application app) {
        simpleApp = (Main) app;
    }

    @Override
    protected void cleanup(Application app) {
        
    }

    @Override
    protected void onEnable() {
        initKeys();
        if (needClean) {
            simpleApp.getRootNode().detachAllChildren();
            needClean = false;
        }
        roundTime = 30f;
        players.clear();
        posPucks.clear();
        negPucks.clear();
        simpleApp.getRootNode().detachAllChildren();
        board = new Board(simpleApp.getAssetManager());
                
        //add full game board(white floor and wooden walls) to the rootNode.
        simpleApp.getRootNode().attachChild(board.createBoard());
        
        for (int i = 0; i < HUMAN_PLAYERS; i++) {
            player = new Player(simpleApp.getAssetManager(), i, STARTING_POINTS);
            player.addPlayer(i);
            players.add(player);
        }
        for(int i = 0; i < players.size(); i++) {
            board.boardNode.attachChild(players.get(i).playerNode);
        }
        
        //add +point(green w/ black dots) to the game board.
        for (int i = 0; i < SPAWN_LOC_POS.length/3; i++) {
            posPuck = new PosPuck(simpleApp.getAssetManager(), i, POS_POINT);
            posPuck.addPos(i);
            posPucks.add(posPuck);
        }
        for(int i = 0; i < posPucks.size(); i++) {
            board.boardNode.attachChild(posPucks.get(i).plusNode);
        }
        
        //add -point(red) to the game board.
        for (int i = 0; i < SPAWN_LOC_NEG.length/3; i++) {
            negPuck = new NegPuck(simpleApp.getAssetManager(), i, NEG_POINT);
            negPuck.addNeg(i);
            negPucks.add(negPuck);
        }
        for(int i = 0; i < negPucks.size(); i++) {
            board.boardNode.attachChild(negPucks.get(i).negNode);
        }
        initScoreBoard();
        simpleApp.getGuiNode().detachAllChildren();
        simpleApp.getGuiNode().attachChild(playerScore);
        simpleApp.getGuiNode().attachChild(timeLeft);
    }

    @Override
    protected void onDisable() {
        needClean = true;
        simpleApp.getGuiNode().detachAllChildren();
        for (int i = 0; i < players.size(); i++) {
            playerScore = new BitmapText(guiFont, false);
            playerScore.setSize(guiFont.getCharSet().getRenderedSize() * 3);
            playerScore.setColor(ColorRGBA.Blue);
            playerScore.setText("Points for player " + (players.get(i).id+1) + ": " + players.get(i).playerPoints);
            playerScore.setLocalTranslation(30, 300 + (50*i), 0);
            simpleApp.getGuiNode().attachChild(playerScore);
        }
    }
    
    private void initKeys() {
        simpleApp.getInputManager().addMapping("Left",  new KeyTrigger(KeyInput.KEY_G));
        simpleApp.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_J));
        simpleApp.getInputManager().addMapping("Down",  new KeyTrigger(KeyInput.KEY_H));
        simpleApp.getInputManager().addMapping("Up",    new KeyTrigger(KeyInput.KEY_Y));
        
        simpleApp.getInputManager().addListener(analogListener,"Left", "Right", "Down", "Up");
    }
    
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            players.get(0).movePlayer(name, tpf);
        }
    };

    @Override
    public void update(float tpf) {
        roundTime = roundTime - tpf;
        if (roundTime <= 3) {
            timeLeft.setColor(ColorRGBA.Red);
        }
        simpleApp.getGuiNode().detachAllChildren();
        for (int i = 0; i < players.size(); i++) {
            playerScore = new BitmapText(guiFont, false);
            playerScore.setSize(guiFont.getCharSet().getRenderedSize() * 3);
            playerScore.setColor(ColorRGBA.Blue);
            playerScore.setText("Points for player " + (players.get(i).id+1) + ": " + players.get(i).playerPoints);
            playerScore.setLocalTranslation(30, 300 + (50*i), 0);
            simpleApp.getGuiNode().attachChild(playerScore);
        }
        timeLeft.setText("Time left: " + roundTime); 
        simpleApp.getGuiNode().attachChild(timeLeft);
        
        //player puck
        for (int i = 0; i < players.size(); i++) {
            players.get(i).puckMovement(tpf);
            players.get(i).wallCollision(board.walls);
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(i).puckCollision(posPucks, negPucks, tpf);
        }
        
        //positive pucks
        for (int i = 0; i < posPucks.size(); i++) {
            posPucks.get(i).puckMovement(tpf);
            posPucks.get(i).wallCollision(board.walls);
        }
        for (int i = 0; i < posPucks.size(); i++) {
            posPucks.get(i).puckCollision(posPucks, negPucks, tpf);
        }
        
        //negative pucks
        for (int i = 0; i < negPucks.size(); i++) {
            negPucks.get(i).puckMovement(tpf);
            negPucks.get(i).wallCollision(board.walls);
        }
        for (int i = 0; i < negPucks.size(); i++) {
            negPucks.get(i).puckCollision(negPucks, tpf);
        }
    }
    
    private void initScoreBoard() {
        guiFont = simpleApp.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        timeLeft = new BitmapText(guiFont, true);
        timeLeft.setSize(guiFont.getCharSet().getRenderedSize()*4);
        timeLeft.setColor(ColorRGBA.White);
        timeLeft.setText("Time left: " + roundTime); 
        timeLeft.setLocalTranslation(100, 800, 0);
        simpleApp.getGuiNode().attachChild(timeLeft);
        
        for (int i = 0; i < players.size(); i++) {
            playerScore = new BitmapText(guiFont, false);
            playerScore.setSize(guiFont.getCharSet().getRenderedSize() * 3);
            playerScore.setColor(ColorRGBA.Blue);
            playerScore.setText("Points for player " + (players.get(i).id+1) + ": " + player.playerPoints);
            playerScore.setLocalTranslation(30, 300 + (50*i), 0);
            simpleApp.getGuiNode().attachChild(playerScore);
        }
    }
}
