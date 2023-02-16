package edu.aubg.ics.api;

import java.io.IOException;

public interface Connector {
    void connect(String imageUrl) throws IOException;
}
