package com.broscraft.cda.modules;

import com.broscraft.cda.CDAPlugin;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CDABinderModule extends AbstractModule {

    private final CDAPlugin plugin;

    public CDABinderModule(CDAPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(CDAPlugin.class).toInstance(this.plugin);
    }
}