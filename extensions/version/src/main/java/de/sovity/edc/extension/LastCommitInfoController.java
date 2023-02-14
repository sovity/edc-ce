package de.sovity.edc.extension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Objects;
import java.util.Scanner;

@Produces({MediaType.APPLICATION_JSON})
@Path("/last-commit-info")
public class LastCommitInfoController {
    private final Monitor monitor;
    private final LastCommitInfoService lastCommitInfoService;

    public LastCommitInfoController(Monitor monitor, LastCommitInfoService lastCommitInfoService) {
        this.monitor = monitor;
        this.lastCommitInfoService = lastCommitInfoService;
    }

    @GET
    @Path("/")
    public String getLastCommitInformation() {
        String result = lastCommitInfoService.getJarLastCommitInfo();
        //TODO: print system-env last commit information, if present, append extension information
        monitor.info(result);
        return result;
    }

}
