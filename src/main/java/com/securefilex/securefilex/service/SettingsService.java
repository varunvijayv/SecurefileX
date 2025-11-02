package com.securefilex.securefilex.service;

import com.securefilex.securefilex.model.AdminSettings;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private AdminSettings settings = new AdminSettings();

    public AdminSettings getSettings(){ return settings; }
    public void save(AdminSettings s){ this.settings = s; /*persist to DB or file for real*/ }
    public void resetDefaults(){ this.settings = new AdminSettings(); }
}
