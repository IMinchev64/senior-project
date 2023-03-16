package edu.aubg.ics.api;

import java.io.IOException;

public interface Connector {
    String connect(String imageUrl) throws IOException;
}