package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.edc.spi.monitor.Monitor;

public class VersionController {
    private final Monitor monitor;

    public VersionController(Monitor monitor) {
        this.monitor = monitor;
    }


    @GET
    @Path("/version")
    public String getCommitInformation() {
        monitor.info("getting values of file---");
        return "test";
    }
}
