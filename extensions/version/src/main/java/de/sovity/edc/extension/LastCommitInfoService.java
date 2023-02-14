package de.sovity.edc.extension;

import java.util.Objects;
import java.util.Scanner;

public class LastCommitInfoService {

    public String getLastCommitInfo(){
        return null;
    }

    public String getJarLastCommitInfo() {
        var classloader = Thread.currentThread().getContextClassLoader();
        var is = classloader.getResourceAsStream("jar-last-commit-info.txt");
        var scanner = new Scanner(Objects.requireNonNull(is)).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public String getEnvLastCommitInfo() {
        return null;
    }

}
