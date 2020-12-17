package bot.rat.services;

import bot.rat.entities.Setting;
import bot.rat.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsService {

    @Autowired
    SettingsRepository settingsRepository;

    public Setting getSettingById(String id) {
        Optional<Setting> optSetting = settingsRepository.findById(id);
        return optSetting.orElse(null);
    }

    public void saveSettingBoolean(String id, Boolean bool) {
        Setting s = new Setting(id);
        s.setBool(bool);
        settingsRepository.save(s);
    }
}
