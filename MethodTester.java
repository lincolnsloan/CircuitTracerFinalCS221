public class MethodTester {
    public static void main(String[] args) {
        // for (int i = 1; i < 13; i++) {
        //     String fileString = "tracer/invalid" + i + ".dat";
        //     try {
        //         CircuitBoard board = new CircuitBoard(fileString);
        //         System.out.println("invalid " + i);
        //         System.out.println(board.toString());
        //     } catch (Exception e) {
        //         System.out.println("invalid " + i);
        //         System.err.println(e.toString());
        //     }
        // }
        
        // for (int i = 1; i < 11; i++) {
        //     String fileString = "tracer/valid" + i + ".dat";
        //     try {
        //         CircuitBoard board = new CircuitBoard(fileString);
        //         System.out.println("valid " + i);
        //         System.out.println(board.toString());
        //     } catch (Exception e) {
        //         System.out.println("valid " + i);
        //         System.err.println(e.toString());
        //     }
        // }

        // try {
        //     int num = 5;
        //     String fileString = "tracer/invalid" + num + ".dat";
        //     CircuitBoard board = new CircuitBoard(fileString);
        //     System.out.println("invalid " + num);
        //     System.out.println(board.toString());

        // } catch (Exception e) {
        //     System.out.println("valid 5");
        //     System.err.println(e.toString());
        // }

        // System.out.println(	"Usage: $ java CircuitTracer <-s | -q> <-c | -g> <filename>\r\n" + 
		// 					"\t\t where\t -s is to use stack storage\r\n" + 
		// 					"\t\t\t -q is to use queue storage\r\n" + 
		// 					"\t\t and\t -c is console output\r\n" + 
		// 					"\t\t\t -g is GUI output\r\n"); 

        // if (!args[0].equals("-a")) {
        //     System.out.println(args[0]);
        // }
        

        String storeArg;
        
        for (int n = 0; n < 2; n++) {
            if (n % 2 == 0) {
                storeArg = "-s";
            } else {
                storeArg = "-q";
            }

            for (int i = 1; i < 13; i++) {
                String fileString = "tracer/invalid" + i + ".dat";
                String[] CTArgs = {storeArg, "-c", fileString};

                System.out.println("invalid " + i);
                new CircuitTracer(CTArgs);
            }
        }
        
        for (int n = 0; n < 2; n++) {
            if (n % 2 == 0) {
                storeArg = "-s";
            } else {
                storeArg = "-q";
            }

            for (int i = 1; i < 11; i++) {
                String fileString = "tracer/valid" + i + ".dat";
                String[] CTArgs = {storeArg, "-c", fileString};

                System.out.println("valid " + i);
                new CircuitTracer(CTArgs);
            }
        }     
    }
}
