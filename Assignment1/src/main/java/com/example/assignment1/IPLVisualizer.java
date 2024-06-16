package com.example.assignment1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IPLVisualizer extends Application {

    private Stage stage;
    private Scene tableScene, pieChartScene;
    private TableView<PlayerSeasonStats> tableView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Load icon
        Image icon = new Image(getClass().getResourceAsStream("/IPL-10-Logo-PNG.png"));
        stage.getIcons().add(icon);

        // Connect to MySQL database (replace with your database credentials)
        String url = "jdbc:mysql://localhost:3306/IPL2023";
        String user = "root";
        String password = "Dikita@0623";
        Connection conn = DriverManager.getConnection(url, user, password);

        // Set up TableView for player season stats
        tableView = createTableView(conn);

        // Set up PieChart for player total runs
        PieChart pieChart = createPieChart(conn);

        // Button to switch to PieChart scene
        Button switchToPieChartButton = new Button("Switch to Pie Chart");
        switchToPieChartButton.setOnAction(e -> stage.setScene(pieChartScene));

        // Button to switch back to TableView scene
        Button switchToTableViewButton = new Button("Back to Table View");
        switchToTableViewButton.setOnAction(e -> stage.setScene(tableScene));

        // Layout for TableView scene
        VBox tableLayout = new VBox(10);
        tableLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        tableLayout.getChildren().addAll(tableView, switchToPieChartButton);
        tableScene = new Scene(tableLayout, 800, 600);

        // Layout for PieChart scene
        VBox pieChartLayout = new VBox(10);
        pieChartLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        pieChartLayout.getChildren().addAll(pieChart, switchToTableViewButton);
        pieChartScene = new Scene(pieChartLayout, 800, 600);

        // Close database connection
        conn.close();

        // Set initial scene
        stage.setScene(tableScene);
        stage.setTitle("IPL 2023 Player Statistics");
        stage.show();
    }

    private TableView<PlayerSeasonStats> createTableView(Connection conn) throws Exception {
        TableView<PlayerSeasonStats> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<PlayerSeasonStats, String> playerNameColumn = new TableColumn<>("Player Name");
        TableColumn<PlayerSeasonStats, String> teamNameColumn = new TableColumn<>("Team Name");
        TableColumn<PlayerSeasonStats, Integer> totalRunsColumn = new TableColumn<>("Total Runs");
        TableColumn<PlayerSeasonStats, Integer> matchesPlayedColumn = new TableColumn<>("Matches Played");
        TableColumn<PlayerSeasonStats, Float> averageScoreColumn = new TableColumn<>("Average Score");

        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        totalRunsColumn.setCellValueFactory(new PropertyValueFactory<>("totalRuns"));
        matchesPlayedColumn.setCellValueFactory(new PropertyValueFactory<>("matchesPlayed"));
        averageScoreColumn.setCellValueFactory(new PropertyValueFactory<>("averageScore"));

        tableView.getColumns().addAll(playerNameColumn, teamNameColumn, totalRunsColumn, matchesPlayedColumn, averageScoreColumn);

        // Query data from database
        String query = "SELECT p.player_name, t.team_name, ps.total_runs, ps.matches_played, ps.average_score " +
                "FROM PlayerSeasonStats ps " +
                "JOIN Players p ON ps.player_id = p.player_id " +
                "JOIN Teams t ON p.team_id = t.team_id " +
                "WHERE ps.season = 2023";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Populate table with data
        List<PlayerSeasonStats> dataList = new ArrayList<>();
        while (resultSet.next()) {
            String playerName = resultSet.getString("player_name");
            String teamName = resultSet.getString("team_name");
            int totalRuns = resultSet.getInt("total_runs");
            int matchesPlayed = resultSet.getInt("matches_played");
            float averageScore = resultSet.getFloat("average_score");
            PlayerSeasonStats data = new PlayerSeasonStats(playerName, teamName, totalRuns, matchesPlayed, averageScore);
            dataList.add(data);
        }

        tableView.getItems().addAll(dataList);

        // Close database connection
        resultSet.close();
        stmt.close();

        return tableView;
    }

    private PieChart createPieChart(Connection conn) throws Exception {
        // Query data from database
        String query = "SELECT p.player_name, ps.total_runs " +
                "FROM PlayerSeasonStats ps " +
                "JOIN Players p ON ps.player_id = p.player_id " +
                "WHERE ps.season = 2023";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Prepare data for pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        while (resultSet.next()) {
            String playerName = resultSet.getString("player_name");
            double totalRuns = resultSet.getDouble("total_runs");
            pieChartData.add(new PieChart.Data(playerName, totalRuns));
        }

        // Close database connection
        resultSet.close();
        stmt.close();

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Total Runs by Player in IPL 2023");

        return pieChart;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // PlayerSeasonStats class for TableView
    public static class PlayerSeasonStats {
        private final String playerName;
        private final String teamName;
        private final int totalRuns;
        private final int matchesPlayed;
        private final float averageScore;

        public PlayerSeasonStats(String playerName, String teamName, int totalRuns, int matchesPlayed, float averageScore) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.totalRuns = totalRuns;
            this.matchesPlayed = matchesPlayed;
            this.averageScore = averageScore;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getTeamName() {
            return teamName;
        }

        public int getTotalRuns() {
            return totalRuns;
        }

        public int getMatchesPlayed() {
            return matchesPlayed;
        }

        public float getAverageScore() {
            return averageScore;
        }
    }
}
