package dev.amble.ait.core.tardis.animation.v2.datapack;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import org.apache.commons.lang3.NotImplementedException;

public class TardisAnimationRegistry extends SimpleDatapackRegistry<TardisAnimation> {
	private static TardisAnimationRegistry INSTANCE;

	private TardisAnimationRegistry() {
		super(DatapackAnimation::fromInputStream, DatapackAnimation.CODEC, "fx/animation", true, AITMod.MOD_ID);
	}

	public static TardisAnimationRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TardisAnimationRegistry();
		}

		return INSTANCE;
	}

	@Override
	protected void defaults() {

	}

	@Override
	public TardisAnimation fallback() {
		throw new NotImplementedException("No fallback animation for the registry.");
	}
}
