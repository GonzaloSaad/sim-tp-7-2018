package utn.frc.sim.model.servers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.model.Event;
import utn.frc.sim.model.TimeEvent;

import java.time.LocalDateTime;

public class ServerWithInterruptions extends Server {

    private static final Logger logger = LogManager.getLogger(ServerWithInterruptions.class);
    private DistributionRandomGenerator generator;
    private final int interruptPeriod;
    private int clientsWithoutInterruptions;
    private LocalDateTime lastCleaning;

    public ServerWithInterruptions(String serverName, TimeEvent timeEvent, int , LocalDateTime init) {
        super(serverName, timeEvent);
        this.interruptPeriod = ;
        this.
    }

    @Override
    public Event getEvent() {
        Event event;
        if (state == ServerState.OCP) {
            event = new Event(servingClient);
            clientsWithoutInterruptions++;
            if (clientsWithoutInterruptions == interruptPeriod) {
                logger.info("{}: {} clients without interruptions. Next event is calibration.",
                        getServerName(),
                        clientsWithoutInterruptions);
                nextEnd = timeEvent.calculateNextEventFromRandom(nextEnd);
                state = ServerState.OUT;
            } else {
                logger.info("{}: {} clients without interruptions.", getServerName(), clientsWithoutInterruptions);
                nextEnd = null;
                state = ServerState.LBR;
            }

        } else {
            clientsWithoutInterruptions = 0;
            nextEnd = null;
            servingClient = null;
            state = ServerState.LBR;
            event = new Event();
        }
        servingClient = null;
        return event;
    }
}
