package io.github.cjustinn.specialiseddeities.enums;

public enum InventoryMenuType {
    CreateDeity("Create Deity"),
    SelectDeity("Select A Patron Deity");

    public final String title;
    InventoryMenuType(String title) {
        this.title = title;
    }
}
