package utn.frc.sim.model;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventGenerator {
    boolean isEventFrom(LocalDateTime clock);
    Optional<LocalDateTime> getNextEvent();
}
