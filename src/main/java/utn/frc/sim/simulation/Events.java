package utn.frc.sim.simulation;

public enum Events {
    INICIO("Inicio."),
    INICIO_DEL_DIA("Inicio del dia."),
    LLEGADA_CLIENTE("Llegada de cliente."),
    INICIO_BREAK("Se requiere break."),
    FIN_BREAK("Fin de break."),
    INICIO_LIMPIEZA("Se requiere limpieza."),
    FIN_LIMPIEZA("Fin de limpieza."),
    FIN_CARPETA("Fin de carpeta.");

    private final String text;

    Events(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
