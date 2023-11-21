package io.github.cjustinn.specialiseddeities.models.custominventory;

import org.bukkit.inventory.ItemStack;

public interface ItemStackConverter<T> {
    ItemStack evaluate(T dataItem);
}
