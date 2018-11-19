package utn.frc.sim.model.servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.model.Event;
import utn.frc.sim.model.TimeEvent;
import utn.frc.sim.model.clients.Client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ServerWithInterruptions extends Server {

    private static final Logger logger = LogManager.getLogger(ServerWithInterruptions.class);
    private final int interruptPeriod;
    private LocalDateTime lastCleaning;
    private LocalDateTime last40MinuteStop;
    private TimeEvent interruptEvent;

    public ServerWithInterruptions(String serverName, TimeEvent timeEvent, int hours, LocalDateTime init, TimeEvent interruptEvent) {
        super(serverName, timeEvent);
        this.interruptPeriod = hours;
        this.lastCleaning = init;
        this.last40MinuteStop = init;
        this.interruptEvent = interruptEvent;
    }

    @Override
    public Event getEvent() {
        Event<Client> event;
        if (getState() == ServerState.OCP) {
            event = new Event<>(getServingClient().orElse(null));
            if (stopIsNeededForCleaning()) {
                logger.info("{}: {} hours without interruptions. Next event is cleaning.",
                        getServerName(),
                        interruptPeriod);
                lastCleaning = getNextEnd();

                setNextEnd(interruptEvent.calculateNextEventFromRandom(lastCleaning));
                last40MinuteStop = getNextEnd();
                setState(ServerState.OUT);

            } else if (stopIsNeededFor40MinutesBreak()) {
                logger.info("{}: Break de 8 minutos.",
                        getServerName());
                last40MinuteStop = getNextEnd();
                setNextEnd(getNextEnd().plus(8, ChronoUnit.MINUTES));
                setState(ServerState.OUT_2);
            } else {
                setNextEnd(null);
                setState(ServerState.LBR);
            }

        } else {
            setNextEnd(null);
            setServingClient(null);
            setState(ServerState.LBR);
            event = new Event<>();
        }
        setServingClient(null);
        return event;
    }

    private boolean stopIsNeededForCleaning() {
        return ChronoUnit.HOURS.between(lastCleaning, getNextEnd()) >= interruptPeriod;
    }

    private boolean stopIsNeededFor40MinutesBreak() {
        return ChronoUnit.MINUTES.between(last40MinuteStop, getNextEnd()) >= 40;
    }
}
