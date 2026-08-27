package hw10;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.modifiers.CounterConfig;
import org.apache.jmeter.modifiers.gui.CounterConfigGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.DoubleProperty;
import org.apache.jmeter.testelement.property.IntegerProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.timers.ConstantThroughputTimer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

// Генератор нагрузки на сервис регистрации из hw-07 (POST /api/clients)
// Запуск: java -Drps=10 -Dthreads=10 -Dduration=60 -jar target/load-generator.jar (нужен JMETER_HOME)
public class LoadGenerator {

    public static void main(String[] args) throws Exception {
        String host = System.getProperty("host", "localhost");
        int port = Integer.parseInt(System.getProperty("port", "8080"));
        int threads = Integer.parseInt(System.getProperty("threads", "10"));
        int ramp = Integer.parseInt(System.getProperty("ramp", "5"));
        int duration = Integer.parseInt(System.getProperty("duration", "60"));
        double rps = Double.parseDouble(System.getProperty("rps", "5"));

        String jmeterHome = System.getenv("JMETER_HOME");
        if (jmeterHome == null) {
            throw new IllegalStateException("не задан JMETER_HOME - путь до каталога apache-jmeter");
        }
        JMeterUtils.loadJMeterProperties(jmeterHome + "/bin/jmeter.properties");
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.initLocale();

        // в теле каждый раз новые name/email (${__Random} тут не работает, поэтому счётчик)
        String body = "{\"name\":\"user-${counter}\",\"email\":\"user-${counter}@example.com\"}";
        HTTPSamplerProxy register = new HTTPSamplerProxy();
        register.setName("POST /api/clients");
        register.setDomain(host);
        register.setPort(port);
        register.setPath("/api/clients");
        register.setMethod("POST");
        register.setPostBodyRaw(true);
        register.addNonEncodedArgument("", body, "");
        register.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        register.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        HeaderManager headers = new HeaderManager();
        headers.setName("HTTP Header Manager");
        headers.add(new Header("Content-Type", "application/json"));
        headers.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headers.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());

        CounterConfig counter = new CounterConfig();
        counter.setName("counter");
        counter.setStart("1");
        counter.setIncrement("1");
        counter.setVarName("counter");
        counter.setProperty(TestElement.TEST_CLASS, CounterConfig.class.getName());
        counter.setProperty(TestElement.GUI_CLASS, CounterConfigGui.class.getName());

        // таймер считает в запросах в минуту, поэтому rps * 60
        ConstantThroughputTimer timer = new ConstantThroughputTimer();
        timer.setName("RPS = " + rps);
        timer.setProperty(new DoubleProperty("throughput", rps * 60));
        timer.setProperty(new IntegerProperty("calcMode", 1)); // 1 = по всем активным потокам
        timer.setProperty(TestElement.TEST_CLASS, ConstantThroughputTimer.class.getName());
        timer.setProperty(TestElement.GUI_CLASS, TestBeanGUI.class.getName());

        LoopController loop = new LoopController();
        loop.setLoops(-1);
        loop.setFirst(true);
        loop.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loop.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loop.initialize();

        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("users");
        threadGroup.setNumThreads(threads);
        threadGroup.setRampUp(ramp);
        threadGroup.setScheduler(true);
        threadGroup.setDuration(duration);
        threadGroup.setSamplerController(loop);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        TestPlan testPlan = new TestPlan("user-registration-load");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // summary в консоль + все ответы в results.jtl
        ResultCollector collector = new ResultCollector(new Summariser("summary"));
        collector.setName("summary + jtl");
        collector.setFilename("results.jtl");
        collector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());

        // корень дерева должен быть ListedHashTree, иначе ClassCastException
        HashTree tree = new ListedHashTree();
        HashTree groupTree = tree.add(testPlan, threadGroup);
        groupTree.add(register, headers);
        groupTree.add(counter);
        groupTree.add(timer);
        tree.add(testPlan, collector);

        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(tree);
        System.out.printf("нагрузка: %d потоков, rps=%.0f, %d сек -> http://%s:%d/api/clients%n",
                threads, rps, duration, host, port);
        jmeter.run();
        System.out.println("готово, выборка в results.jtl");
    }
}
