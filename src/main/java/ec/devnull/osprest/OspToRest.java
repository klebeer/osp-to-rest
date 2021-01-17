package ec.devnull.osprest;

import ec.devnull.osprest.generator.*;

public class OspToRest {

    private Configuration configuration;

    public OspToRest(Configuration configuration) {
        this.configuration = configuration;
    }

    public void generate() {

        MavenPomGenerator.generate(configuration);
        RestGenerator.generate(configuration);
        EntityObjectGenerator entityObjectGenerator = new EntityObjectGenerator(configuration);
        entityObjectGenerator.generate();
        ConverterObjectGenerator.generate(configuration);
        ProcedureContextGenerator.generate(configuration);
        PackageObjectGenerator.generate(configuration);
    }


}
