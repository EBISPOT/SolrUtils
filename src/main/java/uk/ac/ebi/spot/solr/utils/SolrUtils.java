package uk.ac.ebi.spot.solr.utils;

import org.apache.solr.common.SolrDocument;

import java.util.List;

public class SolrUtils {

    public static String getStringFromArrayField(SolrDocument document, String fieldName) {
        Object fieldValues = document.getFieldValue(fieldName);
        if (fieldValues instanceof java.util.List) {
            List fieldValuesAsList = ((java.util.List<?>) fieldValues);
            if (fieldValuesAsList.size() == 1)
                return (String) fieldValuesAsList.get(0);
            else
                throw new IllegalArgumentException("Field  " + fieldName + " should have 1 element. Instead it found " +
                        fieldValuesAsList.size() + " values.");
        } else {
            throw new UnsupportedOperationException("Field " + fieldName + " is not a list of strings.");
        }

    }

    public static String getString(SolrDocument document, String fieldName) {
        Object fieldValue = document.getFieldValue(fieldName);
        if (fieldValue instanceof java.lang.String) {
            return (String) fieldValue;
        } else {
            throw new IllegalArgumentException("Field  " + fieldName + " was expected to be of type String but instead " +
                    "was an instance of " + fieldValue.getClass().getName());
        }
    }
}
