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

begin_interface
specifier|public
interface|interface
name|Job
block|{
comment|/**      * @return the jobId      */
specifier|public
specifier|abstract
name|String
name|getJobId
parameter_list|()
function_decl|;
comment|/**      * @return the repeat      */
specifier|public
specifier|abstract
name|int
name|getRepeat
parameter_list|()
function_decl|;
comment|/**      * @return the start      */
specifier|public
specifier|abstract
name|long
name|getStart
parameter_list|()
function_decl|;
comment|/**      * @return the period      */
specifier|public
specifier|abstract
name|long
name|getPeriod
parameter_list|()
function_decl|;
comment|/**      * @return the cron entry      */
specifier|public
specifier|abstract
name|String
name|getCronEntry
parameter_list|()
function_decl|;
comment|/**      * @return the payload      */
specifier|public
specifier|abstract
name|byte
index|[]
name|getPayload
parameter_list|()
function_decl|;
comment|/**      * Get the start time as a Date time string      * @return the date time      */
specifier|public
name|String
name|getStartTime
parameter_list|()
function_decl|;
comment|/**      * Get the time the job is next due to execute       * @return the date time      */
specifier|public
name|String
name|getNextExecutionTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

