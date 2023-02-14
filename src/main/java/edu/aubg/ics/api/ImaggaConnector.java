package edu.aubg.ics.api;

public class ImaggaConnector implements Connector {
    @Override
    public void connect() {
        System.out.println("This will connect to Imagga API");
    }
}
