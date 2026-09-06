package ru.otus;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
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
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

public class JMeterConfiguration {
    public static HashTree create(TestParams params) {
        ThreadGroup threadGroup = createThreadGroup(params);
        HeaderManager headerManager = createHeaderManager();
        CounterConfig counter = createCounter();
        TestPlan testPlan = createTestPlan();
        ResultCollector resultCollector = createResultCollector();
        HTTPSamplerProxy register = createRegister(params);

        HashTree tree = new ListedHashTree();
        HashTree groupTree = tree.add(testPlan, threadGroup);
        groupTree.add(register, headerManager);
        groupTree.add(counter);
        tree.add(testPlan, resultCollector);

        return tree;
    }

    private static ThreadGroup createThreadGroup(TestParams params) {
        LoopController loop = new LoopController();
        loop.setLoops(params.loop());
        loop.setFirst(true);
        loop.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loop.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loop.initialize();

        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("create users");
        threadGroup.setNumThreads(Integer.parseInt(params.threads()));
        threadGroup.setRampUp(Integer.parseInt(params.ramp()));
        threadGroup.setDuration(Integer.parseInt(params.duration()));
        threadGroup.setSamplerController(loop);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        return threadGroup;
    }

    private static HeaderManager createHeaderManager() {
        HeaderManager headers = new HeaderManager();
        headers.setName("HTTP Header Manager");
        headers.add(new Header("Content-Type", "application/json"));
        headers.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headers.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return headers;
    }

    private static CounterConfig createCounter() {
        CounterConfig counter = new CounterConfig();
        counter.setName("counter");
        counter.setStart("1");
        counter.setIncrement("1");
        counter.setVarName("counter");
        counter.setProperty(TestElement.TEST_CLASS, CounterConfig.class.getName());
        counter.setProperty(TestElement.GUI_CLASS, CounterConfigGui.class.getName());
        return counter;
    }

    private static TestPlan createTestPlan() {
        TestPlan testPlan = new TestPlan("user-registration-load");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());
        return testPlan;
    }

    private static ResultCollector createResultCollector() {
        ResultCollector collector = new ResultCollector(new Summariser("summary"));
        collector.setName("summary + jtl");
        collector.setFilename("jmeter-results/result.jtl");
        collector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        return collector;
    }

    private static HTTPSamplerProxy createRegister(TestParams params) {
        String body = "{\"login\": \"user-${counter}\",\"password\": \"123456789\",\"algorithm\": \"SHA-256\"}";
        HTTPSamplerProxy register = new HTTPSamplerProxy();
        register.setName("POST /api/clients");
        register.setDomain(params.host());
        register.setPort(Integer.parseInt(params.port()));
        register.setPath(params.endPoint());
        register.setMethod("POST");
        register.setPostBodyRaw(true);
        register.addNonEncodedArgument("", body, "");
        register.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        register.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        return register;
    }
}
