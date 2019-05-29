package assignment3.e0

import java.awt.event.{ActionEvent, ActionListener, MouseEvent, MouseListener}
import java.awt.{BorderLayout, Graphics, Graphics2D, RenderingHints}
import java.util.Formatter

import javax.swing._

class WorldViewer(var world: World, val w: Int, val h: Int, var scale: Double) {
  private var frame: VisualiserFrame = new VisualiserFrame(w, h)
  frame.setResizable(false)

  private var controller:Controller = _

  def show(): Unit = frame.setVisible(true)

  def updateView(): Unit = frame.repaint()

  def setController(c: Controller): Unit = controller = c

  protected def notifyNewParticle(pos: P2d): Unit = controller.notifyNewParticle(pos)

  private[e0] class VisualiserFrame(val w: Int, val h: Int) extends JFrame(".:: Particle System ::.") with ActionListener {
    setSize(w, h)

    private var startButton: JButton = _
    private var stopButton: JButton = _
    private var zoomIn: JButton = _
    private var zoomOut: JButton = _
    private var time: JTextField = _
    private var setPanel: VisualiserPanel = _
    private var nParticles: JTextField = _

    nParticles = new JTextField(5)
    nParticles.setText("2000")
    startButton = new JButton("start")
    stopButton = new JButton("stop")
    zoomIn = new JButton("zoom in")
    zoomOut = new JButton("zoom out")
    val controlPanel = new JPanel
    controlPanel.add(startButton)
    controlPanel.add(stopButton)
    controlPanel.add(zoomIn)
    controlPanel.add(zoomOut)
    controlPanel.add(new JLabel("Num Particles"))
    controlPanel.add(nParticles)
    setPanel = new VisualiserPanel(w, h)
    val infoPanel = new JPanel
    time = new JTextField(20)
    time.setText("Idle")
    time.setEditable(false)
    infoPanel.add(new JLabel("Time"))
    infoPanel.add(time)
    val cp = new JPanel
    cp.setLayout(new BorderLayout)
    cp.add(BorderLayout.NORTH, controlPanel)
    cp.add(BorderLayout.CENTER, setPanel)
    cp.add(BorderLayout.SOUTH, infoPanel)
    setContentPane(cp)
    setNotRunningConfig()
    startButton.addActionListener(this)
    stopButton.addActionListener(this)
    zoomIn.addActionListener((ev: ActionEvent) => {
      def foo(ev: ActionEvent): Unit = {
        scale = scale * 1.1
      }

      foo(ev)
    })
    zoomOut.addActionListener((ev: ActionEvent) => {
      def foo(ev: ActionEvent): Unit = {
        scale = scale * 0.9
      }

      foo(ev)
    })
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    override def actionPerformed(e: ActionEvent): Unit = if (e.getSource eq startButton) {
      setRunningConfig()
      controller.notifyStarted(nParticles.getText.toInt)
    }
    else if (e.getSource eq stopButton) {
      setNotRunningConfig()
      controller.notifyStopped()
    }

    private def setNotRunningConfig(): Unit = {
      startButton.setEnabled(true)
      stopButton.setEnabled(false)
      zoomIn.setEnabled(false)
      zoomOut.setEnabled(false)
    }

    private def setRunningConfig(): Unit = {
      startButton.setEnabled(false)
      stopButton.setEnabled(true)
      zoomIn.setEnabled(true)
      zoomOut.setEnabled(true)
    }

    private[e0] class VisualiserPanel(val w: Int, val h: Int) extends JPanel with MouseListener {
      setSize(w, h)
      private var dx = w / 2 - 20
      private var dy = h / 2 - 20
      this.addMouseListener(this)
      private var timeText: StringBuffer = new StringBuffer

      override def paint(g: Graphics): Unit = {
        val g2 = g.asInstanceOf[Graphics2D]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.clearRect(0, 0, this.getWidth, this.getHeight)
        val snap = world.getSnapshotToDisplay
        if (snap != null) {
          val currentPosSnapshot = snap.getPosList
          for (pos <- currentPosSnapshot) {
            val x0 = getViewX(pos.x)
            val y0 = getViewY(pos.y)
            g2.drawOval(x0, y0, 5, 5)
          }
        }
        timeText.setLength(0)
        time.setText(timeText.toString)
      }

      private def getViewX(x: Double) = (dx + x * scale).toInt

      private def getViewY(y: Double) = (dy - y * scale).toInt

      override def mousePressed(e: MouseEvent): Unit = {
        val p = new P2d((e.getX - dx) / scale, (dy - e.getY) / scale)
        // System.out.println("NEW POINT " + e.getX() + " " + e.getY() + " => " + p);
        notifyNewParticle(p)
      }

      override def mouseClicked(e: MouseEvent): Unit = {
      }

      override def mouseReleased(e: MouseEvent): Unit = {
      }

      override def mouseEntered(e: MouseEvent): Unit = {
      }

      override def mouseExited(e: MouseEvent): Unit = {
      }
    }

  }

}