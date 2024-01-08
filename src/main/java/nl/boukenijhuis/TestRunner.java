package nl.boukenijhuis;

import nl.boukenijhuis.dto.InputContainer;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.Collections;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots;

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
        latestTestInfo = new TestInfo(testsFoundCount, testsSucceededCount);
        return getLatestTestInfo();
    }

    public TestInfo getLatestTestInfo() {
        return latestTestInfo;
    }

    record TestInfo(long found, long succeeded){};

}
