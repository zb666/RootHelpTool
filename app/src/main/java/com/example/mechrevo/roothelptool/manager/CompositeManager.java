package com.example.mechrevo.roothelptool.manager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CompositeManager {

   static CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void unSubscribe(){
        compositeDisposable.clear();
    }

    public static void register(Disposable disposable){
        compositeDisposable.add(disposable);
    }

}
