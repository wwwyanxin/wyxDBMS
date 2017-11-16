import java.util.Scanner;

public class DES {
    // IP：初始置换 IPundo：逆初始置换
    private static final int[] IP = {58, 50, 42, 34, 26, 18, 10, 2, 60, 52,
            44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48,
            40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35,
            27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31,
            23, 15, 7}; // 64
    private static final int[] IPundo = {40, 8, 48, 16, 56, 24, 64, 32, 39, 7,
            47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45,
            13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11,
            51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49,
            17, 57, 25}; // 64
    /**
     * 将DES的密钥由64位减到56位 PC_jianduan 密钥置换 PC_yasuo 压缩置换 从56位中选取48位
     */
    private static final int[] PC_jianduan = {57, 49, 41, 33, 25, 17, 9, 1,
            58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60,
            52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4}; // 56
    private static final int[] PC_yasuo = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6,
            21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37,
            47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50,
            36, 29, 32}; // 48
    /**
     * 扩展置换E P-盒置换
     */
    private static final int[] E = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9,
            10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20,
            21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1}; // 48
    private static final int[] P = {16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23,
            26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22,
            11, 4, 25}; // 32
    /**
     * S-盒代替
     */
    private static final int[][][] S_Box = {
            {
//						S_Box[1]
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}},
            {
//						S_Box[2]
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}},
            {
//						S_Box[3]
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}},
            {
//						S_Box[4]
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}},
            {
//						S_Box[5]
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}},
            {
//						S_Box[6]
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}},
            {
//						S_Box[7]
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}},
            {
//						S_Box[8]
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}};
    /**
     * 每轮移动的位数
     */
    private static final int[] LeftMove = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2,
            2, 2, 2, 1};

    private byte[] key1;

    /**
     * 构造函数
     *
     * @param key 密钥
     */
    public DES(String key) {
        this.key1 = key.getBytes();
    }


    /**
     * 将byte类型转换成二进制
     *
     * @param key2
     * @return
     */
    public int[] ByteToInt(byte[] key2) {
        int[] a = new int[64];
        int[] temp = new int[8];
        for (int i = 0; i < 8; i++) {
            temp[i] = key2[i];
            if (temp[i] < 0) {
                temp[i] += 256;
                temp[i] %= 256;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                a[((i * 8) + 7) - j] = temp[i] % 2;
                temp[i] = temp[i] / 2;
            }
        }
        return a;
    }

    /**
     * 密钥置换之后移位 分为左右两个部分，分别进行移位
     *
     * @param key   置换之后的密钥
     * @param count 第count轮运算
     * @return 返回移动之后的密钥
     */
    public void setLeftMove(int[] key, int count) {
        int[] left = new int[28];
        int[] right = new int[28];
        int[] left1 = new int[28];
        int[] right1 = new int[28];
        for (int i = 0; i < left.length; i++) {
            left[i] = key[i];
            right[i] = key[i + 28];
        }

        if (LeftMove[count] == 1) {
            for (int i = 0; i < left.length - 1; i++) {
                left1[i] = left[i + 1];
                right1[i] = right[i + 1];
            }
            left1[27] = left[0];
            right1[27] = right[0];
        }
        if (LeftMove[count] == 2) {
            for (int i = 0; i < left.length - 2; i++) {
                left1[i] = left[i + 2];
                right1[i] = right[i + 2];
            }
            left1[27] = left[1];
            right1[27] = right[1];
            left1[26] = left[0];
            right1[26] = right[0];
        }
        for (int i = 0; i < left.length; i++) {
            key[i] = left1[i];
            key[i + 28] = right1[i];
        }
    }

    /**
     * 合并数组
     *
     * @param after          是合并之后的分组 是从0合并
     * @param insertLocation 合并的位置
     * @param before         合并的分组
     * @param start          合并开始的位置
     * @param length         合并的长度
     */
    public void merge(byte[] after, int insertLocation, byte[] before,
                      int start, int length) {
        for (int i = 0; i < length; i++) {
            after[i + insertLocation] = before[start + i];
        }
    }

    /**
     * 密钥置换 从64位变成56位
     *
     * @param key 原始密钥
     */
    public int[] PCTrans(int[] key) {
        int[] result = new int[56];
        for (int i = 0; i < result.length; i++) {
            result[i] = key[PC_jianduan[i] - 1];
        }
        return result;
    }

    /**
     * 将密钥进行16次循环，存到二维数组[][]中 每次的密钥应该为48位
     * <p>
     * 提交的密钥应该为64位
     *
     * @return
     */
    public int[][] EveryKey(int[] key) {
        int[][] result = new int[16][48];
        int[] test;
        test = PCTrans(key);
        for (int i = 0; i < 16; i++) {
            setLeftMove(test, i);
            for (int j = 0; j < 48; j++) {
                result[i][j] = test[PC_yasuo[j] - 1];
            }
        }
        return result;
    }

    /**
     * 异或运算
     *
     * @param a
     * @param b
     * @return
     */
    public int[] XOR(int[] a, int[] b) {
        int[] result = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = a[i] + b[i];
            if (a[i] + b[i] == 2) {
                result[i] = 0;
            }
        }
        return result;
    }

    /**
     * 对右半部分的扩展置换 32----→48位 同时进行与密钥的异或
     *
     * @param right
     * @return
     * @count 第几轮运算
     * @author dadan
     */
    public int[] EtransAndXOR(int[] right, int count, int[][] EveryKey) {
        int[] resultSet = new int[48];
        for (int i = 0; i < 48; i++) {
            resultSet[i] = right[E[i] - 1];
        }
        int[] result = XOR(resultSet, EveryKey[count]);

        return result;
    }

    /**
     * S-盒代替 48位→→→32位 P-盒置换
     *
     * @param data 压缩后的密钥与扩展分组异或成的48位
     * @return
     */
    public int[] SBoxAndPTrans(int[] data) {
        int[][] temp = new int[8][6];
        int[] S_box_value = new int[8];
        int[] resultSet = new int[32];
        int[] result = new int[32];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                temp[i][j] = data[i * 6 + j];
            }
            // 取出S_BOX中的十进制数并转化为二进制
            //直接使用乘以2的n次方表示
            S_box_value[i] = S_Box[i][temp[i][0] * 2 + temp[i][5]][temp[i][1]
                    * 8 + temp[i][2] * 4 + temp[i][3] * 2 + temp[i][4]];
            for (int k = 0; k < 4; k++) {
                resultSet[(i * 4 + 3) - k] = S_box_value[i] % 2;
                S_box_value[i] = S_box_value[i] / 2;
            }
        }

        // 进行P-盒置换
        for (int i = 0; i < 32; i++) {
            result[i] = resultSet[P[i] - 1];
        }
        return result;
    }

    /**
     * 进行密钥的每一次置换
     *
     * @param data      密钥 64位
     * @param count     轮数
     * @param isEncrypt 加密=1，解密=0
     * @param everyKey  16次循环的二维数组
     */
    public void everychange(int[] data, int count, int isEncrypt,
                            int[][] everyKey) {
        int[] left = new int[32];
        int[] right = new int[32];
        int[] left1 = new int[32];
        int[] right1 = new int[32];
        for (int i = 0; i < right1.length; i++) {
            left[i] = data[i];
            right[i] = data[i + 32];
        }
        int[] temp = SBoxAndPTrans(EtransAndXOR(right, count, everyKey));
        right1 = XOR(temp, left);
        for (int i = 0; i < 32; i++) {
            left1[i] = right[i];
        }


        if ((isEncrypt == 1) && (count == 15) || (isEncrypt == 0)
                && (count == 0)) {
            for (int j = 0; j < 32; j++) {
                data[j] = right1[j];
                data[j + 32] = left1[j];
            }
        } else {
            for (int j = 0; j < 32; j++) {
                data[j] = right1[j];
                data[j + 32] = left1[j];
            }
        }
    }

    /**
     * 将最终的结果64位二进制数据转换成byte
     *
     * @param data
     * @return
     */
    public byte[] changeResultToByte(int[] data) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
//				result[i] += data[8 * i + j] *( 2 ^ (7 - j));  发现加密错误  找到是这里的错误  以为2的n次方是2^n ?!
                //2的n次方  在java中应为 Math.pow(2,n)
                result[i] += data[8 * i + j] * Math.pow(2, (7 - j));

            }
        }
        for (int i = 0; i < 8; i++) {
            result[i] %= 256;
            if (result[i] > 128) {
                result[i] -= 255;
            }
        }
        return result;
    }

    /**
     * 不足8位 进行补充
     * 补充的数为待补充的位数 这样可以在去除补充位数的得到删除位数
     *
     * @param data
     * @return
     */
    public byte[] addByte(byte[] data) {
        int datalength = data.length;
        int addlength = 8 - (datalength % 8);//注意：解密过程中由增加了8位
        int totalLength = datalength + addlength;
        byte[] result = new byte[totalLength];
        merge(result, 0, data, 0, datalength);
        for (int i = datalength; i < result.length; i++) {
            result[i] = (byte) addlength;
        }
        return result;
    }

    /**
     * 整合的加密过程
     *
     * @param data      待加密的数据
     * @param isEnceypt 加密/解密  1加密 0加密
     * @return
     */
    public byte[] finalencrypt(byte[] data, int isEnceypt) {
        byte[] wholekey = addByte(key1);
        byte[] wholedata = addByte(data);
        int wholecount = wholedata.length / 8;
        byte[] result = new byte[wholedata.length];
        //将超出64位的数据分次进行加密
        for (int i = 0; i < wholecount; i++) {
            byte[] tempkey = new byte[8];
            byte[] tempdata = new byte[8];
            merge(tempkey, 0, wholekey, 0, 8);
            merge(tempdata, 0, wholedata, i * 8, 8);
            byte[] tempResult = finalDES(tempdata, tempkey, isEnceypt);
            merge(result, i * 8, tempResult, 0, 8);//将分次加密的数据进行整合
        }
        // 解密过程 去除补充位
        byte[] deciphering = null;
        if (isEnceypt == 0) {
            int deleteLenght = result[wholedata.length - 9];//-9 = -8 -1  -8的原因是在解密过程中addbyte()又加入了8位
            deleteLenght = ((deleteLenght >= 1) && (deleteLenght <= 8)) ? deleteLenght : 0;//判断是否有补充位
            deciphering = new byte[wholedata.length - 8];//-8的原因是在解密过程中addbyte()又加入了8位
            boolean delete = true;//判断是否为补充位
            for (int i = 0; i < deleteLenght; i++) {
                //最后确认最后的连续deleteLenght长度是否都为补充位
                //不是则没有补充位
                if (deleteLenght != result[wholedata.length - 9 - i]) {
                    delete = false;
                }
            }
            if (delete == true) {
                merge(deciphering, 0, result, 0, wholedata.length
                        - deleteLenght - 8);
            }
        }
        return (isEnceypt == 1) ? result : deciphering;
    }

    /**
     * 进行加密/解密
     *
     * @param data
     * @param key
     * @param isEncrypt
     * @return
     */
    public byte[] finalDES(byte[] data, byte[] key, int isEncrypt) {
        int[][] everykey = EveryKey(ByteToInt(key));
        byte[] result = Encrypt(ByteToInt(data), isEncrypt, everykey);
        return result;
    }

    /**
     * 加密/解密过程
     *
     * @param data
     * @param isEncrypt
     * @param everykey
     * @return
     */
    public byte[] Encrypt(int[] data, int isEncrypt, int[][] everykey) {
        int[] IPdata = new int[64];
        int[] ResetIPdata = new int[64];
        for (int i = 0; i < IPdata.length; i++) {
            IPdata[i] = data[IP[i] - 1];
        }
        if (isEncrypt == 1) {
            for (int i = 0; i < 16; i++) {
                everychange(IPdata, i, isEncrypt, everykey);
            }
        } else if (isEncrypt == 0) {
            for (int i = 15; i > -1; i--) {
                everychange(IPdata, i, isEncrypt, everykey);
            }
        }
        for (int i = 0; i < ResetIPdata.length; i++) {
            ResetIPdata[i] = IPdata[IPundo[i] - 1];
        }
        byte[] result = changeResultToByte(ResetIPdata);

        return result;
    }

    public static void main(String args[]) {
        String key = null;
        Scanner in = new Scanner(System.in);
        System.out.println("输入明文：");
        String data = in.next();
        key = "wyx";
        DES finalDES = new DES(key);
        byte[] result = finalDES.finalencrypt(data.getBytes(),
                1);
        System.out.println("加密：" + new String(result));

        System.out.println("输入密钥：");
        String userMimaString = in.next();
        if (userMimaString.equals(key)) {
            System.out.println("明文是：" + new String(finalDES.finalencrypt(result, 0)));
        } else {
            System.out.println("密钥错误");
        }

    }

}
