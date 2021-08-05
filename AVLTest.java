import java.io.*;
import java.util.*;
public class AVLTest {
    
    public AVLTest() {
        
    }
    
    public void test1(int nums[], int len) throws IOException {
        //this test does not require any rotations and only removes leaves
        System.out.println("Start test 1");
        int i;
        int sFieldLens[] = {10, 15};
        AVLTree a = new AVLTree("t1", sFieldLens);
        char sFields[][];
        for ( i = 0; i < len; i++) {
            sFields = new char[2][];
            sFields[0] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 10);
            sFields[1] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 15);

            a.insert(nums[i], sFields);
        }
        
        System.out.println("Past inserts in test 1");
        a.print();
        System.out.println("Past first print in test 1");
        for ( i = len-1; i > 2; i--) {
            a.remove(nums[i]);
        }
        System.out.println("Past first removes in test 1");
        a.print();
        System.out.println("Past second print in test 1");
        a.close();
        a = new AVLTree("t1");
        System.out.println("Past close and reopen");
        a.print();
        a.remove(nums[2]);
        a.remove(nums[1]);
        a.remove(nums[0]);
        sFields = new char[2][];
        sFields[0] = Arrays.copyOf("Root".toCharArray(), 10);
        sFields[1] = Arrays.copyOf("Node Only".toCharArray(), 15);
        a.insert(999, sFields);
        a.print();
        a.close();
        
    }
    
    public void test2(int nums[], int len) throws IOException {
    //this tests uses the same data used in test 1 but inserts and
    //removes in a different order
    //The test will cause some rotations and will remove non-leaf node
        int i;
        System.out.println("\n\nStart test 2");
        int sFieldLens[] = {10, 15, 20};
        AVLTree a = new AVLTree("t2", sFieldLens);
        char sFields[][];
        for ( i = len-1; i >= 0; i--) {
            sFields = new char[3][];
            sFields[0] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 10);
            sFields[1] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 15);
            sFields[2] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 20);
            
            a.insert(nums[i], sFields);
        }
        
        System.out.println("Past inserts in test 2");
        a.print();
        System.out.println("Past first print in test 2");
        for ( i = len-1; i > 2; i--) {
            a.remove(nums[i]);
        }
        System.out.println("Past first removes in test 2");
        a.print();
        System.out.println("Past second print in test 2");
        a.close();
        a = new AVLTree("t2");
        System.out.println("Past close and reopen");
        a.print();
        a.remove(nums[2]);
        a.remove(nums[1]);
        a.remove(nums[0]);
        sFields = new char[3][];
        sFields[0] = Arrays.copyOf("Root".toCharArray(), 10);
        sFields[1] = Arrays.copyOf("Node".toCharArray(), 15);
        sFields[2] = Arrays.copyOf("Only".toCharArray(), 20);
        a.insert(999, sFields);
        a.print();
        a.close();
        
    }
    
    public void test3() throws IOException {
    //does inserts and removes using the data in the example I did in class
    //to run test3 you need the file in5.txt
    //in in3.txt lines that begin with a # are removes
    //lines that begin with # are removes
    //lines that begin with @ are extra key for testing find
        System.out.println("Start test 3");
        @SuppressWarnings("resource")
        BufferedReader b = new BufferedReader(new FileReader("in5.txt"));
        String line;
        int numKeys = 0;
        int keys[] = new int[100];
        String fields[];
        int sFieldLens[] = {30, 30};
        char sFields[][];
        AVLTree a = new AVLTree("t3", sFieldLens);
        line = b.readLine();
        while (line != null) {
            fields = line.split(" ");
            if (fields[0].equals("#")) {
                a.remove(Integer.parseInt(fields[1]));
            } else if (fields[0].equals("@")) {
                keys[numKeys] = Integer.parseInt(fields[1]);
                numKeys++;
            } else {
                sFields = new char[2][];
                keys[numKeys] = Integer.parseInt(fields[0]);
                sFields[0] = Arrays.copyOf(fields[1].toCharArray(), 30);
                sFields[1] = Arrays.copyOf(fields[2].toCharArray(), 30);
                a.insert(keys[numKeys], sFields);
                numKeys++;
            }
            line = b.readLine();
        }
        a.print();
        System.out.println("\nStart Finds");
        for (int j = 0; j < numKeys; j++) {
            LinkedList<String> strs = a.find(keys[j]);
            if (strs != null) {
                System.out.println(keys[j]+" "+strs.get(0)+" "+strs.get(1));
            } else {
                System.out.println(keys[j] + " not found");
            }
            
        }
        a.close();
        a = new AVLTree("t3");
        a.print();
        a.close();
    }
    
    public void test4() throws IOException {
    //insert 200 random numbers, remove them all, inserts them again in reverse order and remove all but one of them.
        System.out.println("Start test 4");
        int i;
        int nums[] = new int[200];
        Random r = new Random(2020);
        for (i = 0; i < 200; i++) {
            nums[i] = Math.abs(r.nextInt())%10000;
        }
        int sFieldLens[] = {10, 15, 20, 30};
        AVLTree a = new AVLTree("t4", sFieldLens);
        char sFields[][];
        for ( i = 0; i < 200; i++) {
            sFields = new char[4][];
            sFields[0] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 10);
            sFields[1] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 15);
            sFields[2] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 20);
            sFields[3] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 30);
            
            a.insert(nums[i], sFields);
        }
        for ( i = 0; i < 200; i++) {
            a.remove(nums[i]);
        }
        for ( i = 199; i >= 0; i--) {
            sFields = new char[4][];
            sFields[0] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 10);
            sFields[1] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 15);
            sFields[2] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 20);
            sFields[3] = Arrays.copyOf(String.valueOf(nums[i]).toCharArray(), 30);
            
            a.insert(nums[i], sFields);
        }
        for ( i = 199; i > 0; i--) {
            a.remove(nums[i]);
        }
        a.close();
        a = new AVLTree("t4");
        a.print();
        a.close();

    }
    
    public static void main(String args[]) throws IOException {
        AVLTest test = new AVLTest();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the maximum value to use for tests 1 and 2: ");
        int max = scan.nextInt();
        System.out.print("Enter a positive int less that log max (log base 2). This is the depth of the tree for tests 1: ");
        int levels = scan.nextInt();
        int nums[] = new int[max];
        int start, increment, i;
        int j = 0;
        int divisor = 2;
        while (levels > 0) {
            start = max/divisor;
            increment = 2*start;
            for (i = start; i < max; i = i+increment) {
                nums[j] = i;
                j++;
            }
            divisor = divisor*2;
            levels--;
        }
        //As an example the data generated by the above code when 100 is the max and depth is 3
        //is 50, 25, 75, 12, 36, 60, 84
        //the depth should be less than log max (log base 2)
        test.test1(nums, j);
        test.test2(nums, j);
        test.test3();
        test.test4();
    }

}

