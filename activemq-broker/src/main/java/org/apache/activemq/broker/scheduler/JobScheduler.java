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
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_interface
specifier|public
interface|interface
name|JobScheduler
block|{
comment|/**      * @return the name of the scheduler      * @throws Exception      */
name|String
name|getName
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Starts dispatch of scheduled Jobs to registered listeners.      *      * Any listener added after the start dispatch method can miss jobs so its      * important to register critical listeners before the start of job dispatching.      *      * @throws Exception      */
name|void
name|startDispatching
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Stops dispatching of scheduled Jobs to registered listeners.      *      * @throws Exception      */
name|void
name|stopDispatching
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Add a Job listener which will receive events related to scheduled jobs.      *      * @param listener      *      The job listener to add.      *      * @throws Exception      */
name|void
name|addListener
parameter_list|(
name|JobListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * remove a JobListener that was previously registered.  If the given listener is not in      * the registry this method has no effect.      *      * @param listener      *      The listener that should be removed from the listener registry.      *      * @throws Exception      */
name|void
name|removeListener
parameter_list|(
name|JobListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Add a job to be scheduled      *      * @param jobId      *            a unique identifier for the job      * @param payload      *            the message to be sent when the job is scheduled      * @param delay      *            the time in milliseconds before the job will be run      *      * @throws Exception if an error occurs while scheduling the Job.      */
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|long
name|delay
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Add a job to be scheduled      *      * @param jobId      *            a unique identifier for the job      * @param payload      *            the message to be sent when the job is scheduled      * @param cronEntry      *            The cron entry to use to schedule this job.      *      * @throws Exception if an error occurs while scheduling the Job.      */
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|String
name|cronEntry
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Add a job to be scheduled      *      * @param jobId      *            a unique identifier for the job      * @param payload      *            the message to be sent when the job is scheduled      * @param cronEntry      *            cron entry      * @param delay      *            time in ms to wait before scheduling      * @param period      *            the time in milliseconds between successive executions of the Job      * @param repeat      *            the number of times to execute the job - less than 0 will be repeated forever      * @throws Exception      */
name|void
name|schedule
parameter_list|(
name|String
name|jobId
parameter_list|,
name|ByteSequence
name|payload
parameter_list|,
name|String
name|cronEntry
parameter_list|,
name|long
name|delay
parameter_list|,
name|long
name|period
parameter_list|,
name|int
name|repeat
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * remove all jobs scheduled to run at this time      *      * @param time      *      The UTC time to use to remove a batch of scheduled Jobs.      *      * @throws Exception      */
name|void
name|remove
parameter_list|(
name|long
name|time
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * remove a job with the matching jobId      *      * @param jobId      *      The unique Job Id to search for and remove from the scheduled set of jobs.      *      * @throws Exception if an error occurs while removing the Job.      */
name|void
name|remove
parameter_list|(
name|String
name|jobId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * remove all the Jobs from the scheduler      *      * @throws Exception      */
name|void
name|removeAllJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * remove all the Jobs from the scheduler that are due between the start and finish times      *      * @param start      *            time in milliseconds      * @param finish      *            time in milliseconds      * @throws Exception      */
name|void
name|removeAllJobs
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|finish
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the next time jobs will be fired      *      * @return the time in milliseconds      * @throws Exception      */
name|long
name|getNextScheduleTime
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all the jobs scheduled to run next      *      * @return a list of jobs that will be scheduled next      * @throws Exception      */
name|List
argument_list|<
name|Job
argument_list|>
name|getNextScheduleJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all the outstanding Jobs      *      * @return a list of all jobs      * @throws Exception      */
name|List
argument_list|<
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get all outstanding jobs due to run between start and finish      *      * @param start      * @param finish      * @return a list of jobs      * @throws Exception      */
name|List
argument_list|<
name|Job
argument_list|>
name|getAllJobs
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|finish
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

