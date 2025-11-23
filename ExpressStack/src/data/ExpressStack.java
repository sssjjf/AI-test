package data;


import view.Views;

import java.util.Random;

/**
 * 快递柜
 */
public class ExpressStack {
    //二维数组存储快递
    private Express[][] data = new Express[10][];{
        for(int i = 0;i<10;i++){
            data[i] = new Express[10];
        }
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
        if(size==100){
            System.out.println("录入失败，快递柜已满！");
            return;
        }
        //1.随机生成快递位置
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
        }
        //2. 随机生成6位取件码
        int code = randomCode();
        e.setCode(code);
        data[x][y] = e;
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
        for(int i = 0;i<10;i++){
            for(int j = 0;j<10;j++){
                if(data[i][j]==null)
                    continue;
                if(data[i][j].getCode()==code){
                    return data[i][j];
                }
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
        data[e.getPlaceX()][e.getPlaceY()] = null;
        size--;
        System.out.println("删除成功");
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
        for(int i = 0;i<10;i++){
            for(int j = 0;j<10;j++){
                if(data[i][j]==null)
                    continue;
                if(data[i][j].getNum().equals(num)){
                    return data[i][j];
                }
            }
        }
        return null;
    }

    /**
     * 查找所有快递
     */

    public void findAll() {
        Views views = new Views();
        for(int i = 0;i<10;i++){
            for(int j = 0;j<10;j++){
                if(data[i][j]==null) {
                    continue;
                }else{
                   views.printExpress(data[i][j]);
                }
            }
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
       data[e.getPlaceX()][e.getPlaceY()] = null;
       size--;
       return true;
    }


}
