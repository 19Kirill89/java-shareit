package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Repository
@Qualifier("ItemRepositoryImpl")
public class ItemRepositoryImpl implements Item {

    public Map<Long, ru.practicum.shareit.item.itemmodels.Item> things;
    private Long currentId;

    public ItemRepositoryImpl() {
        currentId = 0L;
        things = new HashMap<>();
    }

    @Override
    public ru.practicum.shareit.item.itemmodels.Item create(ru.practicum.shareit.item.itemmodels.Item item) {
        item.setId(++currentId);
        things.put(item.getId(), item);
        return item;
    }

    @Override
    public ru.practicum.shareit.item.itemmodels.Item update(ru.practicum.shareit.item.itemmodels.Item item) {
        this.things.put(item.getId(), item);
        return item;
    }

    @Override
    public ru.practicum.shareit.item.itemmodels.Item delete(Long itemId) {
        return things.remove(itemId);
    }

    @Override
    public List<ru.practicum.shareit.item.itemmodels.Item> getItemsByOwner(Long ownerId) {
        return things
                .values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId)).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ru.practicum.shareit.item.itemmodels.Item> getItemsBySearchQuery(String text) {
        return things.values()
                .stream()
                .filter(ru.practicum.shareit.item.itemmodels.Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(toList());
    }

    @Override
    public ru.practicum.shareit.item.itemmodels.Item getItemById(Long itemId) {
        return things.get(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        things.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(ru.practicum.shareit.item.itemmodels.Item::getId)
                .forEach(id -> things.remove(id));
    }
}