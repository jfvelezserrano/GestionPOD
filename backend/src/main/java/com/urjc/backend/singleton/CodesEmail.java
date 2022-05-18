package com.urjc.backend.singleton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CodesEmail {
    Map<Long, List<String>> map = new HashMap<Long, List<String>>();

    private static CodesEmail codesEmail;


    public  static CodesEmail getCodeEmail() {

        if (codesEmail==null) {

            codesEmail=new CodesEmail();
        }
        return codesEmail;
    }

    public Map<Long, List<String>> getMap() {
        return map;
    }

    public Boolean isCorrect(Long code, String ip) {
        if(existsCode(code)){
            List<String> values = map.get(code);

            return (values.get(1) == ip && !isDateExpired(values.get(2)));
        }
        return false;
    }

    public void addCode(Long code, String email, String ip){
        if(!existsCode(code)){
            map.put(code, new ArrayList<String>());
            map.get(code).add(email);
            map.get(code).add(ip);

            map.get(code).add(getDateExpiration());
        }
    }

    public void removeCode(Long code){
        map.remove(code);
    }

    public String getEmailByCode(Long code){
        return this.codesEmail.getMap().get(code).get(0);
    }

    public String getIpByCode(Integer code){
        return this.codesEmail.getMap().get(code).get(1);
    }

    public String getDateExpirationByCode(Long code){
        return this.codesEmail.getMap().get(code).get(2);
    }

    public void removeAllExpiredCodes(){
        for (Long key : map.keySet()) {
            if (isDateExpired(getDateExpirationByCode(key))) {
                map.remove(key);
            }
        }
    }

    public Boolean existsCode(Long code){
        return map.get(code) != null;
    }

    private CodesEmail(){}

    private boolean isDateExpired(String dateCodeEmail){

        boolean isExpired = true;
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(dateCodeEmail);

            Date currentTimeNow = Calendar.getInstance().getTime();

            isExpired = date.before(currentTimeNow);
        } catch (ParseException e) {
            e.printStackTrace();
            isExpired = true;
        }

        return isExpired;
    }

    private String getDateExpiration(){
        Calendar currentTimeNow = Calendar.getInstance();
        currentTimeNow.add(Calendar.MINUTE, 5);
        Date dateExpiration = currentTimeNow.getTime();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        return dateFormat.format(dateExpiration);
    }
}
