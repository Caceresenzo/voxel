package engine.util;

import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;

import org.lwjgl.opengl.GL;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GarbageCollector {
	
	private static Cleaner CLEANER = Cleaner.create();
	private static boolean capabilitiesCreated = false;
	
	public Cleanable registerGL(Object object, Runnable action) {
		return CLEANER.register(object, () -> {
			if (!capabilitiesCreated) {
				GL.createCapabilities();
				capabilitiesCreated = true;
			}
			
			action.run();
		});
	}
	
}