package data;

import java.io.*;

public class DataBank {
    private final File file = new File("data.txt");
    private ExpressStackPlus expressStackPlus =new ExpressStackPlus();

    public DataBank() {
    }

    public ExpressStackPlus getExpressStackPlus() {
        return expressStackPlus;
    }

    public void setExpressStackPlus(ExpressStackPlus expressStackPlus) {
        this.expressStackPlus = expressStackPlus;
    }

    //下载数据
    public void Load() throws IOException {
        ExpressStackPlus express = null;
        if(!file.exists()){
            file.createNewFile();
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            express = (ExpressStackPlus) ois.readObject();
            ois.close();
            fis.close();
        }catch (IOException | ClassNotFoundException  e){ }
        this.expressStackPlus=express;
    }

    //保存数据
    public void save(){
        ObjectOutputStream oos =null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this.expressStackPlus);
            oos.close();
            fos.close();
        }catch (IOException e){
            try {
                oos.writeObject(this.expressStackPlus);
            } catch (IOException ioException) {

            }
        }
    }
}
