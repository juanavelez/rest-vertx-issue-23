package issue.server;

import com.zandero.rest.RestBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Proxy;

public class ServerVerticle extends AbstractVerticle {
    private HttpServer server;

    @Override
    public void start(Future<Void> startFuture) {
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setPort(8980);
        server = vertx.createHttpServer(serverOptions);

        setupRouter(startFuture);
    }

    /**
     * Sets up the Vertx Web Router
     * @param startFuture The start future for the verticle
     */
    private void setupRouter(Future<Void> startFuture) {
        Router router = Router.router(vertx);

        setupLoginController(router);

        server.requestHandler(router::accept).listen(ar -> {
            if (ar.failed()) {
                startFuture.fail(ar.cause());
            } else {
                startFuture.complete();
            }
        });
    }

    /**
     * Sets up the router with the login controller
     * @param router the vertx web router to set up
     */
    private void setupLoginController(Router router) {
        LoginControllerContract loginController = new LoginController();

        LoginControllerContract proxy = (LoginControllerContract) Proxy.newProxyInstance(this.getClass().getClassLoader(),
            new Class[] { LoginControllerContract.class },
            (p, m, a) -> m.invoke(loginController, a)
        );


        // It works if loginController is registered directly.
        new RestBuilder(router)
            .register(proxy)
            .build();
    }

    @Path("/user")
    public interface LoginControllerContract {
        @Path("/login")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        Future<Boolean> login(Credentials credentials);
    }

    public static class LoginController implements LoginControllerContract {
        @Override
        public Future<Boolean> login(Credentials credentials) {
            return Future.succeededFuture(true);
        }
    }

    public static class Credentials {
        private String email;
        private String password;

        public Credentials() {

        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static void main(String[] args) {
        ServerVerticle serverVerticle = new ServerVerticle();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(serverVerticle);
    }
}
