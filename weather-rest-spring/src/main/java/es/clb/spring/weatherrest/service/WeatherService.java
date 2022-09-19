package es.clb.spring.weatherrest.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.clb.spring.weatherrest.model.Weather;

@Service
public class WeatherService {

  private static String weatherApi = "https://api.open-meteo.com/v1/forecast";

  public ResponseEntity<Weather> getWeather(
      Double latitude,
      Double longitude,
      String hourly,
      Boolean current) {

    RestTemplate restTemplate = new RestTemplate();

    String url = UriComponentsBuilder.fromHttpUrl(WeatherService.weatherApi)
        .queryParam("latitude", latitude)
        .queryParam("longitude", longitude)
        .queryParam("hourly", hourly)
        .queryParam("current_weather", current)
        .encode()
        .toUriString();

    return restTemplate.getForEntity(url, Weather.class);

  }

}
