package weatherAPI.weatherAPI.service;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import weatherAPI.weatherAPI.entity.Weather;
import weatherAPI.weatherAPI.repository.WeatherRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public void upload(List<Weather> weatherList) {
        for (Weather weather : weatherList) {
            weatherRepository.save(weather);
        }
    }
}
