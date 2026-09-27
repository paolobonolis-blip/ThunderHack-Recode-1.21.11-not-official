package net.minecraft.world.inventory;

public enum ClickType {
    PICKUP(ContainerInput.PICKUP),
    QUICK_MOVE(ContainerInput.QUICK_MOVE),
    SWAP(ContainerInput.SWAP),
    CLONE(ContainerInput.CLONE),
    THROW(ContainerInput.THROW),
    QUICK_CRAFT(ContainerInput.QUICK_CRAFT),
    PICKUP_ALL(ContainerInput.PICKUP_ALL);

    private final ContainerInput containerInput;

    ClickType(ContainerInput containerInput) {
        this.containerInput = containerInput;
    }

    public ContainerInput toContainerInput() {
        return this.containerInput;
    }
}
