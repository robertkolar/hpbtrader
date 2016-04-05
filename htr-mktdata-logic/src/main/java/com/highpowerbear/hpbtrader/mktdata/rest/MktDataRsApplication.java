package com.highpowerbear.hpbtrader.mktdata.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by robertk on 19.11.2015.
 */
@ApplicationPath("rest")
public class MktDataRsApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        // register root resources/providers
        classes.add(IbAccountService.class);
        classes.add(DataSeriesService.class);
        classes.add(TechAnalysisService.class);
        return classes;
    }
}