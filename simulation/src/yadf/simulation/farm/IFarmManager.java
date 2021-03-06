package yadf.simulation.farm;

import yadf.simulation.IGameObjectManager;
import yadf.simulation.IPlayerComponent;
import yadf.simulation.map.MapArea;

/**
 * The farm manager.
 */
public interface IFarmManager extends IGameObjectManager<Farm>, IPlayerComponent {

    /**
     * Adds a new farm.
     * @param area the area of the farm
     */
    void addNewFarm(final MapArea area);
}
