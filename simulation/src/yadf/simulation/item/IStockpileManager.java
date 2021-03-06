package yadf.simulation.item;

import java.util.Set;

import yadf.simulation.IGameObjectManager;

/**
 * Interface for a stock manager.
 */
public interface IStockpileManager extends IGameObjectManager<Stockpile> {

    void removeItem(Item item);

    Item getItem(String itemTypeName);

    Item getItem(Set<ItemType> itemTypes);

    Item getItem(int id);

    Item getItemFromCategory(String category);

    int getItemQuantity(ItemType itemType);

    int getItemQuantity(String category);
}
