package nl.boukenijhuis;

import nl.boukenijhuis.dto.InputContainer;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.Collections;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots;

// TODO make this the JUnit testrunner
// TODO make this class reponsible for adding it own library to the classpath
public class TestRunner {

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
        String errorOutput = null;
        if (!failureList.isEmpty()) {
            errorOutput = failureList.getFirst().getException().getMessage();
        }
        return latestTestInfo = new TestInfo(testsFoundCount, testsSucceededCount, errorOutput);
    }

    public TestInfo getLatestTestInfo() {
        return latestTestInfo;
    }

    record TestInfo(long found, long succeeded, String errorOutput) {
    }

    ;

}
