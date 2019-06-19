package assignment3.e2.rmi.mapentry

import assignment3.e2.rmi.remoteobjects.Dashboard

case class DashboardEntry(private val id: String, private val dashboard: Dashboard) extends Serializable {

  def getId: String = id

  def getRemoteObject: Dashboard = dashboard
}
