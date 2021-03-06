package yadf.ui.swing.game.military;

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import yadf.simulation.character.IGameCharacter;
import yadf.simulation.military.IMilitaryManager;
import yadf.simulation.military.IMilitaryManagerListener;

/**
 * The soldiers table model.
 */
public class SoldiersTableModel extends AbstractTableModel implements IMilitaryManagerListener {

    /** The serial version UID. */
    private static final long serialVersionUID = -3606638170944881663L;

    /** The military manager. */
    private IMilitaryManager militaryManager;

    /**
     * Constructor.
     * @param militaryManagerTmp the military manager
     */
    public SoldiersTableModel(final IMilitaryManager militaryManagerTmp) {
        militaryManager = militaryManagerTmp;
        militaryManager.addMilitaryManagerListener(this);
        // TODO: remove military manager listener
    }

    @Override
    public int getRowCount() {
        Set<IGameCharacter> soldiers = militaryManager.getSoldiers();
        return soldiers.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return "Soldier name";
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        IGameCharacter[] soldiers = militaryManager.getSoldiers().toArray(new IGameCharacter[0]);
        return soldiers[rowIndex];
    }

    @Override
    public void soldierAdded() {
        fireTableDataChanged();
    }

    @Override
    public void soldierRemoved() {
        fireTableDataChanged();
    }
}
