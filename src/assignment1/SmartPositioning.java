package assignment1;

import assignment1.concurrent.ConcurrentContext;

public class SmartPositioning {
    public static void main(String[] args) throws InterruptedException {
        Context context = new Context();
        //context.createNParticles( 2 );


        context.createParticle(1,2);
        context.createParticle(4,2);



        context.printAllParticles();

        context.doStep(2);

        context.printAllParticles();

        context.doStep(2);

        context.printAllParticles();

        context.doStep(2);

        context.printAllParticles();

        context.doStep(2);

        context.printAllParticles();
        System.out.println();
        System.out.println();


        ConcurrentContext context1 = new ConcurrentContext();
        context1.start();

    }
}
