package xyz.amricko0b.quarkus.jsonrpc.jackson.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import jakarta.inject.Singleton;
import org.jboss.jandex.DotName;
import xyz.amricko0b.quarkus.jsonrpc.jackson.JacksonJsonRpcSerde;

/** Jackson specific build items */
public class QuarkusJsonRpcJacksonProcessor {

  /** Register feature */
  @BuildStep
  FeatureBuildItem feature() {
    return new FeatureBuildItem("json-rpc-jackson");
  }

  /** Provide Jackson-base serialization */
  @BuildStep
  AdditionalBeanBuildItem registerJacksonSerde() {
    return AdditionalBeanBuildItem.builder()
        .addBeanClasses(JacksonJsonRpcSerde.class)
        .setDefaultScope(DotName.createSimple(Singleton.class))
        .build();
  }
}
