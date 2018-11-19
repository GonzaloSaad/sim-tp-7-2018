package utn.frc.sim.model.servers;

public enum ServerState {
    OCP("Ocupado"),
    LBR("Libre"),
    OUT("Limpiando"),
    OUT_2("Corte 8 min");

    private final String text;

    ServerState(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
