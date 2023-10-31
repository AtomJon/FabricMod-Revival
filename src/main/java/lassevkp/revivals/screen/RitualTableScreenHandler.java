package lassevkp.revivals.screen;

import lassevkp.revivals.PersistentPlayerList.PersistentDeadPlayerList;
import lassevkp.revivals.Revivals;
import lassevkp.revivals.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class RitualTableScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;

    public RitualTableScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, new SimpleInventory(1), new ArrayPropertyDelegate(0), ScreenHandlerContext.EMPTY);

    }

    public RitualTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate delegate, ScreenHandlerContext context) {
        super(ModScreenHandlers.RITUAL_TABLE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = delegate;
        this.context = context;

        this.addSlot(new Slot(inventory, 0, 120, 26));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(delegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack() && slot.getStack().getItem() == ModItems.RESURRECTION_TOTEM) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);

            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    private boolean hasTotem() {
        return this.getSlot(0).getStack().getItem() == ModItems.RESURRECTION_TOTEM;
    }

    public void tryRevive(UUID targetUUID, ServerPlayerEntity player) {
        Revivals.LOGGER.info("Trying to revive player with uuid " + targetUUID.toString());
        if (hasTotem()) {

            Optional<World> worldOptional = this.context.get((world, blockPos) -> world);
            ServerWorld world = (ServerWorld) worldOptional.get();
            MinecraftServer server = world.getServer();
            PersistentDeadPlayerList state = PersistentDeadPlayerList.getServerDeadPlayerList(server);

            if(state.getDeadPlayers().contains(targetUUID)){

                Revivals.LOGGER.info("Checks succeeded, reviving player with uuid " + targetUUID.toString());

                // Remove players from the deadPlayers list
                state.setPlayerNotDead(targetUUID);

                // Remove the totem used
                this.getSlot(0).takeStack(1);
                this.context.run(World::markDirty);

                ServerPlayerEntity targetPlayer = server.getPlayerManager().getPlayer(targetUUID);

                // Get the blockposition in a Vec3d
                Optional<BlockPos> blockPosOptional = this.context.get((w, blockPos) -> blockPos);
                BlockPos blockPos = blockPosOptional.get();
                Vec3d pos = blockPos.add(0, 1, 0).toCenterPos();
                targetPlayer.teleport(world, pos.x, pos.y, pos.z, 0.0f, 0.0f);


                targetPlayer.changeGameMode(GameMode.SURVIVAL);

                // Send a message to all players
                Text text = Text.literal((targetPlayer.getDisplayName().getString() + " was revived by " + player.getDisplayName().getString()));
                for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
                    serverPlayer.sendMessage(text, false);
                }

                // Effects
                targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 4));
                targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));


                // Play sound and create particles
                world.playSound(null, blockPos, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                ParticleS2CPacket particlePacket = new ParticleS2CPacket(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        false,
                        pos.x,
                        pos.y,
                        pos.z,
                        0.0f,
                        0.0f,
                        0.0f,
                        0.5f,
                        100
                );

                // Display Particles
                for (ServerPlayerEntity serverPlayer : world.getPlayers()) {
                    // Check if the player is close enough to see the particles
                    if (player.squaredDistanceTo(new Vec3d(pos.x, pos.y, pos.z)) < 64.0) {
                        serverPlayer.networkHandler.sendPacket(particlePacket);
                    }
                }

            }

        }

    }
}
