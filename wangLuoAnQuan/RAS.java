import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class RAS {
    private static boolean isPrime(int number) {
        for (int i = 2; i < number; i++) {
            if (0 == number % i) {
                return false;
            }
        }
        return true;
    }

    private static int createPrime() {
        Random random = new Random();
        while (true) {
            int num = Math.abs(random.nextInt(100)) + 2;
            if (isPrime(num)) {
                return num;
            }
        }
    }

    private static int eachPrime(int fn) {
        for (int i = 2; i <fn ; i++) {
            if (0 != fn % i) {
                return i;
            }
        }
        return -1;
    }

    private static int reciprocal(int e, int fn) {
        int k=1;
        while (true) {
            int fnk = fn * k;
            for (int d = 0; d < fn; d++) {
                if (e * d == fnk + 1) {
                    return d;
                }

            }
            k++;
        }
    }
/*    public static BigInteger ss(int m,int n){
        BigInteger result=BigInteger.valueOf(1);
        int i=0;
        while(i<n){
            result=result.multiply(BigInteger.valueOf(m));
            i++;
        }
        return result;
    }*/

    public static double encryption(int e, int n,long m) {
        return (Math.pow(m, e) % n);
    }
    public static BigDecimal decryption(int d, int n,int c) {
        BigDecimal result =BigDecimal.valueOf(c).pow(d).remainder(BigDecimal.valueOf(n));
        return result;
    }

    public static void main(String[] args) {
        int p,q;
        p = RAS.createPrime();
        while (true) {
            if (p != (q = RAS.createPrime())) {
                break;
            }
        }
       /* p=7;
        q=17;*/
        int n=p*q;
        int fn = (p - 1) * (q - 1);
        int e = RAS.eachPrime(fn);
        int d = RAS.reciprocal(e, fn);

        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        System.out.println(RAS.encryption(e, n, m));
        int c = sc.nextInt();
        System.out.println(RAS.decryption(d, n, c));
    }

}
