import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanner {

    private Pane view;
    private List<WorkoutLabel> customLabels;

    public WorkoutPlanner() {
        customLabels = new ArrayList<>();
        // Étiquettes par défaut
        customLabels.add(new WorkoutLabel("Force", "#FF3B30", "💪"));
        customLabels.add(new WorkoutLabel("Compétences", "#5AC8FA", "🎯"));
        customLabels.add(new WorkoutLabel("Mobilité", "#34C759", "🧘"));
        view = createView();
    }

    public Pane getView() {
        return view;
    }

    private Pane createView() {
        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        // Fond transparent pour laisser voir le background
        root.setStyle("-fx-background-color: transparent;");

        HBox main = main();
        VBox.setVgrow(main, Priority.ALWAYS);
        root.getChildren().add(main);

        return root;
    }

    private HBox main() {
        HBox main = new HBox();
        main.setStyle("-fx-background-color: rgba(20, 20, 20, 0.85);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: rgba(255, 255, 255, 0.15);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 25, 0, 0, 5);");
        main.setPadding(new Insets(30));
        main.setSpacing(30);
        main.setAlignment(Pos.TOP_LEFT);

        // Header
        VBox headerBox = new VBox();
        headerBox.setSpacing(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPrefWidth(220);

        Label Planner = new Label("Planning");
        Planner.setStyle("-fx-text-fill: #ffffff;");
        Planner.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label subtitle = new Label("Planifiez vos séances d'entraînement");
        subtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 13));

        Button manageLabelBtn = new Button("+ Gérer les étiquettes");
        manageLabelBtn.setPrefWidth(200);
        manageLabelBtn.setPrefHeight(45);
        manageLabelBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13;");
        manageLabelBtn.setOnAction(e -> showManageLabelsPopup());
        addButtonHoverEffect(manageLabelBtn);

        headerBox.getChildren().addAll(Planner, subtitle, manageLabelBtn);

        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-background-radius: 15;");

        // En-têtes des jours
        for (int i = 0; i < days.length; i++) {
            StackPane dayHeader = new StackPane();
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
            dayLabel.setStyle("-fx-text-fill: #ffffff;");

            dayHeader.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15);" +
                    "-fx-background-radius: 12 12 0 0;");
            dayHeader.setPadding(new Insets(12));
            dayHeader.setAlignment(Pos.CENTER);
            dayHeader.getChildren().add(dayLabel);
            GridPane.setHgrow(dayHeader, Priority.ALWAYS);
            dayHeader.setMaxWidth(Double.MAX_VALUE);
            grid.add(dayHeader, i + 1, 0);
        }

        // Colonne des heures
        for (int j = 0; j < 24; j++) {
            StackPane hourCell = new StackPane();
            Label hoursLabel = new Label(j + "h");
            hoursLabel.setFont(Font.font("System", FontWeight.MEDIUM, 11));
            hoursLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8);");

            hourCell.setStyle("-fx-background-color: rgba(255, 255, 255, 0.12);");
            hourCell.setPadding(new Insets(8));
            hourCell.setAlignment(Pos.CENTER);
            hourCell.setPrefWidth(60);
            hourCell.setPrefHeight(45);
            hourCell.getChildren().add(hoursLabel);

            if (j == 0) {
                hourCell.setStyle(hourCell.getStyle() + "-fx-background-radius: 0 0 0 0;");
            } else if (j == 23) {
                hourCell.setStyle(hourCell.getStyle() + "-fx-background-radius: 0 0 0 12;");
            }

            grid.add(hourCell, 0, j + 1);
        }

        // Cases du planning
        for (int col = 1; col <= 7; col++) {
            for (int row = 1; row <= 24; row++) {
                Pane cases = new Pane();
                cases.setPrefSize(85, 45);
                cases.setStyle("-fx-background-color: rgba(30, 30, 30, 0.6);" +
                        "-fx-border-color: transparent;");

                final String[] currentColor = {"rgba(30, 30, 30, 0.6)"};
                final String[] categoryName = {""};

                // Animation hover
                DropShadow hoverGlow = new DropShadow();
                hoverGlow.setColor(Color.rgb(255, 255, 255, 0.3));
                hoverGlow.setRadius(10);
                hoverGlow.setSpread(0.3);

                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), cases);
                scaleUp.setToX(1.03);
                scaleUp.setToY(1.03);

                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), cases);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);

                cases.setOnMouseEntered(e -> {
                    if (currentColor[0].contains("30, 30, 30")) {
                        cases.setStyle("-fx-background-color: rgba(50, 50, 50, 0.8); -fx-border-color: transparent; -fx-cursor: hand;");
                    } else {
                        cases.setEffect(hoverGlow);
                        cases.setStyle("-fx-background-color: " + currentColor[0] + "; -fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-width: 1; -fx-cursor: hand;");
                    }
                    scaleUp.playFromStart();
                });

                cases.setOnMouseExited(e -> {
                    cases.setEffect(null);
                    cases.setStyle("-fx-background-color: " + currentColor[0] + "; -fx-border-color: transparent;");
                    scaleDown.playFromStart();
                });

                // Popup au clic
                cases.setOnMouseClicked(e -> showPopup(cases, currentColor, categoryName));

                // Arrondir les coins
                if (col == 7 && row == 1) {
                    cases.setStyle(cases.getStyle() + "-fx-background-radius: 0 12 0 0;");
                } else if (col == 7 && row == 24) {
                    cases.setStyle(cases.getStyle() + "-fx-background-radius: 0 0 12 0;");
                }

                grid.add(cases, col, row);
            }
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        main.getChildren().addAll(headerBox, scrollPane);
        return main;
    }


    private void showPopup(Pane cases, String[] currentColor, String[] categoryName) {
        Stage popUp = new Stage();
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        popUp.setResizable(false);

        // Fond flou sombre
        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(25));
        box.setSpacing(16);

        box.setStyle("-fx-background-color: rgba(20, 20, 20, 0.95);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 35, 0, 0, 10);");
        box.setPrefWidth(360);

        // Bouton fermer
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 8 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;");
        closeBtn.setOnAction(ev -> popUp.close());

        ScaleTransition closeBtnHover = new ScaleTransition(Duration.millis(100), closeBtn);
        closeBtnHover.setToX(1.15);
        closeBtnHover.setToY(1.15);

        ScaleTransition closeBtnExit = new ScaleTransition(Duration.millis(100), closeBtn);
        closeBtnExit.setToX(1.0);
        closeBtnExit.setToY(1.0);

        closeBtn.setOnMouseEntered(e -> {
            closeBtn.setStyle("-fx-background-color: rgba(255, 59, 48, 0.3); " +
                    "-fx-text-fill: #FF3B30; " +
                    "-fx-font-size: 16px; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 8 12; " +
                    "-fx-border-color: #FF3B30;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 20;" +
                    "-fx-cursor: hand;");
            closeBtnHover.playFromStart();
        });

        closeBtn.setOnMouseExited(e -> {
            closeBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                    "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                    "-fx-font-size: 16px; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 8 12;" +
                    "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 20;");
            closeBtnExit.playFromStart();
        });

        HBox topBar = new HBox(closeBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setMaxWidth(Double.MAX_VALUE);

        Label titlePopUp = new Label("Sélectionner le type d'entraînement");
        titlePopUp.setFont(Font.font("System", FontWeight.BOLD, 22));
        titlePopUp.setStyle("-fx-text-fill: #ffffff;");

        Label subtitlePopUp = new Label("Choisissez une catégorie pour ce créneau");
        subtitlePopUp.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitlePopUp.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titlePopUp, subtitlePopUp);

        VBox buttonsBox = new VBox(12);
        buttonsBox.setAlignment(Pos.CENTER);

        // Créer un bouton pour chaque étiquette personnalisée
        for (WorkoutLabel label : customLabels) {
            Button btn = createModernButton(label.name, label.color, label.emoji);
            btn.setOnAction(ev -> {
                currentColor[0] = label.color;
                categoryName[0] = label.name;
                updateCaseWithLabel(cases, label.color, "[" + label.emoji + "] " + label.name, "#ffffff");
                animateClose(popUp, box);
            });
            buttonsBox.getChildren().add(btn);
        }

        Button clearBtn = createModernButton("Effacer le créneau", "#6c757d", "✖");
        clearBtn.setOnAction(ev -> {
            currentColor[0] = "rgba(30, 30, 30, 0.6)";
            categoryName[0] = "";
            cases.getChildren().clear();
            cases.setStyle("-fx-background-color: " + currentColor[0] + "; -fx-border-color: transparent;");
            animateClose(popUp, box);
        });
        buttonsBox.getChildren().add(clearBtn);

        box.getChildren().addAll(topBar, titleBox, buttonsBox);
        backdrop.getChildren().add(box);

        Scene scene = new Scene(backdrop, 400, 500);
        scene.setFill(Color.TRANSPARENT);
        popUp.setScene(scene);

        // Déplacement de la fenêtre
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        box.setOnMousePressed(ev -> {
            offsetX[0] = popUp.getX() - ev.getScreenX();
            offsetY[0] = popUp.getY() - ev.getScreenY();
        });
        box.setOnMouseDragged(ev -> {
            popUp.setX(ev.getScreenX() + offsetX[0]);
            popUp.setY(ev.getScreenY() + offsetY[0]);
        });

        // Animations d'entrée
        FadeTransition backdropFade = new FadeTransition(Duration.millis(200), backdrop);
        backdropFade.setFromValue(0);
        backdropFade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), box);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(250), box);
        fade.setFromValue(0);
        fade.setToValue(1);

        backdropFade.play();
        scale.play();
        fade.play();

        popUp.showAndWait();
    }

    private void updateCaseWithLabel(Pane cases, String color, String text, String labelColor) {
        cases.getChildren().clear();
        cases.setStyle("-fx-background-color: " + color + "; -fx-border-color: transparent;");

        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 9));
        label.setStyle("-fx-text-fill: " + labelColor + ";");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        StackPane.setAlignment(label, Pos.CENTER);
        cases.getChildren().add(label);
    }

    private void animateClose(Stage popUp, VBox box) {
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), box);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), box);
        fadeOut.setToValue(0);

        scaleOut.play();
        fadeOut.play();
        fadeOut.setOnFinished(e -> popUp.close());
    }

    private Button createModernButton(String text, String colorHex, String symbol) {
        Button btn = new Button("[" + symbol + "] " + text);
        btn.setPrefWidth(310);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 18, 0, 18));

        btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + colorHex + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-text-fill: rgba(255, 255, 255, 0.9);");

        DropShadow btnShadow = new DropShadow();
        btnShadow.setColor(Color.web(colorHex, 0.4));
        btnShadow.setRadius(15);
        btnShadow.setSpread(0.3);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(120), btn);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(120), btn);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        TranslateTransition slideRight = new TranslateTransition(Duration.millis(120), btn);
        slideRight.setToX(4);

        TranslateTransition slideBack = new TranslateTransition(Duration.millis(120), btn);
        slideBack.setToX(0);

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.12);" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + colorHex + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-cursor: hand;");
            btn.setEffect(btnShadow);
            scaleIn.playFromStart();
            slideRight.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + colorHex + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: rgba(255, 255, 255, 0.9);");
            btn.setEffect(null);
            scaleOut.playFromStart();
            slideBack.playFromStart();
        });

        return btn;
    }

    private void showManageLabelsPopup() {
        Stage popUp = new Stage();
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        popUp.setResizable(false);

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(25));
        box.setSpacing(18);
        box.setStyle("-fx-background-color: rgba(20, 20, 20, 0.95);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 35, 0, 0, 10);");
        box.setPrefWidth(480);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 8 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;");
        closeBtn.setOnAction(ev -> popUp.close());
        addButtonHoverEffect(closeBtn);

        HBox topBar = new HBox(closeBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setMaxWidth(Double.MAX_VALUE);

        Label titlePopUp = new Label("Gérer les étiquettes");
        titlePopUp.setFont(Font.font("System", FontWeight.BOLD, 24));
        titlePopUp.setStyle("-fx-text-fill: #ffffff;");

        Label subtitlePopUp = new Label("Créer, modifier ou supprimer des étiquettes d'entraînement");
        subtitlePopUp.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitlePopUp.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titlePopUp, subtitlePopUp);

        // Liste des étiquettes existantes
        VBox labelsList = new VBox(10);
        labelsList.setAlignment(Pos.CENTER);
        labelsList.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.1);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 15;");

        updateLabelsList(labelsList);

        // Formulaire d'ajout
        VBox addForm = new VBox(12);
        addForm.setAlignment(Pos.CENTER);
        addForm.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: rgba(255, 255, 255, 0.15);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 20;");

        Label addTitle = new Label("Ajouter une nouvelle étiquette");
        addTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        addTitle.setStyle("-fx-text-fill: #ffffff;");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de l'étiquette (ex: Cardio)");
        nameField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 12;");
        nameField.setPrefWidth(400);

        TextField symbolField = new TextField();
        symbolField.setPromptText("Symbole (optionnel: 🏃, 🔥, 💪)");
        symbolField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);" +
                "-fx-text-fill: #ffffff;" +
                "-fx-prompt-text-fill: rgba(255, 255, 255, 0.5);" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 12;");
        symbolField.setPrefWidth(400);

        HBox colorPicker = new HBox(10);
        colorPicker.setAlignment(Pos.CENTER);

        String[] colors = {"#FF3B30", "#FF9500", "#FFCC00", "#34C759", "#5AC8FA", "#007AFF", "#5856D6", "#AF52DE", "#FF2D55"};
        final String[] selectedColor = {colors[0]};

        for (String color : colors) {
            Pane colorBox = new Pane();
            colorBox.setPrefSize(38, 38);
            colorBox.setStyle("-fx-background-color: " + color + ";" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-cursor: hand;");

            DropShadow colorGlow = new DropShadow();
            colorGlow.setColor(Color.web(color, 0.6));
            colorGlow.setRadius(12);
            colorGlow.setSpread(0.4);

            colorBox.setOnMouseEntered(e -> {
                colorBox.setEffect(colorGlow);
                colorBox.setStyle("-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.8);" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;");
            });

            colorBox.setOnMouseExited(e -> {
                if (!selectedColor[0].equals(color)) {
                    colorBox.setEffect(null);
                    colorBox.setStyle("-fx-background-color: " + color + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-cursor: hand;");
                } else {
                    colorBox.setEffect(colorGlow);
                }
            });

            colorBox.setOnMouseClicked(e -> {
                selectedColor[0] = color;
                // Reset tous les autres
                for (javafx.scene.Node node : colorPicker.getChildren()) {
                    if (node instanceof Pane) {
                        String nodeColor = ((Pane) node).getStyle().split(";")[0].replace("-fx-background-color: ", "");
                        ((Pane) node).setEffect(null);
                        ((Pane) node).setStyle("-fx-background-color: " + nodeColor + ";" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 10;" +
                                "-fx-cursor: hand;");
                    }
                }
                DropShadow selectedGlow = new DropShadow();
                selectedGlow.setColor(Color.web(color, 0.6));
                selectedGlow.setRadius(12);
                selectedGlow.setSpread(0.4);
                colorBox.setEffect(selectedGlow);
                colorBox.setStyle("-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255, 255, 255, 1);" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;");
            });

            colorPicker.getChildren().add(colorBox);
        }

        Button addButton = new Button("+ Ajouter l'étiquette");
        addButton.setPrefWidth(400);
        addButton.setPrefHeight(48);
        addButton.setStyle("-fx-background-color: rgba(52, 199, 89, 0.2);" +
                "-fx-text-fill: #34C759;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #34C759;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 12;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14;");
        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String symbol = symbolField.getText().trim();

            if (!name.isEmpty()) {
                if (symbol.isEmpty()) {
                    symbol = name.substring(0, 1).toUpperCase();
                }
                customLabels.add(new WorkoutLabel(name, selectedColor[0], symbol));
                updateLabelsList(labelsList);
                nameField.clear();
                symbolField.clear();
            }
        });
        addButtonHoverEffect(addButton);

        addForm.getChildren().addAll(addTitle, nameField, symbolField, colorPicker, addButton);

        ScrollPane scrollPane = new ScrollPane(labelsList);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);

        box.getChildren().addAll(topBar, titleBox, scrollPane, addForm);
        backdrop.getChildren().add(box);

        Scene scene = new Scene(backdrop, 550, 700);
        scene.setFill(Color.TRANSPARENT);
        popUp.setScene(scene);

        // Déplacement
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        box.setOnMousePressed(ev -> {
            offsetX[0] = popUp.getX() - ev.getScreenX();
            offsetY[0] = popUp.getY() - ev.getScreenY();
        });
        box.setOnMouseDragged(ev -> {
            popUp.setX(ev.getScreenX() + offsetX[0]);
            popUp.setY(ev.getScreenY() + offsetY[0]);
        });

        // Animations
        FadeTransition backdropFade = new FadeTransition(Duration.millis(200), backdrop);
        backdropFade.setFromValue(0);
        backdropFade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), box);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(250), box);
        fade.setFromValue(0);
        fade.setToValue(1);

        backdropFade.play();
        scale.play();
        fade.play();

        popUp.showAndWait();
    }

    private void updateLabelsList(VBox labelsList) {
        labelsList.getChildren().clear();

        for (int i = 0; i < customLabels.size(); i++) {
            WorkoutLabel label = customLabels.get(i);
            final int index = i;
            boolean isDefault = (i < 3);

            HBox labelBox = new HBox(15);
            labelBox.setAlignment(Pos.CENTER_LEFT);
            labelBox.setPadding(new Insets(14));
            labelBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: rgba(255, 255, 255, 0.1);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 10;");
            labelBox.setPrefWidth(410);

            Pane colorIndicator = new Pane();
            colorIndicator.setPrefSize(28, 28);
            colorIndicator.setStyle("-fx-background-color: " + label.color + ";" +
                    "-fx-background-radius: 8;");

            DropShadow colorGlow = new DropShadow();
            colorGlow.setColor(Color.web(label.color, 0.5));
            colorGlow.setRadius(10);
            colorGlow.setSpread(0.3);
            colorIndicator.setEffect(colorGlow);

            Label symbolLabel = new Label("[" + label.emoji + "]");
            symbolLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
            symbolLabel.setStyle("-fx-text-fill: " + label.color + ";");

            Label nameLabel = new Label(label.name);
            nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
            nameLabel.setStyle("-fx-text-fill: #ffffff;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            labelBox.getChildren().addAll(colorIndicator, symbolLabel, nameLabel);

            if (!isDefault) {
                Button deleteBtn = new Button("Supprimer");
                deleteBtn.setStyle("-fx-background-color: rgba(255, 59, 48, 0.2);" +
                        "-fx-text-fill: #FF3B30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: rgba(255, 59, 48, 0.4);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 8 14;" +
                        "-fx-font-size: 12;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    customLabels.remove(index);
                    updateLabelsList(labelsList);
                });
                addButtonHoverEffect(deleteBtn);
                labelBox.getChildren().add(deleteBtn);
            } else {
                Label defaultLabel = new Label("Par défaut");
                defaultLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
                defaultLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.4);" +
                        "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 4 8;");
                labelBox.getChildren().add(defaultLabel);
            }

            labelsList.getChildren().add(labelBox);
        }
    }

    private void addButtonHoverEffect(Button btn) {
        String originalStyle = btn.getStyle();

        DropShadow hoverGlow = new DropShadow();
        hoverGlow.setColor(Color.rgb(255, 255, 255, 0.3));
        hoverGlow.setRadius(12);
        hoverGlow.setSpread(0.3);

        btn.setOnMouseEntered(e -> {
            btn.setEffect(hoverGlow);
            btn.setStyle(originalStyle + "-fx-opacity: 0.9; -fx-cursor: hand;");
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            btn.setStyle(originalStyle);
        });
    }

    // Classe interne pour stocker les étiquettes
    private static class WorkoutLabel {
        String name;
        String color;
        String emoji;

        WorkoutLabel(String name, String color, String emoji) {
            this.name = name;
            this.color = color;
            this.emoji = emoji;
        }
    }
}