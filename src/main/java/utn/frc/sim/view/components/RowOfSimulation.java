package utn.frc.sim.view.components;

import utn.frc.sim.simulation.SimulationWrapper;

public class RowOfSimulation {
    private String clock;
    private String event;
    private String clientOfEvent;
    private String nextClient;
    private String magicCarpetState;
    private String magicCarpetClient;
    private String magicCarpetNextEnd;
    private String magicCarpetQueue;

    public RowOfSimulation(SimulationWrapper wrapper) {
        extractParametersOfSimulation(wrapper);
    }

    private void extractParametersOfSimulation(SimulationWrapper wrapper) {
        clock = wrapper.getClock();
        event = wrapper.getLastEvent();
        clientOfEvent = wrapper.getClientOfEventNumber();
        nextClient = wrapper.getNextClientEvent();
        magicCarpetState = wrapper.getMagicCarpetState();
        magicCarpetClient = wrapper.getMagicCarpetClient();
        magicCarpetNextEnd = wrapper.getMagicCarpetNextEvent();
        magicCarpetQueue = wrapper.getMagicCarpetQueueSize();
    }

    public String getClock() {
        return clock;
    }

    public String getEvent() {
        return event;
    }

    public String getClientOfEvent() {
        return clientOfEvent;
    }

    public String getNextClient() {
        return nextClient;
    }

    public String getMagicCarpetState() {
        return magicCarpetState;
    }

    public String getMagicCarpetClient() {
        return magicCarpetClient;
    }

    public String getMagicCarpetNextEnd() {
        return magicCarpetNextEnd;
    }

    public String getMagicCarpetQueue() {
        return magicCarpetQueue;
    }

}
