import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import java.util.Random;
import java.util.function.Consumer;

public class Training {

    private VBox root;
    private VBox trainingsList;
    private Consumer<String> onTrainingClick;

    public Training() {
        root = new VBox();
        root.setSpacing(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: transparent;");

        VBox header = createModernHeader();

        VBox contentCard = createContentCard();
        VBox.setVgrow(contentCard, Priority.ALWAYS);

        root.getChildren().addAll(header, contentCard);
    }

    public Pane getView() {
        return root;
    }

    public void setOnTrainingClick(Consumer<String> onTrainingClick) {
        this.onTrainingClick = onTrainingClick;
    }

    // Design du header
    private VBox createModernHeader() {
        VBox header = new VBox();
        header.setSpacing(10);
        header.setPadding(new Insets(25, 30, 25, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.85); " +
                        "-fx-background-radius: 18; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 18;"
        );

        DropShadow shadow = new DropShadow(20, Color.rgb(255, 255, 255, 0.1));
        header.setEffect(shadow);

        HBox titleBox = new HBox();
        titleBox.setSpacing(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("💪");
        icon.setFont(Font.font(32));

        VBox textBox = new VBox();
        textBox.setSpacing(5);

        Label title = new Label("Mes Entraînements");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Gérez vos programmes d'entraînement");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.6));

        textBox.getChildren().addAll(title, subtitle);
        titleBox.getChildren().addAll(icon, textBox);

        header.getChildren().add(titleBox);

        // Animation d'entrée
        header.setOpacity(0);
        header.setTranslateY(-30);
        FadeTransition fade = new FadeTransition(Duration.millis(600), header);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), header);
        slide.setFromY(-30);
        slide.setToY(0);
        fade.play();
        slide.play();

        return header;
    }

    private VBox createContentCard() {
        VBox card = new VBox();
        card.setSpacing(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.85); " +
                        "-fx-background-radius: 18; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 18;"
        );

        DropShadow shadow = new DropShadow(15, Color.rgb(255, 255, 255, 0.1));
        card.setEffect(shadow);

        // Section header
        HBox sectionHeader = new HBox();
        sectionHeader.setSpacing(12);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Label sectionIcon = new Label("📋");
        sectionIcon.setFont(Font.font(22));

        Label sectionTitle = new Label("Programmes d'Entraînement");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Icon
        Image add = new Image(getClass().getResourceAsStream("/images/add.png"));
        ImageView addIcon = new ImageView(add);
        addIcon.setFitWidth(20);
        addIcon.setFitHeight(20);
        addIcon.setPreserveRatio(true);
        addIcon.setSmooth(true);

        // Colorier l'icône en blanc
        javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
        colorAdjust.setBrightness(1.0);
        addIcon.setEffect(colorAdjust);

        Button addBtn = new Button();
        addBtn.setGraphic(addIcon);
        addBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-background-radius: 20;");
        addBtn.setPrefSize(40, 40);

        DropShadow btnShadow = new DropShadow(8, Color.rgb(255, 255, 255, 0.2));
        addBtn.setEffect(btnShadow);

        addBtn.setOnMouseEntered(e -> {
            addBtn.setScaleX(1.1);
            addBtn.setScaleY(1.1);
            addBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); " +
                    "-fx-background-radius: 20;");
            addBtn.setEffect(new DropShadow(15, Color.rgb(255, 255, 255, 0.4)));
        });
        addBtn.setOnMouseExited(e -> {
            addBtn.setScaleX(1.0);
            addBtn.setScaleY(1.0);
            addBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                    "-fx-background-radius: 20;");
            addBtn.setEffect(btnShadow);
        });

        addBtn.setOnAction(e -> addTraining());

        sectionHeader.getChildren().addAll(sectionIcon, sectionTitle, spacer, addBtn);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");

        // Liste des trainings
        trainingsList = new VBox();
        trainingsList.setSpacing(12);

        ScrollPane scroll = new ScrollPane(trainingsList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        card.getChildren().addAll(sectionHeader, separator, scroll);

        // Animation d'entrée
        card.setOpacity(0);
        card.setTranslateY(30);
        FadeTransition fade = new FadeTransition(Duration.millis(700), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(200));
        TranslateTransition slide = new TranslateTransition(Duration.millis(700), card);
        slide.setFromY(30);
        slide.setToY(0);
        slide.setDelay(Duration.millis(200));
        fade.play();
        slide.play();

        return card;
    }

    private void addTraining() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(20);
        container.setPadding(new Insets(20, 25, 20, 25));
        container.setPrefHeight(80);

        // Palette de couleurs aléatoires
        String[][] colorPalette = {
                {"rgba(66, 135, 245, 0.3)", "rgba(37, 99, 235, 0.5)", "rgba(66, 135, 245, 0.8)"}, // Bleu
                {"rgba(245, 158, 66, 0.3)", "rgba(235, 117, 37, 0.5)", "rgba(245, 158, 66, 0.8)"}, // Orange
                {"rgba(66, 245, 167, 0.3)", "rgba(37, 235, 133, 0.5)", "rgba(66, 245, 167, 0.8)"}, // Vert
                {"rgba(245, 66, 200, 0.3)", "rgba(235, 37, 166, 0.5)", "rgba(245, 66, 200, 0.8)"}, // Rose
                {"rgba(157, 66, 245, 0.3)", "rgba(123, 37, 235, 0.5)", "rgba(157, 66, 245, 0.8)"}, // Violet
                {"rgba(245, 66, 66, 0.3)", "rgba(235, 37, 37, 0.5)", "rgba(245, 66, 66, 0.8)"}, // Rouge
                {"rgba(66, 212, 245, 0.3)", "rgba(37, 184, 235, 0.5)", "rgba(66, 212, 245, 0.8)"}, // Cyan
                {"rgba(245, 212, 66, 0.3)", "rgba(235, 194, 37, 0.5)", "rgba(245, 212, 66, 0.8)"}, // Jaune
                {"rgba(66, 245, 245, 0.3)", "rgba(37, 235, 235, 0.5)", "rgba(66, 245, 245, 0.8)"}, // Turquoise
                {"rgba(245, 66, 245, 0.3)", "rgba(235, 37, 235, 0.5)", "rgba(245, 66, 245, 0.8)"}  // Magenta
        };

        // Aléatoirement, on asign une couleur à la carte créer
        Random random = new Random();
        String[] selectedColors = colorPalette[random.nextInt(colorPalette.length)];
        String primaryColor = selectedColors[0];
        String secondaryColor = selectedColors[1];
        String glowColor = selectedColors[2];

        container.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: " + primaryColor + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setOffsetY(5);
        shadow.setColor(Color.web(glowColor, 0.3));
        container.setEffect(shadow);

        container.setOnMouseEntered(e -> {
            DropShadow hoverShadow = new DropShadow();
            hoverShadow.setRadius(20);
            hoverShadow.setOffsetY(8);
            hoverShadow.setColor(Color.web(glowColor, 0.5));
            container.setEffect(hoverShadow);

            container.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-radius: 16;" +
                            "-fx-border-color: " + secondaryColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;"
            );
        });

        container.setOnMouseExited(e -> {
            container.setEffect(shadow);
            container.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-radius: 16;" +
                            "-fx-border-color: " + primaryColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-cursor: hand;"
            );
        });

        // Indicateur visuel à gauche
        VBox indicator = new VBox();
        indicator.setPrefSize(5, 40);
        indicator.setMaxSize(5, 40);
        indicator.setStyle(
                "-fx-background-color: " + glowColor + ";" +
                        "-fx-background-radius: 3;"
        );

        Label trainingName = new Label("Nouvel Entraînement");
        trainingName.setFont(Font.font("System", FontWeight.BOLD, 18));
        trainingName.setTextFill(Color.WHITE);

        container.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof Button || e.getTarget() instanceof ImageView) {
                return;
            }

            if (onTrainingClick != null) {
                onTrainingClick.accept(trainingName.getText());
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Modify button
        Image modifyImg = new Image(getClass().getResourceAsStream("/images/edit.png"));
        ImageView modifyIcon = new ImageView(modifyImg);
        modifyIcon.setFitWidth(18);
        modifyIcon.setFitHeight(18);
        modifyIcon.setPreserveRatio(true);
        modifyIcon.setSmooth(true);

        // Colorier l'icône
        javafx.scene.effect.ColorAdjust colorAdjust1 = new javafx.scene.effect.ColorAdjust();
        colorAdjust1.setBrightness(1.0);
        modifyIcon.setEffect(colorAdjust1);

        Button modifyBtn = new Button();
        modifyBtn.setGraphic(modifyIcon);
        modifyBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        modifyBtn.setPrefSize(38, 38);

        DropShadow modifyShadow = new DropShadow(5, Color.rgb(255, 255, 255, 0.2));
        modifyBtn.setEffect(modifyShadow);

        modifyBtn.setOnMouseEntered(e -> {
            modifyBtn.setScaleX(1.15);
            modifyBtn.setScaleY(1.15);
            modifyBtn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            modifyBtn.setEffect(new DropShadow(10, Color.rgb(255, 255, 255, 0.4)));
        });

        modifyBtn.setOnMouseExited(e -> {
            modifyBtn.setScaleX(1.0);
            modifyBtn.setScaleY(1.0);
            modifyBtn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            modifyBtn.setEffect(modifyShadow);
        });

        modifyBtn.setOnMouseClicked(e -> {
            e.consume();
            TextField editField = new TextField(trainingName.getText());
            editField.setFont(trainingName.getFont());
            editField.setPrefWidth(200);
            editField.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: " + glowColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 8;" +
                            "-fx-padding: 8;" +
                            "-fx-text-fill: white;"
            );

            int index = container.getChildren().indexOf(trainingName);
            container.getChildren().set(index, editField);
            editField.requestFocus();

            editField.setOnAction(ev -> {
                trainingName.setText(editField.getText());
                container.getChildren().set(index, trainingName);
            });

            editField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    trainingName.setText(editField.getText());
                    container.getChildren().set(index, trainingName);
                }
            });
        });

        // Trash button
        Image Trash = new Image(getClass().getResourceAsStream("/images/trash.png"));
        ImageView trashIcon = new ImageView(Trash);
        trashIcon.setFitWidth(18);
        trashIcon.setFitHeight(18);
        trashIcon.setPreserveRatio(true);
        trashIcon.setSmooth(true);

        // Coloriser l'icône
        javafx.scene.effect.ColorAdjust colorAdjust2 = new javafx.scene.effect.ColorAdjust();
        colorAdjust2.setBrightness(1.0);
        trashIcon.setEffect(colorAdjust2);

        Button trashBtn = new Button();
        trashBtn.setGraphic(trashIcon);
        trashBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        trashBtn.setPrefSize(38, 38);

        DropShadow trashShadow = new DropShadow(5, Color.rgb(255, 255, 255, 0.2));
        trashBtn.setEffect(trashShadow);

        trashBtn.setOnMouseEntered(e -> {
            trashBtn.setScaleX(1.15);
            trashBtn.setScaleY(1.15);
            trashBtn.setStyle(
                    "-fx-background-color: rgba(245, 66, 66, 0.3);" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            trashBtn.setEffect(new DropShadow(10, Color.rgb(245, 66, 66, 0.6)));
        });

        trashBtn.setOnMouseExited(e -> {
            trashBtn.setScaleX(1.0);
            trashBtn.setScaleY(1.0);
            trashBtn.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
            trashBtn.setEffect(trashShadow);
        });

        trashBtn.setOnMouseClicked(e -> {
            e.consume();
            ScaleTransition shrink = new ScaleTransition(Duration.millis(200), container);
            shrink.setToX(0.8);
            shrink.setToY(0.8);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), container);
            fadeOut.setToValue(0);

            shrink.play();
            fadeOut.play();

            fadeOut.setOnFinished(ev -> trainingsList.getChildren().remove(container));
        });

        container.setOpacity(0);
        container.setScaleX(0.9);
        container.setScaleY(0.9);
        container.setTranslateY(-30);

        ScaleTransition scale = new ScaleTransition(Duration.millis(400), container);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);

        FadeTransition fade = new FadeTransition(Duration.millis(400), container);
        fade.setFromValue(0);
        fade.setToValue(1.0);

        TranslateTransition translate = new TranslateTransition(Duration.millis(400), container);
        translate.setFromY(-30);
        translate.setToY(0);

        scale.play();
        fade.play();
        translate.play();

        container.getChildren().addAll(indicator, trainingName, spacer, modifyBtn, trashBtn);
        trainingsList.getChildren().add(container);
    }
}