package utn.frc.sim.simulation;

public enum Events {
    INICIO("Inicio."),
    INICIO_DEL_DIA("Inicio del dia."),
    LLEGADA_CLIENTE("Llegada de cliente."),
    FIN_CARPETA("Fin de carpeta."),
    FIN_CARPETA_LIMPIEZA("Fin de limpieza.");

    private final String text;

    Events(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
