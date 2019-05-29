package assignment3.e0

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class ParticleBuffer() {
    private var particles: mutable.ListBuffer[Particle] = new mutable.ListBuffer[Particle]

     def getNewParticleAvail: Particle = {
       if (particles.nonEmpty)
         particles.remove(1)
       else null
     }

     def notifyNewParticle(p: Particle): ListBuffer[Particle] = particles += p
}