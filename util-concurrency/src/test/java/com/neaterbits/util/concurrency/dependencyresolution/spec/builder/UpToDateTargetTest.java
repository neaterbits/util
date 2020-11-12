package com.neaterbits.util.concurrency.dependencyresolution.spec.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.OnBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.executor.TargetBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.spec.TargetBuilderSpec;
import com.neaterbits.util.concurrency.scheduling.Constraint;
import com.neaterbits.util.concurrency.scheduling.QueueAsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class UpToDateTargetTest {

    @Test
    public void testAlreadyUpToDate() throws IOException {
        
        final Runnable action = Mockito.mock(Runnable.class);
        
        final OnBuildResult onBuildResult = Mockito.mock(OnBuildResult.class);
        
        runTargets(true, action, onBuildResult);
        
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
    public void testNotUpToDate() throws IOException {
        
        final Runnable action = Mockito.mock(Runnable.class);
        
        final OnBuildResult onBuildResult = Mockito.mock(OnBuildResult.class);
        
        runTargets(false, action, onBuildResult);
        
        final ArgumentCaptor<TargetBuildResult> resultCaptor
            = ArgumentCaptor.forClass(TargetBuildResult.class);
        
        Mockito.verify(action).run();

        Mockito.verify(onBuildResult).onResult(resultCaptor.capture());
        
        final TargetBuildResult result = resultCaptor.getValue();
        
        assertThat(result.getCompletedTargets()).isNotEmpty();
        assertThat(result.getFailedTargets()).isEmpty();
        
        Mockito.verifyNoMoreInteractions(onBuildResult);
        
        Mockito.verifyNoMoreInteractions(action);
    }

    private void runTargets(boolean isUpToDate, Runnable action, OnBuildResult onResult) throws IOException {

        // Prepare build spec
        final TargetBuilderSpec<TaskContext> builderSpec = new TargetBuilderSpec<TaskContext>() {
            
            @Override
            protected void buildSpec(TargetBuilder<TaskContext> targetBuilder) {
            
                targetBuilder.addTarget("root", "Root target")

                    .withPrerequisites("Target")
                    .fromIterating(context -> Arrays.asList("123"))
                    .buildBy(st -> 
                        st.addFilesSubTarget(
                                String.class,
                                (t, p) -> isUpToDate,
                                Function.identity(),
                                t -> "Up to date test")

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
}
