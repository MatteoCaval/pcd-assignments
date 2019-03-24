package assignment1;

public class SmartPositioning {
    public static void main(String[] args) {
        Context context = new Context();
        //context.createNParticles( 2 );

        context.createParticle( 1, 1 );
        context.createParticle( 2, 2 );
        context.createParticle(3,1);


        context.printAllParticles();

        context.doStep(1);

        context.printAllParticles();

        context.doStep(1);

        context.printAllParticles();
//
//        context.doStep(1);
//
//        context.printAllParticles();

    }
}
