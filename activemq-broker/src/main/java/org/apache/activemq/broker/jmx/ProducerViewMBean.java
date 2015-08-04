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

begin_interface
specifier|public
interface|interface
name|ProducerViewMBean
block|{
comment|/**      * @return the clientId of the Connection the Producer is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"JMS Client id of the Connection the Producer is on."
argument_list|)
name|String
name|getClientId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Connection the Producer is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Connection the Producer is on."
argument_list|)
name|String
name|getConnectionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of the Session the Producer is on      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Session the Producer is on."
argument_list|)
name|long
name|getSessionId
parameter_list|()
function_decl|;
comment|/**      * @return the id of Producer.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"ID of the Producer."
argument_list|)
name|String
name|getProducerId
parameter_list|()
function_decl|;
comment|/**      * @return the destination name      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The name of the destionation the Producer is on."
argument_list|)
name|String
name|getDestinationName
parameter_list|()
function_decl|;
comment|/**      * @return true if the destination is a Queue      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Producer is on a Queue"
argument_list|)
name|boolean
name|isDestinationQueue
parameter_list|()
function_decl|;
comment|/**      * @return true of the destination is a Topic      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Producer is on a Topic"
argument_list|)
name|boolean
name|isDestinationTopic
parameter_list|()
function_decl|;
comment|/**      * @return true if the destination is temporary      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Producer is on a temporary Queue/Topic"
argument_list|)
name|boolean
name|isDestinationTemporary
parameter_list|()
function_decl|;
comment|/**      * @return the windows size configured for the producer      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Configured Window Size for the Producer"
argument_list|)
name|int
name|getProducerWindowSize
parameter_list|()
function_decl|;
comment|/**      * @return if the Producer is configured for Async dispatch      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Is the producer configured for Async Dispatch"
argument_list|)
name|boolean
name|isDispatchAsync
parameter_list|()
function_decl|;
comment|/**      * Returns the User Name used to authorize creation of this Producer.      * This value can be null if display of user name information is disabled.      *      * @return the name of the user that created this Producer      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"User Name used to authorize creation of this Producer"
argument_list|)
name|String
name|getUserName
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"is the producer blocked for Flow Control"
argument_list|)
name|boolean
name|isProducerBlocked
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"total time (ms) Producer Blocked For Flow Control"
argument_list|)
name|long
name|getTotalTimeBlocked
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"percentage of sends Producer Blocked for Flow Control"
argument_list|)
name|int
name|getPercentageBlocked
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"reset flow control state"
argument_list|)
name|void
name|resetFlowControlStats
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Resets statistics."
argument_list|)
name|void
name|resetStatistics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Messages dispatched by Producer"
argument_list|)
name|long
name|getSentCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

