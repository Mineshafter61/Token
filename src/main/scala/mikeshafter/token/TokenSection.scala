package mikeshafter.token

class TokenSection(val maxTrains: Int, val name: String) {
    import Direction._

    var currentTrains: Int = 0
    var currentDir: Direction.Value = Direction.NONE

    def updateSignals(): Unit = {
        val signalSql: SignalSql = new SignalSql
        val upSignal = name + "-up"
        val dnSignal = name + "-dn"

        if (currentTrains == maxTrains) {
            // set both signals to red
            signalSql.setSignal(upSignal, Aspect.STOP)
            signalSql.setSignal(dnSignal, Aspect.STOP)
        } else {
            // set UP to red if the current direction is DOWN
            // set DN to red if the current direction is UP
            currentDir match {
                case Direction.UP =>
                    signalSql.setSignal(dnSignal, Aspect.STOP)
                    signalSql.setSignal(upSignal, Aspect.PROCEED)
                case Direction.DOWN =>
                    signalSql.setSignal(upSignal, Aspect.STOP)
                    signalSql.setSignal(dnSignal, Aspect.PROCEED)
                case _ =>
                    signalSql.setSignal(upSignal, Aspect.PROCEED)
                    signalSql.setSignal(dnSignal, Aspect.PROCEED)
            }
        }
    }

    def insertTrain(direction: Direction.Value): Boolean = {
        if (
          this.currentTrains < maxTrains && (this.currentDir == Direction.NONE || direction == this.currentDir)
        ) {
            this.currentTrains += 1
            this.currentDir = direction
            updateSignals()
            true
        } else false
    }

    def removeTrainOpposite(direction: Direction.Value): Boolean = {
        direction match {
            case Direction.UP   => removeTrain(Direction.DOWN)
            case Direction.DOWN => removeTrain(Direction.UP)
            case _              => false
        }
    }

    def removeTrain(direction: Direction.Value): Boolean = {
        if (direction == currentDir && currentTrains > 0) {
            currentTrains -= 1
            if (currentTrains == 0) currentDir = Direction.NONE
            updateSignals()
            true
        } else if (currentTrains == 1 && direction.id != 0) {
            currentTrains -= 1
            currentDir = Direction.NONE
            updateSignals()
            true
        } else false
    }
}
