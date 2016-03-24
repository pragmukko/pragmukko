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
