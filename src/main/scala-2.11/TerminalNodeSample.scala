import actors.Messages.{BinnaryCmd, ListMembers}
import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.cluster.{Member, Cluster}
import mavlink.pixhawk.DroneCommands
import utils.{ClusterNode, ConfigProvider}
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.io.StdIn
import akka.pattern._

/**
 * Created by max on 11/11/15.
 */
object TerminalNodeSample extends App with ClusterNode with DroneCommands {

  var handler:PartialFunction[List[String], Unit] = startHandler
  var intro = "> "

  while (true) {
    (handler orElse commonHandler) (StdIn.readLine(intro).split("\\s").toList)
  }

  def startHandler:PartialFunction[List[String], Unit] = {

    case "list" :: Nil =>
      listMembers.zipWithIndex.foreach {
        m => println( List(m._2, m._1.uniqueAddress.address, m._1.roles.mkString(" ")).mkString(" "))
      }
      println()

    case "use" :: index :: Nil =>
      handler = selectdNodeHandler(embedded(listMembers(index.toInt)))
      intro = listMembers(index.toInt).address + "> "

  }

  def selectdNodeHandler(member:ActorRef) : PartialFunction[List[String], Unit] = {

    case "ping" :: Nil => println(member ?? "ping")

    case "moveto" :: x :: y :: z :: Nil =>
      member ! moveTo(x.toFloat, y.toFloat, z.toFloat)

    case "direction" :: vx :: vy :: Nil =>
      member ! moveTo(0, 0, 0, vx.toFloat, vy.toFloat, 0f)

    case "where" :: Nil =>
      println( member ?? "where are you man?" )

    case "by" :: Nil =>
      handler = startHandler
      intro = "> "

  }

  def commonHandler: PartialFunction[List[String], Unit] = {

    case "help" :: rest => println(
      """
        | list  - list of cluster members
        | use X - start work with X member of cluster nodes
        | exit  - close terminal
        |
        |  Next commands works only after cluster node selected with 'use x'
        |
        | ping         - ping node (node mast return 'Pong')
        | where        - ask drone for physical position
        | moveto X Y Z - tell drone move to new position relative to current
        | by           - stop working with current node
        |
      """.stripMargin)

    case "exit" :: Nil => System.exit(0)

    case other => println(s"""Unknown command: '${other.mkString(" ")}'""")

  }

}

