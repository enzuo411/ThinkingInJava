import java.util.concurrent.*;

class Car {
	private boolean waxOn = false;
	public synchronized void waxed() {
		waxOn = true;
		notifyAll();
	}
	public synchronized void buffed() {
		waxOn = false;
		notifyAll();
	}
	public synchronized void waitForWaxing() throws InterruptedException {
		while(waxOn == false) {
			wait();
		}
	}
	public synchronized void waitForBuffing() throws InterruptedException {
		while(waxOn == true) {
			wait();
		}
	}
}

class WaxOn implements Runnable {
	private Car car;
	public WaxOn(Car c) {
		car = c;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				System.out.print("WaxOn!  ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
		} catch(InterruptedException e) {
			System.out.println("Exiting via interrupt");
		}
		System.out.println("Ending Wax On Task");
	}
}

class WaxOff implements Runnable {
	private Car car;
	public WaxOff(Car c) {
		car = c;
	}
	public void run() {
		try {
			while(!Thread.interrupted()) {
				car.waitForWaxing();
				System.out.print("WaxOff!  ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.buffed();
			}
		} catch(InterruptedException e) {
			System.out.println("Exiting via interrupt");
		}
		System.out.println("Ending Wax Off Task");
	}
}

public class WaxOMatic {
	public static void main(String[] args) throws Exception {
		Car car = new Car();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new WaxOn(car));
		exec.execute(new WaxOff(car));
		TimeUnit.SECONDS.sleep(3);
		exec.shutdownNow();
	}
}
