package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author johhki-4
 */
public class PauseState extends BaseAppState{
    private Main simpleApp;
    Player player;
    BitmapFont endFont;
    BitmapText endMsg;
    private boolean restart = false;

    @Override
    protected void initialize(Application app) {
        simpleApp = (Main) app;
        restart = false;
    }
    
    private void initKeys() {
        simpleApp.getInputManager().addMapping("Again", new KeyTrigger(KeyInput.KEY_P));
        simpleApp.getInputManager().addMapping("Exit",  new KeyTrigger(KeyInput.KEY_E));
        
        simpleApp.getInputManager().addListener(actionListener,"Again", "Exit");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Exit") && keyPressed) {
                simpleApp.stop();
            }
            if (name.equals("Again") && keyPressed) {
                restart = true;
                runAgain();
            }
        }
    };
    
    public boolean runAgain() {
        return restart;
    }
    
    private void initEndGUI() {
        endFont = simpleApp.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        endMsg = new BitmapText(endFont, true);
        endMsg.setSize(endFont.getCharSet().getRenderedSize()*4);
        endMsg.setColor(ColorRGBA.White);
        endMsg.setText("Press E to exit or P to restart the game"); 
        endMsg.setLocalTranslation(180, 800, 0);
        simpleApp.getGuiNode().attachChild(endMsg);
    }
    
    @Override
    public void update(float tpf) {
        
    }

    @Override
    protected void cleanup(Application app) {
        
    }

    @Override
    protected void onEnable() {
        restart = false;
        initKeys();
        Board board = new Board(simpleApp.getAssetManager());
        initEndGUI();
        simpleApp.getRootNode().attachChild(board.createBoard());
        simpleApp.getGuiNode().attachChild(endMsg);
    }

    @Override
    protected void onDisable() {
        restart = true;
        simpleApp.getGuiNode().detachAllChildren();
        simpleApp.getInputManager().clearMappings();
    }
    
    
}
