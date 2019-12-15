import java.util.concurrent.CountDownLatch;

public class GenerationChecker extends Thread {

    // Board and latch
    private LifeBoard.Tile[][] board;
    private CountDownLatch latch;

    // Number of tiles (NxN) and quadrant to determine where we should search
    private int n, myQuadrant, minX, minY, maxX, maxY;

    GenerationChecker(LifeBoard.Tile[][] board, int n, int myQuadrant, CountDownLatch l){
        this.board = board;
        this.n = n;
        this.myQuadrant = myQuadrant;
        this.latch = l;

        /*
                Quadrants...

                    - q1 : top left
                    - q2 : top right
                    - q3 : bottom left
                    - q4 : bottom right

         */
        switch (myQuadrant){
            case (1):
                minX = 0;
                minY = 0;
                maxX = n/2;
                maxY = n/2;
                break;
            case (2):
                minX = n/2;
                minY = 0;
                maxX = n;
                maxY = n/2;
                break;
            case (3):
                minX = 0;
                minY = n/2;
                maxX = n/2;
                maxY = n;
                break;
            case (4):
                minX = n/2;
                minY = n/2;
                maxX = n;
                maxY = n;
                break;
        }
    }

    @Override
    public void run(){

        for (int x = minX; x < maxX; x++){
            for (int y = minY; y < maxY; y++){

                switch (board[x][y].numNeigborsAlive()){
                    case (3):
                    case (2):
                        board[x][y].setNextLife(true);
                        break;
                    default:
                        board[x][y].setNextLife(false);
                }

            }
        }

        // Finally, countdown the latch...
        latch.countDown();
    }

}
