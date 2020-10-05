package com.neaterbits.util.concurrency.dependencyresolution.spec;

import java.util.List;
import java.util.function.Consumer;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.executor.TargetBuildResult;
import com.neaterbits.util.concurrency.dependencyresolution.executor.TargetExecutor;
import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.TargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.TargetBuilder;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.TargetBuilderImpl;
import com.neaterbits.util.concurrency.scheduling.AsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public abstract class TargetBuilderSpec<CONTEXT extends TaskContext> {

	protected abstract void buildSpec(TargetBuilder<CONTEXT> targetBuilder);
	
	public List<TargetSpec<CONTEXT, ?>> buildTargetSpecs() {

		final TargetBuilderImpl<CONTEXT> builderImpl = new TargetBuilderImpl<>();
		
		buildSpec(builderImpl);

		return builderImpl.build();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void execute(LogContext logContext, CONTEXT context, TargetExecutorLogger logger, AsyncExecutor executor, Consumer<TargetBuildResult> onResult) {
		
		try {
			final List<TargetSpec<CONTEXT, ?>> targetSpecs = buildTargetSpecs();
			
			final TargetFinder targetFinder = new TargetFinder(executor);
			
			final TargetFinderLogger targetFinderLogger = null; // new PrintlnTargetFinderLogger();
			
			targetFinder.computeTargets((List)targetSpecs, logContext, context, targetFinderLogger, targetReference -> {
				
				final TargetDefinition<?> rootTarget = targetReference.getTargetDefinitionIfAny();
				
				rootTarget.logRootObject(logContext);
				
				// target.printTargets();
				
				final TargetExecutor targetExecutor = new TargetExecutor(executor);
				
				targetExecutor.runTargets(context, rootTarget, logger, onResult);
			});
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			
			throw ex;
		}
	}
}
