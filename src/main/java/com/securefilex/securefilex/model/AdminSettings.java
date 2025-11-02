package com.securefilex.securefilex.model;

public class AdminSettings {
    private int maxFileSizeMb = 20;
    private String allowedTypes = "application/pdf,image/png,image/jpeg,.txt";
    private int maxRetries = 5;
    private int blockMinutes = 30;
    private String verifierRules = "{}";

    // getters & setters
    public int getMaxFileSizeMb(){return maxFileSizeMb;}
    public void setMaxFileSizeMb(int v){this.maxFileSizeMb=v;}
    public String getAllowedTypes(){return allowedTypes;}
    public void setAllowedTypes(String s){this.allowedTypes=s;}
    public int getMaxRetries(){return maxRetries;}
    public void setMaxRetries(int r){this.maxRetries=r;}
    public int getBlockMinutes(){return blockMinutes;}
    public void setBlockMinutes(int m){this.blockMinutes=m;}
    public String getVerifierRules(){return verifierRules;}
    public void setVerifierRules(String r){this.verifierRules=r;}
}
