package utn.frc.sim.model;


public class Event<T> {
    private final T component;

    public Event(T client) {
        this.component = client;
    }

    public Event(){
        this(null);
    }

    public T getComponent() {
        return component;
    }

    public boolean hasComponent(){
        return component != null;
    }
}
