package uk.ac.ebi.spot.solr.utils;

public class OntologyMaxNumOfValuesForField {

    private int maxNumberOfValues = 0;

    private String fieldName = "";
    private String fieldType = "";
    private String iri = "";
    private String ontologyId = "";


    public OntologyMaxNumOfValuesForField(String fieldName) {
        this.fieldName = fieldName;
    }

    public OntologyMaxNumOfValuesForField(String fieldName, String fieldType, String iri, String ontologyId,
                                          int maxNumberOfValues) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.iri = iri;
        this.ontologyId = ontologyId;
        this.maxNumberOfValues = maxNumberOfValues;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }

    public int getMaxNumberOfValues() {
        return maxNumberOfValues;
    }

    public void setMaxNumberOfValues(int maxNumberOfValues) {
        this.maxNumberOfValues = maxNumberOfValues;
    }

    public void updateInfo(String fieldType, String iri, String ontologyId,
                           int maxNumberOfValues) {

        this.maxNumberOfValues = maxNumberOfValues;
        this.fieldType = fieldType;
        this.iri = iri;
        this.ontologyId = ontologyId;
    }

    @Override
    public String toString() {
        return "OntologyMaxNumOfValuesForFieldInfo{" +
                "maxNumberOfValues=" + maxNumberOfValues +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", iri='" + iri + '\'' +
                ", ontologyId='" + ontologyId + '\'' +
                '}';
    }

    public String toTSV() {
        return maxNumberOfValues + "\t" + fieldName + "\t" + fieldType + "\t" + iri + "\t" + ontologyId;
    }
}
