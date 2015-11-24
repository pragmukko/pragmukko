package api

/**
 * Created by max on 11/5/15.
 */
trait TelemetryStorage {

  def push(telemetry:Array[Any])

}
