package assignment3.e2.common

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum


case class DashboardGuardianState(id: String, averageTemp: Option[Double], state: GuardianStateEnum, patch: Patch) {
  private val roundedAverageTemp: Option[Double] =
    if (averageTemp.isDefined) Some(BigDecimal(averageTemp.get)
      .setScale(3, BigDecimal.RoundingMode.HALF_UP)
      .toDouble)
    else None

  override def toString: String = {
    s"Guardian $id - ${state.toString} - Avg temp: ${roundedAverageTemp.getOrElse(" - ")}"
  }
}