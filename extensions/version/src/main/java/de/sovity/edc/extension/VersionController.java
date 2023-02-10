package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;

import java.io.IOException;
import java.io.InputStream;
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
    public String getCommitInformation() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("version.txt");
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        monitor.info(result);
        return result;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
