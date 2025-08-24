package mikeshafter.token

class TokenSection(val maxTrains: Int) {
import Direction._

var currentTrains: Int = 0
var currentDir: Direction.Value = Direction.NONE

def insertTrain(direction: Direction.Value): Boolean = {
	if (currentTrains < maxTrains && (currentDir == Direction.NONE || direction == currentDir)) {
		currentTrains += 1
		currentDir = direction
		true
	} else false
}

def removeTrainOpposite(direction: Direction.Value): Boolean = {
	direction match {
		case Direction.UP => removeTrain(Direction.DOWN)
		case Direction.DOWN => removeTrain(Direction.UP)
		case _ => false
	}
}

def removeTrain(direction: Direction.Value): Boolean = {
	if (direction == currentDir && currentTrains > 0) {
		currentTrains -= 1
		if (currentTrains == 0) currentDir = Direction.NONE
		true
	} else if (currentTrains == 1 && direction.id != 0) {
		currentTrains -= 1
		currentDir = Direction.NONE
		true
	} else false
}
}