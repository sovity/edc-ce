package de.sovity.edc.extension;

import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.Objects;
import java.util.Scanner;

public class LastCommitInfoService {

    private final String envLastCommitInfo;

    public LastCommitInfoService(ServiceExtensionContext context) {
        envLastCommitInfo = context.getSetting("edc.last.commit.info.env", "");
    }

    public String getLastCommitInfo(){
        var result = "Env Last Commit Info: \n";
        result += getEnvLastCommitInfo() + "\n";
        result += "Jar Last Commit Info: \n";
        result += getJarLastCommitInfo();
        return result;
    }

    public String getJarLastCommitInfo() {
        var classloader = Thread.currentThread().getContextClassLoader();
        var is = classloader.getResourceAsStream("jar-last-commit-info.txt");
        var scanner = new Scanner(Objects.requireNonNull(is)).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public String getEnvLastCommitInfo() {
        return envLastCommitInfo;
    }

}
