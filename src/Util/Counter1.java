package Util;

class SimpleThread extends Thread{
    int countDown=5;
    int threadNum;
    static  int threadCOunt=0;
    public SimpleThread(){
        threadNum=++threadCOunt;
        System.out.print("making"+threadNum+"\n");
    }

    @Override
    public void run() {
        super.run();
        while (true) {
        System.out.print("thread"+threadNum+"("+countDown+")\n");
            if (--countDown==0)return;
        }
    }
    public static void main(String[] a){
        for (int i=0;i<5;i++){
            new SimpleThread().start();
            System.out.print("thread start\n");
        }
    }
}