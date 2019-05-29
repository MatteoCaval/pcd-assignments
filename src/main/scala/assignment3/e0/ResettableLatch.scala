package assignment3.e0

/*
 * Resettable Latch monitor.
 *
 * - it is created with a number of participants
 * - like a latch, the gate is close until all participants signal
 * - the counter could be reset
 *
 */
class ResettableLatch(var nParticipants: Int = 0) {
    private var counter = 0

    @throws[InterruptedException]
    def await(): Unit = synchronized{
      while (counter > 0) wait()
    }

    def down(): Unit = synchronized{
      counter -= 1
      if (counter == 0) notifyAll()
    }

    def reset(): Unit = synchronized{
      counter = nParticipants
    }
}