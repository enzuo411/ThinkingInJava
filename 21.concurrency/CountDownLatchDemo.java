import java.util.concurrent.*;
import java.util.*;

// Perform som portion of a task
class TaskPortion implements Runnable {
	private static int counter = 0;
	private final int id = counter++;
	private static Random rand = new Random(66);
	private final CountDownLatch latch;

	public TaskPortion(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
			doWork();
			latch.countDown();
		} catch(InterruptedException e) {
			// Acceptable way to exit
		}
	}

	public void doWork() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(rand.nextInt(2000));
		System.out.println(this + " completed");
	}

	public String toString() {
		return String.format("%1$-3d ", id);
	}
}

// Waits on the CountDownLatch
class WaitingTask implements Runnable {
	private static int counter = 0;
	private final int id = counter++;
	private final CountDownLatch latch;
	
	public WaitingTask(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
			latch.await();
			System.out.println("Latch barrier passed for " + this);
		} catch(InterruptedException e) {
			System.out.println(this + " interrupted");
		}
	}

	public String toString() {
		return String.format("WaitingTask %1$-3d", id);
	}
}

public class CountDownLatchDemo {
	static final int SIZE = 100;
	public static void main(String[] args) throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		// All must share a single CountDownLatch object
		CountDownLatch latch = new CountDownLatch(SIZE);
		for(int i = 0; i < 10; ++i) {
			exec.execute(new WaitingTask(latch));
		}
		for(int i = 0; i < SIZE; ++i) {
			exec.execute(new TaskPortion(latch));
		}
		System.out.println("Launched all tasks");
		exec.shutdown();  // Quit when all tasks complete
	}
}
