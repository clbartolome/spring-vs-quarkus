package es.clb.spring.weatherrest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.clb.spring.weatherrest.model.Weather;
import es.clb.spring.weatherrest.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

  @Autowired
  private WeatherService weatherService;

  @GetMapping("/madrid")
  public ResponseEntity<Weather> route() {
    return weatherService.getWeather(40.4167,-3.7033,"temperature_2m", true);
  }








  
}
