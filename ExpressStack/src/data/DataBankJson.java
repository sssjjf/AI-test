package data;

import com.google.gson.Gson;

import java.io.*;

public class DataBankJson {
    private final File file = new File("data.json");
    private ExpressStackPlus expressStackPlus =new ExpressStackPlus();

    public DataBankJson() {
    }

    public ExpressStackPlus getExpressStackPlus() {
        return expressStackPlus;
    }

    public void setExpressStackPlus(ExpressStackPlus expressStackPlus) {
        this.expressStackPlus = expressStackPlus;
    }

    //下载数据
    public void Load() throws IOException {
        String sAll =new String();
        if(!file.exists()){
            file.createNewFile();
        }
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bf = new BufferedReader(reader);
            while(true){
                String s = bf.readLine();
                if(s==null){
                    break;
                }
                sAll = sAll+s;
            }
        }catch (IOException e){ }
        ExpressStackPlus expressStackPlus = new Gson().fromJson(sAll, ExpressStackPlus.class);
        this.expressStackPlus = expressStackPlus;
    }

    //保存数据
    public void save(){
        Gson gson = new Gson();
        String s = gson.toJson(expressStackPlus);
        FileOutputStream fos  = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream ps =new PrintStream(fos);
        ps.println(s);
    }
}
