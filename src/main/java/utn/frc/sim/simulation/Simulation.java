package utn.frc.sim.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.*;
import utn.frc.sim.model.Event;
import utn.frc.sim.model.EventGenerator;
import utn.frc.sim.model.TimeEvent;
import utn.frc.sim.model.clients.Client;
import utn.frc.sim.model.clients.ClientGenerator;
import utn.frc.sim.model.servers.Server;
import utn.frc.sim.model.servers.ServerWithInterruptions;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Simulation {

    private static final Logger logger = LogManager.getLogger(Simulation.class);

    private Server magicCarpet;
    private Queue<Client> magicCarpetQueue;
    private LocalDateTime clock;
    private LocalDateTime dayFirstEvent;
    private LocalDateTime last40MinuteStop;
    private LocalDateTime lastCleaning;
    private boolean is40MinutesBreak;
    private boolean is4HourBreak;
    private ClientGenerator clientGenerator;
    private Events lastEventDescription;
    private Client clientOfEvent;
    private List<EventGenerator> eventGenerators;
    private int day;
    private int limitOfSimulations;


    private Simulation() {
        initSimulation();
    }

    public static Simulation startNewSimulation() {
        return new Simulation();
    }

    private void initSimulation() {
        initListOfEventGenerators();
        initFirstEventOfDay();
        initClientGenerator();
        initEvent();
        initMagicCarpet();
    }

    private void initListOfEventGenerators() {
        eventGenerators = new ArrayList<>();
    }

    private void initMagicCarpet() {
        DistributionRandomGenerator generator = ConstantDistributionGenerator.createOf(1.2);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);
        magicCarpet = new Server("Magic Carpet", timeEvent);
        eventGenerators.add(magicCarpet);
    }

    private void initEvent() {
        lastEventDescription = Events.INICIO;
    }

    private void initFirstEventOfDay() {
        day = 0;
        dayFirstEvent = LocalDateTime.of(2018, 1, 1, 9, 0);
    }

    private void initClientGenerator() {
        DistributionRandomGenerator generator = UniformDistributionGenerator.createOf(135, 225);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);
        LocalDateTime clientsInitial = LocalDateTime.of(2018, 1, 1, 9, 0);
        clientGenerator = new ClientGenerator(clientsInitial, timeEvent);
        eventGenerators.add(clientGenerator);
    }

    public void step() throws SimulationFinishedException {
        clock = getNextEvent();
        handleEventFromFirstEvent(clock);
    }

    private void handleEventFromFirstEvent(LocalDateTime clock) throws SimulationFinishedException {
        if (dayFirstEvent.isEqual(clock)) {

            if (clock.getHour() >= 19) {
                throw new SimulationFinishedException();
            }
            logger.debug("{} - Day start.", clock);
            lastEventDescription = Events.INICIO_DEL_DIA;
            dayFirstEvent = dayFirstEvent.plus(1, ChronoUnit.DAYS);
            clientOfEvent = null;
        } else {
            handleEventFromClientGenerator(clock);
        }
    }

    private void handleEventFromClientGenerator(LocalDateTime clock) {

        if (clientGenerator.isEventFrom(clock)) {
            if(stopIsNeededFor40MinutesBreak()){
                Client newClient = clientGenerator.getNextClient();

            }
            newClient.setInTime(clock);
            if(magicCarpet.isFree()){
                newClient.setServeTime(clock);
            } else {
                magicCarpetQueue.add(newClient);
            }
        } else {
            handleEventFromMagicCarpet(clock);
        }
    }

    private void handleEventFromMagicCarpet(LocalDateTime clock) {
        if (magicCarpet.isEventFrom(clock)) {
            Event event = magicCarpet.getEvent();
            if (event.hasClient()) {
                Client finishedClient = event.getClient();
                logger.info("{} - Magic Carpet finished. Client: {}. ", clock, finishedClient);
                clientOfEvent = finishedClient
            }
        }
    }

    private LocalDateTime getNextEvent() {

        final LocalDateTime firstEvent = dayFirstEvent;

        Optional<LocalDateTime> possibleFirstEvent = eventGenerators.stream()
                .map(EventGenerator::getNextEvent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(localDateTime -> localDateTime.isBefore(firstEvent))
                .min(LocalDateTime::compareTo);

        return possibleFirstEvent.orElse(firstEvent);
    }

    private boolean stopIsNeededFor40MinutesBreak() {
        return ChronoUnit.MINUTES.between(clock, last40MinuteStop) >= 40;
    }

    private boolean stopIsNeededForCleaning() {
        return ChronoUnit.HOURS.between(clock, lastCleaning) >= 4;
    }

    public int getDay() {
        return day;
    }

    public LocalDateTime getClock() {
        return clock;
    }

    public Events getLastEventDescription() {
        return lastEventDescription;
    }

    public ClientGenerator getClientGenerator() {
        return clientGenerator;
    }

    public Optional<Client> getClientOfEvent() {
        return Optional.ofNullable(clientOfEvent);
    }
}
