package ru.practicum.shareit.item.repository;

import java.util.List;

public interface Item {
    ru.practicum.shareit.item.itemmodels.Item create(ru.practicum.shareit.item.itemmodels.Item item);

    ru.practicum.shareit.item.itemmodels.Item update(ru.practicum.shareit.item.itemmodels.Item item);

    ru.practicum.shareit.item.itemmodels.Item delete(Long userId);

    List<ru.practicum.shareit.item.itemmodels.Item> getItemsByOwner(Long ownerId);

    List<ru.practicum.shareit.item.itemmodels.Item> getItemsBySearchQuery(String text);

    ru.practicum.shareit.item.itemmodels.Item getItemById(Long itemId);

    void deleteItemsByOwner(Long ownerId);
}