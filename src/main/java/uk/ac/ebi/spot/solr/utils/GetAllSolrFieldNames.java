package uk.ac.ebi.spot.solr.utils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import static uk.ac.ebi.spot.solr.utils.FileUtils.writeLine;

public class GetAllSolrFieldNames {

    private static final Logger logger = LoggerFactory.getLogger(GetAllSolrFieldNames.class);
    
    public static String main(String[] args) {
        String solrUrl = args[0];
        System.out.println(solrUrl);
//        String outputFileName = args[1];
//        File outputFile = new File(outputFileName);
//
//        SolrClient solrClient = new Http2SolrClient.Builder(solrUrl).build();
//
//        Set<String> allSolrFields = findAllSolrFields(solrUrl);
//
//        System.out.println(allSolrFields.size());
//
//        try (FileWriter writer = new FileWriter(outputFile)) {
//            allSolrFields.forEach(f -> writeLine(writer, f));
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            solrClient.close();
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
        return solrUrl;
    }

    public static Set<String> findAllSolrFields(SolrClient solrClient, File file) {
        Set<String> fields = new HashSet<>();

        LukeRequest lukeRequest = new LukeRequest();
        lukeRequest.setNumTerms(0); // We don't need term details, just field info

        LukeResponse lukeResponse = null;
        try {
            lukeResponse = lukeRequest.process(solrClient);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, LukeResponse.FieldInfo> fieldsMap = lukeResponse.getFieldInfo();
        if (fieldsMap != null )
            fieldsMap.forEach((k,v) -> fields.add(k)) ;

        Map<String, LukeResponse.FieldInfo> dynamicFieldsMap = lukeResponse.getDynamicFieldInfo();
        if (dynamicFieldsMap != null )
            dynamicFieldsMap.forEach((k,v) -> fields.add(k));

        try (FileWriter writer = new FileWriter(file)) {
            fields.forEach(f -> writeLine(writer, f));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fields;
    }
}