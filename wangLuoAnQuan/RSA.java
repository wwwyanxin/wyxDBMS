import java.math.BigDecimal;
import java.util.Random;
import java.util.Scanner;

public class RSA {
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
    public static BigDecimal encryption(int e, int n,long m) {
        BigDecimal result =BigDecimal.valueOf(m).pow(e).remainder(BigDecimal.valueOf(n));
        return result;
    }
    public static BigDecimal decryption(int d, int n,int c) {
        BigDecimal result =BigDecimal.valueOf(c).pow(d).remainder(BigDecimal.valueOf(n));
        return result;
    }

    public static void main(String[] args) {
        int p,q;
        p = RSA.createPrime();
        while (true) {
            if (p != (q = RSA.createPrime())) {
                break;
            }
        }
       /* p=7;
        q=17;*/
        int n=p*q;
        int fn = (p - 1) * (q - 1);
        int e = RSA.eachPrime(fn);
        int d = RSA.reciprocal(e, fn);
        System.out.println("输入整数明文");
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        System.out.println(RSA.encryption(e, n, m));
        System.out.println("输入整数密文");
        int c = sc.nextInt();
        System.out.println(RSA.decryption(d, n, c));
    }

}
