import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class History1 {

    public Pane getView() {
        VBox root = new VBox();
        root.setSpacing(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: transparent;");

        // Header
        VBox header = createDarkHeader();

        // Zone de contenu avec carte dark
        VBox contentCard = createDarkContentCard();
        VBox.setVgrow(contentCard, Priority.ALWAYS);

        root.getChildren().addAll(header, contentCard);

        return root;
    }

    private VBox createDarkHeader() {
        VBox header = new VBox();
        header.setSpacing(20);
        header.setPadding(new Insets(30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(1000);
        header.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.8); " +
                        "-fx-background-radius: 18; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 18;"
        );

        DropShadow shadow = new DropShadow(25, Color.rgb(0, 0, 0, 0.5));
        header.setEffect(shadow);

        // Section du titre
        HBox titleBox = new HBox();
        titleBox.setSpacing(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Historique d'entraînement");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Suivez votre progression");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.6));
        subtitle.setPadding(new Insets(8, 0, 0, 0));

        VBox textBox = new VBox(5);
        textBox.getChildren().addAll(title, subtitle);
        titleBox.getChildren().add(textBox);

        // Stats bar
        HBox statsBar = createDarkStatsBar();

        header.getChildren().addAll(titleBox, statsBar);

        // Animation quand l'utilisateur entre
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

    private HBox createDarkStatsBar() {
        HBox statsBar = new HBox();
        statsBar.setSpacing(15);
        statsBar.setAlignment(Pos.CENTER_LEFT);

        int totalSessions = HistoryManager.getEntries().size();

        HBox stat1 = createDarkStatBox(String.valueOf(totalSessions), "Séances totales");
        HBox stat2 = createDarkStatBox("30", "Jours actifs");
        HBox stat3 = createDarkStatBox("12", "Ce mois-ci");

        statsBar.getChildren().addAll(stat1, stat2, stat3);

        return statsBar;
    }

    private HBox createDarkStatBox(String value, String label) {
        HBox box = new HBox();
        box.setSpacing(12);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15, 20, 15, 20));
        box.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12;"
        );

        VBox textBox = new VBox();
        textBox.setSpacing(3);
        textBox.setAlignment(Pos.CENTER_LEFT);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.WHITE);

        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", FontWeight.NORMAL, 11));
        labelText.setTextFill(Color.rgb(255, 255, 255, 0.6));

        textBox.getChildren().addAll(valueLabel, labelText);
        box.getChildren().add(textBox);

        // Effet hover
        box.setOnMouseEntered(e -> {
            box.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;"
            );
        });

        box.setOnMouseExited(e -> {
            box.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12;"
            );
        });

        return box;
    }

    private VBox createDarkContentCard() {
        VBox card = new VBox();
        card.setSpacing(25);
        card.setPadding(new Insets(30));
        card.setMaxWidth(1000);
        card.setStyle(
                "-fx-background-color: rgba(20, 20, 20, 0.8); " +
                        "-fx-background-radius: 18; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 18;"
        );

        DropShadow shadow = new DropShadow(25, Color.rgb(0, 0, 0, 0.5));
        card.setEffect(shadow);

        // Section header
        HBox sectionHeader = new HBox();
        sectionHeader.setSpacing(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Séances récentes");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label count = new Label(HistoryManager.getEntries().size() + " séances");
        count.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        count.setTextFill(Color.rgb(255, 255, 255, 0.8));
        count.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 6 14 6 14;"
        );

        sectionHeader.getChildren().addAll(sectionTitle, spacer, count);

        // Liste des entrées
        VBox list = new VBox();
        list.setSpacing(12);

        // Charger les entrées existantes
        if (HistoryManager.getEntries().isEmpty()) {
            VBox emptyState = createDarkEmptyState();
            list.getChildren().add(emptyState);
        } else {
            for (HistoryManager.HistoryEntry entry : HistoryManager.getEntries()) {
                HBox entryBox = createDarkHistoryEntry(entry.getTitle(), entry.getDate());
                list.getChildren().add(entryBox);
            }
        }

        Main.setHistoryList(list);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        card.getChildren().addAll(sectionHeader, scroll);

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

    private VBox createDarkEmptyState() {
        VBox empty = new VBox();
        empty.setSpacing(15);
        empty.setPadding(new Insets(50));
        empty.setAlignment(Pos.CENTER);
        empty.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.03); " +
                        "-fx-background-radius: 14; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-border-width: 2; " +
                        "-fx-border-style: dashed; " +
                        "-fx-border-radius: 14;"
        );

        Label emptyTitle = new Label("Aucune séance d'entraînement pour le moment");
        emptyTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        emptyTitle.setTextFill(Color.rgb(255, 255, 255, 0.8));

        Label emptySubtitle = new Label("Commencez votre premier entraînement pour le voir ici !");
        emptySubtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        emptySubtitle.setTextFill(Color.rgb(255, 255, 255, 0.5));

        empty.getChildren().addAll(emptyTitle, emptySubtitle);

        return empty;
    }

    private HBox createDarkHistoryEntry(String title, String date) {
        HBox entry = new HBox();
        entry.setSpacing(15);
        entry.setPadding(new Insets(20));
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                        "-fx-background-radius: 14; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 14;"
        );

        // Contenu texte
        VBox textBox = new VBox();
        textBox.setSpacing(6);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);

        Label dateLabel = new Label(date);
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        dateLabel.setTextFill(Color.rgb(255, 255, 255, 0.6));

        textBox.getChildren().addAll(titleLabel, dateLabel);

        Label badge = new Label("Terminé");
        badge.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        badge.setTextFill(Color.rgb(255, 255, 255, 0.9));
        badge.setStyle(
                "-fx-background-color: rgba(76, 175, 80, 0.3); " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 6 12 6 12; " +
                        "-fx-border-color: rgba(76, 175, 80, 0.5); " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 20;"
        );

        entry.getChildren().addAll(textBox, badge);

        // Animations hover
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 255, 255, 0.3));
        glow.setRadius(15);
        glow.setSpread(0.3);

        entry.setOnMouseEntered(e -> {
            entry.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.12); " +
                            "-fx-background-radius: 14; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.25); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 14; " +
                            "-fx-cursor: hand;"
            );

            entry.setEffect(glow);

            ScaleTransition scale = new ScaleTransition(Duration.millis(150), entry);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        entry.setOnMouseExited(e -> {
            entry.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05); " +
                            "-fx-background-radius: 14; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 14;"
            );

            entry.setEffect(null);

            ScaleTransition scale = new ScaleTransition(Duration.millis(150), entry);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        // Animation d'apparition
        entry.setOpacity(0);
        entry.setTranslateX(-20);
        FadeTransition fade = new FadeTransition(Duration.millis(400), entry);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), entry);
        slide.setFromX(-20);
        slide.setToX(0);
        fade.play();
        slide.play();

        return entry;
    }
}