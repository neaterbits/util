package org.jutils.concurrency.dependencyresolution.executor;

import java.util.function.BiConsumer;

import org.jutils.concurrency.dependencyresolution.model.TargetDefinition;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionFunction;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionLog;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionResult;
import org.jutils.concurrency.dependencyresolution.spec.builder.ActionWithResultFunction;
import org.jutils.concurrency.scheduling.task.ProcessResult;
import org.jutils.concurrency.scheduling.task.TaskContext;

class Actions {
	
	static <CONTEXT extends TaskContext> void runOrScheduleAction(
			TargetExecutionContext<CONTEXT> context,
			Action<?> action,
			TargetDefinition<?> target,
			BiConsumer<Exception, Boolean> onCompleted) {
		
		if (action.getConstraint() == null) {
			final Exception exception = performAction(context, action, target);
			
			onCompleted.accept(exception, false);
		}
		else {
			context.asyncExecutor.schedule(
					action.getConstraint(),
					null,
					param -> {
						return performAction(context, action, target);
					},
					(param, exception) -> {
						onCompleted.accept(exception, true);
					} );
		}
	}
	
	static <CONTEXT extends TaskContext> void runOrScheduleActionWithResult(
			TargetExecutionContext<CONTEXT> context,
			ActionWithResult<?> actionWithResult,
			TargetDefinition<?> target,
			BiConsumer<Exception, Boolean> onCompleted) {
		
		if (actionWithResult.getConstraint() == null) {
			
			final Result result = performActionWithResult(context, actionWithResult, target);
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final ProcessResult<CONTEXT, Object, Object> processResult
			    = (ProcessResult)actionWithResult.getOnResult();
			
			if (processResult != null) {
			    processResult.process(context.taskContext, target.getTargetObject(), result.result);
			}
			
			onCompleted.accept(result.exception, false);
		}
		else {
			context.asyncExecutor.schedule(
					actionWithResult.getConstraint(),
					null,
					param -> {
						return performActionWithResult(context, actionWithResult, target);
					},
					(param, r) -> {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						final ProcessResult<CONTEXT, Object, Object> processResult
						    = (ProcessResult)actionWithResult.getOnResult();
						
						if (target == null) {
						    throw new IllegalStateException();
						}
						
						if (r == null) {
						    throw new IllegalStateException();
						}
						
						if (processResult != null) {
						    processResult.process(context.taskContext, target.getTargetObject(), r.result);
						}
						
						onCompleted.accept(r.exception, true);
					});
		}
	}
	
	private static <CONTEXT extends TaskContext> Exception performAction(
			TargetExecutionContext<CONTEXT> context,
			Action<?> action,
			TargetDefinition<?> target) {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		final ActionFunction<CONTEXT, Object> actionFunction = (ActionFunction)action.getActionFunction();
		
		try {
			final ActionLog actionLog = actionFunction.perform(context.taskContext, target.getTargetObject(), context.state);

			if (context.logger != null) {
				context.logger.onActionCompleted(target, context.state, actionLog);
			}

		} catch (Exception ex) {
			if (context.logger != null) {
				context.logger.onActionException(target, context.state, ex);
			}

			return ex;
		}


		return null;
	}

	private static <CONTEXT extends TaskContext> Result performActionWithResult(
			TargetExecutionContext<CONTEXT> context,
			ActionWithResult<?> action,
			TargetDefinition<?> target) {

		@SuppressWarnings({ "unchecked" })
		final ActionWithResultFunction<CONTEXT, Object, Object> actionFunction
				= (ActionWithResultFunction<CONTEXT, Object, Object>)action.getActionWithResult();

		Exception exception = null;
		Object result = null;
		
		try {
			final ActionResult<Object> actionResult = actionFunction.perform(context.taskContext, target.getTargetObject(), context.state);

			if (context.logger != null) {
				context.logger.onActionCompleted(target, context.state, actionResult.getLog());
			}
			
			result = actionResult.getResult();
			
			context.state.setActionResult(target.getTargetKey(), result);
		}
		catch (Exception ex) {
		    
		    ex.printStackTrace();
		    
			if (context.logger != null) {
				context.logger.onActionException(target, context.state, ex);
			}
			exception = ex;
		}

		return new Result(result, exception);
	}
}
