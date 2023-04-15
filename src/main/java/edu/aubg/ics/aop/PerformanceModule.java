package edu.aubg.ics.aop;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class PerformanceModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(MeasurePerformance.class),
                new PerformanceInterceptor());
    }
}
