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
comment|/**      * remove all jobs scheduled to run at this time      * @param time      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"remove jobs with matching execution time"
argument_list|)
specifier|public
specifier|abstract
name|void
name|removeJobAtScheduledTime
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
comment|/**      * remove a job with the matching jobId      * @param jobId      * @throws Exception      */
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
comment|/**      * remove all the Jobs from the scheduler      * @throws Exception      */
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
comment|/**      * remove all the Jobs from the scheduler that are due between the start and finish times      * @param start time       * @param finish time      * @throws Exception      */
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
comment|/**      * Get the next time jobs will be fired      * @return the time in milliseconds      * @throws Exception       */
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
comment|/**      * Get all the jobs scheduled to run next      * @return a list of jobs that will be scheduled next      * @throws Exception      */
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
comment|/**      * Get all the outstanding Jobs      * @return a  table of all jobs      * @throws Exception       */
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
comment|/**      * Get all outstanding jobs due to run between start and finish      * @param start      * @param finish      * @return a table of jobs in the range      * @throws Exception       */
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

