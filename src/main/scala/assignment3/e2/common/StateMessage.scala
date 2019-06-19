package assignment3.e2.common

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum

case class StateMessage(state: GuardianStateEnum, time: Some[Long]) {

}
