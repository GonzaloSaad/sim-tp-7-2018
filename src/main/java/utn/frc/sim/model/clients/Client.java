package utn.frc.sim.model.clients;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Client {
    public static final String DEFAULT_NONE = "-";
    private final int clientNumber;
    private ClientState state;
    private LocalDateTime inTime;
    private LocalDateTime serveTime;
    private LocalDateTime outTime;


    public Client(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public String getClientNumberString(){
        return Integer.toString(clientNumber);
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public String getInTimeString(){
        return inTime == null? DEFAULT_NONE : inTime.toString();
    }

    public LocalDateTime getServeTime() {
        return serveTime;
    }

    public void setServeTime(LocalDateTime serveTime) {
        this.serveTime = serveTime;
    }

    public String getServeTimeString(){
        return serveTime == null? DEFAULT_NONE : serveTime.toString();
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public String getOutTimeString(){
        return outTime == null? DEFAULT_NONE : outTime.toString();
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public String getStateString(){
        return state == null ? DEFAULT_NONE : state.toString();
    }

    public long getSecondsOfAttention() {
        if (inTime == null || outTime == null) {
            throw new IllegalStateException();
        }
        return ChronoUnit.SECONDS.between(inTime, outTime);
    }

    public long getSecondsOfWaiting() {
        if (inTime == null || serveTime == null) {
            throw new IllegalStateException();
        }
        return ChronoUnit.SECONDS.between(inTime, serveTime);
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientNumber=" + clientNumber +
                ", inTime=" + inTime +
                ", outTime=" + outTime +
                '}';
    }
}
