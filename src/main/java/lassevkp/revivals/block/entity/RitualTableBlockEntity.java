package lassevkp.revivals.block.entity;

import lassevkp.revivals.StateSaverAndLoader;
import lassevkp.revivals.item.ModItems;
import lassevkp.revivals.screen.RitualTableScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RitualTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;


    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_TABLE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.ritual_table");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RitualTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate, ScreenHandlerContext.create(this.world, this.pos));
    }

    public void tryUseTotem(UUID targetUUID, UUID playerUUID) {
        if(hasTotem()){
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(this.getWorld().getServer());
            if(state.deadPlayers.contains(targetUUID)){

                this.getWorld().getServer().sendMessage(Text.literal("Peneesus"));
                // DO thingies here
                this.removeStack(1, 1);
            }
        }
    }

    private boolean hasTotem() {
        return this.getStack(1).getItem() == ModItems.RESURRECTION_TOTEM;
    }

}
