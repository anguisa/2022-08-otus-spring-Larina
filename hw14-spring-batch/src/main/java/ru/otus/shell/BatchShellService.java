package ru.otus.shell;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.config.AppConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.otus.config.JobConfig.INIT_SCHEMA_FILE_NAME;
import static ru.otus.config.JobConfig.UPDATE_SCHEMA_FILE_NAME;

@ShellComponent
public class BatchShellService {

    private final Job copyLibraryJob;
    private final JobLauncher jobLauncher;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final AppConfig appConfig;

    public BatchShellService(Job copyLibraryJob, JobLauncher jobLauncher, JobOperator jobOperator, JobExplorer jobExplorer, AppConfig appConfig) {
        this.copyLibraryJob = copyLibraryJob;
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.appConfig = appConfig;
    }

    @ShellMethod(value = "copyLibrary", key = "copy")
    public void copyLibrary() throws Exception {
        JobExecution execution = jobLauncher.run(copyLibraryJob, new JobParametersBuilder()
            .addString(INIT_SCHEMA_FILE_NAME, appConfig.getInitSchemaFileName())
            .addString(UPDATE_SCHEMA_FILE_NAME, appConfig.getUpdateSchemaFileName())
            .toJobParameters());
        System.out.println(execution);
    }

    @ShellMethod(value = "restartCopyLibrary", key = "restart")
    public void restartCopyLibrary() throws Exception {
        JobInstance lastJobInstance = jobExplorer.getLastJobInstance(copyLibraryJob.getName());
        if (lastJobInstance == null) {
            throw new IllegalStateException("Nothing to restart");
        }
        Long executionId = jobOperator.startNextInstance(lastJobInstance.getJobName());
//        Long executionId = jobOperator.restart(lastJobInstance.getId()); // только для failed
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(copyLibraryJob.getName(), 0, 100);
        System.out.println(jobInstances);
        System.out.println(jobInstances.stream().map(jobExplorer::getJobExecutions).flatMap(Collection::stream).collect(Collectors.toList()));
    }
}
