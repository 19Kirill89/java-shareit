package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.itemmodels.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Repository
@Qualifier("ItemRepositoryImpl")
public class ItemRepositoryImpl implements Items {

    public Map<Long, Item> things;
    private Long currentId;

    public ItemRepositoryImpl() {
        currentId = 0L;
        things = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        this.things.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        this.things.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        return things.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return things
                .values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId)).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        return things.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return things.get(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        things.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(Item::getId)
                .forEach(id -> things.remove(id));
    }
}