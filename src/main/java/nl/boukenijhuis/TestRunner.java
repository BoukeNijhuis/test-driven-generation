package nl.boukenijhuis;

import nl.boukenijhuis.dto.InputContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.Collections;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots;

// TODO make this the JUnit testrunner
// TODO make this class reponsible for adding it own library to the classpath
public class TestRunner {

    private static final Logger LOG = LogManager.getLogger(Generator.class);

    private SummaryGeneratingListener listener = new SummaryGeneratingListener();

    private TestInfo latestTestInfo = null;

    public TestInfo runTestFile(InputContainer inputContainer) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClasspathRoots(Collections.singleton(inputContainer.getOutputDirectory())))
                .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        long testsFoundCount = listener.getSummary().getTestsFoundCount();
        long testsSucceededCount = listener.getSummary().getTestsSucceededCount();
        var failureList = listener.getSummary().getFailures();
        // TODO handle multiple failures
        String failingTest = null;
        String errorOutput = null;
        if (!failureList.isEmpty()) {
            TestExecutionSummary.Failure firstFailure = failureList.get(0);
            failingTest = firstFailure.getTestIdentifier().getDisplayName();
            String errorMessage = firstFailure.getException().toString() ;
            errorOutput = String.format("The test with the name '%s' failed with the following error: %s", failingTest, errorMessage);
        }
        return latestTestInfo = new TestInfo(testsFoundCount, testsSucceededCount, failingTest, errorOutput);
    }

    public TestInfo getLatestTestInfo() {
        return latestTestInfo;
    }

    record TestInfo(long found, long succeeded, String failingTest, String errorOutput) {
    }

    ;

}
