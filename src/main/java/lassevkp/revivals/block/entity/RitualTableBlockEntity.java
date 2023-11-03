package lassevkp.revivals.block.entity;

import lassevkp.revivals.PersistentPlayerList.PersistentDeadPlayerList;
import lassevkp.revivals.screen.RitualTableScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

public class RitualTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
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

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        PersistentDeadPlayerList state = PersistentDeadPlayerList.getServerDeadPlayerList(this.getWorld().getServer());
        byte[] data = SerializationUtils.serialize((HashSet<UUID>) state.getDeadPlayers());
        buf.writeByteArray(data);
    }
}
