package lassevkp.revivals.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Unique
	private boolean revivalsDeadMode;

	@Unique
	public boolean getRevivalsDeadMode() {
		return this.revivalsDeadMode;
	}

	@Unique
	public void setRevivalsDeadMode(boolean value) {
		this.revivalsDeadMode = value;
	}
}
