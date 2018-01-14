package com.example.user.check_mate.model.backend;

/**
 * Created by User on 03/12/2017.
 */

public class FactoryMethod {
    private static BackEndFunc backEndFuncForList=null;
    private static BackEndFunc backEndFuncForFireBase=null;

    private FactoryMethod(DataSourceType dataSourceType) {

    }
    public static BackEndFunc getBackEndFunc(DataSourceType dataSourceType)
    {
        switch (dataSourceType){
            case DATA_INTERNET:
            {
                if (backEndFuncForFireBase==null) {
                    backEndFuncForFireBase=new BackendForFirebase();
                }
                return backEndFuncForFireBase;
            }
            case DATA_LIST:
            {
                if(backEndFuncForList==null)
                {
                    backEndFuncForList=new BackEndForList();
                }
                return backEndFuncForList;
            }
        }
        return backEndFuncForList;
    }
}
