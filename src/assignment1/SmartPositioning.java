package assignment1;

public class SmartPositioning {
    public static void main(String[] args) {
        Context context = new Context();
        //context.createNParticles( 2 );

        context.createParticle( 5, 3 );
        context.createParticle( 5, 6 );


        context.printAllParticles();
        context.calculateForces();
        context.printAllParticles();

    }
}
