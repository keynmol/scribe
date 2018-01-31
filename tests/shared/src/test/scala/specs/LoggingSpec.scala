package specs

import scribe._
import org.scalatest.{Matchers, WordSpec}
import scribe.modify.LogBooster
import scribe.writer.NullWriter

class LoggingSpec extends WordSpec with Matchers with Logging {
  "Logging" should {
    val testingModifier = new TestingModifier
    val testObject = new LoggingTestObject(testingModifier)
    val handler = LogHandler(writer = NullWriter, minimumLevel = Level.Debug).withModifier(testingModifier)

    "set up the logging" in {
      testingModifier.clear()
      update(_.orphan().withHandler(handler))
    }
    "have no logged entries yet" in {
      testingModifier.records.length should be(0)
    }
    "log a single entry after info log" in {
      logger.info("Info Log")
      testingModifier.records.length should be(1)
    }
    "log a second entry after debug log" in {
      logger.debug("Debug Log")
      testingModifier.records.length should be(2)
    }
    "ignore the third entry after reconfiguring without debug logging" in {
      update(_.withoutHandler(handler).withHandler(LogHandler(writer = NullWriter, minimumLevel = Level.Info).withModifier(testingModifier)))
      logger.debug("Debug Log 2")
      testingModifier.records.length should be(2)
    }
    "boost the this logging instance" in {
      update(_.withModifier(LogBooster.multiply(2.0)))
      logger.debug("Debug Log 3")
      testingModifier.records.length should be(3)
    }
    "not increment when logging to the root logger" in {
      Logger.root.error("Error Log 1")
      testingModifier.records.length should be(3)
    }
    "write a detailed log message" in {
      val lineNumber = Some(10)
      testingModifier.clear()
      testObject.testLogger()
      testingModifier.records.length should be(1)
      testingModifier.records.head.methodName should be(Some("testLogger"))
      testingModifier.records.head.lineNumber should be(lineNumber)
    }
    "write an exception" in {
      val lineNumber = Some(14)
      testingModifier.clear()
      testObject.testException()
      testingModifier.records.length should be(1)
      testingModifier.records.head.methodName should be(Some("testException"))
      testingModifier.records.head.lineNumber should be(lineNumber)
      testingModifier.records.head.message should startWith("java.lang.RuntimeException: Testing")
    }
  }
}