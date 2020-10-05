package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
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
			Consumer<TargetBuildResult> onResult) {
		
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
			
			System.out.println("## schedule targets");
			
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
				System.err.println("Not able to trigger more target builds: " + context.state.getNonCompletedTargets().size());
				
				for (TargetStateMachine<CONTEXT> targetState : context.state.getNonCompletedTargets()) {
					
					final List<String> prerequisitesList = targetState.getTarget().getPrerequisites().stream()
							.flatMap(prerequisites -> prerequisites.getPrerequisites().stream())
							.map(prerequisite -> prerequisite.getSubTarget().getTargetObject().toString())
							.collect(Collectors.toList());
					
					System.err.println("Target " + targetState.getObjectDebugString()
						+ " in state " + targetState.getCurStateName()
						+ " with prerequisites " + prerequisitesList);
				}
				
				break;
			}
		}

		if (context.onResult != null) {
			context.onResult.accept(context.state);
		}
	}
}

