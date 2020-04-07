package io.eroshenkoam.allure;

import picocli.CommandLine;

@CommandLine.Command(
        name = "allure-pdf", mixinStandardHelpOptions = true
)
public class MainCommand implements Runnable {

    @Override
    public void run() {
    }

}
