package m.khokhlov.configuration;

import ca.watier.echechessengine.game.GameConstraints;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfiguration {
    @Bean
    public GameConstraints gameConstraints() {
        return new GameConstraints();
    }
}
