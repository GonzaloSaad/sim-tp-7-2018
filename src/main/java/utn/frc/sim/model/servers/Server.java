package utn.frc.sim.model.servers;

import utn.frc.sim.model.Event;
import utn.frc.sim.model.EventGenerator;
import utn.frc.sim.model.TimeEvent;
import utn.frc.sim.model.clients.Client;

import java.time.LocalDateTime;
import java.util.Optional;

public class Server implements EventGenerator {

    private final String serverName;
    protected LocalDateTime nextEnd;
    protected Client servingClient;
    protected TimeEvent timeEvent;
    protected ServerState state;

    public Server(String serverName, TimeEvent timeEvent) {
        this.serverName = serverName;
        this.timeEvent = timeEvent;
        this.state = ServerState.LBR;
    }

    public void serveToClient(LocalDateTime clock, Client client) {
        servingClient = client;
        nextEnd = timeEvent.calculateNextEventFromRandom(clock);
        state = ServerState.OCP;
    }

    public Event getEvent() {
        Event event = new Event(servingClient);
        state = ServerState.LBR;
        nextEnd = null;
        servingClient = null;
        return event;
    }



    public Optional<Client> getServingClient() {
        return Optional.ofNullable(servingClient);
    }

    public ServerState getState() {
        return state;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isFree() {
        return state == ServerState.LBR;
    }

    @Override
    public Optional<LocalDateTime> getNextEvent() {
        return Optional.ofNullable(nextEnd);
    }

    @Override
    public boolean isEventFrom(LocalDateTime clock) {
        return nextEnd != null && nextEnd.equals(clock);
    }

}
