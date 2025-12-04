package com.reservio.reservation_system.reservationsystemdesktop;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class AppInjector {
    private static final Injector injector = Guice.createInjector(new AppModule());

    public static Injector getInjector() {
        return injector;
    }
}
