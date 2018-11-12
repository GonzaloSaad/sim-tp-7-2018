package utn.frc.sim.model;

import utn.frc.sim.generators.distributions.DistributionRandomGenerator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeEvent {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MILLIS_IN_SECOND = 1000;
    private final DistributionRandomGenerator generator;
    private final ChronoUnit bigUnit;
    private final ChronoUnit smallUnit;

    private TimeEvent(DistributionRandomGenerator generator, ChronoUnit bigUnit, ChronoUnit smallUnit) {
        this.generator = generator;
        this.bigUnit = bigUnit;
        this.smallUnit = smallUnit;
    }

    public static TimeEvent create(DistributionRandomGenerator generator, ChronoUnit bigUnit, ChronoUnit smallUnit) {
        return new TimeEvent(generator, bigUnit, smallUnit);
    }

    /**
     * Metodo que calcula el proximo evento desde una fecha, basado en una distribucion.
     * Si el random es menor a 0, se supone que esta en cantidad de horas de un dia, es
     * decir, si el random es 0.5 quiere decir que son 12 horas. El valor 1 refiere a 24 horas.
     * Si el random es mayor a 1, se supone que la cantidad esta en minutos.
     */
    public LocalDateTime calculateNextEventFromRandom(LocalDateTime timeFrom) {
        double random = generator.random();
        return calculateNextEventFromMinutes(timeFrom, random);
    }

    private LocalDateTime calculateNextEventFromMinutes(LocalDateTime timeFrom, double random) {

        int bigUnitAmount = (int) random;
        int smallUnitAmount = (int) ((random - bigUnitAmount) * getRatio());

        LocalDateTime nextEvent = timeFrom.plus(bigUnitAmount, bigUnit);
        return nextEvent.plus(smallUnitAmount, smallUnit);

    }

    private int getRatio() {
        if (bigUnit == ChronoUnit.MINUTES) {
            if (smallUnit == ChronoUnit.SECONDS) {
                return SECONDS_IN_MINUTE;
            } else if (smallUnit == ChronoUnit.MILLIS) {
                return SECONDS_IN_MINUTE * MILLIS_IN_SECOND;
            }
        } else if (bigUnit == ChronoUnit.SECONDS){
            if (smallUnit == ChronoUnit.MILLIS){
                return MILLIS_IN_SECOND;
            }
        }
        throw new IllegalStateException();
    }
}
