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
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ScheduledMessage
block|{
comment|/**      * The time in milliseconds that a message will wait before being scheduled to be      * delivered by the broker      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULED_DELAY
init|=
literal|"AMQ_SCHEDULED_DELAY"
decl_stmt|;
comment|/**      * The time in milliseconds to wait after the start time to wait before scheduling the message again      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULED_PERIOD
init|=
literal|"AMQ_SCHEDULED_PERIOD"
decl_stmt|;
comment|/**      * The number of times to repeat scheduling a message for delivery      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULED_REPEAT
init|=
literal|"AMQ_SCHEDULED_REPEAT"
decl_stmt|;
comment|/**      * Use a Cron tab entry to set the schedule      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULED_CRON
init|=
literal|"AMQ_SCHEDULED_CRON"
decl_stmt|;
comment|/**      * An Id that is assigned to a Scheduled Message, this value is only available once the      * Message is scheduled, Messages sent to the Browse Destination or delivered to the      * assigned Destination will have this value set.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULED_ID
init|=
literal|"scheduledJobId"
decl_stmt|;
comment|/**      * Special destination to send Message's to with an assigned "action" that the Scheduler      * should perform such as removing a message.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
init|=
literal|"ActiveMQ.Scheduler.Management"
decl_stmt|;
comment|/**      * Used to specify that a some operation should be performed on the Scheduled Message,      * the Message must have an assigned Id for this action to be taken.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION
init|=
literal|"AMQ_SCHEDULER_ACTION"
decl_stmt|;
comment|/**      * Indicates that a browse of the Scheduled Messages is being requested.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION_BROWSE
init|=
literal|"BROWSE"
decl_stmt|;
comment|/**      * Indicates that a Scheduled Message is to be remove from the Scheduler, the Id of      * the scheduled message must be set as a property in order for this action to have      * any effect.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION_REMOVE
init|=
literal|"REMOVE"
decl_stmt|;
comment|/**      * Indicates that all scheduled Messages should be removed.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION_REMOVEALL
init|=
literal|"REMOVEALL"
decl_stmt|;
comment|/**      * A property that holds the beginning of the time interval that the specified action should      * be applied within.  Maps to a long value that specified time in milliseconds since UTC.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION_START_TIME
init|=
literal|"ACTION_START_TIME"
decl_stmt|;
comment|/**      * A property that holds the end of the time interval that the specified action should be      * applied within.  Maps to a long value that specified time in milliseconds since UTC.      */
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SCHEDULER_ACTION_END_TIME
init|=
literal|"ACTION_END_TIME"
decl_stmt|;
block|}
end_interface

end_unit

