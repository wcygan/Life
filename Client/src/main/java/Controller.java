import java.util.concurrent.CountDownLatch;

public class Controller extends Thread {

    private LifeBoard.Tile[][] board;
    private int n, time;

    Controller(LifeBoard.Tile[][] board, int n){
        this.board = board;
        this.n = n;
        time = 200;
    }

    @Override
    public void run(){
        while (true){

            CountDownLatch l = new CountDownLatch(4);
            for (int i = 1; i < 5; i++) {
                GenerationChecker g = new GenerationChecker(board, n, i, l);
                g.start();
            }

            try {
                l.await();
                for (LifeBoard.Tile[] row : board){
                    for (LifeBoard.Tile t : row){
                        t.changeGeneration();
                    }
                }

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
