package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.neaterbits.structuredlog.binary.logging.LogContext;
import com.neaterbits.util.concurrency.dependencyresolution.model.FileTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.InfoTarget;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisite;
import com.neaterbits.util.concurrency.dependencyresolution.model.Prerequisites;
import com.neaterbits.util.concurrency.dependencyresolution.model.TargetDefinition;
import com.neaterbits.util.concurrency.dependencyresolution.spec.builder.ActionLog;

public class PrerequisiteCompleteCheckerTest {

	private static Prerequisite<File> makePrerequisite(LogContext logContext, File targetObject) {
		
		final FileTarget<File> fileTarget = new FileTarget<File>(
				logContext, 
				File.class,
				targetObject,
				(context, f) -> f,
				f -> (File)f,
				f -> "File target " + f,
				targetObject,
				Collections.emptyList(),
				new Action<File>(null, (context, target, params) -> {
					return new ActionLog("1234", 0);
				}),
				null);
		
		return new Prerequisite<File>(logContext, targetObject, fileTarget.getTargetReference());
	}
	
	@Test
	public void testCompletion() {
		
		final LogContext logContext = new LogContext();

		final File targetObject1 = new File("/targetobject1");
		final File targetObject2 = new File("/targetobject2");
		final File targetObject3 = new File("/targetobject3");
		
		final Prerequisites prerequisites = new Prerequisites(
				logContext, 
				Arrays.asList(
						makePrerequisite(logContext, targetObject1),
						makePrerequisite(logContext, targetObject2),
						makePrerequisite(logContext, targetObject3)
				),
				null,
				null,
				null);
		
		final String infoTargetObj = "the target object";

		final TargetDefinition<String> infoTarget = new InfoTarget<>(
				logContext, 
				String.class,
				"test info target",
				obj -> "Qualifier name",
				obj -> "Target object " + obj,
				infoTargetObj,
				Arrays.asList(prerequisites),
				null,
				null);


		assertThat(PrerequisiteCompleteChecker.hasCompletedPrerequisites(
				target -> new PrerequisiteCompletion(Status.TO_EXECUTE),
				targetKey -> null,
				infoTarget)
				
				.getStatus()).isEqualTo(Status.TO_EXECUTE);
		

		
		assertThat(PrerequisiteCompleteChecker.hasCompletedPrerequisites(
				target -> new PrerequisiteCompletion(
							target.getTargetObject() == targetObject1
								? Status.SUCCESS
								: Status.TO_EXECUTE),
				targetKey -> null,
				infoTarget)
				
				.getStatus()).isEqualTo(Status.TO_EXECUTE);
		

		assertThat(PrerequisiteCompleteChecker.hasCompletedPrerequisites(
				target -> new PrerequisiteCompletion(
							target.getTargetObject() == targetObject2
								? Status.SUCCESS
								: Status.TO_EXECUTE),
				targetKey -> null,
				infoTarget)
				
				.getStatus()).isEqualTo(Status.TO_EXECUTE);
		

		assertThat(PrerequisiteCompleteChecker.hasCompletedPrerequisites(
				target -> new PrerequisiteCompletion(
							target.getTargetObject() == targetObject3
								? Status.SUCCESS
								: Status.TO_EXECUTE),
				targetKey -> null,
				infoTarget)
				
				.getStatus()).isEqualTo(Status.TO_EXECUTE);
		
		assertThat(PrerequisiteCompleteChecker.hasCompletedPrerequisites(
				target -> new PrerequisiteCompletion(Status.SUCCESS),
				targetKey -> null,
				infoTarget)
				
				.getStatus()).isEqualTo(Status.SUCCESS);
		

	}
	
}