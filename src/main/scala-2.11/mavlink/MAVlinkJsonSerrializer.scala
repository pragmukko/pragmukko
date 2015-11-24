package mavlink

import java.lang.reflect.{Modifier, Field}

import org.mavlink.messages.MAVLinkMessage
import spray.json._

import scala.collection.JavaConversions._

/**
 * Created by max on 10/30/15.
 */
object MAVlinkJsonSerrializer {

  import utils.JsValueUtils._

  private val MAVType = "MAVType"

  def toMavlik(js:JsValue) : List[MAVLinkMessage] = {
    js match {
      case arr:JsArray => arr.elements.map(_.asInstanceOf[JsObject]).map(parseJsObjToMavlink(_)).toList
      case obj:JsObject => parseJsObjToMavlink(obj) :: Nil
      case _ => Nil
    }
  }

  def parseJsObjToMavlink(obj:JsObject) : MAVLinkMessage = {

    val mavType = obj.fields(MAVType).asInstanceOf[JsString].value
    val clazz = Class.forName("org.mavlink.messages.pixhawk." + mavType)
    val mavLinkMessage = clazz.getConstructor().newInstance().asInstanceOf[MAVLinkMessage]
    obj.fields.filterNot(_._1 == MAVType).foreach {
      kvp =>
        try {
          val field = clazz.getField(kvp._1)
          field.getType.getSimpleName match {
            case "int" => field.setInt(mavLinkMessage, kvp._2.asInt)
            case "float"   => field.setFloat(mavLinkMessage, kvp._2.asFloat)
            case "long"    => field.setLong(mavLinkMessage, kvp._2.asLong)
            case "boolean" => field.setBoolean(mavLinkMessage, kvp._2.asBoolean)
            case "String"  => field.set(mavLinkMessage, kvp._2.asString)
            case "int[]"   => field.set(mavLinkMessage, kvp._2.asIntArray)
            case other => println("0_o  type is " + other)
          }
        } catch {
          case ex:NoSuchFieldException => //ignore
          case ex:SecurityException => // ignore
        }
    }
    mavLinkMessage
  }

  def MAVLink2Json(msg:MAVLinkMessage) : JsObject = {
    val clazz = msg.getClass
    val jsFields = clazz.getFields.toList.filterNot(isFinalStatic).map {
      field =>
        field.getType.getSimpleName() match {
          case "int"     => Some( field.getName -> JsNumber(field.getInt(msg))   )
          case "long"    => Some( field.getName -> JsNumber(field.getLong(msg))  )
          case "float"   => Some( field.getName -> JsNumber(field.getFloat(msg)) )
          case "boolean" => Some( field.getName -> JsBoolean(field.getBoolean(msg)) )
          case "String"  => Some( field.getName -> JsString(field.get(msg).toString) )
          case "int[]" =>  Some(
            field.getName -> JsArray(field.get(msg).asInstanceOf[Array[Int]].map(JsNumber(_)).toVector)
          )
          case _ => None
        }
    }.filterNot(_.isEmpty).map(_.get) :+ (MAVType -> JsString(clazz.getSimpleName))

    JsObject(jsFields : _*)
  }

  private def isFinalStatic(field:Field): Boolean = {
    Modifier.isFinal(field.getModifiers) || Modifier.isStatic(field.getModifiers)
  }

}
