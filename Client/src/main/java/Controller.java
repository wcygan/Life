import java.util.concurrent.CountDownLatch;

public class Controller extends Thread {

    private LifeBoard.Tile[][] board;
    private int n, time;

    Controller(LifeBoard.Tile[][] board, int n){
        this.board = board;
        this.n = n;
        time = 75;
    }

    @Override
    public void run(){
        while (true){
            try {

                CountDownLatch l = new CountDownLatch(4);
                for (int i = 1; i < 5; i++) {
                    GenerationChecker g = new GenerationChecker(board, n, i, l);
                    g.start();
                }
                l.await();

                CountDownLatch l2 = new CountDownLatch(4);
                for (int i = 1; i < 5; i++) {
                    Swapper s = new Swapper(board, n, i, l2);
                    s.start();
                }
                l2.await();

                sleep(time);
            } catch (Exception e){
                System.out.println("Error... Shutting down");
                break;
            }
        }
    }

    public void setTime(int time){
        this.time = time;
    }

}
