package scribe

import _root_.cats.effect.Sync
import scribe.data.MDC
import scribe.message.LoggableMessage
import sourcecode.{FileName, Line, Name, Pkg}

class LoggerWrapper[F[_]](val wrapped: Logger, val sync: Sync[F]) extends Scribe[F] {
  override def log(record: LogRecord): F[Unit] = sync.delay(wrapped.log(record))

  override def log(level: Level, mdc: MDC, messages: LoggableMessage*)
                               (implicit pkg: Pkg, fileName: FileName, name: Name, line: Line): F[Unit] =
    sync.defer(super.log(level, mdc, messages: _*)(pkg, fileName, name, line))
}
