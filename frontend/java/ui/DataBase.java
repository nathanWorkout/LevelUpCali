import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Espace pour afficher la progression des figures principales du street workout
public class DataBase {

    public Pane getView() {
        VBox root = new VBox();
        root.setSpacing(0);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: transparent;");

        VBox header = createHeader();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox content = new VBox(25);
        content.setPadding(new Insets(20, 0, 20, 0));
        content.setAlignment(Pos.TOP_CENTER);

        // Contenu des différentes cartes
        content.getChildren().addAll(
                createFigureCard("Planche",
                        "L'une des figures statiques les plus emblématiques de la callisthénie",
                        new String[]{"Tuck Planche (15s)", "Advance Tuck Planch (15s)", "One Leg Planche (8s)", "Straddle Planche (10s)"},
                        "#e63946"),

                createFigureCard("Front Lever",
                        "Une position de force de traction avancée où le corps est maintenu strictement parallèle au sol.",
                        new String[]{"Front Lever Tuck (20s)", "Advance Tuck Front Lever (10s)", "One Leg Front Lever (10s)", "Half Lay ou Straddle (10s)"},
                        "#457b9d"),

                createFigureCard("Human Flag",
                        "Probablement la figure emblématique du street workout !",
                        new String[]{"Élévations Planche Latérale", "Apprendre le Kick", "Human Flag Tuck (15s)", "Human Flag One Leg (15s)", "Négative Human Flag", "Human Flag Straddle"},
                        "#f77f00"),

                createFigureCard("Handstand",
                        "Une bonne base et probablement une des premières figures à débloquer !",
                        new String[]{"Handstand Mural (30s)", "Pike Push Up (12 reps)", "Fog Stand", "Apprendre à tomber", "Handstand contre un mu puis décoller progressivement les pieds", "Tenter le handstand sans le mur"},
                        "#06ffa5"),

                createFigureCard("Back Lever",
                        "Une des seules figures stylées mais simple du street workout",
                        new String[]{"Skin the Cat", "Back Lever Tuck (10s)", "Négative Straddle Back Lever", "Back Lever Straddle (8s)"},
                        "#9d4edd"),

                createFigureCard("L-Sit",
                        "Position fondamentale de force du tronc et des fléchisseurs de hanche",
                        new String[]{"Maintien en Appui Bras Tendus (30s)", "Relevés de Genoux (12 reps)", "Relevés de jambes assis (15 reps)", "L-Sit One Leg (20s)"},
                        "#2a9d8f")
        );

        scrollPane.setContent(content);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, scrollPane);
        return root;
    }

    // TopBar pour présenter la section DataBase
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 30, 20));
        header.setStyle("-fx-background-color: rgba(20, 20, 20, 0.6); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(255, 255, 255, 0.1));
        shadow.setRadius(20);
        shadow.setSpread(0.3);
        header.setEffect(shadow);

        Label title = new Label("BASE DE DONNÉES DES MOUVEMENTS");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #ffffff;");

        Label subtitle = new Label("Maîtrisez les fondamentaux • Débloquez les compétences avancées");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    // Design de la carte du skill
    private VBox createFigureCard(String name, String description, String[] prerequisites, String accentColor) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setMaxWidth(800);
        card.setStyle("-fx-background-color: rgba(20, 20, 20, 0.7); " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: rgba(255, 255, 255, 0.12); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 18;");

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        cardShadow.setRadius(15);
        cardShadow.setSpread(0.2);
        card.setEffect(cardShadow);

        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Pane colorIndicator = new Pane();
        colorIndicator.setPrefSize(5, 50);
        colorIndicator.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 3;");

        VBox titleBox = new VBox(5);
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #ffffff;");

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        descLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.65);");
        descLabel.setWrapText(true);

        titleBox.getChildren().addAll(nameLabel, descLabel);
        cardHeader.getChildren().addAll(colorIndicator, titleBox);

        Pane separator = new Pane();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");

        VBox prereqBox = new VBox(12);
        Label prereqTitle = new Label("CHEMIN DE PROGRESSION");
        prereqTitle.setFont(Font.font("System", FontWeight.BOLD, 13));
        prereqTitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8);");

        VBox prereqList = new VBox(10);
        for (int i = 0; i < prerequisites.length; i++) {
            prereqList.getChildren().add(createPrerequisiteItem(prerequisites[i], i + 1, accentColor));
        }

        prereqBox.getChildren().addAll(prereqTitle, prereqList);

        Label comingSoon = new Label("Générateur d'entraînement bientôt disponible");
        comingSoon.setFont(Font.font("System", FontWeight.MEDIUM, 12));
        comingSoon.setStyle("-fx-text-fill: " + accentColor + "; -fx-opacity: 0.8;");
        comingSoon.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(cardHeader, separator, prereqBox, comingSoon);

        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: rgba(30, 30, 30, 0.8); " +
                    "-fx-background-radius: 18; " +
                    "-fx-border-color: " + accentColor + "; " +
                    "-fx-border-width: 1.5; " +
                    "-fx-border-radius: 18; " +
                    "-fx-cursor: hand;");

            DropShadow glowShadow = new DropShadow();
            glowShadow.setColor(Color.web(accentColor, 0.4));
            glowShadow.setRadius(25);
            glowShadow.setSpread(0.3);
            card.setEffect(glowShadow);
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: rgba(20, 20, 20, 0.7); " +
                    "-fx-background-radius: 18; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.12); " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 18;");
            card.setEffect(cardShadow);
        });

        return card;
    }


    private HBox createPrerequisiteItem(String text, int step, String accentColor) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10));
        item.setStyle("-fx-background-color: rgba(255, 255, 255, 0.03); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgba(255, 255, 255, 0.08); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10;");

        StackPane stepCircle = new StackPane();
        stepCircle.setPrefSize(32, 32);
        stepCircle.setStyle("-fx-background-color: " + accentColor + "; " +
                "-fx-background-radius: 16;");

        Label stepNum = new Label(String.valueOf(step));
        stepNum.setFont(Font.font("System", FontWeight.BOLD, 14));
        stepNum.setStyle("-fx-text-fill: #ffffff;");
        stepCircle.getChildren().add(stepNum);

        Label itemText = new Label(text);
        itemText.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        itemText.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.85);");
        itemText.setWrapText(true);
        HBox.setHgrow(itemText, Priority.ALWAYS);

        item.getChildren().addAll(stepCircle, itemText);

        item.setOnMouseEntered(e -> {
            item.setStyle("-fx-background-color: rgba(255, 255, 255, 0.06); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: " + accentColor + "; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 10;");
        });

        item.setOnMouseExited(e -> {
            item.setStyle("-fx-background-color: rgba(255, 255, 255, 0.03); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.08); " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 10;");
        });

        return item;
    }
}