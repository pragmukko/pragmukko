/*
* Copyright 2015-2016 Pragmukko Project [http://github.org/pragmukko]
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
*    [http://www.apache.org/licenses/LICENSE-2.0]
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package utils

import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Created by yishchuk on 29.10.2015.
 */
class CircularBuffer[T](size: Int)(implicit mf: Manifest[T]) {

  private val arr = new Array[T](size)

  @volatile
  private var cursor = 0
  @volatile
  private var full = false

 // val monitor = new ReentrantReadWriteLock()

  //TODO: Get rid of monitors
  def push(value: T) {
   // monitor.writeLock().lock()
    try {
      arr(cursor) = value
      cursor += 1
      if (cursor >= size) full = true
      cursor %= size
    } finally {
     // monitor.writeLock().unlock()
    }
  }

  def getAll: List[T] = {
    //monitor.readLock().lock()
    try {
      (if (full) arr.drop(cursor).toList else Nil) ::: arr.take(cursor).toList
    } finally {
      //monitor.readLock().unlock()
    }
  }

  private def getArr = arr
  private def getCur = cursor
}

object CircularBuffer extends App {

  val cbuf = new CircularBuffer[String](10)

  for (x <- 0 until 17) {cbuf.push(s"aaa$x")}

  for (x <- 0 until 6) {cbuf.push(s"bbb$x")}

  val arr = cbuf.getAll

  for (v <- arr) {println(v)}

  println(s"----------- ${cbuf.getCur}")

  for (v <- cbuf.getArr) {println(v)}
}
