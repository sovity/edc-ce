package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;

@Produces({MediaType.APPLICATION_JSON})
@Path("/version")
public class VersionController {
    private final Monitor monitor;

    public VersionController(Monitor monitor) {
        this.monitor = monitor;
    }

    @GET
    @Path("/")
    public String getCommitInformation() {
        monitor.info("getting values of file---");
        return "test";
    }
}
