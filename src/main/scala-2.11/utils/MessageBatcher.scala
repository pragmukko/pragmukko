package utils

import actors.HasSize
import akka.event.LoggingAdapter
import com.typesafe.config.Config

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * Created by yishchuk on 02.11.2015.
 */
class MessageBatcher[T <: HasSize](cfg: Config)(log: =>LoggingAdapter)(processBatch: Array[T] => Any)(implicit ct: ClassTag[T]) {
  val NAME = cfg.getString("name")
  val MAX_COUNT = cfg.getInt("max-count")
  val MAX_SIZE = cfg.getMemorySize("max-size-bytes").toBytes
  val MAX_TIMEOUT = cfg.getDuration("max-timeout").toMillis
  val buffer = mutable.Queue.empty[T]

  var bufferByteSize = 0
  var bufferLast = System.currentTimeMillis()

  def process(msg: T) = {
    buffer.enqueue(msg)
    bufferByteSize += buffer.size
    val diffToLastSent = System.currentTimeMillis() - bufferLast
    if (buffer.length > MAX_COUNT || bufferByteSize > MAX_SIZE || diffToLastSent > MAX_TIMEOUT) {
      //log.(s"sending $NAME batch: [ count: ${buffer.length} | bytes: $bufferByteSize | last sent: $diffToLastSent ]")
      processBatch{buffer.dequeueAll(_ => true).toArray}
      bufferByteSize = 0
      bufferLast = System.currentTimeMillis()
    }

  }
}