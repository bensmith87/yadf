package yadf.ui.gdx.screen.game.toolbar;

import java.util.Collection;

import yadf.ui.gdx.screen.game.interactor.IInteractor;
import yadf.ui.gdx.screen.game.interactor.IInteractorListener;
import yadf.ui.gdx.screen.game.interactor.IInteractorManager;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Abstract class for an Interactor Toolbar.
 * <p>
 * Interactor toolbars are toolbars where the buttons install interactors.
 * @param <T> the type
 */
public abstract class AbstractInteractorToolbar<T> extends AbstractToolbar implements IInteractorListener {

    /** The toolbar manager. */
    private IToolbarManager toolbarManager;

    /** The interactor manager. */
    private IInteractorManager interactorManager;

    /**
     * Constructor.
     * @param toolbarManagerTmp the toolbar manager
     * @param interactorManagerTmp the interactor manager
     */
    public AbstractInteractorToolbar(final IToolbarManager toolbarManagerTmp,
            final IInteractorManager interactorManagerTmp) {
        toolbarManager = toolbarManagerTmp;
        interactorManager = interactorManagerTmp;

        setFillParent(true);
        align(Align.top | Align.left);
        pad(10);

        for (T type : getTypes()) {
            Button button = addButton(type.toString(), toolbarManager.getSkin());
            button.addListener(new ButtonListener(type));
        }

        Button cancelButton = addButton("Cancel", toolbarManager.getSkin());
        cancelButton.addListener(new CancelButtonListener());
    }

    /**
     * Get the types, there will be a button per type, will use toString for button name.
     * @return the types
     */
    protected abstract Collection<T> getTypes();

    /**
     * Create the interactor.
     * @param type the type
     * @return the interactor
     */
    protected abstract IInteractor createInteractor(T type);

    @Override
    public void interactionDone(final IInteractor interactor) {
        toolbarManager.closeToolbar();
    }

    /**
     * The listener for all of the buttons.
     */
    private final class ButtonListener extends ClickListener {

        /** The type of the workshop. */
        private T type;

        /**
         * Constructor.
         * @param typeTmp the type
         */
        public ButtonListener(final T typeTmp) {
            type = typeTmp;
        }

        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            IInteractor interactor = createInteractor(type);
            interactor.addListener(AbstractInteractorToolbar.this);
            interactorManager.installInteractor(interactor);
        }
    }

    /**
     * The listener for the cancel button.
     */
    private final class CancelButtonListener extends ClickListener {

        @Override
        public void clicked(final InputEvent event, final float x, final float y) {
            toolbarManager.closeToolbar();
        }
    }
}
