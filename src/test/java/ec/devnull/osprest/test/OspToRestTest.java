package ec.devnull.osprest.test;

import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.OspToRest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
public class OspToRestTest {

    @Test
    @DisplayName("Generate Rest Test")
    public void generateRestTest() {
        Configuration configuration = Configuration.Builder
                .simple()
                .withArtifactGroupId("dev.nullec.demo")
                .withArtifactId("test-rest")
                .withArtifactVersion("1.0.0-SNAPSHOT")
                .withJdbcUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe")
                .withUsername("alfresco")
                .withPassword("alfresco")
                .withProfile("dev")
                .withSourceRoot("/Users/ayalak/Desktop/test")
                .withRootPackageName("dev.nullec.sp")
                .withProjectName("Demo SP")
                .build();
        OspToRest ospToRest = new OspToRest(configuration);
        ospToRest.generate();
    }
}
