package org.java.expert.jmeter;

import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.assertions.gui.AssertionGui;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.modifiers.CounterConfig;
import org.apache.jmeter.modifiers.gui.CounterConfigGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

public final class JMeterConfiguration {

    private JMeterConfiguration() {
    }

    public static HashTree create(TestParams params) {
        TestPlan testPlan = createTestPlan();
        ThreadGroup threadGroup = createThreadGroup(params);
        CounterConfig counter = createCounter();
        ConstantThroughputTimer timer = createThroughputTimer(params);
        HeaderManager headers = createHeaders();
        HTTPSamplerProxy request = createRequest(params);
        ResponseAssertion assertion = createAssertion();
        ResultCollector results = createResultCollector(params);

        HashTree tree = new ListedHashTree();
        HashTree groupTree = tree.add(testPlan, threadGroup);
        groupTree.add(request, headers);
        groupTree.add(request, assertion);
        groupTree.add(counter);
        groupTree.add(timer);
        tree.add(testPlan, results);
        return tree;
    }

    private static TestPlan createTestPlan() {
        var testPlan = new TestPlan("User registration load test");
        testPlan.setUserDefinedVariables(new Arguments());
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        return testPlan;
    }

    private static ThreadGroup createThreadGroup(TestParams params) {
        var loop = new LoopController();
        loop.setLoops(-1);
        loop.setContinueForever(true);
        loop.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loop.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loop.initialize();

        var threadGroup = new ThreadGroup();
        threadGroup.setName("Create users");
        threadGroup.setNumThreads(params.threads());
        threadGroup.setRampUp(1);
        threadGroup.setScheduler(true);
        threadGroup.setDuration(params.duration());
        threadGroup.setSamplerController(loop);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }

    private static CounterConfig createCounter() {
        var counter = new CounterConfig();
        counter.setName("Unique login counter");
        counter.setStart("1");
        counter.setIncrement("1");
        counter.setVarName("counter");
        counter.setProperty(TestElement.TEST_CLASS, CounterConfig.class.getName());
        counter.setProperty(TestElement.GUI_CLASS, CounterConfigGui.class.getName());
        return counter;
    }

    private static ConstantThroughputTimer createThroughputTimer(TestParams params) {
        var timer = new ConstantThroughputTimer();
        timer.setName("Target RPS");
        timer.setThroughput(params.rps() * 60);
        timer.setCalcMode(
                ConstantThroughputTimer.Mode.AllActiveThreadsInCurrentThreadGroup_Shared.ordinal()
        );
        return timer;
    }

    private static HTTPSamplerProxy createRequest(TestParams params) {
        String body = "{\"login\":\"user-${counter}\",\"password\":\"123456789\"}";

        var request = new HTTPSamplerProxy();
        request.setName("POST /api/users");
        request.setProtocol("http");
        request.setDomain(params.host());
        request.setPort(params.port());
        request.setPath(params.endPoint());
        request.setMethod("POST");
        request.setPostBodyRaw(true);
        request.addNonEncodedArgument("", body, "");
        request.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        request.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return request;
    }

    private static HeaderManager createHeaders() {
        var headers = new HeaderManager();
        headers.setName("HTTP Header Manager");
        headers.add(new Header("Content-Type", "application/json"));
        headers.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headers.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return headers;
    }

    private static ResponseAssertion createAssertion() {
        var assertion = new ResponseAssertion();
        assertion.setName("Status is 201");
        assertion.setTestFieldResponseCode();
        assertion.setToEqualsType();
        assertion.addTestString("201");
        assertion.setProperty(TestElement.TEST_CLASS, ResponseAssertion.class.getName());
        assertion.setProperty(TestElement.GUI_CLASS, AssertionGui.class.getName());
        return assertion;
    }

    private static ResultCollector createResultCollector(TestParams params) {
        var collector = new ResultCollector(new Summariser("summary"));
        collector.setName("JTL results");
        collector.setFilename(params.resultPath());
        collector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        return collector;
    }
}
