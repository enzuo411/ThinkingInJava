import java.util.concurrent.*;
import java.util.*;

class Toast {
	public enum Status {
		DRY, BUTTERED, JAMMED
	}
	private Status status = Status.DRY;
	private final int id;
	public Toast(int idn) {
		id = idn;
	}
	public void butter() {
		status = Status.BUTTERED;
	}
	public void jam() {
		status = Status.JAMMED;
	}
	public Status getStatus() {
		return status;
	}
	public int getId() {
		return id;
	}
	public String toString() {
		return "Toast " + id + " : " + status;
	}
}

class ToastQueue extends LinkedBlockingQueue<Toast> {}

class Toaster implements Runnable {
	private ToastQueue toastQueue;
	private int count = 0;
	private Random rand = new Random(66);
	public Toaster(ToastQueue tq) {
		toastQueue = tq;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				TimeUnit.MILLISECONDS.sleep(100 + rand.nextInt(500));
				Toast t = new Toast(count++);
				System.out.println(t);
				// Insert into queue
				toastQueue.put(t);
			}
		} catch(InterruptedException e) {
			System.out.println("Toaster interrupted");
		}
		System.out.println("Toaster off");
	}
}

class Butterer implements Runnable {
	private ToastQueue dryQueue, butteredQueue;
	public Butterer(ToastQueue dry, ToastQueue buttered) {
		dryQueue = dry;
		butteredQueue = buttered;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				// Blocks until next piece of toast is available
				Toast t = dryQueue.take();
				t.butter();
				System.out.println(t);
				butteredQueue.put(t);
			}
		} catch(InterruptedException e) {
			System.out.println("Butterer interrupted");
		}
		System.out.println("Butterer off");
	}
}

class Jammer implements Runnable {
	private ToastQueue butteredQueue, finishedQueue;
	public Jammer(ToastQueue buttered, ToastQueue finished) {
		butteredQueue = buttered;
		finishedQueue = finished;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				// Blocks until next piece of toast is available
				Toast t = butteredQueue.take();
				t.jam();
				System.out.println(t);
				finishedQueue.put(t);
			}
		} catch(InterruptedException e) {
			System.out.println("Jammer interrupted");
		}
		System.out.println("Jammer off");
	}
}

class Eater implements Runnable {
	private ToastQueue finishedQueue;
	private int count = 0;
	public Eater(ToastQueue finished) {
		finishedQueue = finished;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				// Blocks until next piece of toast is available
				Toast t = finishedQueue.take();
				if(t.getId() != count++ || t.getStatus() != Toast.Status.JAMMED) {
					System.out.println(">>>ERROR: " + t);
					System.exit(1);
				} else {
					System.out.println("Chomp! " + t);
				}
			}
		} catch(InterruptedException e) {
			System.out.println("Eater interrupted");
		}
		System.out.println("Eater off");
	}
}

public class ToastOMatic {
	public static void main(String[] args) throws Exception {
		ToastQueue dryQueue = new ToastQueue(), butteredQueue = new ToastQueue(), finishedQueue = new ToastQueue();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Toaster(dryQueue));
		exec.execute(new Butterer(dryQueue, butteredQueue));
		exec.execute(new Jammer(butteredQueue, finishedQueue));
		exec.execute(new Eater(finishedQueue));
		TimeUnit.SECONDS.sleep(3);
		exec.shutdownNow();
	}
}
