package com.neaterbits.util.concurrency.dependencyresolution.executor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.Value;
import com.neaterbits.util.concurrency.dependencyresolution.executor.RecursiveBuildInfo.CreateTargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.executor.logger.PrintlnTargetExecutorLogger;
import com.neaterbits.util.concurrency.dependencyresolution.model.FileTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.InfoTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.CommandLineActionLog;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.FunctionActionLog;
import com.neaterbits.util.concurrency.scheduling.QueueAsyncExecutor;
import com.neaterbits.util.concurrency.scheduling.task.TaskContext;

public class TargetExecutorTest {

	@Test
	public void testTarget() throws IOException {

		// final File file = File.createTempFile("test", "file");

		// file.deleteOnExit();
		
		final CreateTargetDefinition<TaskContext, File> createTargetDefinition
		    = (LogContext logContext, TaskContext context, File targetObject, List<Prerequisites> prerequisitesList) -> {
		        
		        return new FileTarget<File>(
		                logContext,
		                File.class,
		                targetObject,
		                "File target " + targetObject.getName(),
		                targetObject,
		                prerequisitesList,
		                new Action<File>(null, (c, t, params) -> {
		                    return FunctionActionLog.OK;
		                }),
		                null) {

                            @Override
                            protected boolean isUpToDate(File target, Collection<Prerequisites> prerequisites) {
                                return false;
                            }
		            
		        };
	    };

	    final File targetObject = new File("/");

		final RecursiveBuildInfo<TaskContext, File, File> buildInfo = new RecursiveBuildInfo<>(
				(context, target) -> {

					final Collection<File> result;

					int level = 0;

					for (File parentFile = target; parentFile != null; parentFile = parentFile.getParentFile()) {
						++ level;
					}

					if (level > 1) {
						result = Collections.emptyList();
					}
					else {

						final File [] files = target.listFiles();

						result = files != null ? Arrays.asList(files) : Collections.emptyList();
					}

					return result;
				},
				Function.identity(),
				createTargetDefinition);

		final Value<ArrayList<File>> collectedFiles = new Value<>();

		final ProduceFromSubTargets<String> collectSubTargets = new ProduceFromSubTargets<>(ArrayList.class, (infoTarget, files) -> {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			final ArrayList<File> arrayList = new ArrayList(files);

			if (collectedFiles.get() != null) {
				throw new IllegalStateException();
			}

			collectedFiles.set(arrayList);

			return arrayList;
		});

		final Producers<String> collectors = new Producers<>(collectSubTargets, null);

		final LogContext logContext = new LogContext();

		final FileTarget<File> fileTarget = new FileTarget<File>(
				logContext,
				File.class,
				targetObject,
				"File target " + targetObject.getName(),
				targetObject,
				Collections.emptyList(),
				new Action<File>(null, (context, target, params) -> {
					return FunctionActionLog.OK;
				}),
				null);

		final Prerequisites prerequisites = new Prerequisites(
				logContext,
				Arrays.asList(
						new Prerequisite<File>(
								logContext,
								targetObject,
								fileTarget)),
				"Info prerequisites",
				buildInfo,
				collectors);

		final String infoTargetObj = "the target object";

		final TargetDefinition<String> infoTarget = new InfoTarget<>(
				logContext,
				String.class,
				"target_identifier",
				"Target object " + infoTargetObj,
				infoTargetObj,
				Arrays.asList(prerequisites),
				new Action<>(null, (context, target, params) -> {

					try {
						final ArrayList<?> files = params.getCollectedProduct(infoTargetObj, ArrayList.class);

						if (files == null) {
							throw new AssertionError();
						}
						
						if (files.size() != targetObject.listFiles().length + 1) { // +1 for root directory
							throw new AssertionError("files " + files.size() + ", targets " + (targetObject.listFiles().length + 1));
						}
					}
					catch (Exception ex) {
						throw new AssertionError();
					}

					return new CommandLineActionLog("1234", 0);
				}),
				null);


		final TargetExecutor targetExecutor = new TargetExecutor(new QueueAsyncExecutor(false));

		final TaskContext taskContext = new TaskContext();

		targetExecutor.runTargets(
				taskContext,
				infoTarget,
				new PrintlnTargetExecutorLogger(),
				buildResult -> {

			assertThat(collectedFiles.get()).isNotNull();

			final File [] files = targetObject.listFiles();

			assertThat(collectedFiles.get().size()).isEqualTo(files.length + 1);

			for (File file : files) {
				assertThat(collectedFiles.get().contains(file)).isTrue();
			}

			System.out.println("Build finished");
		});
	}

}
