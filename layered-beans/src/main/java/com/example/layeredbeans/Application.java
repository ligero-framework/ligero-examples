package com.example.layeredbeans;

import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.Modules;
import com.ligero.beans.Beans;
import com.ligero.devtools.Devtools;
import com.ligero.middleware.RequestLoggingMiddleware;

/**
 * La misma app por capas que layered-api, pero modular: el startup NO
 * contiene wiring — solo lista módulos ({@link ProductsModule} declara sus
 * beans y rutas). Devtools queda montado en /ligero/dev para navegar el
 * grafo y trazar cada request capa por capa.
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        long startNanos = System.nanoTime();

        Ligero app = create();
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        System.out.println("layered-beans started in " + millis + " ms -> http://localhost:" + app.port());
        System.out.println("devtools -> http://localhost:" + app.port() + "/ligero/dev");
    }

    public static Ligero create() {
        Ligero app = Ligero.create(8080);
        app.use(new RequestLoggingMiddleware());

        // Debugger visual en /ligero/dev (solo desarrollo; LIGERO_DEVTOOLS=false lo apaga).
        Devtools devtools = Devtools.create();
        Beans beans = Modules.install(app, devtools.recorder(), modules());
        devtools.install(app, beans);

        return app;
    }

    /** Los módulos de la app — agregar una feature = agregar una línea. */
    static LigeroModule[] modules() {
        return new LigeroModule[] {
            new ProductsModule(),
        };
    }
}
