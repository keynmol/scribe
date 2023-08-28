package scribe.slf4j

import org.slf4j.helpers.FormattingTuple
import scribe.message.LoggableMessage
import scribe.{Level, LogRecord}

object SLF4JHelper {
  def log(name: String, level: Level, msg: String, t: Option[Throwable]): Unit = {
    val scribeLogger = scribe.Logger(name)
    val messages: List[LoggableMessage] = LoggableMessage.string2LoggableMessage(msg) ::
      LoggableMessage.throwableList2Messages(t.toList)
    val record = LogRecord(
      level = level,
      levelValue = level.value,
      messages = messages,
      fileName = "",
      className = name,
      methodName = None,
      line = None,
      column = None
    )
    scribeLogger.log(record)
  }

  def logTuple(name: String, level: Level, tuple: FormattingTuple): Unit = {
    log(name, level, tuple.getMessage, Option(tuple.getThrowable))
  }

  def includes(name: String, level: Level): Boolean = scribe.Logger(name).includes(level)
}
