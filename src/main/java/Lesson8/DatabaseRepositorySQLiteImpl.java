package Lesson8;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class DatabaseRepositorySQLiteImpl {

    static {
        try{
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    String filename = null;
    String createTableQuery = "CREATE TABLE IF NOT EXISTS weather (\n" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "city TEXT NOT NULL,\n" +
            "date_time TEXT NOT NULL,\n" +
            "weather_text TEXT NOT NULL,\n" +
            "temperature REAL NOT NULL\n" +
            ")";
    String selectWeatherQuery = "SELECT id, city, date_time, weather_text, temperature FROM weather";
    String insertWeatherQuery = "INSERT INTO weather (city, date_time, weather_text, temperature) VALUES (?,?,?,?)";

    public DatabaseRepositorySQLiteImpl() {
        filename = WeatherService.getInstance().getDbFileName();
    }

    private Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
        return connection;
    }

    public void setCreateTableIfNotExists() {
        try (Connection connection = getConnection()) {
            connection.createStatement().execute(createTableQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveWeatherData(DailyForecast data) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement saveWeather = connection.prepareStatement(insertWeatherQuery)) {
            saveWeather.setString(1, WeatherService.getCity());
            saveWeather.setString(2, data.Date);
            saveWeather.setString(3, data.Day.IconPhrase);
            saveWeather.setDouble(4, data.Temperature.Maximum.Value);
            saveWeather.execute();
            saveWeather.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

        public void getAllSavedData() throws IOException {
        try (Connection connection = getConnection();
             PreparedStatement selectWeather = connection.prepareStatement(selectWeatherQuery)) {
            ResultSet rs = selectWeather.executeQuery();
            //System.out.println(rs.getString(2));
            //System.out.println(rs.getString(3));
            //System.out.println(rs.getString(4));
            //System.out.println(rs.getString(5));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }


}
