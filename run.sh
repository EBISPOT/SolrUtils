#!/bin/bash
if [ $# == 0 ]; then
    echo "Usage: $0 <solr_url> <number_of_ranks> <batch_size> <out_fields_file> <out_ranked_results>"
    echo "I.e. /run.sh http://localhost:8983/solr/ols4_entities 10 100000 ./fields.tsv ./rankedResults.tsv"
    exit 1
fi
# exit if anything fails
set -e

solr_url=$1
ranks=$2
batch_size=$3
fields_file=$4
ranked_results_file=$5

java -jar ./target/SolrUtils-1.0-SNAPSHOT-jar-with-dependencies.jar $solr_url $ranks $batch_size $fields_file $ranked_results_file