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
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

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
name|AbortSlowConsumerStrategyViewMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current max slow count, -1 disables"
argument_list|)
name|long
name|getMaxSlowCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"sets the count after which a slow consumer will be aborted, -1 disables"
argument_list|)
name|void
name|setMaxSlowCount
parameter_list|(
name|long
name|maxSlowCount
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current max slow (milliseconds) duration"
argument_list|)
name|long
name|getMaxSlowDuration
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"sets the duration (milliseconds) after which a continually slow consumer will be aborted"
argument_list|)
name|void
name|setMaxSlowDuration
parameter_list|(
name|long
name|maxSlowDuration
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the check period at which a sweep of consumers is done to determine continued slowness"
argument_list|)
specifier|public
name|long
name|getCheckPeriod
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"returns the current list of slow consumers, Not HTML friendly"
argument_list|)
name|TabularData
name|getSlowConsumers
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"aborts the slow consumer gracefully by sending a shutdown control message to just that consumer"
argument_list|)
name|void
name|abortConsumer
parameter_list|(
name|ObjectName
name|consumerToAbort
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"aborts the slow consumer forcefully by shutting down it's connection, note: all other users of the connection will be affected"
argument_list|)
name|void
name|abortConnection
parameter_list|(
name|ObjectName
name|consumerToAbort
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"aborts the slow consumer gracefully by sending a shutdown control message to just that consumer"
argument_list|)
name|void
name|abortConsumer
parameter_list|(
name|String
name|objectNameOfConsumerToAbort
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"aborts the slow consumer forcefully by shutting down it's connection, note: all other users of the connection will be affected"
argument_list|)
name|void
name|abortConnection
parameter_list|(
name|String
name|objectNameOfConsumerToAbort
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

