package com.example.parsejson;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataVM extends ViewModel {
    MyThread mythread;
    static int NUMBER_TICKS=100;

    public DataVM() {
        super();

        //initialize as appropriate
        jsondata = new MutableLiveData<String>();
    }

    //lets add some livedata
    private MutableLiveData<String> jsondata ;
    public MutableLiveData<String> getjsondata() {
        return jsondata;
    }

    public class MyThread extends Thread {
        private final String url;
        public MyThread(String url) {
            this.url=url;
        }

        public void run() {
            //run the task
            Download_https mytask= new Download_https(this.url);
            jsondata.postValue(mytask.get_text());
        }
    }
    public void start_thread(String url){
        //only start 1 thread at a time
        if(mythread == null || !mythread.isAlive()){
            mythread = new MyThread(url);  //if there is an old thread it is GCed
            mythread.start();
         }
    }
}
