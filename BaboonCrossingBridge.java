import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;



public class BaboonCrossingBridge {
	private static int numBaboons;
	//	private static final int MAX_BABOONS = 3;
	//	private static final int MAX_CROSSING = 5;
	private static int baboonsOnBridge = 0;
	private static boolean direction = true; // true for left to right and false for right to left
	private static Semaphore bridgeMutex = new Semaphore(1);
	private static Semaphore crossing;
	private static HashMap<Integer,Boolean> baboonMap = new HashMap<Integer,Boolean>();

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("No baboon selected!");
			System.exit(1);
		}
		numBaboons = Integer.parseInt(args[0]);
		crossing = new Semaphore(numBaboons);
		for (int i = 1; i <= numBaboons; i++) {
			Random random = new Random();
			Baboon b =new Baboon(i);
			baboonMap.put(i, random.nextBoolean());
			new Thread(b).start();
		}
	}

	static class Baboon implements Runnable {

		private final int baboonId;

		public Baboon(int baboonId) {
			this.baboonId = baboonId;
		}

		public void run() {
			while (true) {
				try {

					System.out.println("Baboon " + baboonId + " doing baboon stuffs at " + ((boolean)baboonMap.get(baboonId)?"left":"right")+ 
							"side of bridge $$$$$$$$$$");
					Thread.sleep((int) (Math.random() * 1000));
					System.out.println("Baboon " + baboonId + " wants to cross the bridge ");
					///

					if(baboonMap != null && baboonMap.size()>0 && baboonsOnBridge>0 && baboonMap.get(baboonId)!=null 
							&& baboonMap.get((Integer)baboonId).equals(direction)) {
						System.out.println("Baboon Cant cross the bridge , baboon is at opposite side of current direction"
								+ "...baboon needs to wait");
						System.out.println("Baboon is waiting and will keep doing baboon stuffs at current side");

					}


					if(baboonMap != null && baboonMap.size()>0 && baboonsOnBridge>0 && baboonMap.get(baboonId)!=null && 
							!baboonMap.get((Integer)baboonId).equals(direction)) {
						crossing.acquire();

						bridgeMutex.acquire();

						System.out.println("Baboon " + baboonId + " is using the bridge \\==============//");
						baboonsOnBridge++;
						System.out.println("Number of Baboons on bridge "+baboonsOnBridge);
						bridgeMutex.release();
						baboonMap.put(baboonId,!(boolean)baboonMap.get(baboonId));
						Thread.sleep((int) (Math.random() * 1000));
						//baboonPosition= (boolean)baboonMap.get(baboonId)?"left":"right";
						System.out.println("Baboon " + baboonId + " has crossed the bridge and it is at " + 
						((boolean)baboonMap.get(baboonId)?"left":"right") +"side of bridge @@@@@@@@@");
						baboonsOnBridge--;
						System.out.println("Number of Baboons on bridge "+baboonsOnBridge);
						bridgeMutex.acquire();

						if (baboonsOnBridge == 0) {
							crossing.release(numBaboons); // release all permits if no baboon is on bridge
						}
						bridgeMutex.release();
					}
					if(baboonsOnBridge==0) {
						crossing.acquire();
						bridgeMutex.acquire();
						direction = !(boolean)baboonMap.get(baboonId); // change direction when the first baboon crosses because baboon is going at opposite side
						System.out.println("Direction is : " + (direction ? "right to left" : "left to right"));
						System.out.println("Baboon " + baboonId + " is using the bridge \\==============//");
						baboonsOnBridge++;
						System.out.println("Number of Baboons on bridge "+baboonsOnBridge);
						bridgeMutex.release();
						baboonMap.put(baboonId,!(boolean)baboonMap.get(baboonId));
						Thread.sleep((int) (Math.random() * 1000));
						//baboonPosition= (boolean)baboonMap.get(baboonId)?"left":"right";
						System.out.println("Baboon " + baboonId + " has crossed the bridge  and it is at "
						+ ((boolean)baboonMap.get(baboonId)?"left":"right") +"side of bridge @@@@@@@@@ ");
						baboonsOnBridge--;
						System.out.println("Number of Baboons on bridge "+baboonsOnBridge);
						bridgeMutex.acquire();

						if (baboonsOnBridge == 0) {
							crossing.release(numBaboons); // release all permits if no baboon is on bridge
						}
						bridgeMutex.release();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
