#!/bin/bash
if [ $# == 0 ]; then
    echo "Usage: $0 <solr_url> "
    echo "I.e. /run.sh http://localhost:8983/solr/ols4_entities"
    exit 1
fi
# exit if anything fails
set -e

solr_url=$1

java -jar ./target/SolrUtils-1.0-SNAPSHOT-jar-with-dependencies.jar $solr_url $field_name