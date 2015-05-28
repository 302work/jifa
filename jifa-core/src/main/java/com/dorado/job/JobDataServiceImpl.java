package com.dorado.job;

import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.job.model.JobDefinition;
import com.bstek.bdf2.job.service.IJobDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时调度
 * @author june
 * 15/4/11 00:05
 */
@Service
public class JobDataServiceImpl implements IJobDataService {

    @Override
    public String getCompanyId() {
        if(ContextHolder.getLoginUser()!=null){
            return ContextHolder.getLoginUser().getCompanyId();
        }
        return null;
    }

    @Override
    public List<JobDefinition> filterJobs(List<JobDefinition> jobDefinitions) {
        return jobDefinitions;
    }
}
