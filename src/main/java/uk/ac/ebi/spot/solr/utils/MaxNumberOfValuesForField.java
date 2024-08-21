package uk.ac.ebi.spot.solr.utils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaxNumberOfValuesForField {

    private static final Logger logger = LoggerFactory.getLogger(MaxNumberOfValuesForField.class);

    public static Set<String> findAllSolrFields(String solrUrl) {
        Set<String> fields = new HashSet<>();

        try (SolrClient solrClient = new Http2SolrClient.Builder(solrUrl).build()) {

            LukeRequest lukeRequest = new LukeRequest();
            lukeRequest.setNumTerms(0); // We don't need term details, just field info

            LukeResponse lukeResponse = lukeRequest.process(solrClient);

            Map<String, LukeResponse.FieldInfo> fieldsMap = lukeResponse.getFieldInfo();
            if (fieldsMap != null )
                fieldsMap.forEach((k,v) -> fields.add(k)) ;

            Map<String, LukeResponse.FieldInfo> dynamicFieldsMap = lukeResponse.getDynamicFieldInfo();
            if (dynamicFieldsMap != null )
                dynamicFieldsMap.forEach((k,v) -> fields.add(k));

            logger.debug("List of Solr fields: ");
            fields.forEach(f -> logger.debug(f));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return fields;
    }

    
    public static void main(String[] args) {
        String solrUrl = args[0];

        SolrClient solrClient = new Http2SolrClient.Builder(solrUrl).build();

        final Map<String, Integer> maxValues = new HashMap<>();

        Set<String> fieldNames = findAllSolrFields(solrUrl);

//        Set<String> fieldNames = new HashSet<>();
//        fieldNames.add("http__//purl.obolibrary.org/obo/ICEO_0000133");

        fieldNames.forEach(fieldName -> {
            int startIndex = 0;
            int numberOfRowToFetch = 100000;
            long totalDocuments = 0;
            int numDocsRetrieved = 0;

            logger.info("Processing docs from field: {}", fieldName);

            String fieldNameEscaped = ClientUtils.escapeQueryChars(fieldName);
            logger.debug("fieldName {} escaped: {}", fieldName, fieldNameEscaped);

            String fieldToUseInQuery = null;
            if (fieldName.contains("/")) {
                // Solr field names should consist of alphanumeric or underscore characters only and not start with a digit.
                // This is not currently strictly enforced, but other field names will not have first class support from
                // all components and back compatibility is not guaranteed.
                //
                // OLS uses slashes in field names which work when doing queries such as q=http__//...:*, but it fails
                // when specifying field name with slashes for a field list. For performence we need to set the field list.
                // Hence, this hack to at least reduce field list.
                //
                String prefix = fieldName.substring(0, fieldName.indexOf('/'));
                fieldToUseInQuery = prefix + "*";
            } else {
                fieldToUseInQuery = fieldName;
            }

            logger.debug("fieldToUseInQuery = {}", fieldToUseInQuery);

            while (true) {
                logger.info("Processing docs from {} to {}", startIndex, startIndex + numberOfRowToFetch);

                SolrQuery query = createQueryForDocsWithField(fieldToUseInQuery, fieldNameEscaped, startIndex, numberOfRowToFetch);
                try {
                    numDocsRetrieved = queryAndTraverseResults(solrClient, query, fieldName, maxValues);
                    logger.trace("MaxValues this far: {}", maxValues);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    break;
                }

                totalDocuments += numDocsRetrieved;

                // Break the loop if we have fetched all documents
                if (numDocsRetrieved < numberOfRowToFetch) {
                    break;
                }

                startIndex += numberOfRowToFetch;
            }
            logger.info("Total docs found for fieldName {} is {}", fieldName, totalDocuments);
        });

        logger.debug("Fields with max number of values: ");
        maxValues.forEach((k,v) -> logger.debug(k + ": " + v));

        try {
            solrClient.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static int queryAndTraverseResults(SolrClient solrClient, SolrQuery query, String fieldName,
                                               Map<String, Integer> maxValues) throws Exception {

        QueryResponse response = solrClient.query(query);
        SolrDocumentList documents = response.getResults();

        documents.forEach(document -> {
            Object fieldValues = document.getFieldValue(fieldName);
            if (fieldValues instanceof java.util.List) {
                int maxSizeThisFar = 0;
                if (maxValues.containsKey(fieldName)) {
                    maxSizeThisFar = maxValues.get(fieldName);
                    int currentMaxSize = ((java.util.List<?>) fieldValues).size();
                    if (currentMaxSize > maxSizeThisFar) {
                        maxValues.put(fieldName, currentMaxSize);
                        logger.trace("Put maxValues: {} -> {} ", fieldName, currentMaxSize);
                    }
                } else {
                    int size = ((java.util.List<?>) fieldValues).size();
                    maxValues.put(fieldName, size);
                    logger.trace("Put maxValues: {} -> {} ", fieldName, size);
                }
            }
        });
        return documents.size();
    }

    private static SolrQuery createQueryForDocsWithField(String fieldNameTransformed, String fieldNameEscaped, int start,
                                                         int numberOfRowToFetch) {


        SolrQuery query = new SolrQuery();
        query.setQuery(fieldNameEscaped + ":*");

        query.setFields(fieldNameTransformed);

        query.setStart(start);
        query.setRows(numberOfRowToFetch);
        return query;
    }
}