begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_interface
specifier|public
interface|interface
name|JobSchedulerViewMBean
block|{
comment|/**      * Remove all jobs scheduled to run at this time.  If there are no jobs scheduled      * at the given time this methods returns without making any modifications to the      * scheduler store.      *      * @param time      *        the string formated time that should be used to remove jobs.      *      * @throws Exception if an error occurs while performing the remove.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"remove jobs with matching execution time"
argument_list|)
specifier|public
specifier|abstract
name|void
name|removeAllJobsAtScheduledTime
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"time: yyyy-MM-dd hh:mm:ss"
argument_list|)
name|String
name|time
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Remove a job with the matching jobId.  If the method does not find a matching job      * then it returns without throwing an error or making any modifications to the job      * scheduler store.      *      * @param jobId      *        the Job Id to remove from the scheduler store.      *      * @throws Exception if an error occurs while attempting to remove the Job.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"remove jobs with matching jobId"
argument_list|)
specifier|public
specifier|abstract
name|void
name|removeJob
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"jobId"
argument_list|)
name|String
name|jobId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Remove all the Jobs from the scheduler,      *      * @throws Exception if an error occurs while purging the store.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"remove all scheduled jobs"
argument_list|)
specifier|public
specifier|abstract
name|void
name|removeAllJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Remove all the Jobs from the scheduler that are due between the start and finish times.      *      * @param start      *        the starting time to remove jobs from.      * @param finish      *        the finish time for the remove operation.      *      * @throws Exception if an error occurs while attempting to remove the jobs.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"remove all scheduled jobs between time ranges "
argument_list|)
specifier|public
specifier|abstract
name|void
name|removeAllJobs
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"start: yyyy-MM-dd hh:mm:ss"
argument_list|)
name|String
name|start
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"finish: yyyy-MM-dd hh:mm:ss"
argument_list|)
name|String
name|finish
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the next time jobs will be fired from this scheduler store.      *      * @return the time in milliseconds of the next job to execute.      *      * @throws Exception if an error occurs while accessing the store.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"get the next time a job is due to be scheduled "
argument_list|)
specifier|public
specifier|abstract
name|String
name|getNextScheduleTime
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Gets the number of times a scheduled Job has been executed.      *      * @return the total number of time a scheduled job has executed.      *      * @throws Exception if an error occurs while querying for the Job.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"get the next time a job is due to be scheduled "
argument_list|)
specifier|public
specifier|abstract
name|int
name|getExecutionCount
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"jobId"
argument_list|)
name|String
name|jobId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all the jobs scheduled to run next.      *      * @return a list of jobs that will be scheduled next      *      * @throws Exception if an error occurs while reading the scheduler store.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"get the next job(s) to be scheduled. Not HTML friendly "
argument_list|)
specifier|public
specifier|abstract
name|TabularData
name|getNextScheduleJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all the outstanding Jobs that are scheduled in this scheduler store.      *      * @return a table of all jobs in this scheduler store.      *      * @throws Exception if an error occurs while reading the store.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"get the scheduled Jobs in the Store. Not HTML friendly "
argument_list|)
specifier|public
specifier|abstract
name|TabularData
name|getAllJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all outstanding jobs due to run between start and finish time range.      *      * @param start      *        the starting time range to query the store for jobs.      * @param finish      *        the ending time of this query for scheduled jobs.      *      * @return a table of jobs in the range given.      *      * @throws Exception if an error occurs while querying the scheduler store.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"get the scheduled Jobs in the Store within the time range. Not HTML friendly "
argument_list|)
specifier|public
specifier|abstract
name|TabularData
name|getAllJobs
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"start: yyyy-MM-dd hh:mm:ss"
argument_list|)
name|String
name|start
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"finish: yyyy-MM-dd hh:mm:ss"
argument_list|)
name|String
name|finish
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

