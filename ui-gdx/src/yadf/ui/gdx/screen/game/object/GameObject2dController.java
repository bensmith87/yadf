package yadf.ui.gdx.screen.game.object;

import java.util.HashMap;
import java.util.Map;

import yadf.simulation.IGameObject;
import yadf.simulation.IGameObjectManagerListener;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * A game object 2D controller.
 * <p>
 * The game object 2D controller creates game object 2Ds whenever there in a new game object in the simulation, it also
 * removes them.
 */
public class GameObject2dController implements IGameObjectManagerListener {

    /** The game object 2Ds that this controller has created. */
    private Map<IGameObject, Actor> gameObject2ds = new HashMap<>();

    /** The stage to add the game object 2Ds to. */
    private Stage gameStage;

    /** The class of the game object 2D. */
    private Class<? extends Actor> gameObject2dClass;

    /** The texture atlas. */
    private TextureAtlas textureAtlas;

    /**
     * Constructor.
     * @param gameObject2dClassTmp the class of the game object 2D
     * @param textureAtlasTmp the texture atlas
     * @param gameStageTmp the stage to add the game object 2Ds to
     */
    public GameObject2dController(final Class<? extends Actor> gameObject2dClassTmp,
            final TextureAtlas textureAtlasTmp, final Stage gameStageTmp) {
        gameStage = gameStageTmp;
        textureAtlas = textureAtlasTmp;
        gameObject2dClass = gameObject2dClassTmp;
    }

    @Override
    public void gameObjectAdded(final IGameObject gameObject, final int index) {
        assert !gameObject2ds.containsKey(gameObject);
        Actor gameObject2d = createGameObject2d(gameObject);
        gameObject2ds.put(gameObject, gameObject2d);
        gameStage.addActor(gameObject2d);
    }

    @Override
    public void gameObjectRemoved(final IGameObject gameObject, final int index) {
        assert gameObject2ds.containsKey(gameObject);
        Actor gameObject2d = gameObject2ds.remove(gameObject);
        gameStage.getRoot().removeActor(gameObject2d);
    }

    /**
     * Create the game object 2D.
     * @param gameObject the game object to create the game object 2D for
     * @return the game object 2D
     */
    protected Actor createGameObject2d(final IGameObject gameObject) {
        Actor actor = null;
        try {
            actor = (Actor) gameObject2dClass.getDeclaredConstructors()[0].newInstance(gameObject, textureAtlas);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
        return actor;
    }

    @Override
    public void gameObjectAvailable(final IGameObject gameObject) {
        // Do nothing
    }
}
