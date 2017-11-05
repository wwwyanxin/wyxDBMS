import java.util.*;

public class jiami {
    public static void main(String[] args) {

        char[] zimubiao = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] mimabiao = Arrays.copyOf(zimubiao,26);
        Scanner sc = new Scanner(System.in);
        String key = sc.nextLine();
        char[] keys = key.toCharArray();
        Set keySet = new LinkedHashSet();
        for (char c : keys) {
            keySet.add(c);
        }
        Iterator<Character> iterator = keySet.iterator();
        List l = new LinkedList();
        l.add(new Object());

        int i=0;
        while (iterator.hasNext()) {
            mimabiao[i++] = iterator.next();
        }
        String mingwen = sc.nextLine();
        char[] mingwens = mingwen.toCharArray();
        char[] password = new char[mingwens.length];
        String zimubiaoStr = new String(zimubiao);//zimubiao.toString();
        for (int j = 0; j < mingwens.length; j++) {
            char c = mingwens[j];
            int k = zimubiaoStr.indexOf(String.valueOf(c));
            password[j] = mimabiao[k];
        }
        System.out.println(password);
    }

}
