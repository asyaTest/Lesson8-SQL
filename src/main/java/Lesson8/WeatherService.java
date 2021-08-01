package Lesson8;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.sql.SQLException;

public class WeatherService {
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECAST = "forecasts";
    private static final String API_VERSION = "v1";
    private static final String FORECAST_TYPE = "daily";
    private static final String FORECAST_PERIOD = "5day";

    private static final String SAINT_PETERSBURG_KEY = "2-295212_25_al";
    private static final String CITY = "Санкт-Петербург";
    private static final String API_KEY = "r4o62eCcc0ipvt0T6ldWhDTazL5Kqee2";
    private static WeatherService _instance;

    public static void main(String[] args) {

        OkHttpClient client = new OkHttpClient();

        //Сегментированное построенние URL
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(FORECAST)
                .addPathSegment(API_VERSION)
                .addPathSegment(FORECAST_TYPE)
                .addPathSegment(FORECAST_PERIOD)
                .addPathSegment(SAINT_PETERSBURG_KEY)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("metric", "true")
                .build();

        System.out.println(url.toString());

        //При необходимости указать заголовки
        Request requesthttp = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(url)
                .build();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String jsonResponse = client.newCall(requesthttp).execute().body().string();
            WeatherResponse weatherResponse = objectMapper.readValue(jsonResponse, WeatherResponse.class);
            DatabaseRepositorySQLiteImpl repository = new DatabaseRepositorySQLiteImpl();
            repository.setCreateTableIfNotExists();
            for (int index = 0; index < weatherResponse.DailyForecasts.length; index++) {
                DailyForecast item = weatherResponse.DailyForecasts[index];
                repository.saveWeatherData(item);
                System.out.println("В городе " + CITY + " на дату " + item.Date + " ожидается " + item.Day.IconPhrase + " Минимальная температура " + item.Temperature.Minimum.Value + " Максимальная температура " + item.Temperature.Maximum.Value);

                // System.out.print("В городе " + CITY + "на дату " + DATE + "ожидается " + item.Day.IconPhrase
                // + "температура " + TEMPERATURE);
            }

            repository.getAllSavedData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //http://dataservice.accuweather.com/forecasts/v1/daily/5day/2-295212_25_al?apikey=r4o62eCcc0ipvt0T6ldWhDTazL5Kqee2&language=ru-ru
    }

    public static WeatherService getInstance() {
        if (_instance == null) {
            _instance = new WeatherService();
        }

        return _instance;
    }

    public static String getCity() {
        return CITY;
    }

    public String getDbFileName() {
        return "/Users/anastasia/IdeaProjects/Lesson8-SQL/Lesson8.db";
    }
}