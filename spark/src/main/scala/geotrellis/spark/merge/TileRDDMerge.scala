/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.spark.merge

import geotrellis.raster._
import geotrellis.raster.merge._

import org.apache.spark._
import org.apache.spark.rdd._

import scala.reflect.ClassTag

object TileRDDMerge {
  def apply[K: ClassTag, V <: CellGrid[Int]: ClassTag: ? => TileMergeMethods[V]](rdd: RDD[(K, V)], other: RDD[(K, V)]): RDD[(K, V)] = {
    rdd
      .cogroup(other)
      .map { case (key, (myTiles, otherTiles)) =>
        if (myTiles.nonEmpty && otherTiles.nonEmpty) {
          val a = myTiles.reduce(_ merge _)
          val b = otherTiles.reduce(_ merge _)
          (key, a merge b)
        } else if (myTiles.nonEmpty) {
          (key, myTiles.reduce(_ merge _))
        } else {
          (key, otherTiles.reduce(_ merge _))
        }
      }
  }

  def apply[K: ClassTag, V <: CellGrid[Int]: ClassTag: ? => TileMergeMethods[V]](rdd: RDD[(K, V)], partitioner: Option[Partitioner]): RDD[(K, V)] = {
    partitioner match {
      case Some(p) =>
        rdd
          .reduceByKey(p, _ merge _)
      case None =>
        rdd
          .reduceByKey(_ merge _)
    }
  }
}
