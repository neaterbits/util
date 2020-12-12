package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public final class TargetExecutor {
	
	private final AsyncExecutor asyncExecutor;
	
	public TargetExecutor(AsyncExecutor asyncExecutor) {
		
		Objects.requireNonNull(asyncExecutor);
		
		this.asyncExecutor = asyncExecutor;
	}

	public <CONTEXT extends TaskContext, TARGET> void runTargets(
			CONTEXT context,
			TargetDefinition<TARGET> rootTarget,
			TargetExecutorLogger logger,
			OnBuildResult onResult) {

		final ExecutorState<CONTEXT> state = ExecutorState.createFromTargetTree(rootTarget, this, logger);

		final TargetExecutionContext<CONTEXT> targetExecutionContext = new TargetExecutionContext<CONTEXT>(
				context,
				state,
				asyncExecutor,
				logger, 
				onResult);
		
		scheduleTargets(targetExecutionContext);
	}
	
	<CONTEXT extends TaskContext> void scheduleTargets(TargetExecutionContext<CONTEXT> context) {

		if (context.logger != null) {
			context.logger.onScheduleTargets(asyncExecutor.getNumScheduledJobs(), context.state);
		}
		
		while (!context.state.getNonCompletedTargets().isEmpty() || asyncExecutor.getNumScheduledJobs() != 0) {

			asyncExecutor.runQueuedResultRunnables();
		
			final List<TargetStateMachine<CONTEXT>> targets = new ArrayList<TargetStateMachine<CONTEXT>>(context.state.getNonCompletedTargets());

			final int targetsLeft = targets.size();
			final int priorNumScheduled = asyncExecutor.getNumScheduledJobs();
			
			boolean anyStateChange = false;
			
			for (TargetStateMachine<CONTEXT> target : targets) {
				
				// Try to check if state can be completed
				final boolean stateChangeOccured = target.schedule(state -> state.onCheckPrerequisitesComplete(context));
				
				if (stateChangeOccured) {
					anyStateChange = true;
				}
			}

			if (   !anyStateChange
				&& targetsLeft != 0
				&& targetsLeft == context.state.getNonCompletedTargets().size()
				&& priorNumScheduled == asyncExecutor.getNumScheduledJobs()) {

				// No target scheduled
				System.err.println("Not able to trigger more target builds");

                System.err.println("Completed targets");
                printTargetDefinitions(context.state.getCompletedTargets(), System.err);

                System.err.println();
                
                System.err.println("Failed targets");
                printTargetDefinitions(context.state.getFailedTargets().keySet(), System.err);

                System.err.println();

                System.err.println("Remaining targets");
                printTargets(
                        context.state.getNonCompletedTargets(),
                        context.state.getCompletedTargets(),
                        System.err);
				break;
			}
		}

		if (context.onResult != null) {
			context.onResult.onResult(context.state);
		}
	}

    private static
    void printTargetDefinitions(Collection<TargetDefinition<?>> targetDefinitions, PrintStream printStream) {

        for (TargetDefinition<?> targetDefinition : targetDefinitions) {
            printTarget(targetDefinition, printStream);
        }
    }
	
	private static <CONTEXT extends TaskContext>
	void printTargets(Collection<TargetStateMachine<CONTEXT>> targetStates, Set<TargetDefinition<?>> completed, PrintStream printStream) {
	    
        for (TargetStateMachine<CONTEXT> targetState : targetStates) {
            printTargetState(targetState, completed, printStream);
        }
	}

    private static List<String> getPrerequisites(TargetDefinition<?> targetDefinition) {
        return getPrerequisites(targetDefinition, null);
    }
	
	private static List<String> getPrerequisites(
	        TargetDefinition<?> targetDefinition,
	        Set<TargetDefinition<?>> prefixIfNotInSet) {
	    
	    return targetDefinition.getPrerequisites().stream()
                .flatMap(prerequisites -> prerequisites.getPrerequisites().stream())
                .map(prerequisite -> {
                    
                    final String debugString = prerequisite.getDebugString();
                    
                    final String result;
                    
                    if (prefixIfNotInSet != null) {

                        final String prefix;

                        if (prerequisite.getSubTarget() == null) {
                            prefix = "?";
                        }
                        else {
                            prefix = !prefixIfNotInSet.contains(prerequisite.getSubTarget())
                                    ? "!" 
                                    : "";
                        }

                        result = prefix + debugString;
                    }
                    else {
                        result = debugString;
                    }
                            
                    return result;
                })
                .collect(Collectors.toList());
	}

    private static
    void printTarget(TargetDefinition<?> targetDefinition, PrintStream printStream) {
        
        final List<String> prerequisitesList = getPrerequisites(targetDefinition);
        
        System.err.println("Target " + targetDefinition.getDebugString()
            + " with prerequisites " + prerequisitesList);
    }

    private static <CONTEXT extends TaskContext>
    void printTargetState(TargetStateMachine<CONTEXT> targetState, Set<TargetDefinition<?>> completed, PrintStream printStream) {
	    
        final List<String> prerequisitesList = getPrerequisites(targetState.getTarget(), completed);
        
        System.err.println("Target " + targetState.getTarget().getDebugString()
            + " in state " + targetState.getCurStateName()
            + " with prerequisites " + prerequisitesList);
	}
}

