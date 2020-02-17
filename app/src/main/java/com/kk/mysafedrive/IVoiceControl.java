package com.kk.mysafedrive;

public interface IVoiceControl {
    void processVoiceCommands(String... voiceCommands);
    
    void restartListeningService();
}
