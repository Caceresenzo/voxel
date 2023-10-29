package engine.util;

import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GarbageCollector {
	
	private static Cleaner CLEANER = Cleaner.create();
	
	public Cleanable register(Object object, Runnable action) {
		return CLEANER.register(object, action);
	}
	
}