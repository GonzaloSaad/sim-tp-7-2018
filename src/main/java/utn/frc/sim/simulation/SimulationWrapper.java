package utn.frc.sim.simulation;

import utn.frc.sim.util.DoubleUtils;

import java.time.LocalDateTime;
import java.util.Optional;

public class SimulationWrapper {

    private static final String NONE_SYMBOL = "-";
    private final Simulation simulation;


    private SimulationWrapper(Simulation simulation) {
        this.simulation = simulation;
    }

    public static SimulationWrapper ofType() {
        return new SimulationWrapper(Simulation.startNewSimulation());
    }

    public void step() throws SimulationFinishedException {
        simulation.step();
    }


    /*
    Reloj y eventos.
     */

    public String getLastEvent() {
        return simulation.getLastEventDescription().toString();
    }

    public String getClock() {
        return simulation.getClock().toString();
    }

    public String getClientOfEventNumber(){
        return simulation.getClientOfEvent()
                .map(client -> Integer.toString(client.getClientNumber()))
                .orElse(NONE_SYMBOL);
    }
    /*
    Datos para clientes.
     */

    public String getNextClientEvent() {
        return simulation.getClientGenerator()
                .getNextEvent()
                .map(LocalDateTime::toString)
                .orElse(NONE_SYMBOL);

    }
   // public  String getNumberCliente(){
    //    return Integer.toString(simulation.;
   // }


    /*
    Datos para recepcion.
     */

    public String getMagicCarpetState() {
        return simulation.getMagicCarpet().getState().toString();
    }

    public String getMagicCarpetClient() {
        return simulation.getMagicCarpet()
                .getServingClient()
                .map(client -> Integer.toString(client.getClientNumber()))
                .orElse(NONE_SYMBOL);
    }

    public String getMagicCarpetNextEvent() {
        return simulation.getMagicCarpet().getNextEvent().map(LocalDateTime::toString).orElse(NONE_SYMBOL);
    }

    public String getMagicCarpetQueueSize() {
        return Integer.toString(simulation.getMagicCarpetQueue().size());
    }


    /*
    Range of simulations.
     */

    public boolean verifyRowToAddToTable(String txtFromDay, String txtToDay, String txtFromHour, String txtToHour){
        if(txtToHour.equals("")) txtToHour = "20";
        if(txtFromHour.equals("")) txtFromHour = "5";
        return  isInHourRange(Integer.parseInt(txtFromHour), Integer.parseInt(txtToHour));

    }

    private boolean isInHourRange(Integer fromHour, Integer toHour){
        toHour--;
        return simulation.getClock().getHour() >= fromHour && simulation.getClock().getHour() <= toHour;
    }

}
