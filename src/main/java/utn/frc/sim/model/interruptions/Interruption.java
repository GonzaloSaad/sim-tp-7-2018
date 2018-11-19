package utn.frc.sim.model.interruptions;

import utn.frc.sim.model.Event;
import utn.frc.sim.model.EventGenerator;
import utn.frc.sim.model.TimeEvent;

import java.time.LocalDateTime;
import java.util.Optional;

public class Interruption implements EventGenerator {
    private LocalDateTime nextInterruption;
    private LocalDateTime nextEnd;
    private TimeEvent timeEventForBetween;
    private TimeEvent timeEventForDuration;
    private InterruptionState state;


    public Interruption(LocalDateTime initTime, TimeEvent timeEventForBetween, TimeEvent timeEventForDuration) {
        this.nextInterruption = timeEventForBetween.calculateNextEventFromRandom(initTime);
        this.timeEventForBetween = timeEventForBetween;
        this.timeEventForDuration = timeEventForDuration;
        this.state = InterruptionState.NOT_ACTIVE;
    }

    public Event<InterruptionState> getEvent() {
        if (isActive()) {
            state = InterruptionState.NOT_ACTIVE;
        } else {
            state = InterruptionState.ACTIVE;
        }
        return new Event<>(state);
    }

    private boolean isActive() {
        return state == InterruptionState.ACTIVE;
    }

    public void calculateNextEnd(LocalDateTime timeFrom) {
        nextEnd = timeEventForDuration.calculateNextEventFromRandom(timeFrom);
    }

    public void calculateNextInterruption(LocalDateTime clock){
        nextInterruption = timeEventForBetween.calculateNextEventFromRandom(clock);
    }

    public void stopInterruptions() {
        nextEnd = null;
    }

    @Override
    public boolean isEventFrom(LocalDateTime clock) {
        if (isActive()){
            return nextEnd !=null && nextEnd.equals(clock);
        }
        return nextInterruption != null && nextInterruption.equals(clock);
    }

    @Override
    public Optional<LocalDateTime> getNextInterruption() {
        if (isActive()){
            return Optional.ofNullable(nextEnd);
        }
        return Optional.ofNullable(nextInterruption);
    }
}
