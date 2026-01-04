import javafx.animation.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrainingDetail {
    private VBox root;
    private VBox suggestionsBox;
    private VBox exercisesListBox;
    private List<ExerciseData> sessionExercises;
    private TextField searchField;
    private String trainingName;

    private static List<SavedTraining> savedTrainings = new ArrayList<>();

    // Fenetre principale
    public TrainingDetail(String trainingName) {
        this.trainingName = trainingName;
        sessionExercises = loadTrainingExercises(trainingName);

        root = new VBox();
        root.setSpacing(0);
        root.setPadding(new Insets(0));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #0a0a0a;");

        VBox header = createHeader(trainingName);
        root.getChildren().add(header);

        HBox mainContent = new HBox();
        mainContent.setSpacing(0);
        mainContent.setPadding(new Insets(30, 30, 30, 30));
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        VBox leftColumn = createLeftColumn();
        leftColumn.setPrefWidth(400);
        leftColumn.setMaxWidth(400);

        VBox rightColumn = createRightColumn();
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        root.getChildren().add(mainContent);

        for (ExerciseData ex : sessionExercises) {
            Node card = createExerciseCard(ex);
            exercisesListBox.getChildren().add(card);
            playAddAnimation(card, 0);
        }
    }

    public Pane getView() {
        return root;
    }

    // On sauvegarde les changements
    public void onExit() {
        saveTrainingExercises();
    }

    // Sauvegarde
    private void saveTrainingExercises() {
        savedTrainings.removeIf(t -> t.name.equals(trainingName));
        if (!sessionExercises.isEmpty()) {
            savedTrainings.add(new SavedTraining(trainingName, new ArrayList<>(sessionExercises)));
        }
    }

    private List<ExerciseData> loadTrainingExercises(String name) {
        for (SavedTraining saved : savedTrainings) {
            if (saved.name.equals(name)) {
                return new ArrayList<>(saved.exercises);
            }
        }
        return new ArrayList<>();
    }

    // Headeer design
    private VBox createHeader(String trainingName) {
        Pane accentLine = new Pane();
        accentLine.setPrefHeight(3);
        accentLine.setStyle("-fx-background-color: linear-gradient(to right, " +
                "rgba(255, 255, 255, 0.1) 0%, " +
                "rgba(255, 255, 255, 0.3) 50%, " +
                "rgba(255, 255, 255, 0.1) 100%);");

        HBox header = new HBox();
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(25, 40, 25, 40));
        header.setStyle("-fx-background-color: rgba(15, 15, 15, 0.95); " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 0 0 1 0;");

        Label icon = new Label("💪");
        icon.setFont(Font.font(32));

        Label title = new Label(trainingName);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #ffffff;");

        Label subtitle = new Label("Séance d'entraînement personnalisée");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        subtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);

        VBox headerContainer = new VBox(accentLine, header);
        headerContainer.setSpacing(0);

        headerContainer.setOpacity(0);
        headerContainer.setTranslateY(-20);
        FadeTransition fade = new FadeTransition(Duration.millis(500), headerContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), headerContainer);
        slide.setFromY(-20);
        slide.setToY(0);
        fade.play();
        slide.play();

        return headerContainer;
    }

    // Collonne gauche
    private VBox createLeftColumn() {
        VBox column = new VBox();
        column.setSpacing(20);
        column.setAlignment(Pos.TOP_CENTER);
        column.setPadding(new Insets(0, 15, 0, 0));

        VBox searchContainer = new VBox();
        searchContainer.setSpacing(15);
        searchContainer.setPadding(new Insets(25));
        searchContainer.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 16; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16;");

        DropShadow glow = new DropShadow(20, Color.rgb(255, 255, 255, 0.1));
        searchContainer.setEffect(glow);

        Label searchTitle = new Label("🔍 Rechercher un exercice");
        searchTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        searchTitle.setStyle("-fx-text-fill: #ffffff;");

        searchField = new TextField();
        searchField.setPromptText("Tapez le nom de l'exercice...");
        searchField.setPrefHeight(50);
        searchField.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 15; " +
                        "-fx-font-size: 15; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);"
        );

        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                searchField.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.12); " +
                                "-fx-background-radius: 12; " +
                                "-fx-padding: 15; " +
                                "-fx-font-size: 15; " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-border-color: rgba(255, 255, 255, 0.4); " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 12; " +
                                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);"
                );
            } else {
                searchField.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                                "-fx-background-radius: 12; " +
                                "-fx-padding: 15; " +
                                "-fx-font-size: 15; " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 12; " +
                                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);"
                );
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(newVal));

        searchContainer.getChildren().addAll(searchTitle, searchField);

        suggestionsBox = new VBox();
        suggestionsBox.setSpacing(12);

        // Espace pour scroller
        ScrollPane suggestionsScroll = new ScrollPane(suggestionsBox);
        suggestionsScroll.setFitToWidth(true);
        suggestionsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        suggestionsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        suggestionsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(suggestionsScroll, Priority.ALWAYS);

        VBox manualAddForm = createManualAddForm();

        VBox leftContent = new VBox();
        leftContent.setSpacing(20);
        leftContent.getChildren().addAll(searchContainer, suggestionsScroll, manualAddForm);

        ScrollPane leftScroll = new ScrollPane(leftContent);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(leftScroll, Priority.ALWAYS);

        column.getChildren().add(leftScroll);

        column.setOpacity(0);
        column.setTranslateX(-30);
        FadeTransition fade = new FadeTransition(Duration.millis(600), column);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(200));
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), column);
        slide.setFromX(-30);
        slide.setToX(0);
        slide.setDelay(Duration.millis(200));
        fade.play();
        slide.play();

        return column;
    }

    // Espace pour ajouter de nouveaux exercices manuellement
    private VBox createManualAddForm() {
        VBox form = new VBox();
        form.setSpacing(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 16; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16;");

        DropShadow glow = new DropShadow(20, Color.rgb(255, 255, 255, 0.1));
        form.setEffect(glow);

        Label formTitle = new Label("Créer un exercice personnalisé");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 17));
        formTitle.setStyle("-fx-text-fill: #ffffff;");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de l'exercice...");
        nameField.setPrefHeight(48);
        nameField.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 14; " +
                        "-fx-font-size: 14; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);"
        );

        HBox detailsBox = new HBox();
        detailsBox.setSpacing(10);

        TextField setsField = createCompactField("Séries", "3");
        TextField repsField = createCompactField("Reps", "10");
        TextField holdField = createCompactField("Hold", "");

        HBox.setHgrow(setsField, Priority.ALWAYS);
        HBox.setHgrow(repsField, Priority.ALWAYS);
        HBox.setHgrow(holdField, Priority.ALWAYS);

        detailsBox.getChildren().addAll(setsField, repsField, holdField);

        Button addButton = new Button("➕ Ajouter à la séance");
        addButton.setPrefHeight(48);
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-radius: 12; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand;"
        );

        DropShadow btnGlow = new DropShadow(15, Color.rgb(255, 255, 255, 0.3));
        addButton.setOnMouseEntered(e -> {
            addButton.setEffect(btnGlow);
            addButton.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.4); " +
                            "-fx-border-width: 1; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14; " +
                            "-fx-cursor: hand;"
            );
        });

        addButton.setOnMouseExited(e -> {
            addButton.setEffect(null);
            addButton.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-border-width: 1; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14; " +
                            "-fx-cursor: hand;"
            );
        });

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                addExerciseToSession(
                        name,
                        setsField.getText().trim(),
                        repsField.getText().trim(),
                        holdField.getText().trim()
                );
                nameField.clear();
                setsField.setText("3");
                repsField.setText("10");
                holdField.clear();
            }
        });

        form.getChildren().addAll(formTitle, nameField, detailsBox, addButton);
        return form;
    }

    private TextField createCompactField(String prompt, String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPromptText(prompt);
        field.setPrefHeight(48);
        field.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 12; " +
                        "-fx-font-size: 13; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 10; " +
                        "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);"
        );
        return field;
    }

    // Collonne de droite
    private VBox createRightColumn() {
        VBox column = new VBox();
        column.setSpacing(25);
        column.setPadding(new Insets(25, 0, 0, 15));

        VBox sessionContainer = new VBox();
        sessionContainer.setSpacing(20);
        sessionContainer.setPadding(new Insets(30));
        sessionContainer.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 16; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16;");

        DropShadow glow = new DropShadow(20, Color.rgb(255, 255, 255, 0.1));
        sessionContainer.setEffect(glow);

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(15);

        Label icon = new Label("📋");
        icon.setFont(Font.font(28));

        Label title = new Label("Ma Séance");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #ffffff;");

        titleBox.getChildren().addAll(icon, title);

        Pane separator = new Pane();
        separator.setPrefHeight(2);
        separator.setStyle("-fx-background-color: linear-gradient(to right, " +
                "rgba(255, 255, 255, 0.1) 0%, " +
                "rgba(255, 255, 255, 0.2) 50%, " +
                "rgba(255, 255, 255, 0.1) 100%);");

        exercisesListBox = new VBox();
        exercisesListBox.setSpacing(12);
        exercisesListBox.setMinHeight(300);
        exercisesListBox.setAlignment(Pos.TOP_CENTER);

        Label emptyMessage = new Label("Ajoutez des exercices pour créer votre séance");
        emptyMessage.setFont(Font.font("System", FontWeight.NORMAL, 15));
        emptyMessage.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);");
        emptyMessage.setVisible(sessionExercises.isEmpty());

        exercisesListBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            emptyMessage.setVisible(exercisesListBox.getChildren().isEmpty());
        });

        StackPane exercisesContainer = new StackPane();
        exercisesContainer.getChildren().addAll(exercisesListBox, emptyMessage);
        exercisesContainer.setMinHeight(300);

        ScrollPane exercisesScroll = new ScrollPane(exercisesContainer);
        exercisesScroll.setFitToWidth(true);
        exercisesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        exercisesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        exercisesScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(exercisesScroll, Priority.ALWAYS);

        HBox actionButtons = new HBox();
        actionButtons.setSpacing(15);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        Button saveButton = createModernActionButton("💾 Sauvegarder la séance", true);
        Button resetButton = createModernActionButton("🗑️ Tout effacer", false);

        saveButton.setOnAction(e -> saveSession());
        resetButton.setOnAction(e -> resetSession());

        HBox.setHgrow(saveButton, Priority.ALWAYS);
        HBox.setHgrow(resetButton, Priority.ALWAYS);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setMaxWidth(Double.MAX_VALUE);

        actionButtons.getChildren().addAll(saveButton, resetButton);

        sessionContainer.getChildren().addAll(titleBox, separator, exercisesScroll, actionButtons);
        column.getChildren().add(sessionContainer);
        VBox.setVgrow(sessionContainer, Priority.ALWAYS);

        column.setOpacity(0);
        column.setTranslateX(30);
        FadeTransition fade = new FadeTransition(Duration.millis(600), column);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(200));
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), column);
        slide.setFromX(30);
        slide.setToX(0);
        slide.setDelay(Duration.millis(200));
        fade.play();
        slide.play();

        return column;
    }

    private Button createModernActionButton(String text, boolean isPrimary) {
        Button btn = new Button(text);
        btn.setPrefHeight(52);

        if (isPrimary) {
            btn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.15); " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                            "-fx-border-width: 1; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14; " +
                            "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: rgba(239, 68, 68, 0.15); " +
                            "-fx-text-fill: #ffffff; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-radius: 12; " +
                            "-fx-border-color: rgba(239, 68, 68, 0.4); " +
                            "-fx-border-width: 1; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14; " +
                            "-fx-cursor: hand;"
            );
        }

        DropShadow hoverGlow = new DropShadow(18, isPrimary ?
                Color.rgb(255, 255, 255, 0.4) : Color.rgb(239, 68, 68, 0.5));

        btn.setOnMouseEntered(e -> {
            btn.setEffect(hoverGlow);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();

            if (isPrimary) {
                btn.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.25); " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-background-radius: 12; " +
                                "-fx-border-radius: 12; " +
                                "-fx-border-color: rgba(255, 255, 255, 0.5); " +
                                "-fx-border-width: 1; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: rgba(239, 68, 68, 0.25); " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-background-radius: 12; " +
                                "-fx-border-radius: 12; " +
                                "-fx-border-color: rgba(239, 68, 68, 0.6); " +
                                "-fx-border-width: 1; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand;"
                );
            }
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            if (isPrimary) {
                btn.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.15); " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-background-radius: 12; " +
                                "-fx-border-radius: 12; " +
                                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                                "-fx-border-width: 1; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand;"
                );
            } else {
                btn.setStyle(
                        "-fx-background-color: rgba(239, 68, 68, 0.15); " +
                                "-fx-text-fill: #ffffff; " +
                                "-fx-background-radius: 12; " +
                                "-fx-border-radius: 12; " +
                                "-fx-border-color: rgba(239, 68, 68, 0.4); " +
                                "-fx-border-width: 1; " +
                                "-fx-font-weight: bold; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand;"
                );
            }
        });

        return btn;
    }

    // Champ de recherche
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            suggestionsBox.getChildren().clear();
            return;
        }

        // Base de données des exercices
        List<String> exercises = List.of(
                "Push up","Diamond push up","Wide push up","Explosive push up","One arm push up",
                "Traction","Archer pull up","Chin up","High pull up","Chest to bar",
                "One arm pull up","Commando pull up","L sit pull up","Dips","Explosive dips",
                "Russian dips","Deep push up","Planche","Tuck front lever","Adv tuck front lever",
                "Straddle front lever","Half lay front lever","Front lever","Front lever raise", "Front lever pull up",
                "Muscle up","No dips muscle up","Wide muscle up","Handstand","Wall HSPU","HSPU",
                "L Sit","V sit","Hollow body hold","Toes to bar","Leg raises","Dragon flag",
                "Squat","Pistol squat","Scapula pull up","Scapula push up","Dead hang", "Victorian", "Leg raises"
        );

        List<String> results = exercises.stream()
                .filter(ex -> ex.toLowerCase().contains(query.toLowerCase()))
                .limit(12)
                .collect(Collectors.toList());

        displaySuggestions(results);
    }

    private void displaySuggestions(List<String> exercises) {
        suggestionsBox.getChildren().clear();

        if (exercises.isEmpty()) {
            Label noResults = new Label("Aucun exercice trouvé");
            noResults.setFont(Font.font("System", FontWeight.NORMAL, 14));
            noResults.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);");
            noResults.setPadding(new Insets(20));
            suggestionsBox.getChildren().add(noResults);
            return;
        }

        for (String exercise : exercises) {
            HBox card = createSuggestionCard(exercise);
            suggestionsBox.getChildren().add(card);
        }
    }

    private HBox createSuggestionCard(String exerciseName) {
        HBox card = new HBox();
        card.setSpacing(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-cursor: hand;"
        );

        Label name = new Label(exerciseName);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        name.setStyle("-fx-text-fill: #ffffff;");

        Label addIcon = new Label("➕");
        addIcon.setFont(Font.font(20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(name, spacer, addIcon);

        DropShadow hoverGlow = new DropShadow(15, Color.rgb(255, 255, 255, 0.3));

        card.setOnMouseEntered(e -> {
            card.setEffect(hoverGlow);
            card.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.12); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;"
            );
            ScaleTransition scale = new ScaleTransition(Duration.millis(120), card);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        card.setOnMouseExited(e -> {
            card.setEffect(null);
            card.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;"
            );
            ScaleTransition scale = new ScaleTransition(Duration.millis(120), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        card.setOnMouseClicked(e -> showQuickAddDialog(exerciseName));

        return card;
    }

    // Fonction pour ajouter un exercice
    private void showQuickAddDialog(String exerciseName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un exercice");
        dialog.setHeaderText("➕ " + exerciseName);

        VBox content = new VBox();
        content.setSpacing(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        HBox setsBox = createFieldBox("Séries:", "3");
        HBox repsBox = createFieldBox("Reps:", "10");
        HBox holdBox = createFieldBox("Hold (s):", "");

        content.getChildren().addAll(setsBox, repsBox, holdBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                addExerciseToSession(
                        exerciseName,
                        ((TextField) setsBox.getChildren().get(1)).getText(),
                        ((TextField) repsBox.getChildren().get(1)).getText(),
                        ((TextField) holdBox.getChildren().get(1)).getText()
                );
            }
        });
    }

    private HBox createFieldBox(String labelText, String defaultValue) {
        Label label = new Label(labelText);
        label.setPrefWidth(70);
        label.setFont(Font.font(14));
        TextField field = new TextField(defaultValue);
        field.setPrefWidth(80);
        HBox box = new HBox(label, field);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    //
    private Node createExerciseCard(ExerciseData exercise) {
        final HBox exerciseCard = new HBox();
        exerciseCard.setSpacing(15);
        exerciseCard.setPadding(new Insets(16, 20, 16, 20));
        exerciseCard.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12;"
        );

        Label nameLabel = new Label(exercise.name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        nameLabel.setStyle("-fx-text-fill: #ffffff;");

        String infoText = exercise.sets + " séries × " + exercise.reps + " reps" +
                (exercise.hold.isEmpty() ? "" : " / " + exercise.hold + " s");
        Label infoLabel = new Label(infoText);
        infoLabel.setFont(Font.font(13));
        infoLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.7);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Bouton pour modifier les reps, hold et séries
        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-size: 16; " +
                "-fx-cursor: hand;");
        editBtn.setOnAction(e -> showEditDialog(exercise, exerciseCard));

        Button removeBtn = new Button("❌");
        removeBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-size: 16; " +
                "-fx-cursor: hand;");
        removeBtn.setOnAction(e -> playRemoveAnimation(exerciseCard, exercise));

        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(editBtn, removeBtn);

        VBox textBox = new VBox(4);
        textBox.getChildren().addAll(nameLabel, infoLabel);

        exerciseCard.getChildren().addAll(textBox, spacer, buttons);

        DropShadow hoverGlow = new DropShadow(15, Color.rgb(255, 255, 255, 0.25));
        exerciseCard.setOnMouseEntered(e -> {
            exerciseCard.setEffect(hoverGlow);
            exerciseCard.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.12); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), exerciseCard);
            st.setToX(1.01);
            st.setToY(1.01);
            st.play();
        });

        exerciseCard.setOnMouseExited(e -> {
            exerciseCard.setEffect(null);
            exerciseCard.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.08); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), exerciseCard);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        exerciseCard.setOnDragDetected(event -> {
            int index = exercisesListBox.getChildren().indexOf(exerciseCard);
            Dragboard db = exerciseCard.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(index));
            db.setContent(content);

            DropShadow liftShadow = new DropShadow(25, Color.rgb(255, 255, 255, 0.35));
            exerciseCard.setEffect(liftShadow);
            ScaleTransition lift = new ScaleTransition(Duration.millis(150), exerciseCard);
            lift.setToX(1.05);
            lift.setToY(1.05);
            lift.play();

            event.consume();
        });

        exerciseCard.setOnDragDone(event -> {
            ScaleTransition ret = new ScaleTransition(Duration.millis(120), exerciseCard);
            ret.setToX(1.0);
            ret.setToY(1.0);
            ret.play();
            exerciseCard.setEffect(null);
            event.consume();
        });

        exerciseCard.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString() && event.getGestureSource() != exerciseCard) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        exerciseCard.setOnDragEntered(event -> {
            if (event.getGestureSource() != exerciseCard) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(120), exerciseCard);
                tt.setToY(-8);
                tt.play();
            }
            event.consume();
        });

        exerciseCard.setOnDragExited(event -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(120), exerciseCard);
            tt.setToY(0);
            tt.play();
            event.consume();
        });

        exerciseCard.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int draggedIndex = Integer.parseInt(db.getString());
                    if (draggedIndex >= 0 && draggedIndex < exercisesListBox.getChildren().size()) {
                        Node draggedNode = exercisesListBox.getChildren().get(draggedIndex);
                        int targetIndex = exercisesListBox.getChildren().indexOf(exerciseCard);

                        if (draggedNode != null && draggedNode != exerciseCard) {
                            exercisesListBox.getChildren().remove(draggedNode);
                            exercisesListBox.getChildren().add(targetIndex, draggedNode);

                            ExerciseData exDragged = sessionExercises.remove(draggedIndex);
                            sessionExercises.add(targetIndex, exDragged);

                            playSettleAnimation(draggedNode);
                            saveTrainingExercises();
                        }
                        success = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        exercisesListBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                try {
                    int draggedIndex = Integer.parseInt(db.getString());
                    if (draggedIndex >= 0 && draggedIndex < exercisesListBox.getChildren().size()) {
                        Node draggedNode = exercisesListBox.getChildren().get(draggedIndex);
                        if (draggedNode != null) {
                            exercisesListBox.getChildren().remove(draggedNode);
                            exercisesListBox.getChildren().add(draggedNode);

                            ExerciseData exDragged = sessionExercises.remove(draggedIndex);
                            sessionExercises.add(exDragged);

                            playSettleAnimation(draggedNode);
                            saveTrainingExercises();
                        }
                        success = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return exerciseCard;
    }

    private void addExerciseToSession(String name, String sets, String reps, String hold) {
        ExerciseData exercise = new ExerciseData(name, sets, reps, hold);
        sessionExercises.add(exercise);

        Node exerciseCard = createExerciseCard(exercise);
        exercisesListBox.getChildren().add(exerciseCard);

        playAddAnimation(exerciseCard, 0);
        saveTrainingExercises();
    }


    private void showEditDialog(ExerciseData exercise, HBox exerciseCard) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'exercice");
        dialog.setHeaderText("✏️ " + exercise.name);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        HBox setsBox = createFieldBox("Séries:", exercise.sets);
        HBox repsBox = createFieldBox("Reps:", exercise.reps);
        HBox holdBox = createFieldBox("Hold (s):", exercise.hold);

        content.getChildren().addAll(setsBox, repsBox, holdBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                String newSets = ((TextField) setsBox.getChildren().get(1)).getText();
                String newReps = ((TextField) repsBox.getChildren().get(1)).getText();
                String newHold = ((TextField) holdBox.getChildren().get(1)).getText();

                exercise.sets = newSets;
                exercise.reps = newReps;
                exercise.hold = newHold;

                if (exerciseCard.getChildren().get(0) instanceof VBox) {
                    VBox textBox = (VBox) exerciseCard.getChildren().get(0);
                    if (textBox.getChildren().size() >= 2 && textBox.getChildren().get(1) instanceof Label) {
                        Label info = (Label) textBox.getChildren().get(1);
                        info.setText(exercise.sets + " séries × " + exercise.reps + " reps" +
                                (exercise.hold.isEmpty() ? "" : " / " + exercise.hold + " s"));
                    }
                }

                ScaleTransition st = new ScaleTransition(Duration.millis(160), exerciseCard);
                st.setFromX(1.0);
                st.setFromY(1.0);
                st.setToX(1.03);
                st.setToY(1.03);
                st.setAutoReverse(true);
                st.setCycleCount(2);
                st.play();

                saveTrainingExercises();
            }
        });
    }

    // Gérer la sauvegarde
    private void saveSession() {
        String title = trainingName;
        LocalDate date = LocalDate.now();
        String dateFormatted = String.format("%02d/%02d/%d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear()
        );

        for (ExerciseData ex : sessionExercises) {
            int reps;
            int hold;

            boolean isSkill = PerformanceManager.checkIfSkill(ex.name);

            if (isSkill && !ex.hold.isEmpty()) {
                reps = 1;
                hold = Integer.parseInt(ex.hold);
            } else {
                try {
                    reps = Integer.parseInt(ex.reps);
                } catch (NumberFormatException nfe) {
                    reps = 0;
                }
                hold = ex.hold.isEmpty() ? 0 : Integer.parseInt(ex.hold);
            }

            HistoryManager.addEntry(title, dateFormatted, ex.name, reps, hold);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Séance sauvegardée !");
        alert.setContentText("Votre séance '" + trainingName + "' a été ajoutée à l'historique et aux statistiques.");
        alert.show();
    }

    private void resetSession() {
        sessionExercises.clear();
        List<Node> copy = new ArrayList<>(exercisesListBox.getChildren());
        int delay = 0;
        for (Node n : copy) {
            PauseTransition p = new PauseTransition(Duration.millis(delay));
            p.setOnFinished(ev -> playRemoveAnimationNoData(n));
            p.play();
            delay += 70;
        }

        PauseTransition finalClear = new PauseTransition(Duration.millis(delay + 150));
        finalClear.setOnFinished(ev -> {
            exercisesListBox.getChildren().clear();
            saveTrainingExercises();
        });
        finalClear.play();
    }

    // Animations visuelles
    private void playAddAnimation(Node node, int delayMs) {
        node.setOpacity(0);
        node.setTranslateX(30);
        node.setScaleX(0.95);
        node.setScaleY(0.95);

        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(320), node);
        tt.setFromX(30);
        tt.setToX(0);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(0.95);
        st.setFromY(0.95);
        st.setToX(1.0);
        st.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(ft, tt, st);
        if (delayMs > 0) {
            PauseTransition p = new PauseTransition(Duration.millis(delayMs));
            SequentialTransition seq = new SequentialTransition(p, pt);
            seq.play();
        } else {
            pt.play();
        }
    }

    private void playRemoveAnimation(Node node, ExerciseData data) {
        FadeTransition ft = new FadeTransition(Duration.millis(220), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(220), node);
        tt.setByX(-20);

        ScaleTransition st = new ScaleTransition(Duration.millis(220), node);
        st.setToX(0.95);
        st.setToY(0.95);

        ParallelTransition pt = new ParallelTransition(ft, tt, st);
        pt.setOnFinished(ev -> {
            exercisesListBox.getChildren().remove(node);
            sessionExercises.remove(data);
            saveTrainingExercises();
        });
        pt.play();
    }

    private void playRemoveAnimationNoData(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), node);
        tt.setByX(-18);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(0.96);
        st.setToY(0.96);

        ParallelTransition pt = new ParallelTransition(ft, tt, st);
        pt.setOnFinished(ev -> exercisesListBox.getChildren().remove(node));
        pt.play();
    }

    private void playSettleAnimation(Node node) {
        node.setOpacity(0.85);
        FadeTransition ft = new FadeTransition(Duration.millis(160), node);
        ft.setFromValue(0.85);
        ft.setToValue(1.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(160), node);
        tt.setFromY(-6);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    // ===================== DATA CLASSES =====================
    private static class ExerciseData {
        String name, sets, reps, hold;

        public ExerciseData(String name, String sets, String reps, String hold) {
            this.name = name;
            this.sets = sets;
            this.reps = reps;
            this.hold = hold;
        }
    }

    private static class SavedTraining {
        String name;
        List<ExerciseData> exercises;

        public SavedTraining(String name, List<ExerciseData> exercises) {
            this.name = name;
            this.exercises = exercises;
        }
    }
}