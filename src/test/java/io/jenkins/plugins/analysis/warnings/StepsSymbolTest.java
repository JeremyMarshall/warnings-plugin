package io.jenkins.plugins.analysis.warnings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import org.junit.Test;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponse;

import static edu.hm.hafner.analysis.assertj.Assertions.*;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.analysis.core.model.StaticAnalysisTool;
import io.jenkins.plugins.analysis.core.steps.PublishIssuesStep;
import io.jenkins.plugins.analysis.core.steps.ScanForIssuesStep;
import io.jenkins.plugins.analysis.core.testutil.IntegrationTestWithJenkinsPerTest;


import hudson.DescriptorExtensionList;
import hudson.model.UnprotectedRootAction;
import hudson.util.HttpResponses;


/**
 * Integration tests of the warnings plug-in in pipelines.
 *
 * @author Ullrich Hafner
 * @see ScanForIssuesStep
 * @see PublishIssuesStep
 */
public class StepsSymbolTest extends IntegrationTestWithJenkinsPerTest {

    @Test
    public void iterateSymbolWarningParser() {
        
        DescriptorExtensionList<StaticAnalysisTool,hudson.model.Descriptor<StaticAnalysisTool>> tools = getJenkins().getInstance().getDescriptorList(StaticAnalysisTool.class);

        int s = tools.size();
        int cnt = 0;

        for (hudson.model.Descriptor<StaticAnalysisTool> tool : tools) {
            String name = tool.clazz.getSimpleName();
            char c[] = name.toCharArray();
            c[0] = Character.toLowerCase(c[0]);
            name = new String(c);

            WorkflowJob job = createJob();
            job.setDefinition(asStage(
                    "def issues = scanForIssues tool: " + name + "()",
                    PUBLISH_ISSUES_STEP));
            AnalysisResult result = scheduleBuild(job, tool.clazz);

            assertThat(result.getTotalSize()).isEqualTo(0);
            assertThat(result.getIssues()).hasSize(0);
            cnt++;
        }
        assertThat(cnt == s);
    }

}
