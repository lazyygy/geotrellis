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

package geotrellis.spark.io.s3

import geotrellis.tiling.Boundable
import geotrellis.spark.LayerId
import geotrellis.spark.io.avro._
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.io._
import geotrellis.util._

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import spray.json.JsonFormat
import software.amazon.awssdk.services.s3.S3Client

import scala.reflect.ClassTag

object S3LayerReindexer {
  def apply(attributeStore: S3AttributeStore)(implicit sc: SparkContext): LayerReindexer[LayerId] = {
    val layerReader  = S3LayerReader(attributeStore, attributeStore.getClient)
    val layerWriter  = S3LayerWriter(attributeStore)
    val layerDeleter = S3LayerDeleter(attributeStore, attributeStore.getClient)
    val layerCopier  = S3LayerCopier(attributeStore)

    GenericLayerReindexer[S3LayerHeader](attributeStore, layerReader, layerWriter, layerDeleter, layerCopier)
  }

  def apply(
    bucket: String,
    prefix: String,
    getClient: () => S3Client
  )(implicit sc: SparkContext): LayerReindexer[LayerId] =
    apply(S3AttributeStore(bucket, prefix, getClient))
}
