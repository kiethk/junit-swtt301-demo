package com.rtms.backend.scheduler;

import com.rtms.backend.entity.GroomDailyTask;
import com.rtms.backend.service.GroomDailyTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GroomDailyTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(GroomDailyTaskScheduler.class);

    private final GroomDailyTaskService taskService;

    public GroomDailyTaskScheduler(GroomDailyTaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Tự động quét và sinh bộ 5 việc chuẩn SOP cho tất cả các ngựa đang ở trong chuồng
     * vào đúng 00:00:00 mỗi đêm (Múi giờ Asia/Ho_Chi_Minh).
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    public void scheduleDailyRoutineGeneration() {
        LocalDate today = LocalDate.now();
        log.info("[SOP Scheduler] Bắt đầu tự động sinh công việc SOP cho ngày: {}", today);
        try {
            List<GroomDailyTask> tasks = taskService.generateDailyRoutineTasks(today);
            log.info("[SOP Scheduler] Hoàn thành sinh {} công việc SOP hàng ngày.", tasks.size());
        } catch (Exception e) {
            log.error("[SOP Scheduler] Lỗi khi tự động sinh việc hàng ngày: {}", e.getMessage(), e);
        }
    }
}
