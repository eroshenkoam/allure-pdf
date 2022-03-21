package io.github.eroshenkoam.allure;

import io.github.eroshenkoam.allure.option.StatusColorOptions;
import io.qameta.allure.model.Status;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@CommandLine.Command(
        name = "allure-pdf", mixinStandardHelpOptions = true
)
public class MainCommand implements Runnable {

    @CommandLine.Parameters(
            index = "0",
            description = "The directories with allure result files"
    )
    protected Path reportPath;

    @CommandLine.Option(
            names = {"-o", "--output"},
            defaultValue = "export.pdf",
            description = "Export output directory"
    )
    protected Path outputPath;

    @CommandLine.Option(
            names = {"-n", "--name"},
            defaultValue = "Generated report",
            description = "Report name"
    )
    protected String reportName;

    @CommandLine.Option(names = {"-f", "--filter"})
    protected Map<String, String> filter;

    @CommandLine.ArgGroup
    protected StatusColorOptions statusColorOptions = new StatusColorOptions();

    @Override
    public void run() {
        try {
            final StatusColors statusColors = new StatusColors();
            statusColors.setStatusColors(Status.PASSED, statusColorOptions.getPassed());
            statusColors.setStatusColors(Status.FAILED, statusColorOptions.getFailed());
            statusColors.setStatusColors(Status.BROKEN, statusColorOptions.getBroken());
            statusColors.setStatusColors(Status.SKIPPED, statusColorOptions.getSkipped());

            final AllurePDFGenerator generator = new AllurePDFGenerator(reportName, reportPath, statusColors);
            generator.filter(filter);
            generator.generate(outputPath);
            ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
