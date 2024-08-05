package loqor.ait.tardis.data;

import loqor.ait.api.tardis.TardisEvents;
import loqor.ait.core.data.DirectedGlobalPos;
import loqor.ait.tardis.base.KeyedTardisComponent;
import loqor.ait.tardis.base.TardisTickable;
import loqor.ait.tardis.data.loyalty.Loyalty;
import loqor.ait.tardis.data.properties.bool.BoolProperty;
import loqor.ait.tardis.data.properties.bool.BoolValue;
import loqor.ait.tardis.data.travel.TravelHandler;
import loqor.ait.tardis.data.travel.TravelHandlerBase;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShieldHandler extends KeyedTardisComponent implements TardisTickable {
	private static final BoolProperty IS_SHIELDED = new BoolProperty("is_shielded", false);
	private final BoolValue isShielded = IS_SHIELDED.create(this);
	public static BoolProperty IS_VISUALLY_SHIELDED = new BoolProperty("is_visually_shielded", false);
	private final BoolValue isVisuallyShielded = IS_VISUALLY_SHIELDED.create(this);

	public ShieldHandler() {
		super(Id.SHIELDS);
	}
	@Override
	public void onLoaded() {
		isShielded.of(this, IS_SHIELDED);
		isVisuallyShielded.of(this, IS_VISUALLY_SHIELDED);
	}

	public BoolValue shielded() {
		return isShielded;
	}

	public BoolValue visuallyShielded() {
		return isVisuallyShielded;
	}

	public void enable() {
		this.shielded().set(true);
		TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, true, this.visuallyShielded().get());
	}

	public void disable() {
		this.shielded().set(false);
		TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, false, this.visuallyShielded().get());
	}

	public void toggle() {
		if (this.shielded().get())
			this.disable();
		else this.enable();
	}

	public void enableVisuals() {
		this.visuallyShielded().set(true);
		TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), true);
	}

	public void disableVisuals() {
		this.visuallyShielded().set(false);
		TardisEvents.TOGGLE_SHIELDS.invoker().onShields(this.tardis, this.shielded().get(), false);
	}

	public void toggleVisuals() {
		if (this.visuallyShielded().get())
			this.disableVisuals();
		else this.enableVisuals();
	}

	public void disableAll() {
		this.disableVisuals();
		this.disable();
	}

	@Override
	public void tick(MinecraftServer server) {
		if (!this.shielded().get())
			return;

		TravelHandler travel = tardis.travel();

		if (!this.tardis.engine().hasPower())
			this.disableAll();

		if (travel.getState() == TravelHandlerBase.State.FLIGHT)
			return;

		tardis.removeFuel(2 * travel.instability()); // idle drain of 2 fuel per tick
		DirectedGlobalPos.Cached globalExteriorPos = travel.position();

		World world = globalExteriorPos.getWorld();
		BlockPos exteriorPos = globalExteriorPos.getPos();

		world.getOtherEntities(null, new Box(exteriorPos).expand(8f))
				.stream().filter(entity -> entity.isPushable() || entity instanceof ProjectileEntity)
				.filter(entity -> !(entity instanceof ServerPlayerEntity player
						&& tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))) // Exclude players with loyalty
				.forEach(entity -> {
					if (entity instanceof ServerPlayerEntity player && entity.isSubmergedInWater())
						player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 15, 3, true, false, false));

					if (this.visuallyShielded().get()) {
						Vec3d centerExteriorPos = exteriorPos.toCenterPos();

						if (entity.squaredDistanceTo(centerExteriorPos) <= 8f) {
							Vec3d motion = entity.getBlockPos().toCenterPos().subtract(centerExteriorPos).normalize().multiply(0.1f);

							if (entity instanceof ProjectileEntity projectile) {
								BlockPos pos = projectile.getBlockPos();

								if (projectile instanceof TridentEntity) {
									projectile.getVelocity().add(motion.multiply(2f));

									world.playSound(null, pos, SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.BLOCKS, 1f, 1f);
									return;
								}

								world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_BURN, SoundCategory.BLOCKS, 1f, 1f);

								projectile.discard();
								return;
							}

							entity.setVelocity(entity.getVelocity().add(motion.multiply(2f)));

							entity.velocityDirty = true;
							entity.velocityModified = true;
						}
					}
				});
	}
}
