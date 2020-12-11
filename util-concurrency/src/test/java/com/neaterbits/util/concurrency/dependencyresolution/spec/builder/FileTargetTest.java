package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.Files;
import com.neaterbits.util.concurrency.dependencyresolution.executor.OnBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.executor.TargetBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetBuilderSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.QueueAsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class FileTargetTest {

    @FunctionalInterface
    interface WriteFiles {
        void write(TestFiles testFiles) throws IOException;
    }
    
    private static class TestFiles {

        private final File targetFile;

        private final File testFile1;
        private final File testFile2;
        private final File testFile3;

        TestFiles(File dir) {
            
            this.testFile1 = new File(dir, "testFile1");
            this.testFile2 = new File(dir, "testFile2");
            this.testFile3 = new File(dir, "testFile3");
            
            this.targetFile = new File(dir, "targetFile");
        }
    }
    
    @Test
    public void testFilesUpToDate() throws IOException {
        
        final Runnable action = Mockito.mock(Runnable.class);
        
        final OnBuildResult onBuildResult = Mockito.mock(OnBuildResult.class);
        
        runTargets(action, onBuildResult, files -> {
        
            writeFiles(files.testFile1, files.testFile2, files.testFile3);
        
            // make sure timestamp is newer
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
            
            writeFiles(files.targetFile);
        });
        
        final ArgumentCaptor<TargetBuildResult> resultCaptor
            = ArgumentCaptor.forClass(TargetBuildResult.class);
        
        Mockito.verify(onBuildResult).onResult(resultCaptor.capture());
        
        final TargetBuildResult result = resultCaptor.getValue();
        
        assertThat(result.getCompletedTargets()).isNotEmpty();
        assertThat(result.getFailedTargets()).isEmpty();
        
        Mockito.verifyNoMoreInteractions(onBuildResult);
        
        Mockito.verifyNoMoreInteractions(action);
    }

    @Test
    public void testFilesNotUpToDate() throws IOException {
        
        final Runnable action = Mockito.mock(Runnable.class);
        
        final OnBuildResult onBuildResult = Mockito.mock(OnBuildResult.class);
        
        runTargets(action, onBuildResult, files -> {
        
            writeFiles(files.testFile1, files.testFile2);
        
            writeFiles(files.targetFile);

            // make sure timestamp is newer
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
            
            writeFiles(files.testFile3);
        });
        
        final ArgumentCaptor<TargetBuildResult> resultCaptor
            = ArgumentCaptor.forClass(TargetBuildResult.class);
        
        Mockito.verify(onBuildResult).onResult(resultCaptor.capture());
        
        Mockito.verify(action).run();
        
        final TargetBuildResult result = resultCaptor.getValue();
        
        assertThat(result.getCompletedTargets()).isNotEmpty();
        assertThat(result.getFailedTargets()).isEmpty();
        
        Mockito.verifyNoMoreInteractions(onBuildResult);
        
        Mockito.verifyNoMoreInteractions(action);
    }

    @Test
    public void testTargetNotBuilt() throws IOException {
        
        final Runnable action = Mockito.mock(Runnable.class);
        
        final OnBuildResult onBuildResult = Mockito.mock(OnBuildResult.class);
        
        runTargets(action, onBuildResult, files -> {
        
            writeFiles(files.testFile1, files.testFile2, files.testFile3);

        });
        
        final ArgumentCaptor<TargetBuildResult> resultCaptor
            = ArgumentCaptor.forClass(TargetBuildResult.class);
        
        Mockito.verify(onBuildResult).onResult(resultCaptor.capture());
        
        Mockito.verify(action).run();
        
        final TargetBuildResult result = resultCaptor.getValue();
        
        assertThat(result.getCompletedTargets()).isNotEmpty();
        assertThat(result.getFailedTargets()).isEmpty();
        
        Mockito.verifyNoMoreInteractions(onBuildResult);
        
        Mockito.verifyNoMoreInteractions(action);
    }

    private void runTargets(Runnable action, OnBuildResult onResult, WriteFiles writeFiles) throws IOException {

        final File dir = java.nio.file.Files.createTempDirectory("targetstest").toFile();

        final TestFiles testFiles = new TestFiles(dir);
        
        writeFiles.write(testFiles);
        
        try {

            // Prepare build spec
            final TargetBuilderSpec<TaskContext> builderSpec = new TargetBuilderSpec<TaskContext>() {
                
                @Override
                protected void buildSpec(TargetBuilder<TaskContext> targetBuilder) {
                
                    targetBuilder.addTarget("root", "rootType", "rootAction", "Root target")
                        .withPrerequisites("Target")
                        .fromIterating(context -> Arrays.asList(testFiles.targetFile))
                        
                        .buildBy(st -> 
                            st.addFileSubTarget(
                                    File.class,
                                    "fileType",
                                    "fileAction",
                                    Function.identity(),
                                    t -> "Rebuild multiple files")

                            .withPrerequisites("Prerequisite files")
                            .fromIterating(Constraint.IO, (c, t) -> Arrays.asList(testFiles.testFile1, testFiles.testFile2, testFiles.testFile3))

                            .action(Constraint.IO, (c, t, p) -> {

                                action.run();

                                return FunctionActionLog.OK;
                            }));
                }
            };
 
            final LogContext logContext = new LogContext();
            
            final TaskContext taskContext = new TaskContext();
            
            builderSpec.execute(
                    logContext,
                    taskContext,
                    "root",
                    null,
                    new QueueAsyncExecutor(false),
                    onResult);
        }
        finally {
            Files.deleteRecursively(dir);
        }
    }

    private ActionLog writeFiles(File ... files) throws IOException {
        
        for (int i = 0; i < files.length; ++ i) {
            
            try (FileOutputStream outputStream = new FileOutputStream(files[i])) {
                outputStream.write(String.valueOf(i + 1).getBytes());
            }
        }

        return FunctionActionLog.OK;
    }
}
