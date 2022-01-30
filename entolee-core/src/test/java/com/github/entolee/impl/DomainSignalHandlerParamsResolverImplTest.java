package com.github.entolee.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DomainSignalHandlerParamsResolverImplTest {

    private final TestTarget target = new TestTarget();
    private final TestCommand testCmd = new TestCommand();
    @InjectMocks
    private SignalHandlerParamsResolverImpl resolver;

    @Test
    void commandArgResolvedWhenInterface() {
        final TestCommandArgImpl1 cmdArg1 = new TestCommandArgImpl1();
        final Object[] cmdArgs = {cmdArg1};
        final Method method = ClassUtils.getMethod(target.getClass(), "methodWithArgs", testCmd.getClass(), TestCommandArg.class);

        final Object[] params = resolver.resolveParams(testCmd, cmdArgs, method);

        assertThat(params)
            .containsExactly(testCmd, cmdArg1);
    }

    @Test
    void commandArgResolvedWhenSupperClass() {
        final TestCommandArgImpl2 cmdArg1 = new TestCommandArgImpl2();
        final Object[] cmdArgs = {cmdArg1};
        final Method method = ClassUtils.getMethod(target.getClass(), "methodWithArgs", testCmd.getClass(), TestCommandArg.class);

        final Object[] params = resolver.resolveParams(testCmd, cmdArgs, method);

        assertThat(params)
            .containsExactly(testCmd, cmdArg1);
    }


    public interface TestCommandArg {

    }

    public static class TestCommand {

    }

    public static class TestTarget {


        public void methodWithArgs(TestCommand command, final TestCommandArg arg) {
        }

    }

    public static class TestCommandArgImpl1 implements TestCommandArg {

    }

    public static class TestCommandArgImpl2 extends TestCommandArgImpl1 {

    }
}