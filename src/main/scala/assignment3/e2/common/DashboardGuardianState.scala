package assignment3.e2.common

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum


case class DashboardGuardianState(id: String, averageTemp: Option[Double], state: GuardianStateEnum, patch: Patch) {

  override def toString: String = {
    s"Guardian $id - ${state.toString} - Avg temp: ${averageTemp.getOrElse(" - ")}"
  }

}