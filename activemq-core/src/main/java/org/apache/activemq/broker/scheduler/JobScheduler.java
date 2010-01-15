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
name|io
operator|.
name|IOException
import|;
end_import

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
name|kahadb
operator|.
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_interface
interface|interface
name|JobScheduler
block|{
comment|/**      * @return the name of the scheduler      */
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**  * Add a Job listener  * @param l  */
specifier|public
specifier|abstract
name|void
name|addListener
parameter_list|(
name|JobListener
name|l
parameter_list|)
function_decl|;
comment|/**  * remove a JobListener  * @param l  */
specifier|public
specifier|abstract
name|void
name|removeListener
parameter_list|(
name|JobListener
name|l
parameter_list|)
function_decl|;
comment|/**      * Add a job to be scheduled      * @param jobId a unique identifier for the job      * @param payload the message to be sent when the job is scheduled      * @param delay the time in milliseconds before the job will be run      * @throws IOException      */
specifier|public
specifier|abstract
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
name|IOException
function_decl|;
comment|/**      * Add a job to be scheduled      * @param jobId a unique identifier for the job      * @param payload the message to be sent when the job is scheduled      * @param start       * @param period the time in milliseconds between successive executions of the Job      * @param repeat the number of times to execute the job - less than 0 will be repeated forever      * @throws IOException      */
specifier|public
specifier|abstract
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
name|start
parameter_list|,
name|long
name|period
parameter_list|,
name|int
name|repeat
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * remove all jobs scheduled to run at this time      * @param time      * @throws IOException      */
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|long
name|time
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * remove a job with the matching jobId      * @param jobId      * @throws IOException      */
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|(
name|String
name|jobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get all the jobs scheduled to run next      * @return a list of messages that will be scheduled next      * @throws IOException      */
specifier|public
specifier|abstract
name|List
argument_list|<
name|ByteSequence
argument_list|>
name|getNextScheduleJobs
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the next time jobs will be fired      * @return the time in milliseconds      * @throws IOException       */
specifier|public
specifier|abstract
name|long
name|getNextScheduleTime
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

