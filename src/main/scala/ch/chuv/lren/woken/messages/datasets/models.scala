/*
 * Copyright (C) 2017  LREN CHUV for Human Brain Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.chuv.lren.woken.messages.datasets

import ch.chuv.lren.woken.messages.remoting.RemoteLocation

/**
  * Id of a dataset
  *
  * @param code Unique dataset code
  */
case class DatasetId(
    code: String
) extends Comparable[DatasetId] {
  override def compareTo(t: DatasetId): Int = this.code.compareTo(t.code)
}

object AnonymisationLevel extends Enumeration {
  type AnonymisationLevel = Value
  val Identifying, Depersonalised, Anonymised = Value
}

import AnonymisationLevel.AnonymisationLevel

case class Dataset(dataset: DatasetId,
                   label: String,
                   description: String,
                   tables: List[String],
                   anonymisationLevel: AnonymisationLevel,
                   location: Option[RemoteLocation]) {

  def withoutAuthenticationDetails: Dataset =
    copy(location = location.map(_.copy(credentials = None)))

}

/** Identifier for a table
  *
  * @param database The database owning the table
  * @param dbSchema The schema containing the table, or None for default 'public' schema
  * @param name The name of the table, without any quotation
  */
case class TableId(database: String, dbSchema: Option[String], name: String) {

  assert(!name.contains("."), "Table name should not contain schema information")
  // Disallow access to all system tables for security
  assert(database != "woken" && database != "metadata" && database != "postgres")

  def schemaOrPublic: String = dbSchema.getOrElse("public")

  def same(other: TableId): Boolean =
    (other.dbSchema == dbSchema || dbSchema.isEmpty && other.dbSchema
      .contains("public")) && (other.name == name)

  override def toString: String = s"$database.$schemaOrPublic.$name"
}

object TableId {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def apply(database: String, qualifiedTableName: String): TableId =
    qualifiedTableName.split("\\.").toList match {
      case tableName :: Nil                   => TableId(database, None, tableName)
      case dbSchema :: tableName :: Nil       => TableId(database, Some(dbSchema), tableName)
      case db :: dbSchema :: tableName :: Nil => TableId(db, Some(dbSchema), tableName)
      case _                                  => throw new IllegalArgumentException(s"Invalid table name: $qualifiedTableName")
    }
}

// TODO: doesn't seem to be used
case class Table(tableId: TableId, defaultGroupings: Set[String])
