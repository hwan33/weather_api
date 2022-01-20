package weatherAPI.weatherAPI.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import weatherAPI.weatherAPI.entity.Weather;

@Repository
public class WeatherRepository {

    @PersistenceContext
    EntityManager em;

    public void save(Weather weather) {
        em.persist(weather);
    }
}
