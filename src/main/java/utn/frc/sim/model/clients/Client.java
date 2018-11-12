package utn.frc.sim.model.clients;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Client {
    private final int clientNumber;
    private LocalDateTime inTime;
    private LocalDateTime serveTime;
    private LocalDateTime outTime;


    public Client(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getServeTime() {
        return serveTime;
    }

    public void setServeTime(LocalDateTime serveTime) {
        this.serveTime = serveTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public long getMinutesOfAttention() {
        if (inTime == null || outTime == null) {
            throw new IllegalStateException();
        }
        return ChronoUnit.MILLIS.between(inTime, outTime);
    }

    public long getMinutesOfWaiting() {
        if (inTime == null || serveTime == null) {
            throw new IllegalStateException();
        }
        return ChronoUnit.MILLIS.between(inTime, serveTime);
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
