
# Changelog

* Provenance: Keep track of original query in QueryResult
* Provenance: Keep track of datasets used in QueryResult
* Provide user feedback in QueryResult
* Add flag for 'covariables must exist' on the queries
* [dev] Update Update Akka to 2.5.19, akka-http to 10.1.5, cats-core to 1.5.0, postgresql to 42.2.5

## 2.8.0 - 2018-05-18

* Support filters comparing numeric values
* Add ping message
* Add Tables query and response
* Add shape for scores
* Add optional step spec to algorithm spec
* Rename shape field to type in Query result
* [dev] Add common configuration for Akka remoting
* [dev] Add merge operation for variable meta
* [dev] Change execution plan for better serialization
* [fix] Fix classification of text shape

## 2.7.0 - 2018-04-19

* Update metadata variables messages
* Filters for conjunctions containing empty rules
* [dev] Update Akka to 2.5.12
* [dev] Option to log config on start
* [dev] Move Shapes to this library
* [fix] Fix serialization of Scoring
* [fix] Fix json serialization of Dataset and RemoteLocation
* [test] Add test for serialisation of dataset

## 2.6.0 - 2018-03-16

* Akka serializers for all API messages
* Add table parameter to DatasetsQuery
* Improve VariableForDatasets messaging to include possible errors
* [test] Test churn variables
* [test] Add tests for IN filters

## 2.5.0 - 2018-03-08

* Serialize the validation messages using Json
* Use JsValue for validation result
* Use Either for ValidationResult and ScoringResult, use JsValue in ScoringQuery
* Use more precise types for validation and scoring
* Add statistics for variables
* Improve publishing process
* [dev] Update Scala to 2.11.12
* [dev] Moved convertion of filter rules to SQL from Woken
* [dev] Change DatasetsQuery message to a case object
* [test] Add tests for scores, scoring result
* [fix] Fix serializer
* [fix] Fix serialisation of KFoldCrossValidationScore

## 2.4.0 - 2018-02-16

* Rebranding, migrate classes to package ch.chuv.lren.woken.*
* Add metadata for groups
* Update Dataset

## 2.3.0 - 2018-01-24

* Reorganise and add datasets, queries on variables
* Improve definition of GroupId

## 2.2.0 - 2018-01-15

* Add support for multiple datasets and results harvested from several sites
* Add execution plan for experiments
* Many changes in the definition of each class / json document

## 2.1.0 - 2017-12-05

* Improve validation and scoring classes
* Add common support classes for akka-cluster

## 2.0.0 - 2017-11-07

* Migrate classes to package eu.hbp.mip.woken.*
* Add domain objects, in particular Error which can be send to clients in some cases.

## 1.0.0 - 2016-12-07

* First stable version
