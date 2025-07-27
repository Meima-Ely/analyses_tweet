package com.twitter.analysis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.title.TextTitle;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TweetAnalyzerAndGraph extends Application {

    // IMPORTANT: Make sure this path is correct for your system.
    private static final String CSV_PATH = "C:/Users/hp/Desktop/analyses_tweet/scrap-tweet2025.csv";
    private static final List<Map<String, String>> tweets = new ArrayList<>();
    
    // Updated banned words list to exclude more irrelevant or sensitive terms
    private static final Set<String> bannedWords = new HashSet<>(Arrays.asList(
            // Existing specific banned words (sexuel, nsfw, furry, AI-related, etc.)
            "sexuel", "sexe", "xxx", "nude", "nsfw", "transsexual", "transex", "shemale", "morrigan_x3",
            "ai", "art", "via", "amp", "etc", "com", "fr", "tco", "j", "v", "ani", "ah248", "ah247", "dr", "mrs", "monarch", "content", "link", "bio", "view",
            "more", "nsfwcontent", "hard", "core", "gangbang", "4k", "qualit", "random", "oc", "摔跤手黑熊",
            "白刃", "全兽出击", "because", "character", "lora", "and", "art", "style", "lora", "are", "not",
            "very", "compatible", "so", "i", "haven't", "given", "birth", "to", "him", "anados", "福尔豪斯",
            "fullhouse", "フルハウス", "my", "assistant", "demanded", "raise", "model", "babe", "hot", "hotties",
            "cute", "beauty", "aigirls", "aigirlfiend", "aigirl", "midjourney", "aiartist", "aicommunity",
            "digitalart", "klingai", "aivideo", "veo3", "bigfoot", "bigfootvlogs", "x3", "aiart", "aigenerated", 
            "aiillustration", "aiイラスト", "aiartwork", "ai美女", "ai美少女", "aigravure", "aiグラビア",
            "deadoralive", "doa6", "doaxvv", "nico", "fanart", "waifu", 
            // Short generic chars already present, but good to ensure:
            "r", "t", "p", "s", "c", "g", 
            "a", "à", // French A and a with grave accent

            // --- Extensive French Stop Words ---
            "de", "la", "le", "les", "du", "des", "un", "une", "pour", "avec", "et", "est", "sont", "dans", 
            "sur", "par", "pas", "plus", "vous", "nous", "il", "elle", "ils", "elles", "je", "tu", "on", 
            "se", "ce", "ces", "sa", "son", "ses", "mon", "ma", "mes", "notre", "nos", "votre", "vos", 
            "leur", "leurs", "qui", "que", "quoi", "où", "quand", "comment", "pourquoi", "en", "au", "aux", 
            "d'", "l'", "c'", "s'", "n'", "m'", "t'",
            "être", "avoir", "faire", "dire", "aller", "voir", "pouvoir", "vouloir", "savoir", "falloir", 
            "prendre", "trouver", "donner", "dire", "tout", "tous", "toute", "toutes", "rien", "quelque", 
            "chose", "choses", "personne", "même", "ainsi", "alors", "après", "avant", "bien", "car", 
            "cependant", "donc", "enfin", "encore", "juste", "maintenant", "mais", "moins", "non", "oui", 
            "peut", "peut-être", "si", "souvent", "toujours", "trop", "très", "vite", "voici", "voilà", 
            "vers", "vers", "comment", "plus", "devrait", "chaque", "chacun", "ceux", "celles", "celui", "celle", 
            "donc", "dès", "puis", "tel", "tels", "telle", "telles", "tout", "tous", "toute", "toutes", 
            "vers", "voici", "voilà", "vraiment", "jamais", "souvent", "seulement", "environ", "ainsi", 
            "pendant", "malgré", "depuis", "toujours", "lorsque", "tandis", "afin", "avant", "après", "ici", 
            "là", "loin", "proche", "haut", "bas", "partout", "ailleurs", "dehors", "dedans", "jamais", 
            "rarement", "souvent", "toujours", "peu", "beaucoup", "assez", "trop", "plus", "moins", "aussi", 
            "comment", "combien", "pourquoi", "quand", "quoi", "où", "qui", "qu", "quel", "quelle", "quels", 
            "quelles", "dont", "ceci", "cela", "celles", "ceux", "celle-ci", "celui-ci", "celle-là", "celui-là", 
            "moi", "toi", "lui", "elle", "eux", "elles", "soi", "me", "te", "le", "la", "les", "nous", "vous", 
            "se", "en", "y", "ce", "ça", "chacun", "chacune", "certains", "certaines", "quelques", "tous", 
            "toutes", "plusieurs", "divers", "diverses", "tels", "telles", "tel", "telle", "même", "mêmes", 
            "tout", "toute", "tous", "toutes", "aucun", "aucune", "personne", "rien", "nul", "nulle", 
            "plusieurs", "certains", "certaines", "différents", "différentes", "tel", "telle", "tels", "telles",
            "jusqu", "qu", "quand", "puis", "depuis", "entre", "chacun", "chaque", "moins", "afin", "dès",

            // --- Extensive English Stop Words (common ones, including those found in your screenshots) ---
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", 
            "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", 
            "this", "to", "was", "will", "with", "from", "your", "its", "had", "has", "have", "he", "her", 
            "him", "his", "how", "i", "me", "my", "myself", "our", "ours", "ourselves", "out", "she", "so", 
            "shan", "should", "shouldn", "t", "too", "very", "were", "weren", "what", "when", 
            "where", "which", "while", "who", "whom", "why", "you", "your", "yours", "yourself", "yourselves",
            "about", "above", "after", "again", "all", "am", "among", "any", "around", "before", "below", "between", "both", "can", 
            "could", "did", "do", "does", "doing", "down", "during", "each", "few", "further", "get", "got",
            "hadn", "hasn", "haven", "having", "her", "here", "hers", "herself", "himself", "how", "into", 
            "just", "ll", "m", "mightn", "more", "most", "mustn", "needn", "nor", "now", "o", "off", "once", 
            "only", "or", "over", "own", "re", "s", "same", "should", "shouldn", "some", 
            "such", "t", "than", "too", "very", "was", "wasn", "we", "were", "weren", "what", "when", 
            "where", "which", "while", "whom", "won", "wouldn", "y", "you", "ve", "yours", "yourself", "yourselves",
            "don", "isn", "won", "ain", "aren", "couldn", "didn", "doesn", "hadn", "hasn", "haven", "mightn",
            "mustn", "needn", "shan", "shouldn", "wasn", "weren", "wouldn", "d", "ll", "m", "o", "re", "s", "ve", "y"
    ));

    // Palette de couleurs moderne
    private static final Color[] MODERN_COLORS = {
        new Color(79, 172, 254),   // Bleu moderne
        new Color(0, 184, 169),    // Teal
        new Color(255, 171, 0),    // Orange vif
        new Color(255, 45, 85),    // Rouge moderne
        new Color(52, 199, 89),    // Vert moderne
        new Color(191, 90, 242),   // Violet
        new Color(255, 204, 0),    // Jaune moderne
        new Color(142, 142, 147),  // Gris moderne
        new Color(255, 95, 0),     // Orange rouge
        new Color(30, 215, 96)     // Vert clair
    };

    public static void main(String[] args) {
        // Load data BEFORE launching the JavaFX application
        loadCsvData(); 
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // --- 1. Root Layout Setup ---
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f9ff, #e0f2fe);");

        // --- 2. Title ---
        Label title = new Label("📊 Dashboard d'Analyse de Tweets - Intelligence Sociale");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0f172a; " +
                      "-fx-padding: 20; -fx-background-color: linear-gradient(to right, #ffffff, #f8fafc); " +
                      "-fx-background-radius: 15; -fx-border-radius: 15; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 3); " +
                      "-fx-alignment: center;");
        root.setTop(title); // Set title to the top of the BorderPane

        // --- 3. Check for Data Before Proceeding ---
        if (tweets.isEmpty()) {
            Platform.runLater(() -> { // Ensure dialog is shown on JavaFX Application Thread
                JOptionPane.showMessageDialog(null, "Aucune donnée chargée depuis le fichier CSV : " + CSV_PATH +
                                                    "\nVeuillez vérifier le chemin du fichier et son contenu (doit contenir au moins une ligne d'en-tête et des données).", 
                                                    "Erreur de Données", JOptionPane.ERROR_MESSAGE);
                primaryStage.close(); // Close the window if no data
            });
            return;
        }

        // --- 4. Chart & Table Components ---
        // SwingNodes for charts (JFreeChart integrates via SwingNode)
        SwingNode chart1 = new SwingNode(); // Top Hashtags
        SwingNode chart2 = new SwingNode(); // Engagement by User
        SwingNode chart3 = new SwingNode(); // User Interactions
        SwingNode chart4 = new SwingNode(); // Active Users
        SwingNode chart5 = new SwingNode(); // Tweets by Location (NEW!)

        // Create charts in a separate thread to avoid blocking JavaFX UI thread
        new Thread(() -> {
            try {
                // Charts must be created on the AWT event dispatch thread (SwingUtilities.invokeLater)
                // as JFreeChart is a Swing component.
                SwingUtilities.invokeLater(() -> {
                    try {
                        chart1.setContent(createModernChartPanel(createTopHashtagChart()));
                        chart2.setContent(createModernChartPanel(createMetricsDistributionChart()));
                        chart3.setContent(createModernChartPanel(createUserInteractionChart()));
                        chart4.setContent(createModernChartPanel(createActiveUsersChart()));
                        // Changed this line to call the new chart method
                        chart5.setContent(createModernChartPanel(createTweetsByLocationChart())); 
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> JOptionPane.showMessageDialog(null, "Erreur lors de la création des graphiques: " + e.getMessage(), "Erreur Graphique", JOptionPane.ERROR_MESSAGE));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> JOptionPane.showMessageDialog(null, "Erreur inattendue dans le thread de création de graphique: " + e.getMessage(), "Erreur Thread", JOptionPane.ERROR_MESSAGE));
            }
        }).start();


        // Table des statistiques
        VBox statsSection = createStatsSection();
        
        // --- 5. Layout Charts and Table ---
        // Chart Grid setup - now for 5 charts (2x2 top, 1 spanning bottom)
        GridPane chartGrid = new GridPane();
        chartGrid.setHgap(20);
        chartGrid.setVgap(20);
        chartGrid.setPadding(new Insets(10));
        
        // Row 0
        chartGrid.add(createChartContainer(chart1, "🏷️ Top Hashtags"), 0, 0);
        chartGrid.add(createChartContainer(chart2, "📊 Engagement par Utilisateur"), 1, 0);
        
        // Row 1
        chartGrid.add(createChartContainer(chart3, "💬 Interactions Utilisateurs"), 0, 1);
        chartGrid.add(createChartContainer(chart4, "🔥 Utilisateurs les Plus Actifs"), 1, 1);
        
        // Row 2 (the 5th chart, spanning 2 columns)
        // Changed this line to update the title for the new chart
        chartGrid.add(createChartContainer(chart5, "📍 Tweets par Localisation"), 0, 2, 2, 1); 

        // This ScrollPane will now manage vertical scrolling if content overflows
        ScrollPane chartScrollPane = new ScrollPane(chartGrid);
        chartScrollPane.setFitToWidth(true);
        chartScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chartScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-hbar-policy: never;");
        chartScrollPane.setPrefHeight(780); // Adjusted to ensure all 3 rows of charts are visible, and allow for table height

        // Main content layout (charts + table side-by-side)
        HBox contentLayout = new HBox(15);
        contentLayout.setPadding(new Insets(10));
        HBox.setHgrow(chartScrollPane, Priority.ALWAYS);
        contentLayout.getChildren().addAll(chartScrollPane, statsSection);

        root.setCenter(contentLayout);

        // --- 6. Scene and Stage Setup ---
        // Adjusted initial window size to be "medium" and attractive
        Scene scene = new Scene(root, 1000, 750); // Increased width slightly to accommodate 2 columns + table better
        primaryStage.setTitle("📊 Twitter Analytics Dashboard - Version Pro");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private VBox createStatsSection() {
        VBox statsSection = new VBox(15);
        statsSection.setPrefWidth(400); // Fixed width for the stats section
        statsSection.setMaxWidth(400);
        
        Label statsTitle = new Label("📈 Statistiques Détaillées");
        statsTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b; " +
                           "-fx-padding: 15; -fx-background-color: linear-gradient(to right, #dbeafe, #bfdbfe); " +
                           "-fx-background-radius: 10; -fx-alignment: center;");
        
        TableView<TweetSummary> table = createModernTable();
        
        VBox tableContainer = new VBox(10);
        tableContainer.getChildren().addAll(statsTitle, table);
        tableContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; " +
                               "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 12, 0, 0, 5); " +
                               "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 15; " +
                               "-fx-padding: 15;");
        
        statsSection.getChildren().add(tableContainer);
        return statsSection;
    }

    private VBox createChartContainer(SwingNode chart, String chartTitle) {
        VBox container = new VBox(10);
        
        Label titleLabel = new Label(chartTitle);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b; " +
                           "-fx-padding: 10; -fx-background-color: linear-gradient(to right, #f1f5f9, #e2e8f0); " +
                           "-fx-background-radius: 8; -fx-alignment: center;");
        
        container.getChildren().addAll(titleLabel, chart);
        container.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                          "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 3); " +
                          "-fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 12; " +
                          "-fx-padding: 15;");
        
        // Smaller fixed size for individual chart containers to fit more in the grid
        // The last chart (spanning two columns) will scale horizontally.
        container.setPrefSize(420, 280); // Adjusted for better fit in 2-column layout
        return container;
    }

    private static ChartPanel createModernChartPanel(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(380, 240)); // Adjusted preferred size for content within the container
        panel.setMinimumSize(new Dimension(380, 240));
        panel.setMouseWheelEnabled(true);
        panel.setDomainZoomable(true);
        panel.setRangeZoomable(true);
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private static void loadCsvData() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                System.err.println("❌ Fichier CSV vide ou en-tête manquant : " + CSV_PATH);
                return;
            }
            
            String[] headers = headerLine.split(",");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 
                String[] values = parseCsvLine(line);
                if (values.length != headers.length) {
                    System.err.println("⚠️ Ligne CSV ignorée à cause d'un nombre de colonnes incohérent: " + line);
                    continue; 
                }
                Map<String, String> tweet = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    tweet.put(headers[i].trim(), values[i].trim());
                }
                tweets.add(tweet);
            }
            System.out.println("✅ Chargé " + tweets.size() + " tweets depuis " + CSV_PATH);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement du fichier CSV : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Une erreur inattendue est survenue lors du chargement du CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().replaceAll("^\"|\"$", "")); 
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().replaceAll("^\"|\"$", "")); 
        
        return values.toArray(new String[0]);
    }

    private TableView<TweetSummary> createModernTable() {
        TableView<TweetSummary> table = new TableView<>();
        table.setPrefHeight(400); 
        table.setMaxHeight(400);

        TableColumn<TweetSummary, String> metricColumn = new TableColumn<>("📊 Métriques");
        metricColumn.setCellValueFactory(cellData -> cellData.getValue().metricProperty());
        metricColumn.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-LEFT; -fx-font-size: 14px; -fx-text-fill: #1e293b;");
        metricColumn.setPrefWidth(220);

        TableColumn<TweetSummary, Number> valueColumn = new TableColumn<>("🔢 Valeurs");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        valueColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #059669;");
        valueColumn.setPrefWidth(140);

        table.getColumns().addAll(metricColumn, valueColumn);
        ObservableList<TweetSummary> summary = calculateSummary();
        if (summary != null && !summary.isEmpty()) {
            table.setItems(summary);
        } else {
            System.err.println("Aucune donnée disponible pour la table des statistiques.");
            table.setItems(FXCollections.observableArrayList(new TweetSummary("Erreur: Pas de données", 0)));
        }

        table.setStyle("-fx-font-size: 13px; -fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 1px; -fx-border-radius: 8px;");

        table.getStylesheets().add("data:text/css," + 
            ".table-view .column-header-background {" +
                "-fx-background-color: linear-gradient(to bottom, #1e293b, #334155); " +
                "-fx-border-color: #475569; -fx-border-width: 0 0 2px 0;" +
            "}" +
            ".table-view .column-header .label {" +
                "-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;" +
            "}"
        );
        table.setRowFactory(tv -> new javafx.scene.control.TableRow<TweetSummary>() {
            @Override
            protected void updateItem(TweetSummary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #f8fafc;"); 
                    } else {
                        setStyle("-fx-background-color: #ffffff;"); 
                    }
                }
            }
        });

        return table;
    }

    private ObservableList<TweetSummary> calculateSummary() {
        if (tweets.isEmpty()) { 
            return FXCollections.observableArrayList();
        }

        int totalTweets = tweets.size();
        long totalLikes = 0, totalRetweets = 0, totalReplies = 0, totalFollowers = 0; 
        Set<String> locations = new HashSet<>();
        Set<String> uniqueUsers = new HashSet<>();

        for (Map<String, String> tweet : tweets) {
            String location = tweet.getOrDefault("Location", "");
            String user = tweet.getOrDefault("Username", "");
            
            if (!location.isEmpty()) locations.add(location);
            if (!user.isEmpty()) uniqueUsers.add(user);
            
            try {
                totalLikes += parseIntSafe(tweet.getOrDefault("Like_Count", "0"));
                totalRetweets += parseIntSafe(tweet.getOrDefault("Retweet_Count", "0"));
                totalReplies += parseIntSafe(tweet.getOrDefault("Reply_Count", "0"));
                totalFollowers += parseIntSafe(tweet.getOrDefault("User_Followers", "0"));
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing des données numériques pour un tweet. Valeur possiblement invalide: " + tweet);
            }
        }

        return FXCollections.observableArrayList(
                new TweetSummary("📱 Total Tweets", totalTweets),
                new TweetSummary("👤 Utilisateurs Uniques", uniqueUsers.size()),
                new TweetSummary("❤️ Total Likes", (int) totalLikes),
                new TweetSummary("🔄 Total Retweets", (int) totalRetweets),
                new TweetSummary("💬 Total Réponses", (int) totalReplies),
                new TweetSummary("👥 Total Followers", (int) totalFollowers),
                new TweetSummary("🌍 Lieux Uniques", locations.size()),
                new TweetSummary("📊 Likes Moyens/Tweet", totalTweets > 0 ? (int) (totalLikes / totalTweets) : 0),
                new TweetSummary("📈 Retweets Moyens/Tweet", totalTweets > 0 ? (int) (totalRetweets / totalTweets) : 0),
                new TweetSummary("🎯 Engagement Moyen", totalTweets > 0 ? (int) ((totalLikes + totalRetweets + totalReplies) / totalTweets) : 0)
        );
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0; 
        }
    }

    private JFreeChart createTopHashtagChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Long> hashtagCounts = tweets.stream()
                .map(t -> t.getOrDefault("Hashtags", ""))
                .filter(h -> !h.isEmpty())
                .flatMap(h -> Arrays.stream(h.split("[,;]"))) 
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(h -> !h.isEmpty() && !bannedWords.contains(h) && h.matches("^[a-zA-Z0-9_#]+$")) 
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()));

        hashtagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .forEach(e -> dataset.addValue(e.getValue(), "Hashtags", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top Hashtags Populaires", "Hashtag", "Fréquence",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        
        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        styleBarChart(plot);
        
        return chart;
    }

    private JFreeChart createMetricsDistributionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, List<Map<String, String>>> tweetsByUser = tweets.stream()
                .filter(tweet -> {
                    String user = tweet.get("Username");
                    return user != null && !user.trim().isEmpty() && !bannedWords.contains(user.toLowerCase());
                })
                .collect(Collectors.groupingBy(t -> t.get("Username")));

        tweetsByUser.entrySet().stream()
                .sorted((e1, e2) -> {
                    long engagement1 = e1.getValue().stream().mapToLong(t -> parseIntSafe(t.getOrDefault("Like_Count", "0")) + parseIntSafe(t.getOrDefault("Retweet_Count", "0"))).sum();
                    long engagement2 = e2.getValue().stream().mapToLong(t -> parseIntSafe(t.getOrDefault("Like_Count", "0")) + parseIntSafe(t.getOrDefault("Retweet_Count", "0"))).sum();
                    return Long.compare(engagement2, engagement1); 
                })
                .limit(8) 
                .forEach(entry -> {
                    String user = entry.getKey();
                    long totalLikes = entry.getValue().stream().mapToLong(t -> parseIntSafe(t.getOrDefault("Like_Count", "0"))).sum();
                    long totalRetweets = entry.getValue().stream().mapToLong(t -> parseIntSafe(t.getOrDefault("Retweet_Count", "0"))).sum();
                    dataset.addValue(totalLikes, "Likes", user);
                    dataset.addValue(totalRetweets, "Retweets", user);
                });

        JFreeChart chart = ChartFactory.createBarChart(
                "Engagement par Utilisateur (Top 8)", "Utilisateur", "Interactions",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        
        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        styleBarChart(plot);
        
        return chart;
    }

    private JFreeChart createUserInteractionChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        Map<String, Integer> userInteractions = tweets.stream()
                .filter(t -> t.get("Username") != null && !bannedWords.contains(t.get("Username").toLowerCase()))
                .collect(Collectors.groupingBy(
                        t -> t.get("Username"),
                        Collectors.summingInt(t -> {
                            try {
                                return parseIntSafe(t.getOrDefault("Reply_Count", "0")) +
                                       parseIntSafe(t.getOrDefault("Retweet_Count", "0")); 
                            } catch (Exception e) {
                                return 0;
                            }
                        })
                ));

        // Get top users, and sum others into an "Autres" category
        final int MAX_PIE_SLICES = 6;
        List<Map.Entry<String, Integer>> sortedInteractions = userInteractions.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());
        
        int othersCount = 0;
        for (int i = 0; i < sortedInteractions.size(); i++) {
            if (i < MAX_PIE_SLICES - 1) { // Leave one slot for 'Autres'
                dataset.setValue(sortedInteractions.get(i).getKey(), sortedInteractions.get(i).getValue());
            } else {
                othersCount += sortedInteractions.get(i).getValue();
            }
        }
        if (othersCount > 0) {
            dataset.setValue("Autres", othersCount);
        } else if (sortedInteractions.isEmpty()) {
            dataset.setValue("Aucune Donnée", 1); // Fallback for no data
        }


        JFreeChart chart = ChartFactory.createPieChart("Interactions par Utilisateur (Top " + (MAX_PIE_SLICES -1) + " + Autres)", dataset, true, true, false);
        stylePieChart(chart);
        return chart;
    }

    private JFreeChart createActiveUsersChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Long> userTweetCounts = tweets.stream()
                .map(t -> t.get("Username"))
                .filter(u -> u != null && !u.trim().isEmpty() && !bannedWords.contains(u.toLowerCase()))
                .collect(Collectors.groupingBy(u -> u, Collectors.counting()));

        userTweetCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .forEach(e -> dataset.addValue(e.getValue(), "Tweets", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Utilisateurs les Plus Actifs (Top 8)", "Utilisateur", "Nombre de tweets",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        
        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        styleBarChart(plot);
        
        return chart;
    }

    // NEW CHART METHOD: Tweets by Location
    private JFreeChart createTweetsByLocationChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Long> locationCounts = tweets.stream()
                .map(t -> t.getOrDefault("Location", "").trim()) // Get location, trim whitespace
                .filter(loc -> !loc.isEmpty() && !loc.equalsIgnoreCase("N/A") && !loc.equalsIgnoreCase("None") && !loc.equalsIgnoreCase("Unknown")) // Filter out empty/irrelevant locations
                .collect(Collectors.groupingBy(loc -> loc, Collectors.counting()));

        locationCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10) // Show top 10 locations
                .forEach(entry -> dataset.addValue(entry.getValue(), "Nombre de Tweets", entry.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Tweets par Localisation (Top 10)", "Localisation", "Nombre de Tweets",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        
        styleChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        styleBarChart(plot); // Reuse bar chart styling
        
        // Adjust label rotation for potentially long location names
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        return chart;
    }


    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        
        TextTitle title = chart.getTitle();
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setPaint(new Color(30, 41, 59));
        title.setPadding(5, 0, 10, 0); 
    }

    private void styleBarChart(CategoryPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false); 
        plot.setRangeGridlinesVisible(true);  
        plot.setRangeGridlinePaint(new Color(241, 245, 249)); 
        plot.setOutlineVisible(false);
        
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 9));
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45); 
        plot.getDomainAxis().setLabelPaint(new Color(51, 65, 85));
        
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 9));
        plot.getRangeAxis().setLabelPaint(new Color(51, 65, 85));
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false); 
        renderer.setItemMargin(0.05);

        // Apply distinct colors for each bar in single-series bar charts
        if (plot.getDataset().getRowCount() == 1) {
            for (int i = 0; i < plot.getDataset().getColumnCount(); i++) {
                renderer.setSeriesPaint(0, MODERN_COLORS[i % MODERN_COLORS.length]);
            }
        } else { // For multi-series (like Engagement chart)
             renderer.setSeriesPaint(0, new GradientPaint(0.0f, 0.0f, MODERN_COLORS[0].brighter(), 0.0f, 0.0f, MODERN_COLORS[0]));
             renderer.setSeriesPaint(1, new GradientPaint(0.0f, 0.0f, MODERN_COLORS[1].brighter(), 0.0f, 0.0f, MODERN_COLORS[1]));
             // Add more series colors if needed
        }
    }

    private void stylePieChart(JFreeChart chart) {
        styleChart(chart);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 10));
        plot.setLabelPaint(new Color(51, 65, 85));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 180)); 
        plot.setLabelOutlinePaint(Color.WHITE);
        plot.setLabelShadowPaint(null);
        plot.setLabelLinkStroke(new BasicStroke(0.5f)); 
        plot.setLabelLinkPaint(new Color(100, 116, 139)); 
        
        for (int i = 0; i < plot.getDataset().getItemCount(); i++) {
            plot.setSectionPaint(i, MODERN_COLORS[i % MODERN_COLORS.length]);
        }
        
        plot.setForegroundAlpha(0.9f); 
        plot.setInteriorGap(0.02);
    }

    public static class TweetSummary {
        private final SimpleStringProperty metric;
        private final SimpleIntegerProperty value;

        public TweetSummary(String metric, int value) {
            this.metric = new SimpleStringProperty(metric);
            this.value = new SimpleIntegerProperty(value);
        }

        public String getMetric() {
            return metric.get();
        }

        public SimpleStringProperty metricProperty() {
            return metric;
        }

        public int getValue() {
            return value.get();
        }

        public SimpleIntegerProperty valueProperty() {
            return value;
        }
    }
}