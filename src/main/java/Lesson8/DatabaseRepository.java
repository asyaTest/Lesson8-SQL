package Lesson8;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseRepository {

    boolean saveWeatherData(WeatherService weatherService) throws SQLException;

    List<WeatherService> getAllSavedData() throws IOException;
}
