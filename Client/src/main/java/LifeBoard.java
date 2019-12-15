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

    // Control board size on an 800x800 window
    int n = 100;
    int size = 800/n;
    Controller c;

    // Utility variables
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
            3  :  Gun
            4  :  Blinker
            5  :  Pulsar
     */
    int mode;
    Button tile, glider, gun, blinker, pulsar;

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
            gun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });


        glider = new Button("Glider");
        glider.setOnAction(event -> {
            mode = 2;
            tile.setDisable(false);
            glider.setDisable(true);
            gun.setDisable(false);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });

        gun = new Button("Gun");
        gun.setOnAction(event -> {
            mode = 3;
            tile.setDisable(false);
            glider.setDisable(false);
            gun.setDisable(true);
            blinker.setDisable(false);
            pulsar.setDisable(false);
        });

        blinker = new Button("Blinker");
        blinker.setOnAction(event -> {
            mode = 4;
            tile.setDisable(false);
            glider.setDisable(false);
            gun.setDisable(false);
            blinker.setDisable(true);
            pulsar.setDisable(false);
        });

        pulsar = new Button("Pulsar");
        pulsar.setOnAction(event -> {
            mode = 5;
            tile.setDisable(false);
            glider.setDisable(false);
            gun.setDisable(false);
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

        VBox v1 = new VBox(new Label("Patterns:"), tile);
        VBox v2 = new VBox(glider, gun);
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
                    switch (mode){
                        case (1):
                            activate();
                            break;
                        case (2):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
                            break;
                        case (3):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
                            break;
                        case (4):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
                            break;
                        case (5):
                            // check if there is enough room to create pattern
                            // set pattern in blocks...
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
