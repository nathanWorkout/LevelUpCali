import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {


    // Composants de l'interface graphique
    private VBox sideBar;                       // Menu latéral gauche
    private BorderPane centerArea;              // Zone d'affichage centrale
    private boolean isSidebarVisible = true;    // Le panneau latéral gauche est visible par défault

    private WorkoutPlanner workoutPlanner;                  // Planning d'entrainement
    private Training training;                              // Liste des entrainements créer
    private Graphic graphic = new Graphic();                // Graphiques de progression
    private DeepLearning deepLearning = new DeepLearning(); // Analyse du mouvement
    private BreakDown breakDown = new BreakDown();          // Graphique de la répartition musculaire en fonction des mouvements/figures


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // Configuration de la fenetre principale
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0a0a;");

        // Espace en haut de l'application contenant les différents menu
        HBox barInTop = barInTop();
        Pane paneInTop = paneInTop();

        VBox vBoxInTop = new VBox();
        vBoxInTop.getChildren().addAll(paneInTop, barInTop);
        vBoxInTop.setEffect(new DropShadow(20, 0, 4, Color.rgb(255, 255, 255, 0.1)));
        root.setTop(vBoxInTop);

        centerArea = new BorderPane();

        // Image pour le background
        try {
            Image backgroundImage = new Image(getClass().getResourceAsStream("/images/background.PNG"));
            BackgroundImage bgImage = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, true)
            );
            centerArea.setBackground(new Background(bgImage));
        } catch (Exception e) {
            centerArea.setStyle("-fx-background-color: transparent;");
        }

        root.setCenter(centerArea);

        // Sidebar contenant d'autres menu
        sideBar = sideBar();
        HBox sidebarBox = new HBox(sideBar);
        root.setLeft(sidebarBox);

        // Initialisation des pages
        workoutPlanner = new WorkoutPlanner();
        training = new Training();

        // Si l'utilisateur clique sur un training, alors on ouvre un autre scripts qui affiche les détails de celui-ci
        training.setOnTrainingClick(name -> {
            TrainingDetail detailPage = new TrainingDetail(name);
            centerArea.setCenter(detailPage.getView());
        });

        // Configuration de la scène
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("LEVEL UP CALI - Workout Tracker");



        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    // Espace contenant différents menu comme l'analyse biomécanique
    private VBox sideBar() {
        VBox sideBar = new VBox();
        sideBar.setPrefWidth(240);
        sideBar.setPrefHeight(362);
        sideBar.setPadding(new Insets(30, 20, 30, 20));
        sideBar.setSpacing(15);
        sideBar.setAlignment(Pos.TOP_CENTER);
        sideBar.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 0 0 0 0; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 0 1 0 0;");

        // Nom de l'application placé en haut de la sidebar
        Label appTitle = new Label("LEVEL UP");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        appTitle.setStyle("-fx-text-fill: #ffffff; -fx-padding: 0 0 10 0;");

        // Juste en dessous on affiche ceci
        Label appSubtitle = new Label("CALISTHENICS TRACKER");
        appSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 10));
        appSubtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6); -fx-padding: 0 0 20 0;");

        // Boutons de navigation
        Button dataBase = createModernButton("Database", "/images/data.png");
        dataBase.setOnAction(e -> openDataBaseView());

        Button workoutPlannerBtn = createModernButton("Planning", "/images/trainingPlanner.png");
        workoutPlannerBtn.setOnAction(e -> openWorkoutPlannerView());

        Button progression = createModernButton("Analyse du mouvement", "/images/report.png");
        progression.setOnAction(e -> openDeepLearning());

        // Pousser les boutons vers le haut de la sidebar
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sideBar.getChildren().addAll(appTitle, appSubtitle, dataBase, workoutPlannerBtn, progression, spacer);

        return sideBar;
    }

    // Fonction pour créer un bouton moderne avec icône et animations
    private Button createModernButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(50);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-background-radius: 12; " +
                "-fx-border-radius: 12; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 1; " +
                "-fx-font-size: 14; " +
                "-fx-font-weight: 500;");

        // Ajout de l'icone
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(22);
            iconView.setFitHeight(22);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            btn.setGraphic(iconView);
            btn.setGraphicTextGap(15);
        } catch (Exception e) {
        }

        animationModernBtn(btn);
        return btn;
    }

    // Fonction pour ajouter les animations au survol des boutons
    private void animationModernBtn(Button btn) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 255, 255, 0.3));
        glow.setRadius(20);
        glow.setSpread(0.4);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // Animation au survol
        btn.setOnMouseEntered(e -> {
            btn.setEffect(glow);
            btn.setStyle("-fx-text-fill: #ffffff; " +
                    "-fx-background-color: rgba(255, 255, 255, 0.15); " +
                    "-fx-background-radius: 12; " +
                    "-fx-border-radius: 12; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 1; " +
                    "-fx-font-size: 14; " +
                    "-fx-font-weight: 600; " +
                    "-fx-cursor: hand;");
            scaleUp.playFromStart();
        });

        // Retour à l"état normal
        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            btn.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                    "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                    "-fx-background-radius: 12; " +
                    "-fx-border-radius: 12; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                    "-fx-border-width: 1; " +
                    "-fx-font-size: 14; " +
                    "-fx-font-weight: 500;");
            scaleDown.playFromStart();
        });
    }

    // Fonction pour executer le script Database
    private void openDataBaseView() {
        DataBase db = new DataBase();
        Pane view = db.getView();
        centerArea.setCenter(view);
    }

    // Fonction pour executer le script workoutPlanner
    private void openWorkoutPlannerView() {
        Pane view = workoutPlanner.getView();
        centerArea.setCenter(view);
    }

    // Fonction pour créer la barre de navigation supérieure
    private HBox barInTop() {
        HBox barInTop = new HBox();
        barInTop.setPrefHeight(80);
        barInTop.setPrefWidth(Region.USE_COMPUTED_SIZE);
        barInTop.setAlignment(Pos.CENTER_LEFT);
        barInTop.setStyle("-fx-background-color: rgba(15, 15, 15, 0.9); " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 0 0 1 0;");

        // Bouton Hamburger
        HBox MENU = new HBox();
        Image MenuIcon = new Image(getClass().getResourceAsStream("/images/menu.png"));
        ImageView menuIcon = new ImageView(MenuIcon);
        menuIcon.setFitWidth(26);
        menuIcon.setFitHeight(26);
        menuIcon.setPreserveRatio(true);
        menuIcon.setSmooth(true);

        javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
        colorAdjust.setBrightness(1.0);
        menuIcon.setEffect(colorAdjust);

        MENU.setAlignment(Pos.CENTER);
        MENU.getChildren().add(menuIcon);
        MENU.setPrefWidth(80);
        MENU.setPrefHeight(80);

        // Evenements lorsque on clique sur le bouon MENU
        MENU.setOnMouseEntered(e -> {
            MENU.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-cursor: hand;");
        });

        MENU.setOnMouseExited(e -> {
            MENU.setStyle("-fx-background-color: transparent;");
        });

        MENU.setOnMouseClicked(e -> {
            toggleSidebar();

            // Animation de clic
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(255, 255, 255, 0.5));
            glow.setRadius(25);
            glow.setSpread(0.5);

            ScaleTransition pulse = new ScaleTransition(Duration.millis(150), menuIcon);
            pulse.setToX(1.2);
            pulse.setToY(1.2);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);

            menuIcon.setEffect(glow);
            pulse.play();

            pulse.setOnFinished(ev -> menuIcon.setEffect(null));
        });

        // Conteneur pour les boutons suivants
        HBox navContainer = new HBox(30);
        navContainer.setAlignment(Pos.CENTER);
        navContainer.setPadding(new Insets(0, 40, 0, 40));
        HBox.setHgrow(navContainer, Priority.ALWAYS);


        // Boutons principaux
        Button Training = createTopNavButton("Entrainements", "/images/training.png");
        Training.setOnAction(e -> openWorkoutTrainingView());

        Button History = createTopNavButton("Historique", "/images/history.png");
        History.setOnAction(e -> openHistory());

        Button Graph = createTopNavButton("Graphique", "/images/graph.png");
        Graph.setOnAction(e -> openGraph());

        Button Breakdown = createTopNavButton("Muscle graphs", "/images/breackdown.png");
        Breakdown.setOnAction(e->openBreakDown());

        navContainer.getChildren().addAll(Training, History, Graph, Breakdown);

        barInTop.getChildren().addAll(MENU, navContainer);

        return barInTop;
    }

    // Fonction pour créer les boutons de la barre de navigation supérieure
    private Button createTopNavButton(String text, String iconPath) {
        Button btn = new Button(text);
        btn.setPrefWidth(140);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 1;");

        // Ajout de l'icon
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);

            javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
            colorAdjust.setBrightness(1.0);
            iconView.setEffect(colorAdjust);

            btn.setGraphic(iconView);
            btn.setGraphicTextGap(10);
        } catch (Exception e) {
        }

        addTopButtonAnimation(btn);
        return btn;
    }

    // Fonction pour ajouter les animations aux boutons du haut
    private void addTopButtonAnimation(Button btn) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 255, 255, 0.4));
        glow.setRadius(15);
        glow.setSpread(0.3);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), btn);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), btn);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        // Animation au survol
        btn.setOnMouseEntered(e -> {
            scaleIn.playFromStart();
            btn.setEffect(glow);
            btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-border-width: 1; " +
                    "-fx-cursor: hand;");
        });

        // Retour a l'état normal
        btn.setOnMouseExited(e -> {
            scaleOut.playFromStart();
            btn.setEffect(null);
            btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                    "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                    "-fx-border-width: 1;");
        });
    }
    // Fonction pour afficher/masquer la sidebar avec animation
    private void toggleSidebar() {
        double sidebarWidth = sideBar.getWidth();
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sideBar);

        if (isSidebarVisible) {
            slide.setToX(-sidebarWidth);
            isSidebarVisible = false;
        } else {
            slide.setToX(0);
            isSidebarVisible = true;
        }

        slide.play();
    }

    // Fonction pour créer la barre décorative en haut
    private Pane paneInTop() {
        Pane paneInTop = new Pane();
        paneInTop.setPrefHeight(3);
        paneInTop.setStyle("-fx-background-color: linear-gradient(to right, " +
                "rgba(255, 255, 255, 0.1) 0%, " +
                "rgba(255, 255, 255, 0.3) 50%, " +
                "rgba(255, 255, 255, 0.1) 100%);");
        return paneInTop;
    }

    private static VBox historyList;

    public static void setHistoryList(VBox list) {
        historyList = list;
    }

    public static void addHistoryEntry(String title, String date) {
        if (historyList == null) return;

        HBox entry = new HBox();
        entry.setSpacing(15);
        entry.setPadding(new Insets(15));
        entry.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 12;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #ffffff;");

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        entry.getChildren().addAll(titleLabel, dateLabel);

        historyList.getChildren().add(entry);
    }

    // Fonctions pour executer ces scripts
    private void openWorkoutTrainingView() {
        Pane view = training.getView();
        centerArea.setCenter(view);
    }

    private void openHistory() {
        History1 history = new History1();
        centerArea.setCenter(history.getView());
    }

    private void openGraph() {
        Pane graphView = graphic.getView();
        centerArea.setCenter(graphView);
    }

    private void openDeepLearning() {
        Pane view = deepLearning.getView();
        centerArea.setCenter(view);
    }

    private void openBreakDown() {
        BreakDown breakDown = new BreakDown();
        Pane breakDownView = breakDown.getView();
        centerArea.setCenter(breakDownView);
    }
}