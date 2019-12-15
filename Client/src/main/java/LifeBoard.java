import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.*;


public class LifeBoard extends Application {

    // Control board size on an 800x900 window
    int n = 100;
    int size = 800/n;
    Controller c;

    // Utility variables
    Random r = new Random(System.currentTimeMillis());
    private HashMap<String, Scene> sceneMap;
    private Tile[][] board = new Tile[n][n];
    private Pane pane;

    // Time modification variables
    TextField timing;
    Button start, setTime;
    boolean started = false;

    // Buttons to create patterns
    /*
        Modes:
            1  :  Tile
            2  :  Glider
            3  :  upGun
            4  :  Blinker
            5  :  Pulsar
            6  :  downGun
     */
    int mode;
    Button tile, glider, upGun, downGun, blinker, pulsar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Life");
        init_vars();
        sceneMap.put("Main", createMainScreen());


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        // Set the start scene
        primaryStage.setScene(sceneMap.get("Main"));
        primaryStage.show();
    }

    public void init_vars(){
        sceneMap = new HashMap<>();

        timing = new TextField();
        timing.setPromptText("Enter a timing: ");
        setTime = new Button("Set Timing");
        setTime.setDisable(true);
        setTime.setOnAction(event -> {
            try {
                int t = Integer.parseInt(timing.getText());
                c.setTime(t);
            } catch (Exception e){}
        });

        start = new Button("Start");
        start.setOnAction(event -> {
            started = true;
            start.setDisable(true);
            setTime.setDisable(false);
            c = new Controller(board, n);
            c.start();
        });

        // Start in tile mode
        mode = 1;

        tile = new Button("Tile");
        tile.setOnAction(event -> {
            mode = 1;
            tile.setDisable(true);
            glider.setDisable(false);
            upGun.setDisable(false);
            downGun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });


        glider = new Button("Glider");
        glider.setOnAction(event -> {
            mode = 2;
            tile.setDisable(false);
            glider.setDisable(true);
            upGun.setDisable(false);
            downGun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });

        upGun = new Button("upGun");
        upGun.setOnAction(event -> {
            mode = 3;
            tile.setDisable(false);
            glider.setDisable(false);
            upGun.setDisable(true);
            downGun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });

        downGun = new Button("downGun");
        downGun.setOnAction(event -> {
            mode = 6;
            tile.setDisable(false);
            glider.setDisable(false);
            upGun.setDisable(false);
            downGun.setDisable(true);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });

        blinker = new Button("Blinker");
        blinker.setOnAction(event -> {
            mode = 4;
            tile.setDisable(false);
            glider.setDisable(false);
            upGun.setDisable(false);
            downGun.setDisable(false);
            blinker.setDisable(true);
            pulsar.setDisable(false);
        });

        pulsar = new Button("Pulsar");
        pulsar.setOnAction(event -> {
            mode = 5;
            tile.setDisable(false);
            glider.setDisable(false);
            upGun.setDisable(false);
            downGun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(true);
        });
    }


    // Create the game play scene
    public Scene createMainScreen() {
        pane = new Pane();
        BorderPane p = new BorderPane();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Tile tile = new Tile(j, i);
                tile.setTranslateX(j * size);
                tile.setTranslateY(i * size);
                pane.getChildren().add(tile);
                board[j][i] = tile;
            }
        }

        VBox v1 = new VBox(glider, tile);
        VBox v2 = new VBox(upGun, downGun);
        VBox v3 = new VBox(blinker, pulsar);
        v1.setSpacing(10);
        v2.setSpacing(10);
        v3.setSpacing(10);

        HBox patterns = new HBox(v1, v2, v3);
        patterns.setAlignment(Pos.CENTER);
        patterns.setSpacing(10);


        p.setCenter(pane);
        HBox ts = new HBox(timing, setTime);
        ts.setAlignment(Pos.CENTER);
        ts.setSpacing(10);
        HBox elems = new HBox(patterns, ts , start);
        elems.setSpacing(30);
        elems.setAlignment(Pos.CENTER);
        elems.setPadding(new Insets(0, 0, 20, 0));
        p.setBottom(elems);
        return new Scene(p, 800, 900);
    }


    public class Tile extends StackPane {
        private int x, y;
        private boolean active;
        private boolean nextLife;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
            deactivate();
            nextLife = false;

            Rectangle border = new Rectangle(size, size);
            border.setFill(null);
            border.setStroke(Color.BLACK);
            setAlignment(Pos.CENTER);
            getChildren().addAll(border);

            setOnMouseClicked(event -> {
                if (started) return;

                // When left-mouse is clicked
                if (event.getButton() == MouseButton.PRIMARY) {
                    // Are these nested switches and repeated board activations a good idea? No..
                    // Am I going to do it anyway? Yes...

                    /*
                    Modes:
                        1  :  Tile
                        2  :  Glider
                        3  :  Gun
                        4  :  Blinker
                        5  :  Pulsar
                    */

                    switch (mode){
                        case (1):
                            activate();
                            break;
                        case (2):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
                            if (x - 3 > 0 && x < n - 3 && y - 3> 0 && y < n - 3){
                                int i = r.nextInt(4);
                                switch (i){
                                    case (0):
                                        board[x-1][y+1].activate();
                                        board[x-1][y-1].activate();
                                        board[x-1][y].activate();
                                        board[x][y-1].activate();
                                        board[x+1][y].activate();
                                        break;
                                    case (1):
                                        board[x-1][y-1].activate();
                                        board[x][y-1].activate();
                                        board[x+1][y-1].activate();
                                        board[x+1][y].activate();
                                        board[x][y+1].activate();
                                        break;
                                    case (2):
                                        board[x-1][y].activate();
                                        board[x][y+1].activate();
                                        board[x+1][y+1].activate();
                                        board[x+1][y].activate();
                                        board[x+1][y-1].activate();
                                        break;
                                    case (3):
                                        board[x-1][y+1].activate();
                                        board[x][y+1].activate();
                                        board[x+1][y+1].activate();
                                        board[x-1][y].activate();
                                        board[x][y-1].activate();
                                        break;
                                }
                            }
                            else {
                                return;
                            }
                            break;

                        // Gun
                        case (3):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...

                            if (x - 20 > 0 && x < n - 20 && y - 7 > 0 && y < n - 7){


                                // Left Half of gun
                                board[x][y-1].activate();
                                board[x-1][y].activate();
                                board[x-1][y-1].activate();
                                board[x-1][y-2].activate();
                                board[x-2][y+1].activate();
                                board[x-3][y-1].activate();
                                board[x-2][y-3].activate();
                                board[x-4][y+2].activate();
                                board[x-5][y+2].activate();
                                board[x-6][y+1].activate();
                                board[x-4][y-4].activate();
                                board[x-5][y-4].activate();
                                board[x-6][y-3].activate();
                                board[x-7][y-2].activate();
                                board[x-7][y-1].activate();
                                board[x-7][y].activate();
                                board[x-16][y].activate();
                                board[x-17][y].activate();
                                board[x-16][y-1].activate();
                                board[x-17][y-1].activate();

                                // Right half of gun
                                board[x+3][y].activate();
                                board[x+3][y+1].activate();
                                board[x+3][y+2].activate();
                                board[x+4][y].activate();
                                board[x+4][y+1].activate();
                                board[x+4][y+2].activate();
                                board[x+5][y-1].activate();
                                board[x+7][y-1].activate();
                                board[x+7][y-2].activate();
                                board[x+5][y+3].activate();
                                board[x+7][y+3].activate();
                                board[x+7][y+4].activate();
                                board[x+17][y+1].activate();
                                board[x+17][y+2].activate();
                                board[x+18][y+1].activate();
                                board[x+18][y+2].activate();





                            }
                            else {
                                return;
                            }


                            break;
                        case (4):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
                            if (x - 3 > 0 && x < n - 3 && y - 3> 0 && y < n - 3){

                                int i = r.nextInt(2);
                                switch (i){
                                    case (0):
                                        board[x+1][y].activate();
                                        activate();
                                        board[x-1][y].activate();
                                        break;

                                    case(1):
                                        board[x][y+1].activate();
                                        activate();
                                        board[x][y-1].activate();
                                        break;
                                }
                            }
                            else {
                                return;
                            }
                            break;
                        case (5):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...

                            if (x - 7 > 0 && x < n - 7 && y - 7 > 0 && y < n - 7){
                                // Top Left of Pulsar
                                board[x-3][y+1].activate();
                                board[x-2][y+1].activate();
                                board[x-4][y+1].activate();
                                board[x-1][y+2].activate();
                                board[x-1][y+3].activate();
                                board[x-1][y+4].activate();
                                board[x-2][y+6].activate();
                                board[x-3][y+6].activate();
                                board[x-4][y+6].activate();
                                board[x-6][y+2].activate();
                                board[x-6][y+3].activate();
                                board[x-6][y+4].activate();

                                // Bottom Left of Pulsar
                                board[x-3][y-1].activate();
                                board[x-2][y-1].activate();
                                board[x-4][y-1].activate();
                                board[x-1][y-2].activate();
                                board[x-1][y-3].activate();
                                board[x-1][y-4].activate();
                                board[x-2][y-6].activate();
                                board[x-3][y-6].activate();
                                board[x-4][y-6].activate();
                                board[x-6][y-2].activate();
                                board[x-6][y-3].activate();
                                board[x-6][y-4].activate();

                                // Top Right of Pulsar
                                board[x+3][y+1].activate();
                                board[x+2][y+1].activate();
                                board[x+4][y+1].activate();
                                board[x+1][y+2].activate();
                                board[x+1][y+3].activate();
                                board[x+1][y+4].activate();
                                board[x+2][y+6].activate();
                                board[x+3][y+6].activate();
                                board[x+4][y+6].activate();
                                board[x+6][y+2].activate();
                                board[x+6][y+3].activate();
                                board[x+6][y+4].activate();

                                // Bottom Right of Pulsar
                                board[x+3][y-1].activate();
                                board[x+2][y-1].activate();
                                board[x+4][y-1].activate();
                                board[x+1][y-2].activate();
                                board[x+1][y-3].activate();
                                board[x+1][y-4].activate();
                                board[x+2][y-6].activate();
                                board[x+3][y-6].activate();
                                board[x+4][y-6].activate();
                                board[x+6][y-2].activate();
                                board[x+6][y-3].activate();
                                board[x+6][y-4].activate();
                            }
                            else {
                                return;
                            }
                            break;

                        case (6):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...

                            if (x - 20 > 0 && x < n - 20 && y - 7 > 0 && y < n - 7){
                                // Left Half of gun
                                board[x][y+1].activate();
                                board[x-1][y+2].activate();
                                board[x-1][y+1].activate();
                                board[x-1][y].activate();
                                board[x-2][y+3].activate();
                                board[x-3][y+1].activate();
                                board[x-2][y-1].activate();
                                board[x-4][y+4].activate();
                                board[x-5][y+4].activate();
                                board[x-6][y+3].activate();
                                board[x-4][y-2].activate();
                                board[x-5][y-2].activate();
                                board[x-6][y-1].activate();
                                board[x-7][y].activate();
                                board[x-7][y+1].activate();
                                board[x-7][y+2].activate();
                                board[x-16][y+2].activate();
                                board[x-17][y+2].activate();
                                board[x-16][y+1].activate();
                                board[x-17][y+1].activate();

                                // Right half of gun
                                board[x+3][y-2].activate();
                                board[x+3][y-1].activate();
                                board[x+3][y].activate();
                                board[x+4][y-2].activate();
                                board[x+4][y-1].activate();
                                board[x+4][y].activate();
                                board[x+5][y-3].activate();
                                board[x+7][y-3].activate();
                                board[x+7][y-4].activate();
                                board[x+5][y+1].activate();
                                board[x+7][y+1].activate();
                                board[x+7][y+2].activate();
                                board[x+17][y-1].activate();
                                board[x+17][y].activate();
                                board[x+18][y-1].activate();
                                board[x+18][y].activate();



                            }
                            else {
                                return;
                            }


                            break;
                    }
                }
                else if (event.getButton() == MouseButton.SECONDARY){
                    deactivate();
                }
            });
        }


        public void setNextLife(boolean n){
            nextLife = n;
        }

        public void changeGeneration(){
            if (nextLife){
                activate();
            }
            else {
                deactivate();
            }
        }

        public boolean isActive(){
            return this.active;
        }

        public int numNeigborsAlive(){
            int alive = 0;

            // Use checkX and checkY to iterate over the grid
            for (int cx = -1; cx < 2; cx++) {
                for (int cy = -1; cy < 2; cy++) {
                    if (cx == 0 && cy == 0) continue;
                    try {
                        if (board[x + cx][y + cy].isActive()){
                            alive++;
                        }
                    } catch (Exception e) {}
                }
            }

            return alive;
        }


        public void activate(){
            active = true;
            this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        public void deactivate(){
            active = false;
            this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    // Main
    public static void main(String[] args) {
        launch(args);
    }
}
