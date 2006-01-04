begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|broker
operator|.
name|ConnectionContext
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
name|command
operator|.
name|MessageId
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
name|command
operator|.
name|SubscriptionInfo
import|;
end_import

begin_comment
comment|/**  * A MessageStore for durable topic subscriptions  *  * @version $Revision: 1.4 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|TopicMessageStore
extends|extends
name|MessageStore
block|{
comment|/**      * Stores the last acknowledged messgeID for the given subscription      * so that we can recover and commence dispatching messages from the last      * checkpoint      * @param context TODO      * @param messageId      * @param subscriptionPersistentId      */
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @param sub      * @throws JMSException       */
specifier|public
name|void
name|deleteSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * For the new subscription find the last acknowledged message ID      * and then find any new messages since then and dispatch them      * to the subscription.      *<p/>      * e.g. if we dispatched some messages to a new durable topic subscriber, then went down before      * acknowledging any messages, we need to know the correct point from which to recover from.      * @param subscription      *      * @throws Throwable       */
specifier|public
name|void
name|recoverSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Throwable
function_decl|;
comment|/**      * Finds the subscriber entry for the given consumer info      *       * @param clientId TODO      * @param subscriptionName TODO      * @return      */
specifier|public
name|SubscriptionInfo
name|lookupSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Lists all the durable subscirptions for a given destination.      *       * @param clientId TODO      * @param subscriptionName TODO      * @return      */
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Inserts the subscriber info due to a subscription change      *<p/>      * If this is a new subscription and the retroactive is false, then the last      * message sent to the topic should be set as the last message acknowledged by they new      * subscription.  Otherwise, if retroactive is true, then create the subscription without       * it having an acknowledged message so that on recovery, all message recorded for the       * topic get replayed.      * @param retroactive TODO      *      */
specifier|public
name|void
name|addSubsciption
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

