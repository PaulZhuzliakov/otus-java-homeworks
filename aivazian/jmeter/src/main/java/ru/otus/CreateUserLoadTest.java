package ru.otus;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jorphan.collections.HashTree;

public class CreateUserLoadTest {
    public static void main(String[] args) throws Exception {
        // Инициализация JMeter
        JmeterHomeConfig.init();

        //Получаем параметры запуска
        TestParams testParams = TestParamProvider.get();

        //Создаем конфигурацию JMeter
        HashTree tree = JMeterConfiguration.create(testParams);

        //Запускаем
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(tree);
        jmeter.run();
        System.out.println("готово, выборка в results.jtl");
    }
}
