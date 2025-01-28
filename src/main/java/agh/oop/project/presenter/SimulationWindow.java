package agh.oop.project.presenter;

import agh.oop.project.model.*;
import agh.oop.project.model.AbstractMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Semaphore;

public class SimulationWindow {
    private static final int CELL_SIZE_MIN = 3;
    private static final int CELL_SIZE_MAX = 50;
    private static final int MAX_AXES_CELL_SIZE = 13;

    private final int cellSize = 50;
    private final Vector2d xDir = Rotation.EAST.nextMove();
    private final Vector2d yDir = Rotation.NORTH.nextMove();

    private Simulation simulation;
    private AbstractMap worldMap;
    private boolean simulationRunning = false;
    private Vector2d owlbearPosition;

    private final int showAxis = 0;
    private final Node lastNode = null;
    private final boolean animalsSelectable = false;
    private Stage stage;

    // Elementy statystyk
    private final XYChart.Series<Number, Number> animalCountSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantCountSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> freeFieldSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> avgEnergySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> avgLifeSpanSeries = new XYChart.Series<>();

    @FXML
    private Label statisticsPresenter; // Do poprawki, narazie prowizorycznie
    @FXML
    private Label selectedAnimalPresenter; // Do poprawki, narazie prowizorycznie

    @FXML
    private Button chooseAnimal;

    @FXML
    private GridPane mapGrid;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton resumeButton;
    @FXML
    private ToggleButton startButton;
    @FXML
    private Label simulationStatusLabel;

    @FXML
    private Animal choosenAnimal = null;

    @FXML
    final Canvas[] choosenAnimalHighlight = {null}; // Przechowuje aktualnie podświetloną komórkę

    // Elementy statystyk
    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private ComboBox<String> viewModeComboBox;

    @FXML
    private Button saveStatistics;

    @FXML
    private boolean isCellSelected = false;

    @FXML
    private void onClickPlay() {
        if (!simulationRunning) {
            resumeButton.setSelected(true);
            pauseButton.setSelected(false);
            System.out.println("simulation.resume();");
            simulationRunning = true;
            simulation.resume();
        }

    }

    @FXML
    private void onClickPause() {
        if (simulationRunning) {
            pauseButton.setSelected(true);
            resumeButton.setSelected(false);
            System.out.println("simulation.pause();");
            simulationRunning = false;
            simulation.pause();
        }

    }

    @FXML
    public void onClickStart() {
        System.out.println("simulation.start();");
        if (!simulationRunning) {
            simulationRunning = true;
            Thread simulationThread = new Thread(() -> {
                try {
                    simulation.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    @FXML
    public void chooseAnimal() {
        final Canvas[] activeHighlight = {null}; // Przechowuje aktualnie podświetloną komórkę
        isCellSelected = false;

        mapGrid.setOnMouseMoved(event -> {
            // Śledzenie współrzędnych myszy
            Point2D localCoords = mapGrid.sceneToLocal(event.getSceneX(), event.getSceneY());
            double localX = localCoords.getX();
            double localY = localCoords.getY();

            int cellX = (int) (localX / cellSize);
            int cellY = (int) (localY / cellSize);

            if (cellX < 0 || cellX >= mapGrid.getColumnCount() || cellY < 0 || cellY >= mapGrid.getRowCount()) {
                if (activeHighlight[0] != null) {
                    mapGrid.getChildren().remove(activeHighlight[0]);
                    activeHighlight[0] = null; // Resetujemy zmienną aktywnego podświetlenia
                }
                return; // Poza mapą - ignorujemy
            }

            if ((activeHighlight[0] == null ||
                    cellX != activeHighlight[0].getTranslateX() / cellSize ||
                    cellY != activeHighlight[0].getTranslateY() / cellSize) &&
                    !isCellSelected) {

                // Usunięcie poprzedniego podświetlenia
                if (activeHighlight[0] != null) {
                    mapGrid.getChildren().remove(activeHighlight[0]);
                }

                // Utworzenie nowego podświetlenia
                activeHighlight[0] = createHighlightCell(cellX, cellY,new Color(0, 0, 1, 1.0));
                mapGrid.getChildren().add(activeHighlight[0]);
            }
        });

        mapGrid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) { // Lewy przycisk myszy
                Point2D localCoords = mapGrid.sceneToLocal(event.getSceneX(), event.getSceneY());
                double clickX = localCoords.getX();
                double clickY = localCoords.getY();

                int clickedCellX = (int) (clickX / cellSize);
                int clickedCellY = (int) (clickY / cellSize);

                // Warunek: jeśli spełniony, podświetlamy komórkę na zielono i blokujemy dalsze podświetlanie
                if (shouldHighlightGreen(clickedCellX, clickedCellY)) {
                    // Jeśli spełniony warunek, podświetlamy komórkę na zielono
                    if (activeHighlight[0] != null) {
                        mapGrid.getChildren().remove(activeHighlight[0]); // Usuwamy poprzednie podświetlenie
                    }

                    // Tworzymy nową komórkę na zielono
                    activeHighlight[0] = createHighlightCell(clickedCellX, clickedCellY,new Color(0,1,0,1));
                    mapGrid.getChildren().add(activeHighlight[0]);

                    // Ustawiamy, że komórka została wybrana
                    isCellSelected = true; // Blokujemy możliwość podświetlania innych komórek

//                    updateAnimalData(worldMap.getFirstAnimalAt(new Vector2d(clickedCellX, clickedCellY)));
                    choosenAnimal = worldMap.getFirstAnimalAt(new Vector2d(clickedCellX,clickedCellY));
                    drawMapReforged();
                } else {
                    if (activeHighlight[0] != null) {
                        mapGrid.getChildren().remove(activeHighlight[0]); // Usuwamy poprzednie podświetlenie
                    }
                    activeHighlight[0] = createHighlightCell(clickedCellX, clickedCellY,new Color(1,0,0,1));
                    mapGrid.getChildren().add(activeHighlight[0]);

                    isCellSelected = true;
                }
                mapGrid.setOnMouseMoved(null);  // Usuwamy obsługę ruchu myszy
                mapGrid.setOnMouseClicked(null); // Usuwamy obsługę wyjścia myszy
            }
        });

        // Dodajemy zdarzenie wyjścia myszy poza mapę
        mapGrid.setOnMouseExited(event -> {
            // Gdy myszka opuści mapę, usuwamy podświetlenie
            if (activeHighlight[0] != null) {
                mapGrid.getChildren().remove(activeHighlight[0]);
                activeHighlight[0] = null; // Resetujemy zmienną aktywnego podświetlenia
            }
        });
    }

    // Funkcja tworząca podświetlenie dla określonej komórki
    private Canvas createHighlightCell(int cellX, int cellY,Color color) {
        Canvas highlightCanvas = new Canvas(cellSize, cellSize);

        // Pozycjonowanie Canvasa na odpowiedniej komórce
        highlightCanvas.setTranslateX(cellX * cellSize + 0.400001525878906*cellX);
        highlightCanvas.setTranslateY(cellY * cellSize + 0.400001525878906*cellY);

        // Rysowanie gradientowej ramki
        GraphicsContext gc = highlightCanvas.getGraphicsContext2D();

        // Gradient od środka na zewnątrz
        gc.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color.deriveColor(0, 1, 1, 0.3)), // Początek: półprzezroczysty kolor
                new Stop(1, Color.TRANSPARENT)               // Koniec: przezroczysty
        ));

        gc.fillRect(0, 0, cellSize, cellSize);

        // Ramka w mocnym niebieskim kolorze
        gc.setStroke(color);
        gc.setLineWidth(3); // Grubość krawędzi
        gc.strokeRect(0, 0, cellSize, cellSize);

        return highlightCanvas;
    }

    // Przykładowy warunek sprawdzający, czy kliknięta komórka spełnia wymagania
    private boolean shouldHighlightGreen(int x, int y) {
        Animal animal = worldMap.getFirstAnimalAt(new Vector2d(x, y));
        return animal != null;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        this.simulation.setSimulationWindow(this);
    }

    public void init(Stage stage) {
        this.stage = stage;

        chooseAnimal.disableProperty().bind(pauseButton.selectedProperty().not());
    }

    public void setWorldMap(AbstractMap map){
        this.worldMap = map;
    }

    public void drawMapReforged() {
        if (worldMap == null || mapGrid == null) return;
        clearGrid();

        Boundary bounds = worldMap.getBoundary();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        // 0.Pobieram obrazki
        Image Desert = new Image(getClass().getResourceAsStream("/images/Desert.png"));
        Image Jungle = new Image(getClass().getResourceAsStream("/images/Jungle.png"));
        Image Palm = new Image(getClass().getResourceAsStream("/images/Palm.png"));
        Image RedArea = new Image(getClass().getResourceAsStream("/images/RedArea.png"));


        // 1. Rysuje tło z elementami mapy owlbear
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);

                // Sprawdzenie, czy pozycja znajduje się w granicach jungli
                Image backgroundImage = worldMap.getEquator().test(position) ? Jungle : Desert;

                // Tworzenie ImageView dla tła
                ImageView backgroundView = new ImageView(backgroundImage);

                // Dopasowanie rozmiaru
                backgroundView.setFitWidth(50);
                backgroundView.setFitHeight(50);

                // Ustawienie w gridzie
                mapGrid.add(backgroundView, x - lowerLeft.getX(), y - lowerLeft.getY()); // Zmieniona pozycja Y

                // Dodanie prostokąta z współrzędnymi w prawym górnym rogu
                Label coordinatesLabel = new Label(x + "," + y);
                coordinatesLabel.setStyle("-fx-font-size: 10; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5);");
                GridPane.setHalignment(coordinatesLabel, HPos.RIGHT); // Ustawienie do prawego górnego rogu
                GridPane.setValignment(coordinatesLabel, VPos.BOTTOM);   // Ustawienie do prawego górnego rogu
                mapGrid.add(coordinatesLabel, x - lowerLeft.getX(), y - lowerLeft.getY()); // Dodanie do siatki

                // Sprawdzamy, czy mapą jest OwlbearMap
                if (worldMap instanceof OwlbearMap map) {
                    if (map.getHuntingGround().test(position)) {
                        // Jeśli jest to instancja OwlbearMap, dodajemy RedArea
                        ImageView redAreaView = new ImageView(RedArea);
                        redAreaView.setFitWidth(50);
                        redAreaView.setFitHeight(50);

                        // Ustawiamy pozycję RedArea
                        mapGrid.add(redAreaView, x - lowerLeft.getX(), y - lowerLeft.getY());
                    }

                    // Rysowanie Owlbeara
                    Owlbear owlbear = map.getOwlbear();
                    this.owlbearPosition = owlbear.getPosition();

                    if (position.equals(owlbearPosition)) {
                        Image owlbearImage = new Image(getClass().getResourceAsStream("/images/Owlbear/" + owlbear.getRotation() + ".png"));
                        ImageView owlbearImageView = new ImageView(owlbearImage);
                        owlbearImageView.setFitWidth(50);
                        owlbearImageView.setFitHeight(50);

                        // Ustawienie pozycji dla Owlbeara
                        mapGrid.add(owlbearImageView, x - lowerLeft.getX(), y - lowerLeft.getY());
                    }
                }
            }
        }

        // 2. Rysuje animale
        for (Map.Entry<Vector2d, SortedSet<Animal>> entry : worldMap.getAnimalsMap().entrySet()) {
            Vector2d position = entry.getKey();
            SortedSet<Animal> animalsAtPosition = entry.getValue();

            if (!animalsAtPosition.isEmpty()) {
                // Dla każdego zwierzęcia na tej pozycji
                for (Animal animal : animalsAtPosition) {
                    if (animal == null) continue;
                    // Tworzymy odpowiednią ścieżkę do obrazka na podstawie kierunku
                    String animalDirection = animal.getRotation().name();  // Zgodnie z enum Rotation
                    Vector2d animalPosition = animal.getPosition();

                    // Budujemy ścieżkę do obrazka zwierzęcia
                    Image animalImage = new Image(getClass().getResourceAsStream("/images/Animal/" + animalDirection + ".png"));

                    // Tworzymy ImageView, aby wyświetlić obrazek
                    ImageView animalImageView = new ImageView(animalImage);

                    // Dopasowanie rozmiaru obrazka
                    animalImageView.setFitWidth(40);  // Możesz dostosować wymiary
                    animalImageView.setFitHeight(40);

                    // Tworzymy StackPane, aby trzymać obrazek i pasek życia
                    StackPane animalPane = new StackPane();

                    // Pasek życia
                    double energyFraction = animal.energyFraction(); // Proporcja energii
                    double barWidth = 50; // Stała szerokość paska
                    double barHeight = 5; // Wysokość paska

                    // Czerwony pasek (tło)
                    Rectangle redBar = new Rectangle(barWidth, barHeight);
                    redBar.setFill(Color.RED);

                    // Zielony pasek
                    Rectangle greenBar = new Rectangle(energyFraction * barWidth, barHeight);
                    greenBar.setFill(Color.GREEN);

                    // Ustawienie paska życia w HBox
                    StackPane healthBar = new StackPane();
                    healthBar.getChildren().addAll(redBar, greenBar);
                    StackPane.setAlignment(greenBar, Pos.CENTER_LEFT);
                    StackPane.setAlignment(redBar, Pos.CENTER_LEFT);

                    // Dodajemy pasek i obrazek do StackPane
                    VBox animalWithHealthBar = new VBox();
                    animalWithHealthBar.getChildren().addAll(animalImageView, healthBar);
                    animalWithHealthBar.setAlignment(Pos.CENTER); // Centrowanie obrazka i paska
                    animalWithHealthBar.setSpacing(0);

                    // Przekształcenie współrzędnych na siatkę
                    int gridX = animalPosition.getX();
                    int gridY = animalPosition.getY();

                    // Ustawienie pozycji na siatce
                    GridPane.setHalignment(animalWithHealthBar, HPos.CENTER);
                    GridPane.setValignment(animalWithHealthBar, VPos.CENTER);

                    // Dodanie do siatki
                    mapGrid.add(animalWithHealthBar, gridX, gridY);

                    // Rysujemy tylko jednego zwierzaka z danego miejsca
                    break;
                }
            }
        }

        //2.1 Rysuje podświetlenie wybranego animala
        if (choosenAnimal != null && choosenAnimal.isAlive()) {
            Vector2d position = choosenAnimal.getPosition();
            choosenAnimalHighlight[0] = createHighlightCell(position.getX(), position.getY(), new Color(0.5,0,0.5,1));
            mapGrid.getChildren().add(choosenAnimalHighlight[0]);
            updateAnimalData(choosenAnimal);
        }


        // 3. Rysuje trawe
        for (Map.Entry<Vector2d, Grass> entry : worldMap.getGrassMap().entrySet()) {
            Vector2d position = entry.getKey();

            // Jeśli pozycja nie jest równa pozycji Owlbeara
            if (!position.equals(owlbearPosition)) {
                // Tworzenie ImageView dla trawy
                ImageView grassImageView = new ImageView(Palm);

                // Dopasowanie rozmiaru obrazka
                grassImageView.setFitWidth(50);
                grassImageView.setFitHeight(50);

                // Przekształcenie współrzędnych na siatkę
                int gridX = position.getX();
                int gridY = position.getY();

                // Ustawienie wyrównania
                GridPane.setHalignment(grassImageView, HPos.CENTER);
                GridPane.setValignment(grassImageView, VPos.CENTER);

                // Dodanie obrazka do siatki
                mapGrid.add(grassImageView, gridX, gridY);
            }
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // Hack dla linii siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void mapChanged(Semaphore semaphore) {
        Platform.runLater(() -> {
            drawMapReforged(); // Rysowanie mapy
            updateStats(simulation.getDay(), // day
                    worldMap.getAnimals().size(), // animals
                    worldMap.getPlantsAmount(), // plants
                    worldMap.getAllFreeCells(), // freeFields
                    worldMap.getAverageEnergy(), // avgEnergy
                    worldMap.getAverageLifeSpan() // avgLifeSpan
                    );
            semaphore.release();
        });
    }

    // Metody statystyk
    public void initialize() {
        // Dodanie serii do wykresu
        animalCountSeries.setName("Animal Count");
        plantCountSeries.setName("Plant Count");
        freeFieldSeries.setName("Free Fields");
        avgEnergySeries.setName("Average Energy");
        avgLifeSpanSeries.setName("Average Lifespan");

        lineChart.getData().addAll(animalCountSeries, plantCountSeries, freeFieldSeries, avgEnergySeries, avgLifeSpanSeries);

        // Wyłączenie symboli (kropek) na liniach
        disableSymbols(animalCountSeries);
        disableSymbols(plantCountSeries);
        disableSymbols(freeFieldSeries);
        disableSymbols(avgEnergySeries);
        disableSymbols(avgLifeSpanSeries);

        // Obsługa wyboru trybu wyświetlania
        viewModeComboBox.setOnAction(e -> updateViewMode());
    }

    public void updateStats(int day, int animals, int plants, int freeFields, double avgEnergy, double avgLifeSpan) {
        // Aktualizacja bieżącego dnia
        int currentDay = simulation.getDay();

        // Dodanie nowych danych
        animalCountSeries.getData().add(new XYChart.Data<>(day, animals));
        plantCountSeries.getData().add(new XYChart.Data<>(day, plants));
        freeFieldSeries.getData().add(new XYChart.Data<>(day, freeFields));
        avgEnergySeries.getData().add(new XYChart.Data<>(day, avgEnergy));
        avgLifeSpanSeries.getData().add(new XYChart.Data<>(day, avgLifeSpan));

        // Automatyczna zmiana zakresu osi X (dla ostatnich 100 dni)
        if ("Last 100 Days".equals(viewModeComboBox.getValue())) {
            xAxis.setLowerBound(Math.max(0, currentDay - 100));
            xAxis.setUpperBound(currentDay);
        } else {
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(currentDay);
        }

        // Wyłącz ponownie symbole po dodaniu nowych punktów
        disableSymbols(animalCountSeries);
        disableSymbols(plantCountSeries);
        disableSymbols(freeFieldSeries);
        disableSymbols(avgEnergySeries);
        disableSymbols(avgLifeSpanSeries);

        lineChart.setAnimated(true);
    }

    private void updateViewMode() {
        int currentDay = simulation.getDay();
        // Zmiana trybu wyświetlania
        if ("Last 100 Days".equals(viewModeComboBox.getValue())) {
            xAxis.setLowerBound(Math.max(0, currentDay - 100));
            xAxis.setUpperBound(currentDay);
        } else {
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(currentDay);
        }
    }

    private void disableSymbols(XYChart.Series<Number, Number> series) {
        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 2px;"); // Ustawienie grubości linii
        for (XYChart.Data<Number, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setVisible(false); // Wyłączenie kropek
            }
        }
    }

    public void saveData() throws IOException {
        simulation.getStatsCollector().exportToCsv();
    }

    public void updateAnimalData(Animal animal) {
        String genome = animal.getGenome().toString();
        int energy = animal.getEnergy();
        int eatenPlants = animal.getGrassEaten();
        int childrenAmount = animal.childrenCount();
        int descendantsCount = animal.descendantsCount();
        int daysAlive = animal.getAge();
        boolean isAlive = animal.isAlive();

        if (descendantsCount < 0) {descendantsCount = 0;}

        String text = String.format(
                "Genome: %s\nEnergy: %d\nEaten Plants: %d\nChildren: %d\nDescendants: %d\nDays Alive: %d\nAlive: %s",
                genome, energy, eatenPlants, childrenAmount, descendantsCount, daysAlive, isAlive ? "Yes" : "No"
        );

        // Przekazanie tekstu do metody updateLabelText
        updateLabelText(text);
    }

    public void updateLabelText(String text) {
        selectedAnimalPresenter.setText(text); // Zmiana tekstu Label
    }
}