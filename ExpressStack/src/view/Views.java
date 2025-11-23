package view;

import data.Express;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Views implements Serializable {
    private Scanner input = new Scanner(System.in);

    public void welcome(){
        System.out.println("欢迎使用快递E栈！");
    }
    public void bye(){
        System.out.println("欢迎下次使用快递E栈！");
    }
    /**
    * 主菜单，选择使用用户身份
    */
    public int menu(){
        System.out.println("根据提示输入功能序号");
        System.out.println("1.快递员");
        System.out.println("2.普通用户");
        System.out.println("0.quit");

        String text = input.nextLine();
        int num=-1;
        try {
            num = Integer.parseInt(text);
        }catch (NumberFormatException e){
            System.out.println("输入错误!请重新输入");
            return menu();
        }

        if(num>2||num<0){
            System.out.println("输入序号错误!请重新输入");
            return menu();
        }
        return num;
    }
    /**
    * 快递员菜单
    */
    public int cmenu(){
        System.out.println("根据提示输入功能序号");
        System.out.println("1.快递录入");
        System.out.println("2.快递删除");
        System.out.println("3.快递修改");
        System.out.println("4.查询所有快递");
        System.out.println("0.返回上级目录");

        String text = input.nextLine();
        int num=-1;
        try {
            num = Integer.parseInt(text);
        }catch (NumberFormatException e){
            System.out.println("输入错误!请重新输入");
            return cmenu();
        }

        if(num>4||num<0){
            System.out.println("输入序号错误!请重新输入");
            return cmenu();
        }
        return num;
    }

    /**
     * 快递员存入快递
     * @param
     */
    public Express insert(){
        System.out.println("请按提示输入要录入的快递信息:");
        System.out.println("请输入快递公司:");
        String s1 = input.nextLine();
        System.out.println("请输入快递单号:");
        String s2 = input.nextLine();
        Express e = new Express();
        e.setCompany(s1);
        e.setNum(s2);
        return e;
    }

    /**
     * 寻问是否删除快递
     * @param
     */
    public Express delete(){
        System.out.println("请按提示输入要删除的快递信息:");
        System.out.println("请输入快递公司:");
        String s1 = input.nextLine();
        System.out.println("请输入快递单号:");
        String s2 = input.nextLine();
        Express e = new Express();
        e.setCompany(s1);
        e.setNum(s2);
        return e;
    }
    /**
     * 修改快递
     */
    public Express update(){
        System.out.println("请按提示输入要更新的快递信息:");
        System.out.println("请输入快递公司:");
        String s1 = input.nextLine();
        System.out.println("请输入快递单号:");
        String s2 = input.nextLine();
        Express e = new Express();
        e.setCompany(s1);
        e.setNum(s2);
        return e;
    }

    /**
     * 查询快递
     */
    public Express findByNumber(){
        System.out.println("请输入快递公司:");
        String s1 = input.nextLine();
        System.out.println("请输入快递单号:");
        String s2 = input.nextLine();
        Express e = new Express();
        e.setCompany(s1);
        e.setNum(s2);
        return e;
    }
    /**
     * 寻问是否查找所以快递
     */
    public boolean findAll(){
        System.out.println("是否查询所有快递");
        System.out.println("1.是 ， 2.否");
        String s1 = input.nextLine();
        int num =-1;
        try {
            num = Integer.parseInt(s1);
        }catch (NumberFormatException e){
            System.out.println("输入有误，请重新输入");
            return findAll();
        }
        if(num == 1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 打印快递
     */
    public void printExpress(Express e){
        System.out.println("_______________________________");
        System.out.println("快递单号:"+e.getNum());
        System.out.println("快递公司:"+e.getCompany());
        //System.out.println("快递位置:("+e.getPlaceX()+","+e.getPlaceY()+")");
        System.out.println("快递取件码:"+e.getCode());
        System.out.println("_______________________________");
        System.out.println();
    }

    /**
     * 用户菜单
     */
    public int umenu(){
        System.out.println("根据提示进行取件");
        System.out.println("请输入6位取件码，输入0返回上级目录:");
        String text = input.nextLine();
        int code = -1;
        try{
            code = Integer.parseInt(text);
        }catch (NumberFormatException e){
            System.out.println("输入错误!请重新输入");
            return umenu();
        }
        if(code == 0){
            return 0;
        }

        if(code<100000||code>199999){
            System.out.println("输入取件码错误!请重新输入");
            return umenu();
        }
        return code;
    }
}
