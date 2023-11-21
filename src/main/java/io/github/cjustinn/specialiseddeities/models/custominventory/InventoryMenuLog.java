package io.github.cjustinn.specialiseddeities.models.custominventory;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMenuLog<T> {
    private int pages;
    private List<T> data;
    private ItemStackConverter<T> converter;
    private int currentPage;

    public InventoryMenuLog(final int maxPages, final List<T> data, final ItemStackConverter<T> func) {
        this.pages = maxPages;
        this.currentPage = 1;
        this.data = data;
        this.converter = func;
    }

    public int getPages() { return this.pages; }
    public int getCurrentPage() { return this.currentPage; }
    public void setCurrentPage(int page) { this.currentPage = page; }
    public void incrementPage() { this.currentPage++; }
    public void decrementPage() { this.currentPage--; }

    public void overrideData(List<T> data, ItemStackConverter<T> func) {
        this.data = data;
        this.converter = func;

        this.pages = (int) Math.ceil(data.size() / 45);
        this.currentPage = 1;
    }

    public List<ItemStack> getCurrentItems() {
        final int startIndex = (this.currentPage - 1) * 45;
        final int endIndex = Math.min(this.data.size(), this.currentPage * 45);

        final List<T> pageData = this.data.subList(startIndex, endIndex);
        return pageData.stream().map((T dataItem) -> this.converter.evaluate(dataItem)).collect(Collectors.toList());
    }
}
