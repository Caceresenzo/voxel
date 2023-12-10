package voxel.util;

import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import lombok.experimental.Delegate;

public class DoubleBufferedBlockingQueue<T> implements BlockingQueue<T> {

	@Delegate
	private BlockingQueue<T> current;
	private final BlockingQueue<T> first;
	private final BlockingQueue<T> second;

	public DoubleBufferedBlockingQueue(Supplier<BlockingQueue<T>> queueFactory) {
		this(
			queueFactory.get(),
			queueFactory.get()
		);
	}

	public DoubleBufferedBlockingQueue(BlockingQueue<T> first, BlockingQueue<T> second) {
		if (first == second) {
			throw new IllegalArgumentException("first == second");
		}

		this.current = first;
		this.first = first;
		this.second = second;
	}

	public BlockingQueue<T> swap() {
		if (current == first) {
			current = second;
			return first;
		} else if (current == second) {
			current = first;
			return second;
		} else {
			throw new IllegalStateException("invalid current");
		}
	}

}