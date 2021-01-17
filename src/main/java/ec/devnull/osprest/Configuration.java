package ec.devnull.osprest;

import ec.devnull.osprest.model.PackageFunction;
import ec.devnull.osprest.model.Packages;
import lombok.Data;

import java.util.List;

@Data
public class Configuration {

    public static final boolean GENERATE_SOURCE_FOR_PLSQL_TYPES = false;
    public static final String INTERFACES_MODULE = "api";
    public static final String IMPL_MODULE = "impls";
    public static final String REST_CONTROLLERS_MODULE = "rest";

    private int maxPool = 5;
    private String jdbcUrl;
    private String jdbcDriverClassName = "oracle.jdbc.OracleDriver";
    private String username;
    private String password;
    private String sourceRoot;
    private String rootPackageName;
    private String packageName;
    private String procedureName;
    private String artifactGroupId;
    private String artifactId;
    private String artifactVersion;
    private String profile;
    private String projectName;
    private String projectDescription;
    private List<PackageFunction> packageFunctions;


    public String getPackageName() {
        if (packageName == null) {
            return "%";
        }
        return packageName;
    }

    public Packages getPackages() {
        Packages packages = new Packages();
        packages.setEntityObjects("entities");
        packages.setConverterObjects("converters");
        packages.setProcedureContextObjects("contexts");
        packages.setPackageObjects("servicesImpl");
        packages.setInterfacesObjects("services");
        packages.setRestObjects("rest");
        return packages;
    }

    public static final class Builder {

        private Configuration configuration;

        private Builder() {
            configuration = new Configuration();
        }

        public static Builder simple() {
            return new Builder();
        }


        public Builder withJdbcUrl(String jdbcUrl) {
            configuration.setJdbcUrl(jdbcUrl);
            return this;
        }


        public Builder withUsername(String username) {
            configuration.setUsername(username);
            return this;
        }

        public Builder withPassword(String password) {
            configuration.setPassword(password);
            return this;
        }

        public Builder withSourceRoot(String sourceRoot) {
            configuration.setSourceRoot(sourceRoot);
            return this;
        }

        public Builder withRootPackageName(String rootPackageName) {
            configuration.setRootPackageName(rootPackageName);
            return this;
        }


        public Builder withPackageName(String packageName) {
            configuration.setPackageName(packageName);
            return this;
        }

        public Builder withProcedureName(String procedureName) {
            configuration.setProcedureName(procedureName);
            return this;
        }

        public Builder withArtifactGroupId(String artifactGroupId) {
            configuration.setArtifactGroupId(artifactGroupId);
            return this;
        }

        public Builder withArtifactId(String artifactId) {
            configuration.setArtifactId(artifactId);
            return this;
        }

        public Builder withArtifactVersion(String artifactVersion) {
            configuration.setArtifactVersion(artifactVersion);
            return this;
        }

        public Builder withProfile(String profile) {
            configuration.setProfile(profile);
            return this;
        }

        public Builder withProjectName(String projectName) {
            configuration.setProjectName(projectName);
            return this;
        }

        public Builder withProjectDescription(String projectDescription) {
            configuration.setProjectDescription(projectDescription);
            return this;
        }


        public Configuration build() {
            return configuration;
        }
    }
}
