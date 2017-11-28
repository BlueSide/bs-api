package nl.blueside.api;

import org.springframework.web.socket.server.endpoint.ServerEndpointExporter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

  @Bean
  public EchoEndpoint echoEndpoint() {
    return new EchoEndpoint(echoService());
  }

  @Bean
  public EchoService echoService() {
    // ...
  }

  @Bean
  public ServerEndpointExporter endpointExporter() {
    return new ServerEndpointExporter();
  }

}
