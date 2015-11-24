package utils

import spray.json._

/**
 * Created by max on 11/2/15.
 */
object JsValueUtils {

  implicit class JsValueUtils(js:JsValue) {

    def asInt = js.asInstanceOf[JsNumber].value.toInt

    def asLong = js.asInstanceOf[JsNumber].value.toLong

    def asFloat= js.asInstanceOf[JsNumber].value.toFloat

    def asString = js.asInstanceOf[JsString].value

    def asBoolean = js.asInstanceOf[JsBoolean].value

    def asIntArray = js.asInstanceOf[JsArray].elements.map(_.asInt).toArray[Int]

  }

}
