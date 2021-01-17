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

package ec.devnull.osprest.model;

/**
 * @author fkarsany
 */
public class Packages {

    private String entityObjects = "objects";
    private String converterObjects = "converters";
    private String procedureContextObjects = "contexts";
    private String packageObjects = "implementations";
    private String interfacesObjects = "interfaces";
    private String restObjects = "rest";

    public String getEntityObjects() {
        return entityObjects;
    }

    public void setEntityObjects(String entityObjects) {
        this.entityObjects = entityObjects;
    }

    public String getConverterObjects() {
        return converterObjects;
    }

    public void setConverterObjects(String converterObjects) {
        this.converterObjects = converterObjects;
    }

    public String getProcedureContextObjects() {
        return procedureContextObjects;
    }

    public void setProcedureContextObjects(String procedureContextObjects) {
        this.procedureContextObjects = procedureContextObjects;
    }

    public String getPackageObjects() {
        return packageObjects;
    }

    public void setPackageObjects(String packageObjects) {
        this.packageObjects = packageObjects;
    }

    public String getInterfacesObjects() {
        return interfacesObjects;
    }

    public void setInterfacesObjects(String interfacesObjects) {
        this.interfacesObjects = interfacesObjects;
    }

    public String getRestObjects() {
        return restObjects;
    }

    public void setRestObjects(String restObjects) {
        this.restObjects = restObjects;
    }
}
