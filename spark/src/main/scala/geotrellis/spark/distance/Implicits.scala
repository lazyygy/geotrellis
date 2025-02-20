/*
 * Copyright 2018 Azavea
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

package geotrellis.spark.distance

import org.locationtech.jts.geom.Coordinate
import org.apache.spark.rdd.RDD

import geotrellis.tiling.SpatialKey
import geotrellis.spark._
import geotrellis.vector.{MultiPoint, Point}

object Implicits extends Implicits

trait Implicits {
  implicit class withEuclideanDistanceRDDMethods(val self: RDD[(SpatialKey, Array[Coordinate])]) extends EuclideanDistanceRDDMethods

  implicit class withEuclideanDistancePointRDDMethods(val self: RDD[(SpatialKey, Array[Point])]) extends EuclideanDistancePointRDDMethods

  implicit class withEuclideanDistanceMultiPointRDDMethods(val self: RDD[(SpatialKey, MultiPoint)]) extends EuclideanDistanceMultiPointRDDMethods
}
