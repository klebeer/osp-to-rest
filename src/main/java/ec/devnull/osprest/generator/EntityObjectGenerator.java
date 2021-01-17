/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Ferenc Karsany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package ec.devnull.osprest.generator;

import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.db.TypeDao;
import ec.devnull.osprest.generator.data.Pojo;
import ec.devnull.osprest.generator.data.PojoMapper;
import ec.devnull.osprest.model.TypeAttribute;
import ec.devnull.osprest.util.CodeFormatter;
import ec.devnull.osprest.util.DataSourceProvider;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fkarsany on 2015.01.28..
 */
public final class EntityObjectGenerator {

    private Configuration configuration;
    private String packageName;
    private String outputDir;


    public EntityObjectGenerator(Configuration configuration) {
        this.configuration = configuration;
        this.packageName = configuration.getRootPackageName() + "." + configuration.getPackages().getEntityObjects();
        this.outputDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.INTERFACES_MODULE + "/src/main/java/" + packageName.replace(".", "/") + "/";
    }


    public void generate(List<String> types) {
        try {
            TypeDao typeDao = new TypeDao(DataSourceProvider.getDataSource(configuration));
            typeDao.validateTypeList(types);
            for (String typeName : types) {
                generateEntityObject(packageName, outputDir, typeName, typeDao.getTypeAttributes(typeName));
            }

            if (types.size() == 0) {
                generateEntityObject(packageName, outputDir, "Dummy", new ArrayList<>());
            }

            if (Configuration.GENERATE_SOURCE_FOR_PLSQL_TYPES) {
                List<String> embeddedTypes = typeDao.getEmbeddedTypeList();
                for (String typeName : embeddedTypes) {
                    generateEntityObject(packageName, outputDir, typeName, typeDao.getEmbeddedTypeAttributes(typeName));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void generate() {
        try {
            TypeDao typeDao = new TypeDao(DataSourceProvider.getDataSource(configuration));
            List<String> types = typeDao.getTypeList(configuration.getPackageName());
            generate(types);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private void generateEntityObject(String packageName, String outputDir, String typeName, List<TypeAttribute> typeAttributes) throws IOException {
        Pojo pojo = PojoMapper.typeToPojo(typeName, typeAttributes);
        pojo.setPackageName(packageName);
        pojo.setGeneratorName("EntityObjectGenerator");

        String javaSource = MustacheRunner.build("pojo.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + pojo.getClassName() + ".java"), CodeFormatter.format(javaSource), "utf-8");
    }

}
