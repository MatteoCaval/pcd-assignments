package assignment3.e2.common

import java.awt._

import javax.swing._

import scala.collection.Map

trait ViewListener {
  def resetAlarmPressed(patchId: Int): Unit
}

object TestView extends App {

  val view = new MapMonitorViewImpl(new ViewListener {
    override def resetAlarmPressed(patchId: Int): Unit = {
      println(patchId)
    }
  }, PatchManager.getPatches.size)

  view.show()
  view.notifySensor(DashboardSensorPosition("1", P2d(50, 50)))
  view.notifySensor(DashboardSensorPosition("2", P2d(80, 80)))

  view.notifyGuardian(DashboardGuardianState("0", Some(58.3), GuardianStateEnum.IDLE, PatchManager.getPatches(0)))
  view.notifyGuardian(DashboardGuardianState("1", Some(58.3), GuardianStateEnum.IDLE, PatchManager.getPatches(1)))

  Thread.sleep(2000)

  view.notifySensorRemoved("1")


  Thread.sleep(2000)

  view.notifyAlarmStateEnabled(2, enabled = true)


  Thread.sleep(2000)

  view.notifyGuardianRemoved("0", 0)


}


class MapMonitorViewImpl(listener: ViewListener, patchNumber: Int) extends MapMonitorView {
  private val rowNum = PatchManager.M
  private val colNum = PatchManager.N

  private val w: Int = PatchManager.width.toInt
  private val h: Int = PatchManager.height.toInt

  private var sensorMap: Map[String, P2d] = Map()
  private var patchMap: Map[Int, Map[String, DashboardGuardianState]] = Map()
  private var patchPanelList: Seq[PatchVisualizerPanel] = Seq()

  private val frame: VisualiserFrame = new VisualiserFrame(w, h)

  def show(): Unit = {
    SwingUtilities.invokeLater(() => {
      frame.setVisible(true)
    })
  }

  def updateMap(): Unit = {
    SwingUtilities.invokeLater(() => {
      frame.repaint()
    })
  }

  override def notifySensor(sensorPos: DashboardSensorPosition): Unit = {
    sensorMap += (sensorPos.id -> sensorPos.position)
    updateMap()
  }

  override def notifyGuardian(guardianState: DashboardGuardianState): Unit = {
    val patchId = guardianState.patch.id

    // aggiungo o aggiorno nella patch indicata
    var guardianMap: Map[String, DashboardGuardianState] = if (patchMap.contains(patchId)) patchMap(patchId) else Map()
    guardianMap = guardianMap + (guardianState.id -> guardianState)
    patchMap += (patchId -> guardianMap)

    patchPanelList(patchId).updateGuardian()
  }

  override def notifySensorRemoved(sensorId: String): Unit = {
    sensorMap -= sensorId
    updateMap()
  }

  override def notifyGuardianRemoved(guardianId: String, patchId: Int): Unit = {
    var guardianMap: Map[String, DashboardGuardianState] = patchMap(patchId)
    if (patchMap != null){
      guardianMap -= guardianId
      patchMap += (patchId -> guardianMap)

      patchPanelList(patchId).updateGuardian()
    }
  }

  override def notifyAlarmStateEnabled(patchId: Int, enabled: Boolean): Unit = {
    patchPanelList(patchId).setAlarmEnabled(enabled)
  }

  private class VisualiserFrame(val w: Int, val h: Int) extends JFrame(".:: MapMonitor ::.") {
    setResizable(true)

    private val mapPanel: MapVisualizerPanel = new MapVisualizerPanel(w, h)

    var controlPanel = new JPanel()
    controlPanel.setLayout(new GridLayout(0, 1))

    for (i <- 0 until patchNumber) {
      val patchPanel = new PatchVisualizerPanel(i)
      controlPanel.add(patchPanel)
      patchPanelList = patchPanelList :+ patchPanel
    }

    private val scrollPanel = new JScrollPane(controlPanel)
    scrollPanel.setPreferredSize(new Dimension(550, h))

    val cp = new JPanel
    cp.setLayout(new BorderLayout)
    cp.add(mapPanel, BorderLayout.CENTER)
    cp.add(scrollPanel, BorderLayout.EAST)

    setContentPane(cp)

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    pack()

    private class MapVisualizerPanel(width: Int, height: Int) extends JPanel {
      setPreferredSize(new Dimension(width, height))

      override def paint(g: Graphics): Unit = {
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.clearRect(0, 0, this.getWidth, this.getHeight)

        // draw the rows
        val rowHt = height / rowNum
        val rowWid = width / colNum

        for (i <- 0 to rowNum) {
          g2.drawLine(0, i * rowHt.toInt, width.toInt, i * rowHt.toInt)
        }

        for (i <- 0 to colNum) {
          g.drawLine(i * rowWid.toInt, 0, i * rowWid.toInt, height.toInt)
        }

        if (sensorMap.nonEmpty) {
          for (pos <- sensorMap.values) {
            val x0 = pos.x
            val y0 = pos.y
            g2.drawOval(x0.toInt, y0.toInt, 5, 5)
          }
        }
      }
    }

  }

  private class PatchVisualizerPanel(patchId: Int) extends JPanel {
    setLayout(new BorderLayout)

    val headerPanel = new JPanel()
    headerPanel.setLayout(new BorderLayout)

    val patchLabel = new JLabel("Patch" + patchId)
    val disableAlarmBtn = new JButton("Stop alarm")

    disableAlarmBtn.addActionListener { e => {
      listener.resetAlarmPressed(patchId)
      setAlarmEnabled(false)
    }
    }

    setAlarmEnabled(false)

    headerPanel.add(BorderLayout.WEST, patchLabel)
    headerPanel.add(BorderLayout.EAST, disableAlarmBtn)

    val guardianListPanel = new JPanel()
    guardianListPanel.setLayout(new BorderLayout)

    private var guardianList: JList[String] = _
    private var guardianListModel: DefaultListModel[String] = new DefaultListModel()

    guardianList = new JList(guardianListModel)
    private var guardianScrollPanel = new JScrollPane(guardianList)
    guardianScrollPanel.setPreferredSize(new Dimension(400, 80))

    add(BorderLayout.NORTH, headerPanel)
    add(BorderLayout.SOUTH, guardianScrollPanel)

    def updateGuardian(): Unit = {
      SwingUtilities.invokeLater(() =>  {
        guardianListModel.clear()
        addAllPatchGuardians()
      })
    }

    def setAlarmEnabled(enabled: Boolean): Unit = {
      if (enabled) {
        this.disableAlarmBtn.setEnabled(true)
        this.disableAlarmBtn.setBackground(Color.RED)
        this.disableAlarmBtn.setForeground(Color.WHITE)
        this.disableAlarmBtn.setBorderPainted(true)
      } else {
        this.disableAlarmBtn.setBorderPainted(false)
        this.disableAlarmBtn.setBackground(null)
        this.disableAlarmBtn.setForeground(null)
        this.disableAlarmBtn.setEnabled(false)
      }

    }


    private def addAllPatchGuardians(): Unit = {
      var guardianMap: Map[String, DashboardGuardianState] = patchMap(patchId)
      if (guardianListModel == null) {
        guardianListModel = new DefaultListModel()
      }
//      println("Numero guardiani in patch " + patchId + " : " + guardianMap.size)
      guardianMap.values.foreach(d => {

//        println(d.toString)

          guardianListModel.addElement(d.toString)

//        println(s"Size: ${guardianListModel.size()}")
      })
    }
  }

}