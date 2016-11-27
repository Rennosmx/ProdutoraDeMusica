import java.util.Random;

public class Utils {
	private static final Random random = new Random();
	
	public static void randomSleep(double min, double max) {
		try {
			Thread.sleep(Math.round(random(min, max)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public static void randomSleep(double max) {
		randomSleep(0, max);
	}	
	
	public static double random(double min, double max) {
		return min + Math.random() * (max - min);	
	}
	
	public static double random(double max) {
		return random(0, max);
	}	
	
	public static boolean randomBoolean() {
		return random.nextBoolean();
	}	

	public static int random(int min, int max) {
		return min + random.nextInt(max - min + 1);	
	}
	
	public static void printlnAndFlush(String text) {
		System.out.println(text);
		System.out.flush();	
	}	
}
