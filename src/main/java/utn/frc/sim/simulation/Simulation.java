package utn.frc.sim.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.*;
import utn.frc.sim.model.Event;
import utn.frc.sim.model.EventGenerator;
import utn.frc.sim.model.TimeEvent;
import utn.frc.sim.model.clients.Client;
import utn.frc.sim.model.clients.ClientGenerator;
import utn.frc.sim.model.clients.ClientState;
import utn.frc.sim.model.interruptions.Interruption;
import utn.frc.sim.model.interruptions.InterruptionState;
import utn.frc.sim.model.servers.Server;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Simulation {

    private static final Logger logger = LogManager.getLogger(Simulation.class);


    private LocalDateTime clock;
    private LocalDateTime dayFirstEvent;

    private ClientGenerator clientGenerator;

    private Server magicCarpet;
    private Queue<Client> magicCarpetQueue;

    private Interruption interruptionOf40Minutes;
    private Interruption interruptionOf4Hours;

    private boolean is40MinutesBreakPending;
    private boolean is4HourBreakPending;

    private Events lastEventDescription;
    private Client clientOfEvent;
    private List<EventGenerator> eventGenerators;
    private double maxDurationInQueue;
    private Client clientOfMaxDuration;
    private int maxAmountInQueue;
    private List<Client> clients;

    private Simulation() {
        initSimulation();
    }

    public static Simulation startNewSimulation() {
        return new Simulation();
    }

    private void initSimulation() {
        initGlobalParameters();
        initListOfEventGenerators();
        initFirstEventOfDay();
        initEventDescription();
        initClientGenerator();
        initMagicCarpet();
        initMagicCarpetQueue();
        initInterruptionOf40Minutes();
        initInterruptionOf4Hours();
        initStatistics();
        startClientList();
    }

    private void initInterruptionOf40Minutes() {
        DistributionRandomGenerator generator40Minutes = ConstantDistributionGenerator.createOf(40);
        TimeEvent timeEventOf40Minutes = TimeEvent.create(generator40Minutes, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        DistributionRandomGenerator generatorOfDuration = ConstantDistributionGenerator.createOf(8);
        TimeEvent timeEventOfDuration = TimeEvent.create(generatorOfDuration, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        interruptionOf40Minutes = new Interruption(dayFirstEvent, timeEventOf40Minutes, timeEventOfDuration);
        eventGenerators.add(interruptionOf40Minutes);
    }

    private void initInterruptionOf4Hours() {
        DistributionRandomGenerator generator40Minutes = ConstantDistributionGenerator.createOf(4 * 60);
        TimeEvent timeEventOf4Hours = TimeEvent.create(generator40Minutes, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        DistributionRandomGenerator generatorOfDuration = ConstantDistributionGenerator.createOf(20);
        TimeEvent timeEventOfDuration = TimeEvent.create(generatorOfDuration, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        interruptionOf4Hours = new Interruption(dayFirstEvent, timeEventOf4Hours, timeEventOfDuration);
        eventGenerators.add(interruptionOf4Hours);
    }

    private void initGlobalParameters() {
        is40MinutesBreakPending = false;
        is4HourBreakPending = false;
    }

    private void initFirstEventOfDay() {
        dayFirstEvent = LocalDateTime.of(2018, 1, 1, 9, 0);
    }

    private void initEventDescription() {
        lastEventDescription = Events.INICIO;
    }

    private void initClientGenerator() {
        DistributionRandomGenerator generator = UniformDistributionGenerator.createOf(135, 225);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);
        clientGenerator = new ClientGenerator(dayFirstEvent, timeEvent);
        eventGenerators.add(clientGenerator);
    }

    private void initMagicCarpet() {
        DistributionRandomGenerator generator = ConstantDistributionGenerator.createOf(1.2);
        TimeEvent timeEvent = TimeEvent.create(generator, ChronoUnit.SECONDS, ChronoUnit.MILLIS);

        DistributionRandomGenerator generatorOfInterruption = ConstantDistributionGenerator.createOf(20);
        TimeEvent timeEventOfInterruptions = TimeEvent.create(generatorOfInterruption, ChronoUnit.MINUTES, ChronoUnit.SECONDS);

        magicCarpet = new Server("Magic Carpet", timeEvent);
        eventGenerators.add(magicCarpet);
    }

    private void initMagicCarpetQueue() {
        magicCarpetQueue = new LinkedList<>();
    }

    private void initStatistics() {
        maxAmountInQueue = 0;
        maxDurationInQueue = 0;
    }

    private void initListOfEventGenerators() {
        eventGenerators = new ArrayList<>();
    }

    private void startClientList() {
        clients = new ArrayList<>();
    }

    public void step() throws SimulationFinishedException {
        clock = getNextEvent();
        handleEventFromFirstEvent(clock);
    }

    private void handleEventFromFirstEvent(LocalDateTime clock) throws SimulationFinishedException {

        if (clock.getHour() >= 19) {
            throw new SimulationFinishedException();
        }
        calculateMaxAmountOfQueue();
        if (dayFirstEvent.isEqual(clock)) {
            logger.info("{} - Day start.", clock);
            lastEventDescription = Events.INICIO_DEL_DIA;
            dayFirstEvent = dayFirstEvent.plus(1, ChronoUnit.DAYS);
            clientOfEvent = null;
        } else {
            handleEventFrom40MinutesInterruption(clock);
        }
    }

    private void handleEventFrom40MinutesInterruption(LocalDateTime clock) {
        if (interruptionOf40Minutes.isEventFrom(clock)) {
            InterruptionState state = interruptionOf40Minutes.getEvent().getComponent();
            if (state == InterruptionState.ACTIVE) {

                logger.info("{} - Break is needed.", clock);
                lastEventDescription = Events.INICIO_BREAK;
                clientOfEvent = null;
                interruptionOf40Minutes.calculateNextInterruption(clock);

                if (magicCarpet.isFree()) {
                    logger.info("Break has started. Magic carpet was free.");
                    magicCarpet.smallBreak();
                    is40MinutesBreakPending = false;
                    interruptionOf40Minutes.calculateNextEnd(clock);

                } else {
                    logger.info("Break is pending. Magic carpet was not free.");
                    is4HourBreakPending = true;
                }
            } else {

                logger.info("{} - Break has finished.", clock);
                interruptionOf40Minutes.stopInterruptions();
                lastEventDescription = Events.FIN_BREAK;
                clientOfEvent = null;
                magicCarpet.free();

                if(is4HourBreakPending){
                    performPendingCleaning(clock);
                } else {
                    getClientFromQueueAndAssignToCarpet(clock);
                }

            }
        } else {
            handleEventFrom4HoursInterruption(clock);
        }
    }

    private void handleEventFrom4HoursInterruption(LocalDateTime clock) {
        if (interruptionOf4Hours.isEventFrom(clock)) {
            InterruptionState state = interruptionOf4Hours.getEvent().getComponent();
            if (state == InterruptionState.ACTIVE) {

                logger.info("{} - Cleaning is needed.", clock);
                lastEventDescription = Events.INICIO_LIMPIEZA;
                clientOfEvent = null;
                interruptionOf4Hours.calculateNextInterruption(clock);

                if (magicCarpet.isFree()) {
                    logger.info("Cleaning has started. Magic carpet was free.");
                    magicCarpet.smallBreak();
                    is40MinutesBreakPending = false;
                    interruptionOf4Hours.calculateNextEnd(clock);
                } else {
                    logger.info("Cleaning is pending. Magic carpet was not free.");
                    is4HourBreakPending = true;
                }
            } else {

                logger.info("{} - Cleaning has finished.");
                interruptionOf4Hours.stopInterruptions();
                lastEventDescription = Events.FIN_LIMPIEZA;
                clientOfEvent = null;
                magicCarpet.free();

                if(is40MinutesBreakPending){
                    performPendingBreak(clock);
                } else {
                    getClientFromQueueAndAssignToCarpet(clock);
                }
            }
        } else {
            handleEventFromClientGenerator(clock);
        }
    }

    private void handleEventFromClientGenerator(LocalDateTime clock) {

        if (clientGenerator.isEventFrom(clock)) {
            Client newClient = clientGenerator.getNextClient();
            newClient.setInTime(clock);

            clients.add(newClient);

            logger.info("{} - New client into the system. Client: {}.", clock, newClient);

            if (magicCarpet.isFree()) {
                newClient.setServeTime(clock);
                newClient.setState(ClientState.EN_CARPETA);
                magicCarpet.serveToClient(clock, newClient);
            } else {
                newClient.setState(ClientState.COLA_CARPETA);
                magicCarpetQueue.add(newClient);
            }
            lastEventDescription = Events.LLEGADA_CLIENTE;
            clientOfEvent = newClient;
        } else {
            handleEventFromMagicCarpet(clock);
        }
    }

    private void handleEventFromMagicCarpet(LocalDateTime clock) {
        if (magicCarpet.isEventFrom(clock)) {
            Event<Client> event = magicCarpet.getEvent();
            if (event.hasComponent()) {
                Client finishedClient = event.getComponent();
                finishedClient.setOutTime(clock);
                finishedClient.setState(ClientState.TERMINADO);
                calculateMaxDurationInQueue(finishedClient);
                logger.info("{} - Magic Carpet finished. Client: {}. ", clock, finishedClient);
                lastEventDescription = Events.FIN_CARPETA;
                clientOfEvent = finishedClient;

                if (is40MinutesBreakPending) {
                    performPendingBreak(clock);
                } else if (is4HourBreakPending) {
                    performPendingCleaning(clock);
                }
            }

            getClientFromQueueAndAssignToCarpet(clock);
        }
    }

    private void performPendingBreak(LocalDateTime clock) {
        logger.info("Starting break that was pending.");
        magicCarpet.smallBreak();
        interruptionOf40Minutes.calculateNextEnd(clock);
        is40MinutesBreakPending = false;
    }

    private void performPendingCleaning(LocalDateTime clock) {
        logger.info("Starting cleaning that was pending.");
        magicCarpet.clean();
        interruptionOf4Hours.calculateNextEnd(clock);
        is4HourBreakPending = false;
    }

    private void getClientFromQueueAndAssignToCarpet(LocalDateTime clock) {
        if (!magicCarpetQueue.isEmpty() && magicCarpet.isFree()) {
            Client firstClient = magicCarpetQueue.poll();
            firstClient.setServeTime(clock);
            firstClient.setState(ClientState.EN_CARPETA);
            magicCarpet.serveToClient(clock, firstClient);
        }
    }

    private LocalDateTime getNextEvent() {

        final LocalDateTime firstEvent = dayFirstEvent;

        Optional<LocalDateTime> possibleFirstEvent = eventGenerators.stream()
                .map(EventGenerator::getNextInterruption)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(localDateTime -> localDateTime.isBefore(firstEvent))
                .min(LocalDateTime::compareTo);

        return possibleFirstEvent.orElse(firstEvent);
    }

    private void calculateMaxAmountOfQueue() {
        if (magicCarpetQueue.size() > maxAmountInQueue) {
            maxAmountInQueue = magicCarpetQueue.size();
        }
    }

    private void calculateMaxDurationInQueue(Client client) {
        if (client.getSecondsOfWaiting() > maxDurationInQueue) {
            maxDurationInQueue = client.getSecondsOfWaiting();
            clientOfMaxDuration = client;
        }
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

    public Optional<Client> getClientOfMaxDuration() {
        return Optional.ofNullable(clientOfMaxDuration);
    }

    public double getMaxDurationInQueue() {
        return maxDurationInQueue;
    }

    public int getMaxAmountInQueue() {
        return maxAmountInQueue;
    }

    public List<Client> getClients() {
        return clients;
    }
}
