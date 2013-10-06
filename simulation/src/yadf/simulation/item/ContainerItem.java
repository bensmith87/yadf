package yadf.simulation.item;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import yadf.logger.Logger;
import yadf.simulation.IGameObjectManagerListener;
import yadf.simulation.IPlayer;
import yadf.simulation.job.HaulJob;
import yadf.simulation.job.IJob;
import yadf.simulation.job.IJobListener;
import yadf.simulation.job.IJobManager;
import yadf.simulation.map.MapIndex;

/**
 * An item that can contain other items, i.e. barrel or chest.
 */
public class ContainerItem extends Item implements IContainer, IJobListener, IItemAvailableListener {

    /** The type of item that this item is storing if it's a container. */
    private ItemType contentItemType;

    /** The container component. */
    private final ContainerComponent containerComponent = new ContainerComponent(this);

    /** The haul jobs created to haul content items to this item if it's a container. */
    private final Set<HaulJob> haulJobs = new LinkedHashSet<>();

    /**
     * Constructor from a DOM element. This item will not belong to a player. Only useful for temporary items that can
     * be cloned.
     * @param itemElement the DOM element to get attributes from
     * @throws Exception something went wrong
     */
    ContainerItem(final Element itemElement) throws Exception {
        super(itemElement);
        String contentTypeName = itemElement.getAttribute("contentType");
        if (!"".equals(contentTypeName)) {
            contentItemType = ItemTypeManager.getInstance().getItemType(contentTypeName);
            String tempString = itemElement.getAttribute("contentQuantity");
            int quantity = "".equals(tempString) ? 0 : Integer.parseInt(tempString);
            for (int i = 0; i < quantity; i++) {
                Item contentItem = new Item(new MapIndex(), contentItemType, player);
                containerComponent.addItem(contentItem);
            }
        }
    }

    /**
     * Create an item from an item type.
     * @param position the position of the new item
     * @param itemTypeTmp the type of the new item
     * @param player the player that the item will belong to
     */
    ContainerItem(final MapIndex position, final ItemType itemTypeTmp, final IPlayer player) {
        super(position, itemTypeTmp, player);
        contentItemType = null;
        listenForAllContentItemTypes();
    }

    /**
     * Create an item from another item, i.e. copy constructor.
     * @param item the item to clone
     * @param playerTmp the player that the new item will belong to
     */
    ContainerItem(final ContainerItem item, final IPlayer playerTmp) {
        super(item, playerTmp);
        for (Item contentItem : item.getItems()) {
            Item newContentItem = new Item(contentItem, playerTmp);
            containerComponent.addItem(newContentItem);
        }
        contentItemType = item.contentItemType;
        if (contentItemType == null) {
            listenForAllContentItemTypes();
        } else {
            player.getComponent(IStockManager.class).addItemAvailableListener(contentItemType, this);
            createAllHaulJobs();
        }
    }

    @Override
    public Item getItem(final String itemTypeName, final boolean used, final boolean placed) {
        Item foundItem = null;
        if (!used) {
            foundItem = containerComponent.getItem(itemTypeName, used, placed);
        }
        return foundItem;
    }

    @Override
    public Item getItemFromCategory(final String category, final boolean used, final boolean placed) {
        Item foundItem = null;
        if (!used) {
            foundItem = containerComponent.getItemFromCategory(category, used, placed);
        }
        return foundItem;
    }

    @Override
    public boolean addItem(final Item item) {
        Logger.getInstance().log(this, "Adding item - itemType: " + item.getType());
        boolean itemAdded = false;
        if (!item.getPosition().equals(position)) {
            Logger.getInstance().log(this,
                    "Item could not be added to container as they are not in the same location", true);
        } else if (isFull()) {
            Logger.getInstance().log(this, "Item could not be added to container as the container is full", true);
        } else if (contentItemType != null && !contentItemType.equals(item.getType())) {
            Logger.getInstance().log(this,
                    "Item could not be added to container as the container does not accept this item type", true);
        } else {
            if (contentItemType == null) {
                contentItemType = item.getType();
                // Stop listening to all items and only listen to this new content item type
                stopListenForAllContentItemTypes();
                player.getComponent(IStockManager.class).addItemAvailableListener(contentItemType, this);
                createAllHaulJobs();
            }
            itemAdded = containerComponent.addItem(item);
            if (itemAdded) {
                if (isFull()) {
                    player.getComponent(IStockManager.class).removeItemAvailableListener(contentItemType, this);
                }
            }
        }
        return itemAdded;
    }

    @Override
    public boolean removeItem(final Item item) {
        Logger.getInstance().log(this, "Removing item - itemType: " + item.getType());
        boolean itemRemoved = containerComponent.removeItem(item);
        if (itemRemoved) {
            // If the container was full, we want to be listening for an item to fill it again
            if (getItems().size() + 1 == itemType.capacity) {
                player.getComponent(IStockManager.class).addItemAvailableListener(contentItemType, this);
            }
            if (isEmpty()) {
                player.getComponent(IStockManager.class).removeItemAvailableListener(contentItemType, this);
                contentItemType = null;
                listenForAllContentItemTypes();
            }
        }
        return itemRemoved;
    }

    /**
     * Is the container empty.
     * @return true if empty
     */
    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    /**
     * Is the container full.
     * @return true if full
     */
    public boolean isFull() {
        return getItems().size() >= itemType.capacity;
    }

    /**
     * Get the type of item that this container stores.
     * @return the item type
     */
    public ItemType getContentItemType() {
        return contentItemType;
    }

    @Override
    public void setPosition(final MapIndex positionTmp) {
        super.setPosition(positionTmp);
        for (Item item : getItems()) {
            item.setPosition(positionTmp);
        }
    }

    @Override
    public boolean canBeStored(final Set<ItemType> itemTypes) {
        boolean storable = false;
        if (!isDeleted() && !placed) {
            if (contentItemType == null && itemTypes.contains(itemType)) {
                storable = true;
            } else if (itemTypes.contains(contentItemType)) {
                storable = true;
            }
        }
        return storable;
    }

    /**
     * If an item is available that we care about and we're not full, add a haul job for it. {@inheritDoc}
     */
    @Override
    public void itemAvailable(final Item availableItem, final IContainer container) {
        assert !availableItem.used;
        if (container == null || !(container instanceof ContainerItem)) {
            if (contentItemType == null) {
                selectContentItemType();
            } else if (getItems().size() + haulJobs.size() < itemType.capacity
                    && contentItemType.equals(availableItem.itemType)) {
                availableItem.setUsed(true);
                HaulJob haulJob = new HaulJob(availableItem, this, position, player);
                haulJob.addListener(this);
                player.getComponent(IJobManager.class).addJob(haulJob);
                haulJobs.add(haulJob);
            }
        }
    }

    /**
     * Looks for unused items in the stock manager and creates haul jobs for them all.
     */
    private void createAllHaulJobs() {
        Logger.getInstance().log(this, "Create haul jobs");
        for (int i = getItems().size() + haulJobs.size(); i < itemType.capacity; i++) {
            // TODO: this should also get items that are stored in stockpiles but not in containers
            Item item = player.getComponent(IStockManager.class).getUnstoredItem(contentItemType);
            if (item != null) {
                item.setUsed(true);
                HaulJob haulJob = new HaulJob(item, this, position, player);
                haulJob.addListener(this);
                player.getComponent(IJobManager.class).addJob(haulJob);
                haulJobs.add(haulJob);
            } else {
                break;
            }
        }
    }

    /**
     * Allocate this container to an item type based on what unstored items need to be stored.
     */
    private void selectContentItemType() {
        int maxQuantity = 0;
        ItemType maxItemType = null;
        for (String contentItemTypeName : itemType.contentItemTypeNames) {
            ItemType itemType = ItemTypeManager.getInstance().getItemType(contentItemTypeName);
            int quantity = player.getComponent(IStockManager.class).getItemQuantity(itemType);
            if (quantity > maxQuantity) {
                maxQuantity = quantity;
                maxItemType = itemType;
            }
        }
        if (maxQuantity > 2) {
            Logger.getInstance().log(this,
                    "Content type of " + itemType + " is set to " + maxItemType + ", " + maxQuantity + " found");
            contentItemType = maxItemType;
            createAllHaulJobs();
        }
    }

    /**
     * If the job is finished remove it from our list. {@inheritDoc}
     */
    @Override
    public void jobDone(final IJob job) {
        assert job.isDone();
        assert haulJobs.contains(job);
        haulJobs.remove(job);
        HaulJob haulJob = (HaulJob) job;
        Logger.getInstance().log(this,
                "Haul job is finished, job removed - itemType: " + haulJob.getItem().getType());
        haulJob.getItem().setUsed(false);
    }

    @Override
    public void jobChanged(final IJob job) {
        // do nothing
    }

    /**
     * Listen for all item types that this container accepts.
     */
    private void listenForAllContentItemTypes() {
        for (String contentItemTypeName : itemType.contentItemTypeNames) {
            ItemType itemType = ItemTypeManager.getInstance().getItemType(contentItemTypeName);
            player.getComponent(IStockManager.class).addItemAvailableListener(itemType, this);
        }
    }

    /**
     * Stop listening for all item types that this container accepts.
     */
    private void stopListenForAllContentItemTypes() {
        for (String contentItemTypeName : itemType.contentItemTypeNames) {
            ItemType itemType = ItemTypeManager.getInstance().getItemType(contentItemTypeName);
            player.getComponent(IStockManager.class).removeItemAvailableListener(itemType, this);
        }
    }

    @Override
    public List<Item> getItems() {
        return containerComponent.getItems();
    }

    @Override
    public void addGameObjectManagerListener(final IGameObjectManagerListener listener) {
        containerComponent.addGameObjectManagerListener(listener);
    }

    @Override
    public void removeGameObjectManagerListener(final IGameObjectManagerListener listener) {
        containerComponent.removeGameObjectManagerListener(listener);
    }

    @Override
    public void addItemAvailableListener(final ItemType itemType, final IItemAvailableListener listener) {
        containerComponent.addItemAvailableListener(itemType, listener);
    }

    @Override
    public void addItemAvailableListenerListener(final String category, final IItemAvailableListener listener) {
        containerComponent.addItemAvailableListenerListener(category, listener);
    }

    @Override
    public void removeItemAvailableListener(final ItemType itemType, final IItemAvailableListener listener) {
        containerComponent.removeItemAvailableListener(itemType, listener);
    }

    @Override
    public void removeItemAvailableListener(final String category, final IItemAvailableListener listener) {
        containerComponent.removeItemAvailableListener(category, listener);
    }

    @Override
    public int getItemQuantity(final String category) {
        return containerComponent.getItemQuantity(category);
    }

    @Override
    public int getItemQuantity(final ItemType itemType) {
        return containerComponent.getItemQuantity(itemType);
    }
}