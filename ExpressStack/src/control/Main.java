package control;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import data.*;
import view.Views;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
   private static Views view = new Views();
   //private static ExpressStack data = new ExpressStack();
   private static ExpressStackPlus data;
   private static DataBank dataBank; //以文本文件格式存储数据
   private static DataBankJson dataBankJson; //以JSon格式存储数据

   public static void main(String[] args) throws IOException, ClassNotFoundException {

      //欢迎
      view.welcome();
      //初始化数据
      init();
      //进入主菜单,选择用户身份
      cMenu();
      //退出系统
      view.bye();

      //保存数据
      //dataBank.setExpressStackPlus(data);
      //dataBank.save();

      //保存数据
      dataBankJson.setExpressStackPlus(data);
      dataBankJson.save();


      //将数据导入服务器
      //String fileName = "ExpressStackPlus.txt";
      //sendDataToServer(fileName);


   }

   private static void cMenu() {
      while(true) {
         int menu = view.menu();
         switch (menu) {
            case 0: //退出
               return;
            case 1:cClient(); //快递员
               break;
            case 2:uClient();  //普通用户
               break;
         }
      }
   }

   private static void getDataFromServer(String fileName ) throws IOException {
      Socket socket = new Socket("127.0.0.1",65521);
      System.out.println("连接服务器成功！");

      //1. 先向服务器发送Flag
      String flag = "get";
      OutputStream os = socket.getOutputStream();
      PrintStream ps = new PrintStream(os);
      ps.println(flag);

      //2. 向服务器要传送要获取数据的文件名
      ps.println(fileName);

      //3. 从服务器文件中获取数据，写入本地文件
      InputStream is = socket.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      PrintWriter pw = new PrintWriter("data.txt");
      while(true){
         String text = br.readLine();
         if(text==null){
            break;
         }
         pw.println(text);
      }
      System.out.println("数据获取完成");
      ps.close();
      os.close();
      pw.close();
      br.close();
      is.close();
   }

   private static void sendDataToServer(String fileName) throws IOException {
      Socket socket = new Socket("127.0.0.1",65521);
      System.out.println("连接服务器成功！");

      //1. 向服务器发送Flag
      OutputStream os = socket.getOutputStream();
      PrintStream ps = new PrintStream(os);
      ps.println(new String("send"));

      //2. 先向服务器要传送生成的文件名
      ps.println(fileName);

      //3. 从本地文件中获取数据，写入服务器文件
      BufferedReader br = new BufferedReader(new FileReader("data.txt"));
      PrintWriter pw = new PrintWriter(os);
      while(true){
         String text = br.readLine();
         if(text == null){
            break;
         }
         pw.println(text);
      }
      System.out.println("数据发送完成");
      pw.close();
      ps.close();
      os.close();
      br.close();
   }

   private static void init() throws IOException, ClassNotFoundException {
     /*/
      String fileName = "ExpressStackPlus.txt";
      getDataFromServer(fileName);
      */
      data =new ExpressStackPlus();
      /*
      /
      dataBank = new DataBank();
      dataBank.Load();
      if(dataBank.getExpressStackPlus()!=null){
         data = dataBank.getExpressStackPlus();
      }*/
      dataBankJson = new DataBankJson();
      dataBankJson.Load();
      if(dataBankJson.getExpressStackPlus()!=null){
         data=dataBankJson.getExpressStackPlus();
      }
   }

   private static void cClient() {
      while(true) {
         int cmenu = view.cmenu();
         switch (cmenu) {
            case 0:return;//返回上级目录
            case 1: {  //录入快递
               Express e = view.insert();
               data.add(e);
            }
            break;
            case 2:{  //删除快递
               //1.输入要删除的快递信息
               Express e =view.delete();
               //2.在快递栈查询快递
               Express e1 = data.findByNumber(e.getNum());
               //3.删除快递
               if(e1 != null) {
                  data.delete(e1);
               }else{
                  System.out.println("未找到该快递！");
               }
            }
               break;
            case 3: {  //更新快递信息
               //1.输入需要更新的快递信息
               Express e = view.findByNumber();
               //2.在快递栈中查找快递
               Express e1 = data.findByNumber(e.getNum());
               //3.输入更改信息
               if(e1==null){
                  System.out.println("未找到该快递!");
                  break;
               }
               Express e2 = view.update();
               //4.更新快递
               data.update(e1,e2);
            }
               break;
            case 4: { //查看所有快递
               //1.寻问是否查找所有快递
               boolean b = view.findAll();
               if(b)
                  data.findAll();
            }
               break;
         }
      }
   }

   private static void uClient() {
      while(true){
         int code = view.umenu();
         if(code==0){
            return; //返回上级目录
         }
         boolean b = data.getExpress(code);
         if(b){
            System.out.println("取件成功！");
         }else{
            System.out.println("取件失败！");
         }
      }
   }
}
