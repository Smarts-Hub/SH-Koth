package dev.smartshub.shkoth.service.gui.menu.cache;

import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothValidation.ValidationResult;
import dev.smartshub.shkoth.service.gui.menu.aux.WaitingToFill;
import dev.smartshub.shkoth.service.koth.KothRegistrationFromTempDataService;
import dev.smartshub.shkoth.service.koth.KothRegistrationFromTempDataService.RegistrationResult;

import java.util.*;

public class KothToRegisterCache {

    private GuiService guiService;
    private KothRegistrationFromTempDataService registrationService;
    private final Set<KothTempData> kothsToRegister = new HashSet<>();

    public void addKothToRegister(UUID uuid){
        if(getKothToRegister(uuid) == null) {
            kothsToRegister.add(new KothTempData(guiService, uuid));
        }
    }

    public void removeKothToRegister(UUID uuid){
        kothsToRegister.removeIf(kothTempData -> kothTempData.getCreatorUUID().equals(uuid));
    }

    public boolean isWaitingToChat(UUID uuid){
        KothTempData kothTempData = getKothToRegister(uuid);
        return kothTempData != null && kothTempData.isWaitingToChat();
    }

    public void fillChatInput(UUID uuid, String input){
        KothTempData kothTempData = getKothToRegister(uuid);
        if(kothTempData != null) {
            kothTempData.fillChatInput(input);
        }
    }

    public void setWaitingToFill(UUID uuid, WaitingToFill waitingToFill) {
        KothTempData kothTempData = getKothToRegister(uuid);
        if(kothTempData != null) {
            kothTempData.setWaitingToFill(waitingToFill);
        }
    }

    public void cancelWaiting(UUID uuid) {
        KothTempData kothTempData = getKothToRegister(uuid);
        if(kothTempData != null) {
            kothTempData.cancelWaiting();
        }
    }

    public KothTempData getKothToRegister(UUID uuid){
        return kothsToRegister.stream()
                .filter(kothTempData -> kothTempData.getCreatorUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean hasKothToRegister(UUID uuid) {
        return getKothToRegister(uuid) != null;
    }

    public Set<KothTempData> getAllKothsToRegister() {
        return new HashSet<>(kothsToRegister);
    }

    public void clearCache() {
        kothsToRegister.clear();
    }

    public int getCacheSize() {
        return kothsToRegister.size();
    }

    public ValidationResult validateKoth(UUID uuid) {
        KothTempData tempData = getKothToRegister(uuid);
        if(tempData == null) {
            return new ValidationResult(false, List.of("KOTH data not found"));
        }
        return KothValidation.validate(tempData);
    }

    public void buildKoth(UUID uuid) {
        KothTempData tempData = getKothToRegister(uuid);
        if(tempData == null) {
            return;
        }
        var result = registerKoth(uuid);
    }

    public RegistrationResult registerKoth(UUID uuid) {
        if (registrationService == null) {
            return new RegistrationResult(false, "KothRegistrationFromTempDataService not set!");
        }
        return registrationService.registerKoth(uuid);
    }

    public KothRegistrationFromTempDataService.KothPreview getKothPreview(UUID uuid) {
        if (registrationService == null) {
            return null;
        }
        return registrationService.getKothPreview(uuid);
    }

    public boolean cancelRegistration(UUID uuid) {
        if (registrationService != null) {
            return registrationService.cancelRegistration(uuid);
        }
        return false;
    }

    public void setGuiService(GuiService guiService) {
        this.guiService = guiService;
    }

    public void setRegistrationService(KothRegistrationFromTempDataService registrationService) {
        this.registrationService = registrationService;
    }
}