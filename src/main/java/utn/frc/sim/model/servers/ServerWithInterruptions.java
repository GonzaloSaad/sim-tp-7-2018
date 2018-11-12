package utn.frc.sim.model.servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.model.Event;
import utn.frc.sim.model.TimeEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ServerWithInterruptions extends Server {

    private static final Logger logger = LogManager.getLogger(ServerWithInterruptions.class);
    private final int interruptPeriod;
    private LocalDateTime lastCleaning;
    private TimeEvent interruptEvent;

    public ServerWithInterruptions(String serverName, TimeEvent timeEvent, int hours, LocalDateTime init, TimeEvent interruptEvent) {
        super(serverName, timeEvent);
        this.interruptPeriod = hours;
        this.lastCleaning = init;
        this.interruptEvent = interruptEvent;
    }

    @Override
    public Event getEvent() {
        Event event;
        if (state == ServerState.OCP) {
            event = new Event(servingClient);
            if (stopIsNeededForCleaning(nextEnd)) {
                logger.info("{}: {} hours without interruptions. Next event is cleaning.",
                        getServerName(),
                        interruptPeriod);
                nextEnd = timeEvent.calculateNextEventFromRandom(nextEnd);
                state = ServerState.OUT;
            } else {
                nextEnd = null;
                state = ServerState.LBR;
            }

        } else {
            nextEnd = null;
            servingClient = null;
            state = ServerState.LBR;
            event = new Event();
        }
        servingClient = null;
        return event;
    }

    private boolean stopIsNeededForCleaning(LocalDateTime clock) {
        return ChronoUnit.HOURS.between(clock, lastCleaning) >= interruptPeriod;
    }
}
