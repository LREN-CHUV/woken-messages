
# Changelog

## Unreleased

* Add Akka management to the library dependencies
* Add Bugsnag error reporter and a Log4j configuration that forwards errors to Bugsnag

## 2.9.0  2019-01-14

* Provenance: Keep track of original query in QueryResult
* Provenance: Keep track of datasets used in QueryResult
* Provide user feedback in QueryResult
* Add flag for 'covariables must exist' on the queries
* __dev__ Update Update Akka to 2.5.19, akka-http to 10.1.5, cats-core to 1.5.0, postgresql to 42.2.5

## 2.8.0 - 2018-05-18

* Support filters comparing numeric values
* Add ping message
* Add Tables query and response
* Add shape for scores
* Add optional step spec to algorithm spec
* Rename shape field to type in Query result
* __dev__ Add common configuration for Akka remoting
* __dev__ Add merge operation for variable meta
* __dev__ Change execution plan for better serialization
* __fix__ Fix classification of text shape

## 2.7.0 - 2018-04-19

* Update metadata variables messages
* Filters for conjunctions containing empty rules
* __dev__ Update Akka to 2.5.12
* __dev__ Option to log config on start
* __dev__ Move Shapes to this library
* __fix__ Fix serialization of Scoring
* __fix__ Fix json serialization of Dataset and RemoteLocation
* __test__ Add test for serialisation of dataset

## 2.6.0 - 2018-03-16

* Akka serializers for all API messages
* Add table parameter to DatasetsQuery
* Improve VariableForDatasets messaging to include possible errors
* __test__ Test churn variables
* __test__ Add tests for IN filters

## 2.5.0 - 2018-03-08

* Serialize the validation messages using Json
* Use JsValue for validation result
* Use Either for ValidationResult and ScoringResult, use JsValue in ScoringQuery
* Use more precise types for validation and scoring
* Add statistics for variables
* Improve publishing process
* __dev__ Update Scala to 2.11.12
* __dev__ Moved convertion of filter rules to SQL from Woken
* __dev__ Change DatasetsQuery message to a case object
* __test__ Add tests for scores, scoring result
* __fix__ Fix serializer
* __fix__ Fix serialisation of KFoldCrossValidationScore

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
