package ink.verge.yiban_auto_checkin.run;

import ink.verge.yiban_auto_checkin.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResetCheckinStatus {
    private CheckinService checkinService;

    @Autowired
    public void setCheckinService(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetCheckinStatusToFalse(){
        checkinService.resetCheckinStatus();
    }
}
