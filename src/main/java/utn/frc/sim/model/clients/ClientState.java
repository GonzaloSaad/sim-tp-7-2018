package utn.frc.sim.model.clients;

public enum ClientState {
    COLA_CARPETA("En cola de carpeta"),
    EN_CARPETA("En carpeta"),
    TERMINADO("Terminado");

    private final String text;

    ClientState(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
