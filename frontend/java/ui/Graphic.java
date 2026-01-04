import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class Graphic {

    private VBox suggestionsBox;
    private LineChart<Number, Number> holdChart;
    private LineChart<Number, Number> repsChart;
    private Set<String> selectedExercises = new HashSet<>();
    private Map<String, CheckBox> checkBoxMap = new HashMap<>();
    private Label statsLabel;
    private Label selectedCountLabel;

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

    // TopBar pour présenter cette section
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

        Label icon = new Label("📊");
        icon.setFont(Font.font(38));
        DropShadow iconGlow = new DropShadow();
        iconGlow.setColor(Color.rgb(90, 200, 250, 0.6));
        iconGlow.setRadius(20);
        iconGlow.setSpread(0.4);
        icon.setEffect(iconGlow);

        VBox textBox = new VBox(6);
        Label title = new Label("Statistiques & Progrès");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Suivez votre évolution avec des graphiques détaillés");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.rgb(255,255,255,0.65));

        textBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox statsBadge = new VBox(5);
        statsBadge.setAlignment(Pos.CENTER);
        statsBadge.setPadding(new Insets(12, 20, 12, 20));
        statsBadge.setStyle("-fx-background-color: rgba(90, 200, 250, 0.15);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(90, 200, 250, 0.4);" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 12;");

        selectedCountLabel = new Label("0 Sélectionné");
        selectedCountLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        selectedCountLabel.setTextFill(Color.rgb(90, 200, 250));

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

        VBox rightColumn = createChartsArea();
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        content.getChildren().addAll(leftColumn, rightColumn);
        return content;
    }

    // Espace pour rechercher les mouvements
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
                                "-fx-font-size: 15; -fx-text-fill: white; -fx-border-color: rgba(90,200,250,0.6);" +
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

    // Ici, on définit tout les mouvements qui seront disponible pour le graphique
    private void performSearch(String query) {
        List<String> allExercises = List.of(
                // Skills
                "Planche", "Front lever", "Back lever", "Handstand", "HSPU",
                "L Sit", "V sit", "I Sit",

                // Reps
                "Push up", "Diamond push up", "Wide push up", "Explosive push up",
                "Traction", "Archer pull up", "Chin up", "One arm pull up",
                "Dips", "Explosive dips", "Russian dips",
                "Muscle up", "No dips muscle up", "Wide muscle up",
                "Squat", "Pistol squat", "Leg raises", "Dragon flag"
        );

        List<String> results = allExercises.stream()
                .filter(s -> s.toLowerCase().contains(query.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());

        displaySuggestions(results);
    }

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


        Map<String, String> exerciseColors = new HashMap<>();
        exerciseColors.put("Planche", "#FF3B30");
        exerciseColors.put("Front Lever", "#FF9500");
        exerciseColors.put("Back Lever", "#FFCC00");
        exerciseColors.put("Handstand", "#34C759");
        exerciseColors.put("HSPU", "#5AC8FA");
        exerciseColors.put("L Sit", "#007AFF");
        exerciseColors.put("V Sit", "#5856D6");
        exerciseColors.put("I Sit", "#AF52DE");
        exerciseColors.put("Muscle Up", "#FF2D55");
        exerciseColors.put("Pompes", "#FF3B30");
        exerciseColors.put("Tractions", "#34C759");
        exerciseColors.put("Dips", "#5AC8FA");

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
                updateCharts();
                updateSelectedCount();
            });

            Label name = new Label(ex);
            name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
            name.setTextFill(Color.WHITE);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label typeBadge = new Label(PerformanceManager.checkIfSkill(ex) ? "Maintien" : "Reps");
            typeBadge.setFont(Font.font("System", FontWeight.BOLD, 10));
            typeBadge.setTextFill(Color.rgb(255, 255, 255, 0.7));
            typeBadge.setPadding(new Insets(4, 8, 4, 8));
            typeBadge.setStyle("-fx-background-color: rgba(255,255,255,0.1);" +
                    "-fx-background-radius: 8;");

            card.getChildren().addAll(checkBox, name, spacer, typeBadge);

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

    private VBox createChartsArea() {
        VBox card = new VBox(25);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-background-radius: 20;" +
                "-fx-border-color: rgba(255,255,255,0.2); -fx-border-width:1; -fx-border-radius:20;");

        DropShadow cardShadow = new DropShadow(20, Color.rgb(255,255,255,0.12));
        cardShadow.setSpread(0.2);
        card.setEffect(cardShadow);

        HBox chartsHeader = new HBox(15);
        chartsHeader.setAlignment(Pos.CENTER_LEFT);

        Label chartsIcon = new Label("📈");
        chartsIcon.setFont(Font.font(28));

        Label chartsTitle = new Label("Graphiques de Performance");
        chartsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        chartsTitle.setTextFill(Color.WHITE);

        chartsHeader.getChildren().addAll(chartsIcon, chartsTitle);

        VBox chartsBox = new VBox(20);
        chartsBox.setPrefHeight(500);
        VBox.setVgrow(chartsBox, Priority.ALWAYS);

// --- Hold Chart ---
        NumberAxis xHold = new NumberAxis();
        xHold.setLabel("Jours d'Entraînement");
        xHold.setStyle("-fx-tick-label-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");
        xHold.setTickMarkVisible(false);
        xHold.setMinorTickVisible(false);

        NumberAxis yHold = new NumberAxis();
        yHold.setLabel("Durée de Maintien (sec)");
        yHold.setStyle("-fx-tick-label-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");
        yHold.setTickMarkVisible(false);
        yHold.setMinorTickVisible(false);

        holdChart = new LineChart<>(xHold, yHold);
        holdChart.setTitle("Progrès Maintien Statique");
        holdChart.setLegendVisible(true);
        holdChart.setAnimated(true);
        holdChart.setCreateSymbols(true);
        holdChart.setPrefHeight(400);
        holdChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(255,255,255,0.2);"
        );


        NumberAxis xReps = new NumberAxis();
        xReps.setLabel("Jours d'Entraînement");
        xReps.setStyle("-fx-tick-label-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");
        xReps.setTickMarkVisible(false);
        xReps.setMinorTickVisible(false);

        NumberAxis yReps = new NumberAxis();
        yReps.setLabel("Répétitions");
        yReps.setStyle("-fx-tick-label-fill: rgba(255,255,255,0.8); -fx-font-size: 12px;");
        yReps.setTickMarkVisible(false);
        yReps.setMinorTickVisible(false);

        repsChart = new LineChart<>(xReps, yReps);
        repsChart.setTitle("Progrès Répétitions");
        repsChart.setLegendVisible(true);
        repsChart.setAnimated(true);
        repsChart.setCreateSymbols(true);
        repsChart.setPrefHeight(400);
        repsChart.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: rgba(255,255,255,0.2);"
        );

        chartsBox.getChildren().addAll(holdChart, repsChart);


        ScrollPane chartsScroll = new ScrollPane(chartsBox);
        chartsScroll.setFitToWidth(true);
        chartsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chartsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chartsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        card.getChildren().addAll(chartsHeader, chartsScroll);

        updateCharts();

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



    private void updateCharts() {
        holdChart.getData().clear();
        repsChart.getData().clear();

        if (selectedExercises.isEmpty()) {
            holdChart.setVisible(false);
            repsChart.setVisible(false);
            return;
        }

        boolean hasHold = selectedExercises.stream().anyMatch(PerformanceManager::checkIfSkill);
        boolean hasReps = selectedExercises.stream().anyMatch(PerformanceManager::checkIfRep);

        holdChart.setVisible(hasHold);
        repsChart.setVisible(hasReps);

        String[] colors = {"#FF3B30", "#FF9500", "#FFCC00", "#34C759", "#5AC8FA", "#007AFF", "#5856D6", "#AF52DE", "#FF2D55"};
        int colorIndex = 0;

        int dayCount;
        for (String ex : selectedExercises) {
            String chartColor = colors[colorIndex % colors.length];
            colorIndex++;

            if (PerformanceManager.checkIfSkill(ex)) {
                List<perfStock> data = PerformanceManager.getPerformancesByExercise(ex);
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(ex);
                dayCount = 1;
                for (perfStock p : data) {
                    series.getData().add(new XYChart.Data<>(dayCount++, p.hold));
                }
                if (hasHold) {
                    holdChart.getData().add(series);
                    series.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-stroke: " + chartColor + "; -fx-stroke-width: 4px;" +
                                    "-fx-effect: dropshadow(gaussian, " + chartColor + "66, 8, 0.4, 0, 0);");
                        }
                    });
                    for (XYChart.Data<Number, Number> point : series.getData()) {
                        point.nodeProperty().addListener((obs, oldNode, newNode) -> {
                            if (newNode != null) {
                                newNode.setStyle("-fx-background-color: " + chartColor + ", white;" +
                                        "-fx-background-insets: 0, 2;" +
                                        "-fx-background-radius: 8px;" +
                                        "-fx-padding: 8px;" +
                                        "-fx-effect: dropshadow(gaussian, " + chartColor + "99, 6, 0.5, 0, 0);");
                            }
                        });
                    }
                }
            } else {
                List<perfStock> data = PerformanceManager.getPerformancesByExercise(ex);
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(ex);
                dayCount = 1;
                for (perfStock p : data) {
                    series.getData().add(new XYChart.Data<>(dayCount++, p.reps));
                }
                if (hasReps) {
                    repsChart.getData().add(series);
                    series.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-stroke: " + chartColor + "; -fx-stroke-width: 4px;" +
                                    "-fx-effect: dropshadow(gaussian, " + chartColor + "66, 8, 0.4, 0, 0);");
                        }
                    });
                    for (XYChart.Data<Number, Number> point : series.getData()) {
                        point.nodeProperty().addListener((obs, oldNode, newNode) -> {
                            if (newNode != null) {
                                newNode.setStyle("-fx-background-color: " + chartColor + ", white;" +
                                        "-fx-background-insets: 0, 2;" +
                                        "-fx-background-radius: 8px;" +
                                        "-fx-padding: 8px;" +
                                        "-fx-effect: dropshadow(gaussian, " + chartColor + "99, 6, 0.5, 0, 0);");
                            }
                        });
                    }
                }
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