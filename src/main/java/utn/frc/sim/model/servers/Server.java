package utn.frc.sim.model.servers;

import utn.frc.sim.model.Event;
import utn.frc.sim.model.EventGenerator;
import utn.frc.sim.model.TimeEvent;
import utn.frc.sim.model.clients.Client;

import java.time.LocalDateTime;
import java.util.Optional;

public class Server implements EventGenerator {

    private final String serverName;
    private LocalDateTime nextEnd;
    private Client servingClient;
    private TimeEvent timeEvent;
    private ServerState state;

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

    public Event<Client> getEvent() {
        Event<Client> event = new Event<>(servingClient);
        state = ServerState.LBR;
        nextEnd = null;
        servingClient = null;
        return event;
    }



    public Optional<Client> getServingClient() {
        return Optional.ofNullable(servingClient);
    }

    public String getServerName() {
        return serverName;
    }

    public LocalDateTime getNextEnd() {
        return nextEnd;
    }

    public void setNextEnd(LocalDateTime nextEnd) {
        this.nextEnd = nextEnd;
    }

    public void setServingClient(Client servingClient) {
        this.servingClient = servingClient;
    }

    public TimeEvent getTimeEvent() {
        return timeEvent;
    }

    public void setTimeEvent(TimeEvent timeEvent) {
        this.timeEvent = timeEvent;
    }

    public ServerState getState() {
        return state;
    }

    public void setState(ServerState state) {
        this.state = state;
    }

    public void clean(){
        setState(ServerState.OUT);
    }

    public void smallBreak(){
        setState(ServerState.OUT_2);
    }

    public void free(){
        setState(ServerState.LBR);
    }



    public boolean isFree() {
        return state == ServerState.LBR;
    }

    @Override
    public Optional<LocalDateTime> getNextInterruption() {
        return Optional.ofNullable(nextEnd);
    }

    @Override
    public boolean isEventFrom(LocalDateTime clock) {
        return nextEnd != null && nextEnd.equals(clock);
    }

}
