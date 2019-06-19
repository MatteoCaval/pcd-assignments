package assignment3.e2.rmi.stub_scheleton

case class DashboardStr(id: String, dashboard: Dashboard) extends Serializable {

  def getId: String = id

  def getDashboard: Dashboard = dashboard
}
