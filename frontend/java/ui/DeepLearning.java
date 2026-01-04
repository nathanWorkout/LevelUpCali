import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import org.json.JSONObject;

public class DeepLearning {

    private BorderPane mainPane;
    private ImageView previewImageView;
    private MediaView previewMediaView;
    private MediaPlayer mediaPlayer;
    private StackPane mediaContainer;
    private Label fileNameLabel;
    private Label fileInfoLabel;
    private Button analyzeBtn;
    private File selectedFile;
    private Label statusLabel;
    private VBox resultsSection;
    private boolean isVideo = false;

    // Constantes pour les URLs
    private static final String API_BASE_URL = "http://localhost:5000";
    private static final String ANALYZE_STATIC_URL = API_BASE_URL + "/analyze_static";
    private static final String ANALYZE_DYNAMIC_URL = API_BASE_URL + "/analyze_dynamic";

    public DeepLearning() {
        createView();
    }

    // ============================================================================
    // CRÉATION DE L'INTERFACE
    // ============================================================================

    private void createView() {
        mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: #000000;");

        VBox centerContent = new VBox(40);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(60));
        centerContent.setStyle("-fx-background-color: #000000;");

        VBox header = createHeader();
        VBox uploadSection = createUploadSection();
        VBox previewSection = createPreviewSection();
        resultsSection = createResultsSection();

        centerContent.getChildren().addAll(header, uploadSection, previewSection, resultsSection);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setStyle("-fx-background: #000000; -fx-background-color: #000000;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainPane.setCenter(scrollPane);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        Label title = new Label("Analyse de Mouvement");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #ffffff;");

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.rgb(255, 255, 255, 0.3));
        titleGlow.setRadius(15);
        title.setEffect(titleGlow);

        Label subtitle = new Label("Télécharge une photo ou vidéo pour analyser ton mouvement");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createUploadSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(50));
        section.setMaxWidth(700);
        section.setStyle("-fx-background-color: rgba(20, 20, 20, 0.6); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 20; " +
                "-fx-border-style: dashed;");

        DropShadow sectionShadow = new DropShadow();
        sectionShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        sectionShadow.setRadius(25);
        section.setEffect(sectionShadow);

        Label uploadTitle = new Label("Sélectionne ton Média");
        uploadTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        uploadTitle.setStyle("-fx-text-fill: #ffffff;");

        Label uploadDesc = new Label("Choisis une image (JPG, PNG) ou une vidéo (MP4, AVI, MOV)");
        uploadDesc.setFont(Font.font("System", FontWeight.NORMAL, 13));
        uploadDesc.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        Button browseBtn = createActionButton("Parcourir les Fichiers");
        browseBtn.setOnAction(e -> openFileChooser());

        section.getChildren().addAll(uploadTitle, uploadDesc, browseBtn);
        return section;
    }

    private VBox createPreviewSection() {
        VBox section = new VBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(30));
        section.setMaxWidth(800);
        section.setVisible(false);
        section.setManaged(false);
        section.setStyle("-fx-background-color: rgba(20, 20, 20, 0.6); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 20;");

        DropShadow sectionShadow = new DropShadow();
        sectionShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        sectionShadow.setRadius(25);
        section.setEffect(sectionShadow);

        Label previewTitle = new Label("Aperçu");
        previewTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        previewTitle.setStyle("-fx-text-fill: #ffffff;");

        mediaContainer = new StackPane();
        mediaContainer.setPrefSize(600, 400);
        mediaContainer.setMaxSize(600, 400);
        mediaContainer.setStyle("-fx-background-color: rgba(10, 10, 10, 0.8); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15;");

        previewImageView = new ImageView();
        previewImageView.setPreserveRatio(true);
        previewImageView.setFitWidth(580);
        previewImageView.setFitHeight(380);
        previewImageView.setSmooth(true);

        previewMediaView = new MediaView();
        previewMediaView.setPreserveRatio(true);
        previewMediaView.setFitWidth(580);
        previewMediaView.setFitHeight(380);
        previewMediaView.setVisible(false);

        mediaContainer.getChildren().addAll(previewImageView, previewMediaView);

        HBox videoControls = createVideoControls();

        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);

        fileNameLabel = new Label();
        fileNameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        fileNameLabel.setStyle("-fx-text-fill: #ffffff;");

        fileInfoLabel = new Label();
        fileInfoLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        fileInfoLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6);");

        statusLabel = new Label();
        statusLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        statusLabel.setStyle("-fx-text-fill: rgba(100, 200, 100, 0.8);");

        infoBox.getChildren().addAll(fileNameLabel, fileInfoLabel, statusLabel);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        analyzeBtn = createActionButton("Analyser le Mouvement");
        analyzeBtn.setOnAction(e -> analyzeMedia());

        Button changeBtn = createSecondaryButton("Changer de Fichier");
        changeBtn.setOnAction(e -> openFileChooser());

        buttonBox.getChildren().addAll(analyzeBtn, changeBtn);

        section.getChildren().addAll(previewTitle, mediaContainer, videoControls, infoBox, buttonBox);

        section.setId("previewSection");
        mainPane.setUserData(section);

        return section;
    }

    private VBox createResultsSection() {
        VBox section = new VBox(25);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(40));
        section.setMaxWidth(900);
        section.setVisible(false);
        section.setManaged(false);
        section.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 20;");

        DropShadow sectionShadow = new DropShadow();
        sectionShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        sectionShadow.setRadius(30);
        section.setEffect(sectionShadow);

        Label resultsTitle = new Label("Résultats de l'Analyse");
        resultsTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        resultsTitle.setStyle("-fx-text-fill: #ffffff;");

        section.getChildren().add(resultsTitle);
        return section;
    }

    // ============================================================================
    // GESTION DES FICHIERS
    // ============================================================================

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un Média");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        selectedFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if (selectedFile != null) {
            loadFile(selectedFile);
            statusLabel.setText("");
            resultsSection.setVisible(false);
            resultsSection.setManaged(false);
        }
    }

    private void loadFile(File file) {
        VBox previewSection = (VBox) mainPane.getUserData();

        if (previewSection != null) {
            previewSection.setVisible(true);
            previewSection.setManaged(true);

            mediaContainer.getChildren().removeIf(node ->
                    node instanceof VBox && node != previewImageView && node != previewMediaView
            );

            previewImageView.setVisible(false);
            previewMediaView.setVisible(false);

            fileNameLabel.setText(file.getName());
            String fileSize = String.format("%.2f MB", file.length() / (1024.0 * 1024.0));
            String fileType = getFileExtension(file).toUpperCase();
            fileInfoLabel.setText(fileType + " • " + fileSize);

            String extension = getFileExtension(file).toLowerCase();
            isVideo = extension.equals("mp4") || extension.equals("avi") || extension.equals("mov");

            if (isVideo) {
                loadVideo(file);
            } else {
                loadImage(file);
            }
        }
    }

    private void loadImage(File file) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
            previewMediaView.setVisible(false);
            previewImageView.setVisible(true);

            Image image = new Image(file.toURI().toString());
            previewImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }
    }

    private void loadVideo(File file) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            previewImageView.setVisible(false);
            previewMediaView.setVisible(true);

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            previewMediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la vidéo: " + e.getMessage());
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }

    // ============================================================================
    // COMPOSANTS UI
    // ============================================================================

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-text-fill: #ffffff; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 12;");
        addButtonAnimation(btn);
        return btn;
    }

    private Button createSecondaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        btn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                "-fx-text-fill: rgba(255, 255, 255, 0.8); " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: rgba(255, 255, 255, 0.15); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 12;");
        addButtonAnimation(btn);
        return btn;
    }

    private void addButtonAnimation(Button btn) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 255, 255, 0.4));
        glow.setRadius(20);
        glow.setSpread(0.4);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setEffect(glow);
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            scaleDown.playFromStart();
        });
    }

    private HBox createVideoControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: rgba(20, 20, 20, 0.8); " +
                "-fx-background-radius: 10;");

        Button playPauseBtn = new Button("⏯ Lecture/Pause");
        playPauseBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 12px;");
        playPauseBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.play();
                }
            }
        });

        Button stopBtn = new Button("⏹ Stop");
        stopBtn.setStyle("-fx-background-color: rgba(255, 100, 100, 0.2); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 12px;");
        stopBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });

        Button replayBtn = new Button("🔄 Rejouer");
        replayBtn.setStyle("-fx-background-color: rgba(100, 200, 255, 0.2); " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8; " +
                "-fx-font-size: 12px;");
        replayBtn.setOnAction(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            }
        });

        controls.getChildren().addAll(playPauseBtn, stopBtn, replayBtn);
        return controls;
    }

    public Pane getView() {
        return mainPane;
    }

    // ============================================================================
    // ANALYSE DES MÉDIAS
    // ============================================================================

    private void analyzeMedia() {
        if (selectedFile == null) {
            System.out.println("Aucun fichier sélectionné !");
            return;
        }

        analyzeBtn.setDisable(true);
        statusLabel.setText("Analyse en cours...");
        statusLabel.setStyle("-fx-text-fill: rgba(255, 200, 100, 0.8);");

        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

        new Thread(() -> {
            try {
                if (isVideo) {
                    analyzeVideoFile();
                } else {
                    analyzeImageFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Erreur: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                    analyzeBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void analyzeImageFile() throws Exception {
        System.out.println("Envoi de l'image vers: " + ANALYZE_STATIC_URL);

        BufferedImage bufferedImage = ImageIO.read(selectedFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        String encodedImage = Base64.getEncoder().encodeToString(baos.toByteArray());

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("image_base64", encodedImage);

        String jsonInputString = jsonPayload.toString();

        sendJsonToPython(jsonInputString, ANALYZE_STATIC_URL);
    }

    private void analyzeVideoFile() throws Exception {
        System.out.println("📤 Envoi de la vidéo vers: " + ANALYZE_DYNAMIC_URL);

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        URL url = new URL(ANALYZE_DYNAMIC_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.setDoOutput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(120000);

        try (OutputStream os = con.getOutputStream();
             DataOutputStream dos = new DataOutputStream(os)) {

            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + selectedFile.getName() + "\"\r\n");
            dos.writeBytes("Content-Type: video/mp4\r\n\r\n");

            Files.copy(selectedFile.toPath(), dos);

            dos.writeBytes("\r\n--" + boundary + "--\r\n");
            dos.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("📥 Code de réponse: " + responseCode);

        StringBuilder response = new StringBuilder();

        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
            if (inputStream == null) {
                throw new IOException("Le serveur a retourné le code " + responseCode);
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode >= 400) {
            JSONObject errorResponse = new JSONObject(response.toString());
            String errorMessage = errorResponse.optString("message", "Erreur serveur: " + responseCode);

            Platform.runLater(() -> {
                statusLabel.setText(errorMessage);
                statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                analyzeBtn.setDisable(false);
            });
            return;
        }

        final String finalResponse = response.toString();

        Platform.runLater(() -> {
            try {
                JSONObject jsonResponse = new JSONObject(finalResponse);
                String videoBase64 = jsonResponse.optString("video_base64", "");

                if (!videoBase64.isEmpty()) {
                    System.out.println("🎥 Décodage de la vidéo...");

                    byte[] videoBytes = Base64.getDecoder().decode(videoBase64);

                    File tempDir = new File(System.getProperty("java.io.tmpdir"), "analyzed_videos");
                    if (!tempDir.exists()) {
                        tempDir.mkdirs();
                    }

                    File tempVideoFile = new File(tempDir, "analyzed_" + System.currentTimeMillis() + ".mp4");

                    try (FileOutputStream fos = new FileOutputStream(tempVideoFile)) {
                        fos.write(videoBytes);
                        fos.flush();
                    }

                    System.out.println("✅ Vidéo sauvegardée: " + tempVideoFile.getAbsolutePath());

                    previewImageView.setVisible(false);
                    previewMediaView.setVisible(false);

                    VBox videoResultBox = new VBox(25);
                    videoResultBox.setAlignment(Pos.CENTER);
                    videoResultBox.setPadding(new Insets(40));
                    videoResultBox.setStyle("-fx-background-color: rgba(30, 30, 30, 0.95); " +
                            "-fx-background-radius: 15;");

                    Label successIcon = new Label("✓");
                    successIcon.setFont(Font.font("System", FontWeight.BOLD, 72));
                    successIcon.setStyle("-fx-text-fill: #64ff64;");

                    DropShadow iconGlow = new DropShadow();
                    iconGlow.setColor(Color.rgb(100, 255, 100, 0.5));
                    iconGlow.setRadius(25);
                    successIcon.setEffect(iconGlow);

                    Label mainMessage = new Label("Analyse Terminée !");
                    mainMessage.setFont(Font.font("System", FontWeight.BOLD, 24));
                    mainMessage.setStyle("-fx-text-fill: #ffffff;");

                    Label subMessage = new Label("Ta vidéo avec les landmarks est prête");
                    subMessage.setFont(Font.font("System", FontWeight.NORMAL, 14));
                    subMessage.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.7);");

                    Button openVideoBtn = new Button("📹 Voir la Vidéo");
                    openVideoBtn.setPrefWidth(280);
                    openVideoBtn.setPrefHeight(55);
                    openVideoBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
                    openVideoBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-border-width: 1.5; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;");

                    DropShadow btnGlow = new DropShadow();
                    btnGlow.setColor(Color.rgb(76, 175, 80, 0.6));
                    btnGlow.setRadius(20);
                    btnGlow.setSpread(0.3);

                    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), openVideoBtn);
                    scaleUp.setToX(1.05);
                    scaleUp.setToY(1.05);

                    ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), openVideoBtn);
                    scaleDown.setToX(1.0);
                    scaleDown.setToY(1.0);

                    openVideoBtn.setOnMouseEntered(e -> {
                        openVideoBtn.setEffect(btnGlow);
                        scaleUp.playFromStart();
                    });

                    openVideoBtn.setOnMouseExited(e -> {
                        openVideoBtn.setEffect(null);
                        scaleDown.playFromStart();
                    });

                    openVideoBtn.setOnAction(e -> {
                        try {
                            if (java.awt.Desktop.isDesktopSupported()) {
                                java.awt.Desktop.getDesktop().open(tempVideoFile);

                                openVideoBtn.setText("✓ Vidéo Ouverte");
                                openVideoBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                                        "-fx-text-fill: white; " +
                                        "-fx-background-radius: 12; " +
                                        "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                                        "-fx-border-width: 1.5; " +
                                        "-fx-border-radius: 12;");

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2000);
                                        Platform.runLater(() -> {
                                            openVideoBtn.setText("📹 Voir la Vidéo");
                                            openVideoBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); " +
                                                    "-fx-text-fill: white; " +
                                                    "-fx-background-radius: 12; " +
                                                    "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                                                    "-fx-border-width: 1.5; " +
                                                    "-fx-border-radius: 12;");
                                        });
                                    } catch (InterruptedException ignored) {}
                                }).start();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            statusLabel.setText("Erreur lors de l'ouverture de la vidéo");
                            statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                        }
                    });

                    Label filePathLabel = new Label("📁 " + tempVideoFile.getName());
                    filePathLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
                    filePathLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);");

                    videoResultBox.getChildren().addAll(
                            successIcon,
                            mainMessage,
                            subMessage,
                            openVideoBtn,
                            filePathLabel
                    );

                    mediaContainer.getChildren().add(videoResultBox);

                    statusLabel.setText("✓ Analyse terminée avec succès !");
                    statusLabel.setStyle("-fx-text-fill: rgba(100, 255, 100, 0.8);");
                    analyzeBtn.setDisable(false);

                } else {
                    statusLabel.setText("❌ Aucune vidéo reçue du serveur");
                    statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                    analyzeBtn.setDisable(false);
                }

                displayResults(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("❌ Erreur: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                analyzeBtn.setDisable(false);
            }
        });
    }

    private void sendJsonToPython(String jsonInputString, String urlString) throws IOException {
        System.out.println("📤 Envoi JSON vers: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        con.setConnectTimeout(10000);
        con.setReadTimeout(30000);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        System.out.println("📥 Code de réponse: " + responseCode);

        StringBuilder response = new StringBuilder();

        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            inputStream = con.getInputStream();
        } else {
            inputStream = con.getErrorStream();
            if (inputStream == null) {
                throw new IOException("Le serveur a retourné le code " + responseCode);
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode >= 400) {
            JSONObject errorResponse = new JSONObject(response.toString());
            String errorMessage = errorResponse.optString("message", "Erreur serveur: " + responseCode);

            Platform.runLater(() -> {
                statusLabel.setText(errorMessage);
                statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                analyzeBtn.setDisable(false);
            });
            return;
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        String status = jsonResponse.getString("status");

        if (status.equals("ok")) {
            String imageBase64 = jsonResponse.getString("image_base64");
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);

            File tempFile = File.createTempFile("analyzed_", ".jpg");
            ImageIO.write(bufferedImage, "jpg", tempFile);

            Platform.runLater(() -> {
                try {
                    Image analyzedImage = new Image(tempFile.toURI().toString());
                    previewImageView.setImage(analyzedImage);
                    statusLabel.setText("✓ Analyse terminée !");
                    statusLabel.setStyle("-fx-text-fill: rgba(100, 255, 100, 0.8);");
                    analyzeBtn.setDisable(false);

                    displayResults(jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Erreur d'affichage du résultat");
                    statusLabel.setStyle("-fx-text-fill: rgba(255, 100, 100, 0.8);");
                    analyzeBtn.setDisable(false);
                }
            });
        }
    }

    // ============================================================================
    // AFFICHAGE DES RÉSULTATS
    // ============================================================================

    private void displayResults(JSONObject response) {
        Platform.runLater(() -> {
            resultsSection.getChildren().clear();
            resultsSection.setVisible(true);
            resultsSection.setManaged(true);

            Label resultsTitle = new Label("Résultats de l'Analyse");
            resultsTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
            resultsTitle.setStyle("-fx-text-fill: #ffffff;");

            String detectedFigure = response.optString("detected_figure", "unknown");
            VBox figureBlock = createFigureBlock(detectedFigure);

            resultsSection.getChildren().addAll(resultsTitle, figureBlock);

            if (response.has("analysis") && !response.isNull("analysis")) {
                JSONObject analysis = response.getJSONObject("analysis");
                VBox analysisBlock = createAnalysisBlock(analysis);
                resultsSection.getChildren().add(analysisBlock);
            }

            if (response.has("deviations") && !response.isNull("deviations")) {
                JSONObject deviations = response.getJSONObject("deviations");
                if (deviations.length() > 0) {
                    VBox deviationsBlock = createDeviationsBlock(deviations);
                    resultsSection.getChildren().add(deviationsBlock);
                }
            }

            if (response.has("scapular_info") && !response.isNull("scapular_info")) {
                JSONObject scapularInfo = response.getJSONObject("scapular_info");
                VBox scapularBlock = createScapularBlock(scapularInfo);
                resultsSection.getChildren().add(scapularBlock);
            }

            if (response.has("total_reps")) {
                int totalReps = response.getInt("total_reps");
                Label repsLabel = new Label("Répétitions détectées: " + totalReps);
                repsLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
                repsLabel.setStyle("-fx-text-fill: #64c8ff;");
                resultsSection.getChildren().add(repsLabel);
            }
        });
    }

    private VBox createFigureBlock(String detectedFigure) {
        VBox figureBlock = new VBox(15);
        figureBlock.setAlignment(Pos.CENTER);
        figureBlock.setPadding(new Insets(25, 40, 25, 40));
        figureBlock.setMaxWidth(600);
        figureBlock.setStyle("-fx-background-color: rgba(30, 30, 30, 0.9); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 20;");

        Label figureTitle = new Label("Figure Détectée");
        figureTitle.setFont(Font.font("System", FontWeight.NORMAL, 13));
        figureTitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);");

        Label figureLabel = new Label(getFigureDisplayName(detectedFigure));
        figureLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        figureLabel.setStyle("-fx-text-fill: #ffffff;");

        DropShadow figureGlow = new DropShadow();
        figureGlow.setColor(Color.rgb(100, 200, 255, 0.4));
        figureGlow.setRadius(15);
        figureLabel.setEffect(figureGlow);

        figureBlock.getChildren().addAll(figureTitle, figureLabel);
        return figureBlock;
    }

    private VBox createAnalysisBlock(JSONObject analysis) {
        VBox block = new VBox(15);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setPadding(new Insets(25));
        block.setMaxWidth(800);
        block.setStyle("-fx-background-color: rgba(40, 40, 70, 0.5); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(100, 150, 255, 0.4); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 15;");

        Label title = new Label("🔍 Analyse Détaillée");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #64c8ff;");

        if (analysis.has("cause")) {
            String causeText = analysis.getString("cause");
            String label = causeText.contains("correcte") || causeText.contains("correct") ? "Statut" : "Problème";
            String color = causeText.contains("correcte") || causeText.contains("correct") ? "#64ff64" : "#ff6464";

            HBox problemBox = createInfoRow(label, causeText, color);
            block.getChildren().add(problemBox);
        }

        if (analysis.has("compensation")) {
            HBox compBox = createInfoRow("Compensation", analysis.getString("compensation"), "#ffaa64");
            block.getChildren().add(compBox);
        }

        if (analysis.has("correction")) {
            HBox corrBox = createInfoRow("Correction", analysis.getString("correction"), "#64ff64");
            block.getChildren().add(corrBox);
        }

        block.getChildren().add(0, title);
        return block;
    }

    private VBox createDeviationsBlock(JSONObject deviations) {
        VBox block = new VBox(12);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setPadding(new Insets(20));
        block.setMaxWidth(800);
        block.setStyle("-fx-background-color: rgba(70, 40, 40, 0.4); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(255, 100, 100, 0.3); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15;");

        Label title = new Label("⚠ Erreurs détectées");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #ff6464;");

        block.getChildren().add(title);

        for (String key : deviations.keySet()) {
            Object value = deviations.get(key);
            String displayText = formatDeviationKey(key) + ": " + formatDeviationValue(value);

            Label devLabel = new Label("• " + displayText);
            devLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
            devLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.9);");
            devLabel.setWrapText(true);
            devLabel.setMaxWidth(750);

            block.getChildren().add(devLabel);
        }

        return block;
    }

    private VBox createScapularBlock(JSONObject scapularInfo) {
        VBox block = new VBox(12);
        block.setAlignment(Pos.CENTER_LEFT);
        block.setPadding(new Insets(20));
        block.setMaxWidth(800);
        block.setStyle("-fx-background-color: rgba(40, 50, 40, 0.4); " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: rgba(100, 200, 100, 0.3); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15;");

        Label title = new Label("💪 Position Scapulaire");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #64ff64;");

        block.getChildren().add(title);

        String position = "Neutre";
        if (scapularInfo.optBoolean("is_protracted", false)) {
            position = "Protractée (épaules en avant)";
        } else if (scapularInfo.optBoolean("is_retracted", false)) {
            position = "Rétractée (épaules en arrière)";
        }

        Label posLabel = new Label("Position: " + position);
        posLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        posLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.9);");
        posLabel.setWrapText(true);
        posLabel.setMaxWidth(750);

        block.getChildren().add(posLabel);
        return block;
    }

    private HBox createInfoRow(String label, String value, String color) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelText = new Label(label + ":");
        labelText.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelText.setStyle("-fx-text-fill: " + color + ";");
        labelText.setMinWidth(120);

        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        valueText.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.9);");
        valueText.setWrapText(true);
        valueText.setMaxWidth(600);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    // ============================================================================
    // UTILITAIRES
    // ============================================================================

    private String formatDeviationKey(String key) {
        switch (key) {
            case "hips_low": return "Abaissement des hanches";
            case "elbows_flexed": return "Coudes fléchis";
            case "knees_bent": return "Genoux fléchis";
            case "back_arched": return "Dos cambré";
            case "coudes_flechis": return "Coudes fléchis";
            case "hanches_flechies": return "Hanches fléchies";
            case "bassin_abaisse": return "Bassin abaissé";
            case "hanches_basses": return "Hanches basses";
            case "amplitude_insuffisante": return "Amplitude insuffisante";
            case "verrouillage_incomplet": return "Verrouillage incomplet";
            case "extension_incomplete": return "Extension incomplète";
            case "profondeur_insuffisante": return "Profondeur insuffisante";
            case "amplitude_reduite": return "Amplitude réduite";
            case "execution_rapide": return "Exécution trop rapide";
            case "execution_lente": return "Exécution trop lente";
            case "amplitude_totale": return "Amplitude totale";
            default: return key.replace("_", " ").substring(0, 1).toUpperCase() +
                    key.replace("_", " ").substring(1);
        }
    }

    private String formatDeviationValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? "Oui" : "Non";
        }
        return value.toString();
    }

    private String getFigureDisplayName(String figure) {
        switch (figure.toLowerCase()) {
            case "handstand": return "HANDSTAND";
            case "planche": return "PLANCHE";
            case "front_lever": return "FRONT LEVER";
            case "frontlever": return "FRONT LEVER";
            case "human_flag": return "HUMAN FLAG";
            case "humanflag": return "HUMAN FLAG";
            case "push_up": return "PUSH UP";
            case "pull_up": return "PULL UP";
            case "unknown": return "FIGURE INCONNUE";
            default: return figure.toUpperCase().replace("_", " ");
        }
    }
}