package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Objects;
import java.util.Scanner;

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
        var classloader = Thread.currentThread().getContextClassLoader();
        var is = classloader.getResourceAsStream("jar-last-commit-info.txt");
        var scanner = new Scanner(Objects.requireNonNull(is)).useDelimiter("\\A");
        var result = scanner.hasNext() ? scanner.next() : "";
        monitor.info(result);
        return result;
    }
}
