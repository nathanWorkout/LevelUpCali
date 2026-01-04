import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class BreakDown {

    private VBox suggestionsBox;
    private PieChart muscleChart;
    private final Set<String> selectedExercises = new HashSet<>();
    private final Map<String, CheckBox> checkBoxMap = new HashMap<>();
    private Label selectedCountLabel;

    // Map des exercices vers leurs muscles
    private static final Map<String, Map<String, Double>> EXERCISE_MUSCLES = new HashMap<>();

    static {
        // Exercices de poussée
        EXERCISE_MUSCLES.put("Pompe", Map.of("Pectoraux", 50.0, "Triceps", 25.0, "Épaules", 15.0, "Abdos", 10.0));
        EXERCISE_MUSCLES.put("Pompe Diamant", Map.of("Triceps", 45.0, "Pectoraux", 30.0, "Épaules", 15.0, "Abdos", 10.0));
        EXERCISE_MUSCLES.put("Pompe Écartée", Map.of("Pectoraux", 55.0, "Épaules", 20.0, "Triceps", 15.0, "Abdos", 10.0));
        EXERCISE_MUSCLES.put("HSPU", Map.of("Épaules", 60.0, "Triceps", 20.0, "Abdos", 20.0));
        EXERCISE_MUSCLES.put("Dips", Map.of("Pectoraux", 40.0, "Triceps", 35.0, "Épaules", 20.0));

        // Exercices de traction
        EXERCISE_MUSCLES.put("Traction", Map.of("Dorsaux", 50.0, "Biceps", 25.0, "Dos", 20.0, "Avant-bras", 5.0));
        EXERCISE_MUSCLES.put("Chin Up", Map.of("Biceps", 45.0, "Dorsaux", 35.0, "Dos", 10.0, "Avant-bras", 10.0));
        EXERCISE_MUSCLES.put("Muscle Up", Map.of("Dorsaux", 30.0, "Pectoraux", 25.0, "Triceps", 20.0, "Épaules", 15.0, "Abdos", 10.0));

        // Skills - Hold
        EXERCISE_MUSCLES.put("Planche", Map.of("Épaules", 45.0, "Abdos", 25.0, "Pectoraux", 20.0, "Triceps", 10.0));
        EXERCISE_MUSCLES.put("Front Lever", Map.of("Dorsaux", 50.0, "Abdos", 30.0, "Dos", 15.0, "Biceps", 5.0));
        EXERCISE_MUSCLES.put("Back Lever", Map.of("Épaules", 35.0, "Dos", 30.0, "Biceps", 20.0, "Abdos", 15.0));
        EXERCISE_MUSCLES.put("Équilibre", Map.of("Épaules", 55.0, "Dos", 25.0, "Abdos", 15.0, "Avant-bras", 5.0));

        // Exercices de gainage
        EXERCISE_MUSCLES.put("L Sit", Map.of("Abdos", 55.0, "Fléchisseurs Hanche", 30.0, "Épaules", 10.0, "Triceps", 5.0));
        EXERCISE_MUSCLES.put("V Sit", Map.of("Abdos", 40.0, "Fléchisseurs Hanche", 45.0, "Épaules", 10.0, "Triceps", 5.0));
        EXERCISE_MUSCLES.put("I Sit", Map.of("Abdos", 30.0, "Fléchisseurs Hanche", 55.0, "Dorsaux", 10.0, "Épaules", 5.0));
    }

    public Pane getView() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        VBox mainContent = new VBox();
        mainContent.setSpacing(25);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);

        VBox header = createHeader();
        HBox content = createContentArea();

        VBox.setVgrow(content, Priority.ALWAYS);
        mainContent.getChildren().addAll(header, content);
        root.getChildren().add(mainContent);

        return root;
    }

    // Barre de présentation en haut de la page Muscle Graph
    private VBox createHeader() {
        VBox header = new VBox();
        header.setSpacing(15);
        header.setPadding(new Insets(30, 35, 30, 35));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-background-radius: 20;" +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 20;");

        DropShadow shadow = new DropShadow(25, Color.rgb(255,255,255,0.15));
        shadow.setSpread(0.2);
        header.setEffect(shadow);

        HBox titleBox = new HBox(18);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("💪");
        icon.setFont(Font.font(38));
        DropShadow iconGlow = new DropShadow();
        iconGlow.setColor(Color.rgb(250, 90, 90, 0.6));
        iconGlow.setRadius(20);
        iconGlow.setSpread(0.4);
        icon.setEffect(iconGlow);

        VBox textBox = new VBox(6);
        Label title = new Label("Répartition Musculaire");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Analysez les groupes musculaires sollicités par les exercices");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.rgb(255,255,255,0.65));

        textBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox statsBadge = new VBox(5);
        statsBadge.setAlignment(Pos.CENTER);
        statsBadge.setPadding(new Insets(12, 20, 12, 20));
        statsBadge.setStyle("-fx-background-color: rgba(250, 90, 90, 0.15);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(250, 90, 90, 0.4);" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 12;");

        selectedCountLabel = new Label("0 Sélectionné");
        selectedCountLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        selectedCountLabel.setTextFill(Color.rgb(250, 90, 90));

        Label badgeSubtitle = new Label("Exercices");
        badgeSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 11));
        badgeSubtitle.setTextFill(Color.rgb(255, 255, 255, 0.6));

        statsBadge.getChildren().addAll(selectedCountLabel, badgeSubtitle);

        titleBox.getChildren().addAll(icon, textBox, spacer, statsBadge);
        header.getChildren().add(titleBox);

        header.setOpacity(0);
        header.setTranslateY(-30);
        FadeTransition fade = new FadeTransition(Duration.millis(600), header);
        fade.setFromValue(0); fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), header);
        slide.setFromY(-30); slide.setToY(0);
        fade.play(); slide.play();

        return header;
    }


    private HBox createContentArea() {
        HBox content = new HBox(25);
        content.setAlignment(Pos.TOP_CENTER);

        VBox leftColumn = createSkillsSearch();
        leftColumn.setMinWidth(280);
        leftColumn.setMaxWidth(380);
        HBox.setHgrow(leftColumn, Priority.NEVER);

        VBox rightColumn = createChartArea();
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        content.getChildren().addAll(leftColumn, rightColumn);
        return content;
    }

    // Espace pour chercher des mouvement
    private VBox createSkillsSearch() {
        VBox column = new VBox();
        column.setSpacing(20);
        column.setAlignment(Pos.TOP_CENTER);

        VBox searchContainer = new VBox(15);
        searchContainer.setPadding(new Insets(25));
        searchContainer.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-background-radius: 18;" +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 18;");

        DropShadow searchShadow = new DropShadow(20, Color.rgb(255,255,255,0.12));
        searchShadow.setSpread(0.2);
        searchContainer.setEffect(searchShadow);

        HBox searchTitleBox = new HBox(10);
        searchTitleBox.setAlignment(Pos.CENTER_LEFT);

        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font(22));

        Label searchTitle = new Label("Sélection d'Exercices");
        searchTitle.setFont(Font.font("System", FontWeight.BOLD, 19));
        searchTitle.setTextFill(Color.WHITE);

        searchTitleBox.getChildren().addAll(searchIcon, searchTitle);

        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPromptText("Rechercher des exercices...");
        searchField.setPrefHeight(50);
        searchField.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 12; -fx-padding: 15;" +
                        "-fx-font-size: 15; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.25);" +
                        "-fx-border-width: 1.5; -fx-border-radius: 12; -fx-prompt-text-fill: rgba(255,255,255,0.45);"
        );

        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                searchField.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 12; -fx-padding: 15;" +
                                "-fx-font-size: 15; -fx-text-fill: white; -fx-border-color: rgba(250,90,90,0.6);" +
                                "-fx-border-width: 2; -fx-border-radius: 12; -fx-prompt-text-fill: rgba(255,255,255,0.45);"
                );
            } else {
                searchField.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 12; -fx-padding: 15;" +
                                "-fx-font-size: 15; -fx-text-fill: white; -fx-border-color: rgba(255,255,255,0.25);" +
                                "-fx-border-width: 1.5; -fx-border-radius: 12; -fx-prompt-text-fill: rgba(255,255,255,0.45);"
                );
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(newVal));
        searchContainer.getChildren().addAll(searchTitleBox, searchField);

        suggestionsBox = new VBox(10);
        suggestionsBox.setPadding(new Insets(10));

        ScrollPane suggestionsScroll = new ScrollPane(suggestionsBox);
        suggestionsScroll.setFitToWidth(true);
        suggestionsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        suggestionsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        suggestionsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(suggestionsScroll, Priority.ALWAYS);

        VBox leftContent = new VBox(20, searchContainer, suggestionsScroll);
        ScrollPane leftScroll = new ScrollPane(leftContent);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        leftScroll.setPrefWidth(320);
        VBox.setVgrow(leftScroll, Priority.ALWAYS);

        column.getChildren().add(leftScroll);

        performSearch("");

        column.setOpacity(0);
        column.setTranslateX(-50);
        FadeTransition fade = new FadeTransition(Duration.millis(700), column);
        fade.setFromValue(0); fade.setToValue(1);
        fade.setDelay(Duration.millis(100));
        TranslateTransition slide = new TranslateTransition(Duration.millis(700), column);
        slide.setFromX(-50); slide.setToX(0);
        slide.setDelay(Duration.millis(100));
        fade.play(); slide.play();

        return column;
    }

    private void performSearch(String query) {
        List<String> allExercises = new ArrayList<>(EXERCISE_MUSCLES.keySet());
        allExercises.sort(String::compareTo);

        List<String> results = allExercises.stream()
                .filter(s -> s.toLowerCase().contains(query.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());

        displaySuggestions(results);
    }

    // Afficher les exercices en fonction du mot entré par l'utilisateur
    private void displaySuggestions(List<String> exercises) {
        suggestionsBox.getChildren().clear();
        checkBoxMap.clear();

        if (exercises.isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(30));

            Label emptyIcon = new Label("🔍");
            emptyIcon.setFont(Font.font(48));
            emptyIcon.setOpacity(0.3);

            Label noResults = new Label("Aucun exercice trouvé");
            noResults.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
            noResults.setTextFill(Color.rgb(255,255,255,0.5));

            emptyState.getChildren().addAll(emptyIcon, noResults);
            suggestionsBox.getChildren().add(emptyState);
            return;
        }

        // Couleur du mouvement dans le graphique camanbert
        Map<String, String> exerciseColors = new HashMap<>();
        exerciseColors.put("Pompe", "#FF3B30");
        exerciseColors.put("Pompe Diamant", "#FF9500");
        exerciseColors.put("Pompe Écartée", "#FFCC00");
        exerciseColors.put("Traction", "#34C759");
        exerciseColors.put("Chin Up", "#5AC8FA");
        exerciseColors.put("Muscle Up", "#007AFF");
        exerciseColors.put("Planche", "#5856D6");
        exerciseColors.put("Front Lever", "#AF52DE");
        exerciseColors.put("Back Lever", "#FF2D55");
        exerciseColors.put("Handstand", "#FF3B30");
        exerciseColors.put("HSPU", "#34C759");
        exerciseColors.put("Dips", "#5AC8FA");
        exerciseColors.put("L Sit", "#007AFF");
        exerciseColors.put("V Sit", "#5856D6");
        exerciseColors.put("I Sit", "#AF52DE");

        // Graphique
        int delay = 0;
        for (String ex : exercises) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(14, 18, 14, 18));
            card.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 14;" +
                    "-fx-border-color: rgba(255,255,255,0.15); -fx-border-width:1.5; -fx-border-radius:14;");

            CheckBox checkBox = new CheckBox();
            checkBox.setStyle("-fx-mark-color: " + exerciseColors.getOrDefault(ex, "#5AC8FA") + ";" +
                    "-fx-background-color: rgba(255,255,255,0.1);" +
                    "-fx-background-radius: 6;");

            if (selectedExercises.contains(ex)) checkBox.setSelected(true);
            checkBoxMap.put(ex, checkBox);

            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    selectedExercises.add(ex);
                    card.setStyle("-fx-background-color: " + hexToRgba(exerciseColors.getOrDefault(ex, "#5AC8FA"), 0.15) +
                            "; -fx-background-radius: 14;" +
                            "-fx-border-color: " + exerciseColors.getOrDefault(ex, "#5AC8FA") +
                            "; -fx-border-width:2; -fx-border-radius:14;");
                } else {
                    selectedExercises.remove(ex);
                    card.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 14;" +
                            "-fx-border-color: rgba(255,255,255,0.15); -fx-border-width:1.5; -fx-border-radius:14;");
                }
                updateChart();
                updateSelectedCount();
            });

            Label name = new Label(ex);
            name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
            name.setTextFill(Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Map<String, Double> muscles = EXERCISE_MUSCLES.get(ex);
            String primaryMuscle = muscles.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Inconnu");

            Label muscleBadge = new Label(primaryMuscle);
            muscleBadge.setFont(Font.font("System", FontWeight.BOLD, 10));
            muscleBadge.setTextFill(Color.rgb(255, 255, 255, 0.7));
            muscleBadge.setPadding(new Insets(4, 8, 4, 8));
            muscleBadge.setStyle("-fx-background-color: rgba(255,255,255,0.1);" +
                    "-fx-background-radius: 8;");

            card.getChildren().addAll(checkBox, name, spacer, muscleBadge);

            DropShadow hoverGlow = new DropShadow();
            hoverGlow.setColor(Color.web(exerciseColors.getOrDefault(ex, "#5AC8FA"), 0.4));
            hoverGlow.setRadius(15);
            hoverGlow.setSpread(0.3);

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
            scaleUp.setToX(1.02);
            scaleUp.setToY(1.02);

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            card.setOnMouseEntered(e -> {
                card.setEffect(hoverGlow);
                card.setStyle(card.getStyle() + "-fx-cursor: hand;");
                scaleUp.playFromStart();
            });

            card.setOnMouseExited(e -> {
                card.setEffect(null);
                scaleDown.playFromStart();
            });

            card.setOpacity(0);
            card.setTranslateX(-20);
            FadeTransition cardFade = new FadeTransition(Duration.millis(300), card);
            cardFade.setFromValue(0);
            cardFade.setToValue(1);
            cardFade.setDelay(Duration.millis(delay));

            TranslateTransition cardSlide = new TranslateTransition(Duration.millis(300), card);
            cardSlide.setFromX(-20);
            cardSlide.setToX(0);
            cardSlide.setDelay(Duration.millis(delay));

            cardFade.play();
            cardSlide.play();

            delay += 50;

            suggestionsBox.getChildren().add(card);
        }
    }


    private VBox createChartArea() {
        VBox card = new VBox(25);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-background-radius: 20;" +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width:1; -fx-border-radius:20;");

        DropShadow cardShadow = new DropShadow(20, Color.rgb(255,255,255,0.12));
        cardShadow.setSpread(0.2);
        card.setEffect(cardShadow);

        HBox chartHeader = new HBox(15);
        chartHeader.setAlignment(Pos.CENTER_LEFT);

        Label chartIcon = new Label("🎯");
        chartIcon.setFont(Font.font(28));

        Label chartTitle = new Label("Distribution Musculaire");
        chartTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        chartTitle.setTextFill(Color.WHITE);

        chartHeader.getChildren().addAll(chartIcon, chartTitle);

        muscleChart = new PieChart();
        muscleChart.setTitle("");
        muscleChart.setLegendVisible(true);
        muscleChart.setLabelsVisible(true);
        muscleChart.setPrefHeight(600);
        muscleChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(255,255,255,0.1);" +
                        "-fx-border-radius: 12;"
        );

        VBox chartBox = new VBox(20);
        chartBox.setPrefHeight(500);
        VBox.setVgrow(chartBox, Priority.ALWAYS);
        chartBox.getChildren().add(muscleChart);

        ScrollPane chartScroll = new ScrollPane(chartBox);
        chartScroll.setFitToWidth(true);
        chartScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chartScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chartScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().addAll(chartHeader, chartScroll);

        updateChart();

        Platform.runLater(this::applyWhiteLabelStyles);

        card.setOpacity(0);
        card.setTranslateX(50);
        FadeTransition fade = new FadeTransition(Duration.millis(700), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(150));
        TranslateTransition slide = new TranslateTransition(Duration.millis(700), card);
        slide.setFromX(50);
        slide.setToX(0);
        slide.setDelay(Duration.millis(150));
        fade.play();
        slide.play();

        return card;
    }

    // Update le graphique
    private void updateChart() {
        muscleChart.getData().clear();

        if (selectedExercises.isEmpty()) {
            muscleChart.setVisible(true);
            PieChart.Data emptyData = new PieChart.Data("Aucun exercice sélectionné", 1);
            muscleChart.getData().add(emptyData);

            emptyData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: rgba(255,255,255,0.2);" +
                            "-fx-border-color: rgba(255,255,255,0.2);" +
                            "-fx-border-width: 2;");
                }
            });

            Platform.runLater(() -> {
                muscleChart.applyCss();
                muscleChart.layout();
                applyWhiteLabelStyles();
            });
            return;
        }

        // Combiner tous les muscles des exercices sélectionnés
        Map<String, Double> combinedMuscles = new HashMap<>();

        for (String exercise : selectedExercises) {
            Map<String, Double> muscles = EXERCISE_MUSCLES.get(exercise);
            if (muscles != null) {
                for (Map.Entry<String, Double> entry : muscles.entrySet()) {
                    combinedMuscles.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
        }

        // Convertir en données du graphique
        for (Map.Entry<String, Double> entry : combinedMuscles.entrySet()) {
            PieChart.Data slice = new PieChart.Data(
                    entry.getKey() + " (" + String.format("%.1f", entry.getValue()) + "%)",
                    entry.getValue()
            );
            muscleChart.getData().add(slice);
        }

        // Appliquer les couleurs
        String[] colors = {"#FF3B30", "#FF9500", "#FFCC00", "#34C759", "#5AC8FA",
                "#007AFF", "#5856D6", "#AF52DE", "#FF2D55"};

        int colorIndex = 0;
        for (PieChart.Data data : muscleChart.getData()) {
            String color = colors[colorIndex % colors.length];
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-pie-color: " + color + ";" +
                            "-fx-border-color: rgba(255,255,255,0.2);" +
                            "-fx-border-width: 2;");

                    DropShadow glow = new DropShadow();
                    glow.setColor(Color.web(color, 0.5));
                    glow.setRadius(12);
                    glow.setSpread(0.3);

                    newNode.setOnMouseEntered(e -> {
                        newNode.setEffect(glow);
                        ScaleTransition st = new ScaleTransition(Duration.millis(150), newNode);
                        st.setToX(1.1);
                        st.setToY(1.1);
                        st.play();
                    });

                    newNode.setOnMouseExited(e -> {
                        newNode.setEffect(null);
                        ScaleTransition st = new ScaleTransition(Duration.millis(150), newNode);
                        st.setToX(1.0);
                        st.setToY(1.0);
                        st.play();
                    });
                }
            });
            colorIndex++;
        }

        muscleChart.setVisible(true);

        Platform.runLater(() -> {
            Platform.runLater(this::applyWhiteLabelStyles);
        });
    }

    private void applyWhiteLabelStyles() {
        muscleChart.applyCss();
        muscleChart.layout();

        // Forcer TOUS les Text nodes à être blancs(ne marche pas mais je garde au cas ou je ressaie)
        Set<Node> allNodes = muscleChart.lookupAll(".text");
        for (Node node : allNodes) {
            if (node instanceof Text) {
                Text text = (Text) node;
                text.setFill(Color.WHITE);
                text.setStyle("-fx-fill: white;");
            }
        }

        // Cibler spécifiquement les labels du pie chart
        Set<Node> pieLabels = muscleChart.lookupAll(".chart-pie-label");
        for (Node node : pieLabels) {
            if (node instanceof Text) {
                Text text = (Text) node;
                text.setFill(Color.WHITE);
                text.setStyle("-fx-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            }
        }

        // Cibler les labels de la légende
        Set<Node> legendLabels = muscleChart.lookupAll(".chart-legend-item-text");
        for (Node node : legendLabels) {
            if (node instanceof Text) {
                Text text = (Text) node;
                text.setFill(Color.WHITE);
                text.setStyle("-fx-fill: white; -fx-font-size: 12px;");
            }
        }

        // Fallback: tous les labels
        Set<Node> labels = muscleChart.lookupAll(".label");
        for (Node node : labels) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setTextFill(Color.WHITE);
                label.setStyle("-fx-text-fill: white;");
            }
        }
    }


    private void updateSelectedCount() {
        if (selectedCountLabel != null) {
            int count = selectedExercises.size();
            selectedCountLabel.setText(count + " Sélectionné" + (count > 1 ? "s" : ""));

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), selectedCountLabel);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.play();
        }
    }

    private String hexToRgba(String hex, double alpha) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, alpha);
    }
}