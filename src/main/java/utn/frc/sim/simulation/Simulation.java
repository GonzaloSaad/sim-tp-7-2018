package utn.frc.sim.simulation;

import com.sun.org.apache.xpath.internal.operations.Bool;
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
        initMagicCarpetQueue();
        initGlobalParameters();

    }

    private void initMagicCarpetQueue() {
        magicCarpetQueue = new LinkedList<>();
    }

    private void initGlobalParameters() {
        is4HourBreak = false;
        is40MinutesBreak = false;

    }

    private void initListOfEventGenerators() {
        eventGenerators = new ArrayList<>();
    }

    private void initMagicCarpet() {
        DistributionRandomGenerator generator = ConstantDistributionGenerator.createOf(1.2);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);

        DistributionRandomGenerator generatorOfInterruption = ConstantDistributionGenerator.createOf(20);
        TimeEvent timeEventOfInterruptions = TimeEvent.create(generatorOfInterruption, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        magicCarpet = new ServerWithInterruptions("Magic Carpet", timeEvent, 4, dayFirstEvent, timeEventOfInterruptions);
        eventGenerators.add(magicCarpet);
    }

    private void initEvent() {
        lastEventDescription = Events.INICIO;
    }

    private void initFirstEventOfDay() {
        dayFirstEvent = LocalDateTime.of(2018, 1, 1, 9, 0);
        last40MinuteStop = dayFirstEvent;
    }

    private void initClientGenerator() {
        DistributionRandomGenerator generator = UniformDistributionGenerator.createOf(135, 225);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);
        clientGenerator = new ClientGenerator(dayFirstEvent, timeEvent);
        eventGenerators.add(clientGenerator);
    }

    public void step() throws SimulationFinishedException {
        clock = getNextEvent();
        handleEventFromFirstEvent(clock);
    }

    private void handleEventFromFirstEvent(LocalDateTime clock) throws SimulationFinishedException {

        if (clock.getHour() >= 19) {
            throw new SimulationFinishedException();
        }


        if (dayFirstEvent.isEqual(clock)) {
            logger.info("{} - Day start.", clock);
            lastEventDescription = Events.INICIO_DEL_DIA;
            dayFirstEvent = dayFirstEvent.plus(1, ChronoUnit.DAYS);
            clientOfEvent = null;
        } else {
            handleEventFromClientGenerator(clock);
        }
    }

    private void handleEventFromClientGenerator(LocalDateTime clock) {

        if (clientGenerator.isEventFrom(clock)) {

            boolean generateNewClient = Boolean.TRUE;
            if (stopIsNeededFor40MinutesBreak()) {
                is40MinutesBreak = true;
                last40MinuteStop = clock;
                generateNewClient = false;
            }
            Client newClient = clientGenerator.getNextClient(generateNewClient);
            newClient.setInTime(clock);

            logger.info("{} - New client into the system. Client: {}.", clock, newClient);
            if (!generateNewClient) {
                logger.warn("The 40 minutes break is active. No clients for the proximate time. Actual queue: {}.", magicCarpetQueue.size());
            }


            if (magicCarpet.isFree()) {
                newClient.setServeTime(clock);
                magicCarpet.serveToClient(clock, newClient);
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
                finishedClient.setOutTime(clock);
                logger.info("{} - Magic Carpet finished. Client: {}. ", clock, finishedClient);
                clientOfEvent = finishedClient;
            } else {
                logger.info("{} - Magic Carpet finished. Just cleaning. ", clock);
            }

            if (!magicCarpetQueue.isEmpty() && magicCarpet.isFree()) {
                Client firstClient = magicCarpetQueue.poll();
                firstClient.setServeTime(clock);
                magicCarpet.serveToClient(clock, firstClient);
            } else if (is40MinutesBreak) {
                logger.warn("The 40 minutes break finished. No clients in the queue. New clients will arrive.");
                is40MinutesBreak = false;
                clientGenerator.forceNewNextEventFromClock(clock);
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
        return ChronoUnit.MINUTES.between(last40MinuteStop, clock) >= 40;
    }

    public Server getMagicCarpet() {
        return magicCarpet;
    }

    public Queue<Client> getMagicCarpetQueue() {
        return magicCarpetQueue;
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
