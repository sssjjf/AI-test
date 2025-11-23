package data;


import view.Views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * 快递柜
 */
public class ExpressStackPlus implements Serializable {
    //ArrayList集合存储数据
    private final int MAX_EXPRESS=100;
    ArrayList<Express> data = new ArrayList<>(100);

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    //快递柜现存快递大小
    private int size=0;
    private  Random random = new Random();

    /**
     * 存快递
     * @param e
     * @return
     */
    public void add(Express e){
        //快递柜满了
        if(size==MAX_EXPRESS){
            System.out.println("录入失败，快递柜已满！");
            return;
        }
        /*1.随机生成快递位置
        int x = -1;
        int y = -1;

        while(true){
            x = random.nextInt(10);
            y = random.nextInt(10);
            if(data[x][y] == null){
                e.setPlaceX(x);
                e.setPlaceY(y);
                break;
            }
        }*/
        //2. 随机生成6位取件码
        int code = randomCode();
        e.setCode(code);
        data.add(e);
        size++;
        System.out.println("录入成功！");
    }

    /**
     * 随机生成6位取件码
     * @return
     */
    private int randomCode() {
        while(true) {
            int code = random.nextInt(100000) + 100000;
            Express e = findByCode(code);
            if (e == null) {
                return code;
            }
        }
    }

    /**
     * 通过取件码查找快递
     * @param code
     * @return
     */
    private Express findByCode(int code) {
        for(Express e:data){
            if(e.getCode()==code){
                return e;
            }
        }
        return null;
    }

    /**
     * 删除快递
     * @param e
     * @return
     */
    public void delete(Express e){
        Express e1 = findByCode(e.getCode());
        if(e1==null){
            System.out.println("未找到该快递");
        }else{
            data.remove(e1);
            size--;
            System.out.println("删除成功");
        }
    }

    /**
     * 更新快递信息
     * @param
     */
    public void update(Express Old,Express New){
       Old.setNum(New.getNum());
       Old.setCompany(New.getCompany());
       System.out.println("更新成功！");
    }

    public Express findByNumber(String num) {
        for(Express e:data){
            if(e.getNum().equals(num)){
                return e;
            }
        }
        return null;
    }

    /**
     * 查找所有快递
     */

    public void findAll() {
        Views views = new Views();
        for(Express e:data){
            views.printExpress(e);
        }
    }

    /**
     * 用户取快递
     */
    public boolean getExpress(int code){
       Express e = findByCode(code);
       if(e == null){ //没找到快递
           return false;
       }
       //找到快递并取出，size减1
       data.remove(e);
       size--;
       return true;
    }
}
