/**   
 * @Copyright:  江西金磊科技发展有限公司  All rights reserved.Notice 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */

package com.jeecms.system.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeecms.auth.domain.CoreUser;
import com.jeecms.auth.service.CoreUserService;
import com.jeecms.common.base.scheduler.IBaseJob;
import com.jeecms.common.exception.GlobalException;
import com.jeecms.common.web.ApplicationContextProvider;

/**
 * 解除用户禁用
 * 
 * @author: tom
 * @date: 2019年5月15日 上午10:30:37
 */
public class UserReleaseLockJob implements IBaseJob {
        private Logger logger = LoggerFactory.getLogger(UserReleaseLockJob.class);

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
                JobDataMap map = context.getMergedJobDataMap();
                logger.info("Running Job name : {} ", map.getString("name"));
                Integer userId = Integer.parseInt((String) map.get("params"));
                long startTime = System.currentTimeMillis();
                initService();
                try {
                        CoreUser user = userService.findById(userId);
                        user.setEnabled(true);
                        userService.update(user);
                } catch (GlobalException e) {
                        logger.error("UserReleaseLockJob job userid =" + userId + " not find");
                }
                long endTime = System.currentTimeMillis();
                logger.info(">>>>>>>>>>>>> Running Job has been completed , cost time :  " + (endTime - startTime)
                                + "ms\n");
        }

        private void initService() {
                userService = ApplicationContextProvider.getBean(CoreUserService.class);
        }

        private CoreUserService userService;

}
