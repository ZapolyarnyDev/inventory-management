package io.github.zapolyarnydev.inventoryservice.exception;

public class SmallItemQuantityException extends RuntimeException {
    public SmallItemQuantityException(int itemAmount, int decreaseValue) {
        super(String.format("There are %d items in the inventory, but trying to decrease its number by %d", itemAmount, decreaseValue));
    }
}
