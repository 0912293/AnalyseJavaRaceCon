import java.util.ArrayList;

/**
 * Created by floris-jan on 29-11-16.
 */
class RunnableThread implements Runnable {

    int id;
    int counter = 0;
    private Thread thread;
    static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    RunnableThread(int threadid) {
        id = threadid;
    }

    @Override
    public void run() {
        try {
            while(counter < 100) {
                if(alphabet.length > id) {
                    System.out.println(alphabet[id-1]);
                    RaceCondition1.list.add(alphabet[id - 1]);
                }
                counter++;
            }
            System.out.println("Thread " + id);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if(thread == null) {
            thread = new Thread(this, id + "");
            thread.start();
        }
    }
}

public class RaceCondition1 {

    static ArrayList<Character> list = new ArrayList<>();

    public static void main(String args[]) {
        list.add(':');

        RunnableThread R1 = new RunnableThread(1);
        RunnableThread R2 = new RunnableThread(2);
        RunnableThread R3 = new RunnableThread(3);

        R1.start();
        R2.start();
        R3.start();

        try {
            Thread.sleep(1000);
            System.out.println("List: " + list);
            System.out.println("Size: " + RaceCondition1.list.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
