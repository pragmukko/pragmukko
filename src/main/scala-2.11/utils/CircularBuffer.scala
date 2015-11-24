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