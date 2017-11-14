import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

interface Check
{
    public static final double k =180*60*60/Math.PI;
    public abstract void getC();               //用来实现检核的抽象方法
}//定义接口

class QianFangJiaoHui implements Check
{
    //将拟十进制角值转换为度
    public static double qt(String st)
    {
        int a=st.indexOf(".");             //找到字符串中"."的位置
        String mmss=st.substring(a+1);     //截取字符串中"."之后的字符
        int len=mmss.length();             //求所截取字符串的长度
        if(len==1)
        {
            mmss=mmss.concat("000");
        }                                  //处理角度值无秒值的情况
        if(len==2)
        {
            mmss=mmss.concat("00");
        }                                  //处理角度值为整分的情况
        if(len==3)
        {
            mmss=mmss.concat("0");
        }                                  //处理角度值中秒为10的倍数的情况
        String dd=st.substring(0,a);              //截取"."之前的字符，即表示度的字符
        String mm=mmss.substring(0,2);            //截取表示分的字符
        String ss=mmss.substring(2,4);            //截取表示秒的字符
        double d=Double.parseDouble(dd);          //将表示度的字符转换为Double
        double m=Double.parseDouble(mm)/60;       //将表示分的字符转换为Double，并转化为度
        double s=Double.parseDouble(ss)/(60*60);  //将表示秒的字符转换为Double，并转化为度
        double ddmmss=d+m+s;                      //将拟十进制角值转换为度的值
        return ddmmss;
    }

    //将Double型数保留三位小数
    public static String three(double dbs)
    {
        String str3=String.valueOf(dbs);           //将Double型数据转化为字符串
        int imb=str3.indexOf(".");                  //找到字符串中"."的位置
        String c=str3.substring(imb+4,imb+5);         //截取小数点后四位的字符
        int d=Integer.parseInt(c);
        if(d>5)
        {
            dbs+=0.001;
            str3=String.valueOf(dbs);
        }                                      //对小数点后四位进行四舍五入
        String st1=str3.substring(0,imb);           //截取"."之前的字符
        String st2=str3.substring(imb,imb+4);         //截取"."之后的三位数
        String st=st1+st2;                     //Double型数保留三位小数的数值的字符串形式
        return st;
    }

    //将度转换为拟十进制角值
    public static String dmss(double db)
    {
        String a=String.valueOf(db);           //将Double型数据转化为字符串
        int n1=a.indexOf(".");               //找到字符串中"."的位置
        String str1=a.substring(0,n1+1);      //截取"."之前的字符，包括"."
        String st2=a.substring(n1);          //截取"."之后的字符，包括"."
        double b=Double.parseDouble(st2)*60;   //将小数位的字符转换为Double,并且将度转化为分
        String st3=String.valueOf(b);          //将分值转换为字符串
        int n2=st3.indexOf(".");
        String st4=st3.substring(0,n2);      //截取分的整数位
        int n3=st4.length();
        if(n3==0)
        {
            st4=st4.concat("00");
        }
        if(n3==1)
        {
            st4=st4.concat("0");
        }                                        //将表示分的字符串格式化为两位
        String st5=st3.substring(n2);          //截取分的小数位
        double c=Double.parseDouble(st5)*60;     //将分的小数位转换为Double，并且将分转化为秒
        String st6=String.valueOf(c);            //将秒转换为字符串
        int num4=st6.indexOf(".");               //找到字符串中"."的位置
        String stt=st6.substring(num4+1,num4+2);
        int t=Integer.parseInt(stt);
        if(t>=5)
        {
            c+=1;
        }                                        //将秒值整数位进行四舍五入
        st6=String.valueOf(c);
        num4=st6.indexOf(".");
        String st7=st6.substring(0,num4);        //截取秒值的整数位
        int n5=st7.length();
        if(n5==0)
        {
            st7=st7.concat("00");
        }
        if(n5==1)
        {
            st7=st7.concat("0");
        }                                        //将表示秒的字符串格式化为两位
        String st=str1+st4+st7;                   //将表示度分秒的字符串连接
        return st;
    }


    public double x1;      //A的x坐标
    public double y1;      //A的y坐标
    public double x2;      //B的x坐标
    public double y2;      //B的y坐标
    public double x3;      //C的x坐标
    public double y3;      //C的y坐标
    public double _a;      //A角内角的弧度值
    public double _b1;     //第一个B角内角的弧度值
    public double _b2;     //第二个B角内角的弧度值
    public double _c;      //C角内角的弧度值

    public double x;       //待定点x坐标
    public double y;       //待定点y坐标
    public double _x1;     //第一次待定点x坐标
    public double _y1;     //第一次待定点y坐标
    public double _x2;     //第二次待定点x坐标
    public double _y2;     //第二次待定点y坐标

    public double d_pa;  //p1与A点之间的距离
    public double d_pb;  //p1与B点之间的距离
    public double d_pc;  //p1与C点之间的距离

    public double alpha_a; //AP1的方位角
    public double alpha_b; //BP1的方位角
    public double alpha_c; //CP1的方位角

    public QianFangJiaoHui(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j)
    {
        x1=a;
        y1=b;
        x2=c;
        y2=d;
        x3=e;
        y3=f;
        _a=Math.toRadians(g);
        _b1=Math.toRadians(h);
        _b2=Math.toRadians(i);
        _c=Math.toRadians(j);
    }	//构函对变量赋值

    public void zb()
    {

        _x1=(x1*(Math.cos(_b1)/Math.sin(_b1))+x2*(Math.cos(_a)/Math.sin(_a))+y2-y1)/(Math.cos(_a)/Math.sin(_a)+Math.cos(_b1)/Math.sin(_b1));  //第一次求得待定点x的值
        _y1=(y1*(Math.cos(_b1)/Math.sin(_b1))+y2*(Math.cos(_a)/Math.sin(_a))-x2-x1)/(Math.cos(_a)/Math.sin(_a)+Math.cos(_b1)/Math.sin(_b1));  //第一次求得待定点y的值
        _x2=(x2*(Math.cos(_c)/Math.sin(_c))+x3*(Math.cos(_b2)/Math.sin(_b2))+y3-y2)/(Math.cos(_b2)/Math.sin(_b2)+Math.cos(_c)/Math.sin(_c));  //第二次求得待定点x的值
        _y2=(y2*(Math.cos(_c)/Math.sin(_c))+y3*(Math.cos(_b2)/Math.sin(_b2))-x3-x2)/(Math.cos(_b2)/Math.sin(_b2)+Math.cos(_c)/Math.sin(_c));  //第二次求得待定点y的值

        x=(_x1+_x2)/2;  //所求待定点x值
        y=(_y1+_y2)/2;  //所求待定点y值
    }//计算待定点坐标的方法

    public void distance()
    {
        d_pa =Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));  //p1与A点之间的距离
        d_pb =Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y));  //p1与B点之间的距离
        d_pc =Math.sqrt((x3-x)*(x3-x)+(y3-y)*(y3-y));  //p1与C点之间的距离
    }//计算两点之间距离的方法

    public void fwj()
    {
        double deltaX=x1-x;
        double deltaY=y1-y;
        double alpha =Math.atan(deltaY/deltaX);
        alpha_a=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_a*=180/Math.PI;  //计算出AP的方位角

        deltaX=x2-x;
        deltaY=y2-y;
        alpha =Math.atan(deltaY/deltaX);
        alpha_b=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_b*=180/Math.PI;  //计算出BP的方位角

        deltaX=x3-x;
        deltaY=y3-y;
        alpha =Math.atan(deltaY/deltaX);
        alpha_c=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_c*=180/Math.PI;  //计算出CP的方位角
    }//计算方位角的方法

    public void result()
    {
        String s1="P1点的坐标为：("+three(x)+","+three(y)+")";
        String s2="P1到A点的距离为："+three(d_pa)+"    "+"A-P1的方位角为："+BigDecimal.valueOf(Double.parseDouble(dmss(alpha_a))).setScale(0, BigDecimal.ROUND_HALF_UP);
        String s3="P1到B点的距离为："+three(d_pb)+"    "+"B-P1的方位角为："+ BigDecimal.valueOf(Double.parseDouble(dmss(alpha_b))).setScale(0, BigDecimal.ROUND_HALF_UP);
        String s4="P1到C点的距离为："+three(d_pc)+"    "+"C-P1的方位角为："+ BigDecimal.valueOf(Double.parseDouble(dmss(alpha_c))).setScale(0, BigDecimal.ROUND_HALF_UP);

        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter("result.txt",true));
            bw.write("您选择的是前方交会，结果如下：");
            bw.newLine();
            bw.write(s1);
            bw.newLine();
            bw.write(s2);
            bw.newLine();
            bw.write(s3);
            bw.newLine();
            bw.write(s4);
            bw.newLine();
            bw.close();
        }
        catch (IOException e)
        {
            System.err.println("发生异常:"+e);
            e.printStackTrace();
        }//try-catch结构结束

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
    }//输出结果

    @Override
    public void getC()
    {
        double dis=Math.sqrt((_x1-_x2)*(_x1-_x2)+(_y1-_y2)*(_y1-_y2));

        String s1="前方交会的检验值为："+three(dis);

        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter("result.txt",true));
            bw.write(s1);
            bw.newLine();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }//try-catch结构结束

        System.out.println(s1);
    }//实现抽象方法进行检验

}//前方交会


class SideIntersection implements Check
{
    public double x1;  //A的x坐标
    public double y1;  //A的y坐标
    public double x2;  //B的x坐标
    public double y2;  //B的y坐标
    public double x3;  //C的x坐标
    public double y3;  //C的y坐标
    public double _a;  //A角内角的弧度值
    public double _p2; //P2内角的弧度值
    public double _c;  //检核角的弧度值

    public double dis_pa;  //p2与A点之间的距离
    public double dis_pb;  //p2与B点之间的距离
    public double dis_pc;  //p2与C点之间的距离

    public double x;   //待定点P2的x坐标
    public double y;   //待定点P2的y坐标

    public double alpha_a; //AP2的方位角
    public double alpha_b; //BP2的方位角
    public double alpha_c; //CP2的方位角
    public double _b;      //B角内角的弧度值
    public SideIntersection(double a,double b,double c,double d,double e,double f,double g,double h,double i)
    {
        x1=a;
        y1=b;
        x2=c;
        y2=d;
        x3=e;
        y3=f;
        _a=Math.toRadians(g);
        _p2=Math.toRadians(h);
        _c=Math.toRadians(i);
        _b=Math.PI-_a-_p2;
    }//构函对变量赋值

    public void zuoBiao()
    {
        x=(x1*(Math.cos(_b)/Math.sin(_b))+x2*(Math.cos(_a)/Math.sin(_a))+y2-y1)/(Math.cos(_a)/Math.sin(_a)+Math.cos(_b)/Math.sin(_b));  //求得待定点x的值
        y=(y1*(Math.cos(_b)/Math.sin(_b))+y2*(Math.cos(_a)/Math.sin(_a))-x2-x1)/(Math.cos(_a)/Math.sin(_a)+Math.cos(_b)/Math.sin(_b));  //求得待定点y的值
    }//求待定点坐标的方法

    public void DIS()
    {
        dis_pa=Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));  //p2与A点之间的距离
        dis_pb=Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y));  //p2与B点之间的距离
        dis_pc=Math.sqrt((x3-x)*(x3-x)+(y3-y)*(y3-y));  //p2与C点之间的距离
    }//求两点间距离的方法

    public void fangWeiJiao()
    {
        double deltaX=x1-x;
        double deltaY=y1-y;
        double alpha =Math.atan(deltaY/deltaX);
        alpha_a=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_a*=180/Math.PI;  //计算出AP2的方位角

        deltaX=x2-x;
        deltaY=y2-y;
        alpha =Math.atan(deltaY/deltaX);
        alpha_b=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_b*=180/Math.PI;  //计算出BP2的方位角

        deltaX=x3-x;
        deltaY=y3-y;
        alpha =Math.atan(deltaY/deltaX);
        alpha_c=deltaX>0?(deltaY>0?alpha:alpha+2*Math.PI):alpha+Math.PI;
        alpha_c*=180/Math.PI;  //计算出CP2的方位角
    }//求方位角的方法

    public void result()
    {
        String s1="P2点的坐标为：("+ QianFangJiaoHui.three(x)+","+ QianFangJiaoHui.three(y)+")";
        String s2="P2到A点的距离为："+ QianFangJiaoHui.three(dis_pa)+"    "+"A-P2的方位角为："+ QianFangJiaoHui.dmss(alpha_a);
        String s3="P2到B点的距离为："+ QianFangJiaoHui.three(dis_pb)+"    "+"B-P2的方位角为："+ QianFangJiaoHui.dmss(alpha_b);
        String s4="P2到C点的距离为："+ QianFangJiaoHui.three(dis_pc)+"    "+"C-P2的方位角为："+ QianFangJiaoHui.dmss(alpha_c);

        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter("result.txt",true));
            bw.write("您选择的是侧方交会，结果如下:");
            bw.newLine();
            bw.write(s1);
            bw.newLine();
            bw.write(s2);
            bw.newLine();
            bw.write(s3);
            bw.newLine();
            bw.write(s4);
            bw.newLine();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }//try-catch结构结束

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
    }//输出计算结果

    public void getC()
    {

        double alpha=Math.acos((dis_pa*dis_pa+dis_pc*dis_pc-((x1-x3)*(x1-x3)+(y1-y3)*(y1-y3)))/(2*dis_pa*dis_pc)); //根据余弦定理求检验角的计算值
        double check=Math.abs(alpha-_c)* k;             //侧方交会的检验值
        String ck=String.valueOf(check);
        int a=ck.indexOf(".");
        String b=ck.substring(a+1,a+2);                //截取小数点后一位
        int c=Integer.parseInt(b);
        if(c>4)
        {
            check+=1;
        }                                              //对结果进行四舍五入到整数
        int result=(int)check;

        String s1="侧方交会的检验值为："+result+"秒";
        try
        {
            BufferedWriter bw=new BufferedWriter(new FileWriter("result.txt",true));
            bw.write(s1);
            bw.newLine();
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }//try-catch语句结束

        System.out.println();
    }//实现抽象方法进行检核

}//侧方交会

public class JiaoHuiZuoYe
{
    public static int mbgeti(BufferedReader f)
    {
        try
        {
            String s=f.readLine();
            int i=Integer.parseInt(s);
            return i;
        }
        catch (Exception e)
        {
            return -1;
        }//try-catch结构结束
    }//方法mb_getInt结束
    public static double mbgetdou(BufferedReader f)
    {
        try
        {
            String s=f.readLine();
            double d=Double.parseDouble(s);
            return d;
        }
        catch (Exception e)
        {
            return 0d;
        }//try-catch结构结束
    }//方法mb_getDouble结束
    public static void main(String[] args)
    {

        int sel;           //选择前方交会和侧方交会的参数，"1"为前方交会，"2"为侧方交会
        double x1;
        double y1;
        double x2;
        double y2;
        double x3;
        double y3;

        try
        {
            System.out.println("请选择前方交会或侧方交会");
            System.out.println("\t1：前方交会； 2：侧方交会");
            BufferedReader f1=new BufferedReader(new InputStreamReader(System.in));
            sel= mbgeti(f1);
            System.out.println("请输入A点x坐标：");
            BufferedReader f2=new BufferedReader(new InputStreamReader(System.in));
            x1= mbgetdou(f2);
            System.out.println("请输入A点y坐标：");
            BufferedReader f3=new BufferedReader(new InputStreamReader(System.in));
            y1= mbgetdou(f3);
            System.out.println("请输入B点x坐标：");
            BufferedReader f4=new BufferedReader(new InputStreamReader(System.in));
            x2= mbgetdou(f4);
            System.out.println("请输入B点y坐标：");
            BufferedReader f5=new BufferedReader(new InputStreamReader(System.in));
            y2= mbgetdou(f5);
            System.out.println("请输入C点x坐标：");
            BufferedReader f6=new BufferedReader(new InputStreamReader(System.in));
            x3= mbgetdou(f6);
            System.out.println("请输入C点y坐标：");
            BufferedReader f7=new BufferedReader(new InputStreamReader(System.in));
            y3= mbgetdou(f7);

            if(sel==1)
            {
                System.out.println("请输入A点的内角");
                BufferedReader f8=new BufferedReader(new InputStreamReader(System.in));
                double aaa= mbgetdou(f8);
                String s1=String.valueOf(aaa);
                aaa= QianFangJiaoHui.qt(s1);
                System.out.println("请输入B点的第一个内角");
                BufferedReader f9=new BufferedReader(new InputStreamReader(System.in));
                double bbb= mbgetdou(f9);
                String s2=String.valueOf(bbb);
                bbb= QianFangJiaoHui.qt(s2);
                System.out.println("请输入B点的第二个内角");
                BufferedReader f10=new BufferedReader(new InputStreamReader(System.in));
                double bb2= mbgetdou(f10);
                String s3=String.valueOf(bb2);
                bb2= QianFangJiaoHui.qt(s3);
                System.out.println("请输入C点的内角");
                BufferedReader f11=new BufferedReader(new InputStreamReader(System.in));
                double ccc= mbgetdou(f11);
                String s4=String.valueOf(ccc);
                ccc= QianFangJiaoHui.qt(s4);

                QianFangJiaoHui fi=new QianFangJiaoHui(x1,y1,x2,y2,x3,y3,aaa,bbb,bb2,ccc);//前方交会
                System.out.println("您选择的是前方交会,结果如下：");
                fi.zb();      //求前方交会待定点坐标
                fi.distance();     //求两点间的距离
                fi.fwj();  //求方位角
                fi.result();       //输出结果
                fi.getC();     //进行检核
            }
            if(sel==2)
            {
                System.out.println("请输入A点的内角");
                BufferedReader f12=new BufferedReader(new InputStreamReader(System.in));
                double aaa2= mbgetdou(f12);
                String s5=String.valueOf(aaa2);
                aaa2= QianFangJiaoHui.qt(s5);
                System.out.println("请输入P2处内角");
                BufferedReader f13=new BufferedReader(new InputStreamReader(System.in));
                double pp2= mbgetdou(f13);
                String s6=String.valueOf(pp2);
                pp2= QianFangJiaoHui.qt(s6);
                System.out.println("请输入P2观测的检核角");
                BufferedReader f14=new BufferedReader(new InputStreamReader(System.in));
                double _c= mbgetdou(f14);
                String s7=String.valueOf(_c);
                _c= QianFangJiaoHui.qt(s7);

                SideIntersection si=new SideIntersection(x1,y1,x2,y2,x3,y3,aaa2,pp2,_c);//侧方交会
                System.out.println("您选择的是侧方交会，结果如下：");
                si.zuoBiao();      //求侧方交会待定点坐标
                si.DIS();     //求两点间的距离
                si.fangWeiJiao();  //求方位角
                si.result();       //输出结果
                si.getC();     //进行检核
            }
        }
        catch (Exception e)
        {
            System.err.println("发生异常："+e);
            e.printStackTrace();
        }//try-catch结构结束
    }//方法main结束
}//类Intersection3结束






