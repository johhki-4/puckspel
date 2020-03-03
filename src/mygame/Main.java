package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 
 * 
 * @author Boberz
 */
public class Main extends SimpleApplication {
    private GameState gs = new GameState();
    private PauseState ps = new PauseState();
    Player player;
    
    boolean running = true;
    final float gameTime = 30f;
    public float roundTime = gameTime;
    final float initSpeed = 0.5f;
    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();  
    }
    Main() {
        gs.setEnabled(true);
        ps.setEnabled(false);
        
        stateManager.attach(gs);
        stateManager.attach(ps);
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-160f, 0.0f, 720f));
        cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
        player = new Player(assetManager, 0, 0);
        flyCam.setMoveSpeed(600);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (gs.isEnabled()) {
            roundTime = roundTime - tpf;
        if (roundTime <= 0f) {  
            roundTime = gameTime;
            gs.setEnabled(false);
            ps.setEnabled(true);
        }
        }
        if (ps.isEnabled()) {
            if (ps.runAgain()) {
                ps.setEnabled(false);
                gs.setEnabled(true);
            }
            
        }
    }
}

